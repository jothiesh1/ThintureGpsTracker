/**
 * VehicleTracker - Core class for tracking vehicles
 * Responsible for map management and vehicle marker updates
 */
class VehicleTracker {
    constructor(mapElementId) {
        // Initialize map
        this.map = L.map(mapElementId).setView([13.07757, 77.55928], 5);
        
        // Data structures
        this.markers = {};
        this.liveDevices = new Set();
        this.deviceLastUpdate = {};
        this.previousCourses = {};
        this.vehicleData = {};
        this.inactivityThreshold = 30000;
        this.activeAlerts = new Set();
        
        // Get user info
        this.userRole = document.getElementById('userRole')?.value || 'ROLE_USER';
        this.userId = document.getElementById('userId')?.value || '0';
        
        // Initialize map components
        this.initializeMapLayers();
        
        // Create dependent components
        this.uiManager = new UIManager(this);
        this.communicationManager = new CommunicationManager(this);
        this.speedometer = new Speedometer();
        
        // Initialize WebSocket and load data
        this.communicationManager.initWebSocket();
        this.communicationManager.fetchLastKnownPositions();
        
        // Start monitoring
        this.communicationManager.startPolling();
        this.startInactivityMonitoring();
        
        console.log(`VehicleTracker initialized for ${this.userRole} (User ID: ${this.userId})`);
    }
    
    /**
     * Initialize map layers
     */
    initializeMapLayers() {
        // Add OpenStreetMap tile layer
        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
            attribution: '&copy; <a href="https://www.openstreetmap.org/copyright">OpenStreetMap</a> contributors',
            maxZoom: 19
        }).addTo(this.map);
        
        // Initialize marker cluster group
        this.markerClusterGroup = L.markerClusterGroup({
            iconCreateFunction: function(cluster) {
                const count = cluster.getChildCount();
                let className = 'marker-cluster-small';
                
                if (count > 10) {
                    className = 'marker-cluster-medium';
                } else if (count > 50) {
                    className = 'marker-cluster-large';
                }
                
                return L.divIcon({
                    html: `<div><span>${count}</span></div>`,
                    className: className,
                    iconSize: L.point(40, 40)
                });
            }
        });
        
        this.map.addLayer(this.markerClusterGroup);
    }
    
    /**
     * Update or create a vehicle marker
     */
    updateVehicle(vehicle, isLive = false) {
        // Store vehicle data for reference
        const deviceId = vehicle.deviceId || vehicle.deviceID || 'Unknown';
        this.vehicleData[deviceId] = vehicle;
        
        // Update marker on map
        this.createOrUpdateMarker(vehicle, isLive);
        
        // Update vehicle list
        this.uiManager.updateVehicleList();
    }
    
    /**
     * Create or update a marker for a vehicle
     */
    createOrUpdateMarker(vehicle, isLive = false) {
        if (!vehicle.latitude || !vehicle.longitude) {
            console.error('Invalid latitude or longitude for vehicle:', vehicle);
            return;
        }
        
        const deviceId = vehicle.deviceId || vehicle.deviceID || 'Unknown';
        const latitude = parseFloat(vehicle.latitude);
        const longitude = parseFloat(vehicle.longitude);
        const course = parseFloat(vehicle.course || 0);
        const ignition = vehicle.ignition || 'IGoff';
        const vehicleStatus = vehicle.vehicleStatus || vehicle.VehicleStatus || 'Unknown';
        const speed = vehicle.speed || '0';
        
        // Calculate rotation for smooth transition
        let rotation = course;
        if (this.previousCourses[deviceId]) {
            const prevCourse = this.previousCourses[deviceId];
            const diff = course - prevCourse;
            if (Math.abs(diff) > 180) {
                rotation = diff > 0 ? course - 360 : course + 360;
            }
        }
        this.previousCourses[deviceId] = course;
        
        if (isLive) {
            console.log('Live update received:', {
                deviceId,
                status: vehicleStatus,
                ignition,
                course: rotation,
                speed,
                isLive
            });
        }
        
        const icon = this.getVehicleIcon(rotation, ignition, vehicleStatus);
        const timestamp = vehicle.timestamp ? new Date(vehicle.timestamp).toLocaleString() : 'N/A';
        
        const markerHtml = `
            <img 
                src="${icon.path}" 
                style="transform: rotate(${icon.rotation}deg); width: 38px; height: 38px;" 
                alt="vehicle-icon"
                onerror="
                    if (!this.retryCount) {
                        this.retryCount = 1;
                        this.src = '/THINTURE_IMAGE/car/${icon.color}/car0.png';
                    } else {
                        // If image load fails, use a default SVG icon
                        this.outerHTML = '<svg xmlns=\\'http://www.w3.org/2000/svg\\' viewBox=\\'0 0 24 24\\' width=\\'38\\' height=\\'38\\'><circle cx=\\'12\\' cy=\\'12\\' r=\\'10\\' fill=\\'${icon.color}\\' /><path d=\\'M7 12 L12 7 L17 12 L12 17 Z\\' fill=\\'white\\' /></svg>';
                    }
                "
            >`;
        
        const popupContent = `
            <div class="vehicle-popup">
                <div class="popup-header ${isLive ? 'live-update' : 'last-known'}">
                    ${isLive ? 'Live Update' : 'Last Known Position'}
                </div>
                <div class="popup-content">
                    <p><span class="popup-label">Device ID:</span> ${deviceId}</p>
                    <p><span class="popup-label">Timestamp:</span> ${timestamp}</p>
                    <p><span class="popup-label">Speed:</span> ${speed} km/h</p>
                    <p>
                        <span class="popup-label">Status:</span> 
                        <span class="status-indicator ${
                            (ignition === 'IGoff' && (vehicleStatus || '').toUpperCase() === 'PARKED') 
                            ? 'status-parked' 
                            : 'status-running'
                        }">
                            ${vehicleStatus}
                        </span>
                    </p>
                    <p>
                        <span class="popup-label">Ignition:</span> 
                        <span class="${ignition === 'IGon' ? 'ignition-on' : 'ignition-off'}">
                            ${ignition === 'IGon' ? 'ON' : 'OFF'}
                        </span>
                    </p>
                    ${vehicle.address ? `
                        <p>
                            <span class="popup-label">Address:</span>
                            <span class="address">${vehicle.address}</span>
                        </p>
                    ` : ''}
                    <div style="margin-top: 10px; text-align: center;">
                        <button onclick="thintureApp.centerOnVehicle('${deviceId}')">Center Map</button>
                        <button onclick="thintureApp.showSpeedometer('${deviceId}', ${speed})">📊 Speedometer</button>
                        <button class="playback-btn" onclick="navigateToReplay('${deviceId}')">⏮ Playback</button>
                        ${isLive ? `
                            <button class="btn-red" onclick="thintureApp.showAlert('${deviceId}', 'live')">
                                Show Alert
                            </button>
                        ` : ''}
                    </div>
                </div>
            </div>
        `;
        
        if (this.markers[deviceId]) {
            // Update existing marker
            this.markers[deviceId]
                .setLatLng([latitude, longitude])
                .setIcon(L.divIcon({
                    className: 'custom-icon',
                    html: markerHtml,
                    iconSize: [38, 38],
                    iconAnchor: [19, 19],
                    popupAnchor: [0, -19],
                }))
                .bindPopup(popupContent);
        } else {
            // Create new marker
            const marker = L.marker([latitude, longitude], {
                icon: L.divIcon({
                    className: 'custom-icon',
                    html: markerHtml,
                    iconSize: [38, 38],
                    iconAnchor: [19, 19],
                    popupAnchor: [0, -19],
                }),
            }).bindPopup(popupContent);
            
            this.markers[deviceId] = marker;
            this.markerClusterGroup.addLayer(marker);
        }
        
        // Check for parking alert condition
        if (isLive && ignition === 'IGoff' && (vehicleStatus || '').toUpperCase() === 'PARKED') {
            this.uiManager.showAlert(vehicle, 'parking');
        }
        
        this.deviceLastUpdate[deviceId] = Date.now();
        if (isLive) {
            this.liveDevices.add(deviceId);
        }
        
        // Update speedometer if this vehicle is being tracked
        if (isLive && this.speedometer.currentVehicleId === deviceId) {
            this.speedometer.update(parseFloat(speed) || 0);
        }
    }
    
    /**
     * Get the icon for a vehicle based on its status
     */
    getVehicleIcon(course, ignition, vehicleStatus) {
        const basePath = '/THINTURE_IMAGE/car';
        
        // Default color if images don't load
        let color = 'red';
        
        const normalizedStatus = (vehicleStatus || '').toUpperCase();
        const normalizedIgnition = (ignition || '').toUpperCase();
        
        if (normalizedStatus === 'PARKED' && normalizedIgnition === 'IGOFF') {
            color = 'orange';
        } else if (normalizedIgnition === 'IGON' || normalizedStatus === 'RUNNING') {
            color = 'green';
        }
        
        const adjustedCourse = ((parseFloat(course) % 360) + 360) % 360;
        const imagePath = `${basePath}/${color}/car0.png`;
        
        return {
            path: imagePath,
            color: color,
            rotation: adjustedCourse,
        };
    }
    
    /**
     * Center the map on a specific vehicle
     */
    centerOnVehicle(deviceId) {
        const marker = this.markers[deviceId];
        if (marker) {
            const position = marker.getLatLng();
            this.map.setView(position, 15);
            marker.openPopup();
            
            // Update speedometer if it's visible and tracking this vehicle
            if (this.speedometer.currentVehicleId === deviceId) {
                const speed = this.getVehicleSpeed(deviceId);
                this.speedometer.update(speed);
            }
        }
    }
    
    /**
     * Get the current speed of a vehicle
     */
    getVehicleSpeed(deviceId) {
        const vehicle = this.vehicleData[deviceId];
        if (vehicle) {
            return parseFloat(vehicle.speed) || 0;
        }
        return 0;
    }
    
    /**
     * Show all vehicles on the map
     */
    showAllVehicles() {
        const markers = Object.values(this.markers);
        if (markers.length === 0) {
            return;
        }
        
        const bounds = L.latLngBounds(markers.map(marker => marker.getLatLng()));
        this.map.fitBounds(bounds, { padding: [50, 50], maxZoom: 15 });
    }
    
    /**
     * Filter vehicles based on user input
     */
    filterVehicles() {
        // Update the vehicle list with current filters
        this.uiManager.updateVehicleList();
        
        // Also update markers on map
        const filterText = document.getElementById('vehicle-filter')?.value?.toLowerCase() || '';
        const statusFilter = document.getElementById('status-filter')?.value?.toUpperCase() || '';
        
        Object.entries(this.markers).forEach(([deviceId, marker]) => {
            // Get vehicle data
            const vehicle = this.vehicleData[deviceId];
            const status = (vehicle?.vehicleStatus || '').toLowerCase();
            
            // Apply filters
            const matchesText = !filterText || 
                deviceId.toLowerCase().includes(filterText) || 
                status.includes(filterText);
            
            const matchesStatus = !statusFilter || 
                (vehicle?.vehicleStatus || '').toUpperCase() === statusFilter;
            
            // Show or hide marker based on filters
            if (matchesText && matchesStatus) {
                if (!this.markerClusterGroup.hasLayer(marker)) {
                    this.markerClusterGroup.addLayer(marker);
                }
            } else {
                this.markerClusterGroup.removeLayer(marker);
            }
        });
    }
    
    /**
     * Start monitoring for inactive devices
     */
    startInactivityMonitoring() {
        setInterval(() => {
            const now = Date.now();
            this.liveDevices.forEach(deviceId => {
                if (now - this.deviceLastUpdate[deviceId] > this.inactivityThreshold) {
                    console.log('Device became inactive:', deviceId);
                    this.liveDevices.delete(deviceId);
                    
                    // Only fetch if using WebSocket (API will handle via polling)
                    if (this.communicationManager.isUsingWebSocket) {
                        this.communicationManager.fetchData(`/api/vehicle/latest-location/${deviceId}`, false);
                    }
                }
            });
        }, this.inactivityThreshold);
    }
    
    /**
     * Show the speedometer for a specific vehicle
     */
    showSpeedometer(deviceId, speed) {
        this.speedometer.showForVehicle(deviceId, speed);
    }
}