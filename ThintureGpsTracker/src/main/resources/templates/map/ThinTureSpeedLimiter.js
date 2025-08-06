/**
 * ThinTureSpeedLimitChecker - Self-contained class to check and alert on speed limits
 * for Thinture GPS tracking system.
 * 
 * Usage:
 * 1. Include this script in your map.html
 * 2. Once VehicleTracker is initialized, create an instance with:
 *    const speedChecker = new ThinTureSpeedLimitChecker(vehicleTracker);
 */
class ThinTureSpeedLimitChecker {
    /**
     * Initialize the speed limit checker
     * @param {Object} vehicleTracker - The existing VehicleTracker instance
     * @param {Object} options - Configuration options
     */
    constructor(vehicleTracker, options = {}) {
        if (!vehicleTracker) {
            console.error("ThinTureSpeedLimitChecker: VehicleTracker is required");
            return;
        }

        // Store reference to the vehicle tracker
        this.vehicleTracker = vehicleTracker;
        
        // Configuration options with defaults
        this.config = {
            checkInterval: options.checkInterval || 5000,       // How often to check (ms)
            tolerance: options.tolerance || 5,                  // Speed tolerance (km/h)
            alertDuration: options.alertDuration || 8000,       // How long to show alerts (ms)
            showSpeedLimitOnMap: options.showSpeedLimitOnMap !== undefined ? 
                options.showSpeedLimitOnMap : true,            // Whether to show speed limit on map
            cacheExpiryTime: options.cacheExpiryTime || 10 * 60 * 1000, // Cache expiry (ms)
            enabled: options.enabled !== undefined ? options.enabled : true, // Whether enabled
            minSpeed: options.minSpeed || 10                    // Min speed to check
        };
        
        // Cache for speed limits to reduce API calls
        this.speedLimitCache = {};
        
        // Store active speed alerts
        this.activeSpeedAlerts = new Set();
        
        // Speed limit markers
        this.speedLimitMarkers = {};
        
        // Initialize speed limit layer group
        this.speedLimitLayerGroup = L.layerGroup();
        this.vehicleTracker.map.addLayer(this.speedLimitLayerGroup);

        // Add styles to document
        this._addStyles();
        
        // Initialize UI
        this._initializeUI();
        
        // Start monitoring
        this._startMonitoring();
        
        console.log('ThinTureSpeedLimitChecker initialized');
    }
    
    /**
     * Add required CSS styles to the document
     * @private
     */
    _addStyles() {
        const styleId = 'thinture-speed-limit-styles';
        
        // Only add styles if not already present
        if (!document.getElementById(styleId)) {
            const styleEl = document.createElement('style');
            styleEl.id = styleId;
            styleEl.textContent = `
                .speed-limit-circle {
                    background-color: white;
                    border: 2px solid #FF5722;
                    color: #FF5722;
                    border-radius: 50%;
                    width: 32px;
                    height: 32px;
                    display: flex;
                    align-items: center;
                    justify-content: center;
                    font-weight: bold;
                    font-size: 14px;
                    box-shadow: 0 2px 5px rgba(0,0,0,0.2);
                }
                
                .speed-alert {
                    background-color: white;
                    border-left: 4px solid #FF5722;
                    margin-bottom: 10px;
                    padding: 10px;
                    box-shadow: 0 2px 4px rgba(0,0,0,0.2);
                    display: flex;
                    align-items: center;
                    border-radius: 4px;
                    transition: opacity 0.3s, transform 0.3s;
                    opacity: 0;
                    animation: fade-in 0.3s ease-out forwards;
                }
                
                @keyframes fade-in {
                    from { opacity: 0; transform: translateY(-10px); }
                    to { opacity: 1; transform: translateY(0); }
                }
                
                .slide-out {
                    opacity: 0;
                    transform: translateX(-100%);
                    transition: opacity 0.3s, transform 0.3s;
                }
                
                .pulse-marker {
                    animation: pulse 1s infinite;
                }
                
                @keyframes pulse {
                    0% { transform: scale(1); }
                    50% { transform: scale(1.2); }
                    100% { transform: scale(1); }
                }
                
                .thinture-speed-section {
                    padding: 15px;
                    margin-top: 15px;
                    border-top: 1px solid #eee;
                }
                
                .thinture-speed-section h3 {
                    margin-top: 0;
                    margin-bottom: 10px;
                    font-size: 16px;
                }
            `;
            document.head.appendChild(styleEl);
        }
    }
    
    /**
     * Initialize UI components
     * @private
     */
    _initializeUI() {
        // Find the sidebar
        const sidebar = document.querySelector('.sidebar');
        if (!sidebar) {
            console.warn("ThinTureSpeedLimitChecker: Could not find sidebar element");
            return;
        }
        
        // Create settings section
        const settingsSection = document.createElement('div');
        settingsSection.className = 'form-group thinture-speed-section';
        settingsSection.innerHTML = `
            <h3>Speed Limit Settings</h3>
            <div class="form-group">
                <label>
                    <input type="checkbox" id="thinture-speed-enabled" ${this.config.enabled ? 'checked' : ''}>
                    Enable Speed Limit Alerts
                </label>
            </div>
            <div class="form-group">
                <label for="thinture-speed-tolerance">Speed Tolerance (km/h):</label>
                <input type="number" id="thinture-speed-tolerance" min="0" max="20" value="${this.config.tolerance}" class="form-control">
            </div>
            <div class="form-group">
                <label>
                    <input type="checkbox" id="thinture-show-speed-limit" ${this.config.showSpeedLimitOnMap ? 'checked' : ''}>
                    Show Speed Limits on Map
                </label>
            </div>
        `;
        
        // Append to sidebar
        sidebar.appendChild(settingsSection);
        
        // Add event listeners
        document.getElementById('thinture-speed-enabled')?.addEventListener('change', (e) => {
            this.config.enabled = e.target.checked;
        });
        
        document.getElementById('thinture-speed-tolerance')?.addEventListener('change', (e) => {
            this.config.tolerance = parseInt(e.target.value, 10);
        });
        
        document.getElementById('thinture-show-speed-limit')?.addEventListener('change', (e) => {
            this.config.showSpeedLimitOnMap = e.target.checked;
            if (!e.target.checked) {
                this._clearSpeedLimitMarkers();
            }
        });
    }
    
    /**
     * Start monitoring vehicles for speed limit violations
     * @private
     */
    _startMonitoring() {
        // Clear existing interval if it exists
        if (this.monitoringInterval) {
            clearInterval(this.monitoringInterval);
        }
        
        // Set interval to check vehicles periodically
        this.monitoringInterval = setInterval(() => {
            if (!this.config.enabled) return;
            
            // Check each vehicle that's currently being tracked
            const liveDevices = Array.from(this.vehicleTracker.liveDevices);
            liveDevices.forEach(deviceId => {
                this._checkVehicleSpeedLimit(deviceId);
            });
            
        }, this.config.checkInterval);
    }
    
    /**
     * Check if a vehicle is exceeding the speed limit
     * @param {string} deviceId - The vehicle's device ID
     * @private
     */
    async _checkVehicleSpeedLimit(deviceId) {
        const vehicle = this.vehicleTracker.vehicleData[deviceId];
        if (!vehicle || !vehicle.latitude || !vehicle.longitude || !vehicle.speed) {
            return;
        }
        
        // Get the vehicle's current speed and location
        const speed = parseFloat(vehicle.speed) || 0;
        const latitude = parseFloat(vehicle.latitude);
        const longitude = parseFloat(vehicle.longitude);
        
        // If speed is very low, we can skip checking
        if (speed < this.config.minSpeed) {
            return;
        }
        
        try {
            // Get the speed limit for the current location
            const speedLimit = await this._getSpeedLimit(latitude, longitude);
            
            // If we couldn't get a speed limit, skip checking
            if (!speedLimit || speedLimit <= 0) {
                return;
            }
            
            // Show the speed limit on the map if enabled
            if (this.config.showSpeedLimitOnMap) {
                this._displaySpeedLimitOnMap(deviceId, latitude, longitude, speedLimit);
            }
            
            // Check if the vehicle is exceeding the speed limit
            if (speed > speedLimit + this.config.tolerance) {
                // Only show alert if we haven't shown one recently for this device
                if (!this.activeSpeedAlerts.has(deviceId)) {
                    this._showSpeedLimitAlert(deviceId, speed, speedLimit);
                    
                    // Mark this device as having an active alert
                    this.activeSpeedAlerts.add(deviceId);
                    
                    // Clear the alert after the specified duration
                    setTimeout(() => {
                        this.activeSpeedAlerts.delete(deviceId);
                    }, this.config.alertDuration);
                }
            }
        } catch (error) {
            console.error('Error checking speed limit:', error);
        }
    }
    
    /**
     * Get the speed limit for a specific location
     * @param {number} latitude - The latitude
     * @param {number} longitude - The longitude
     * @returns {Promise<number>} The speed limit in km/h
     * @private
     */
    async _getSpeedLimit(latitude, longitude) {
        // Create a cache key based on the coordinates (rounded to 4 decimal places)
        const cacheKey = `${latitude.toFixed(4)},${longitude.toFixed(4)}`;
        
        // Check if we have a cached result that isn't expired
        const cachedResult = this.speedLimitCache[cacheKey];
        if (cachedResult && (Date.now() - cachedResult.timestamp < this.config.cacheExpiryTime)) {
            return cachedResult.speedLimit;
        }
        
        // If no cached result, or it's expired, fetch from OSM API
        try {
            // Using Overpass API to query OSM data
            const overpassEndpoint = 'https://overpass-api.de/api/interpreter';
            
            // Query to fetch roads and their speed limits near the specified coordinates
            const query = `
                [out:json];
                way(around:20,${latitude},${longitude})[highway];
                out body;
                >;
                out skel;
            `;
            
            const response = await fetch(overpassEndpoint, {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded'
                },
                body: `data=${encodeURIComponent(query)}`
            });
            
            if (!response.ok) {
                throw new Error(`Overpass API error: ${response.status}`);
            }
            
            const data = await response.json();
            
            // Process the OSM data
            let speedLimit = 0;
            
            // Find the closest road with a speed limit
            let closestRoad = null;
            let minDistance = Infinity;
            
            // Process each way (road) in the response
            if (data.elements) {
                for (const element of data.elements) {
                    if (element.type === 'way' && element.tags && element.tags.highway) {
                        // Calculate distance to this road (simple approximation)
                        // In a real implementation, you'd calculate the closest point on the way
                        const roadCenter = this._calculateWayCentroid(element, data.elements);
                        if (roadCenter) {
                            const distance = this._calculateDistance(
                                latitude, longitude, 
                                roadCenter.lat, roadCenter.lon
                            );
                            
                            if (distance < minDistance) {
                                minDistance = distance;
                                closestRoad = element;
                            }
                        }
                    }
                }
            }
            
            // Extract speed limit from the closest road
            if (closestRoad && closestRoad.tags) {
                if (closestRoad.tags.maxspeed) {
                    // Direct speed limit tag
                    speedLimit = this._parseSpeedLimit(closestRoad.tags.maxspeed);
                } else {
                    // Estimate based on road type if no explicit speed limit
                    speedLimit = this._estimateSpeedLimitFromRoadType(closestRoad.tags.highway);
                }
            }
            
            // Cache the result
            this.speedLimitCache[cacheKey] = {
                speedLimit: speedLimit,
                timestamp: Date.now(),
                roadName: closestRoad?.tags?.name || 'Unknown Road'
            };
            
            return speedLimit;
        } catch (error) {
            console.error('Error fetching speed limit from OSM:', error);
            return 0;
        }
    }
    
    /**
     * Calculate the centroid of a way from its nodes
     * @param {Object} way - The OSM way object
     * @param {Array} elements - All elements from the OSM response
     * @returns {Object|null} - The centroid coordinates or null
     * @private
     */
    _calculateWayCentroid(way, elements) {
        if (!way.nodes || way.nodes.length === 0) {
            return null;
        }
        
        // Find all nodes that are part of this way
        const wayNodes = elements.filter(
            el => el.type === 'node' && way.nodes.includes(el.id)
        );
        
        if (wayNodes.length === 0) {
            return null;
        }
        
        // Calculate the center
        let sumLat = 0;
        let sumLon = 0;
        
        wayNodes.forEach(node => {
            sumLat += node.lat;
            sumLon += node.lon;
        });
        
        return {
            lat: sumLat / wayNodes.length,
            lon: sumLon / wayNodes.length
        };
    }
    
    /**
     * Calculate distance between two points using Haversine formula
     * @param {number} lat1 - Latitude of point 1
     * @param {number} lon1 - Longitude of point 1
     * @param {number} lat2 - Latitude of point 2
     * @param {number} lon2 - Longitude of point 2
     * @returns {number} - Distance in meters
     * @private
     */
    _calculateDistance(lat1, lon1, lat2, lon2) {
        const R = 6371e3; // Earth radius in meters
        const φ1 = lat1 * Math.PI / 180;
        const φ2 = lat2 * Math.PI / 180;
        const Δφ = (lat2 - lat1) * Math.PI / 180;
        const Δλ = (lon2 - lon1) * Math.PI / 180;

        const a = Math.sin(Δφ/2) * Math.sin(Δφ/2) +
                Math.cos(φ1) * Math.cos(φ2) *
                Math.sin(Δλ/2) * Math.sin(Δλ/2);
        const c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        
        return R * c;
    }
    
    /**
     * Parse a speed limit string to a number
     * @param {string} speedLimitStr - The speed limit string from OSM
     * @returns {number} - The speed limit in km/h
     * @private
     */
    _parseSpeedLimit(speedLimitStr) {
        if (!speedLimitStr) return 0;
        
        // Handle simple numeric speed limits
        const numericMatch = speedLimitStr.match(/^\d+/);
        if (numericMatch) {
            return parseInt(numericMatch[0], 10);
        }
        
        // Handle mph conversions
        if (speedLimitStr.includes('mph')) {
            const mphMatch = speedLimitStr.match(/(\d+)\s*mph/);
            if (mphMatch) {
                // Convert mph to km/h (multiply by 1.60934)
                return Math.round(parseInt(mphMatch[1], 10) * 1.60934);
            }
        }
        
        // Handle special values
        switch(speedLimitStr.trim().toLowerCase()) {
            case 'walk':
            case 'walking':
                return 5;
            case 'none':
                return 0;
            case 'signals':
                return 0; // Depends on signals
            default:
                return 0;
        }
    }
    
    /**
     * Estimate speed limit based on road type if not explicitly tagged
     * @param {string} roadType - The OSM highway tag value
     * @returns {number} - Estimated speed limit in km/h
     * @private
     */
    _estimateSpeedLimitFromRoadType(roadType) {
        if (!roadType) return 0;
        
        // Default speed limits by road type (vary by country)
        // These are rough estimates for urban areas
        switch(roadType.toLowerCase()) {
            case 'motorway':
                return 100;
            case 'trunk':
                return 90;
            case 'primary':
                return 80;
            case 'secondary':
                return 60;
            case 'tertiary':
                return 50;
            case 'unclassified':
                return 40;
            case 'residential':
                return 30;
            case 'service':
                return 20;
            case 'living_street':
                return 10;
            default:
                return 50; // Default assumption
        }
    }
    
    /**
     * Display the speed limit on the map near the vehicle
     * @param {string} deviceId - The vehicle's device ID
     * @param {number} latitude - The vehicle's latitude
     * @param {number} longitude - The vehicle's longitude
     * @param {number} speedLimit - The speed limit in km/h
     * @private
     */
    _displaySpeedLimitOnMap(deviceId, latitude, longitude, speedLimit) {
        // If we're not showing speed limits, return
        if (!this.config.showSpeedLimitOnMap) {
            return;
        }
        
        // Clear existing marker for this device
        if (this.speedLimitMarkers[deviceId]) {
            this.speedLimitLayerGroup.removeLayer(this.speedLimitMarkers[deviceId]);
        }
        
        // Create icon with the speed limit
        const speedLimitIcon = L.divIcon({
            className: 'speed-limit-icon',
            html: `<div class="speed-limit-circle">${speedLimit}</div>`,
            iconSize: [36, 36],
            iconAnchor: [18, 18]
        });
        
        // Calculate offset position (30 meters behind the vehicle)
        const marker = this.vehicleTracker.markers[deviceId];
        if (!marker) return;
        
        const position = marker.getLatLng();
        const course = this.vehicleTracker.previousCourses[deviceId] || 0;
        
        // Convert course to radians and add 180 degrees to get position behind
        const courseRad = (course + 180) * Math.PI / 180;
        const offsetDistance = 0.0003; // ~30 meters
        
        const offsetLat = position.lat + Math.sin(courseRad) * offsetDistance;
        const offsetLng = position.lng + Math.cos(courseRad) * offsetDistance;
        
        // Create marker at the offset position
        const speedLimitMarker = L.marker([offsetLat, offsetLng], {
            icon: speedLimitIcon,
            zIndexOffset: -100 // Put below vehicle markers
        });
        
        // Add to layer group and store reference
        this.speedLimitLayerGroup.addLayer(speedLimitMarker);
        this.speedLimitMarkers[deviceId] = speedLimitMarker;
    }
    
    /**
     * Clear all speed limit markers from the map
     * @private
     */
    _clearSpeedLimitMarkers() {
        this.speedLimitLayerGroup.clearLayers();
        this.speedLimitMarkers = {};
    }
    
    /**
     * Show an alert when a vehicle exceeds the speed limit
     * @param {string} deviceId - The vehicle's device ID
     * @param {number} currentSpeed - The vehicle's current speed
     * @param {number} speedLimit - The speed limit
     * @private
     */
    _showSpeedLimitAlert(deviceId, currentSpeed, speedLimit) {
        const container = document.getElementById('alert-container');
        if (!container) return;
        
        const vehicle = this.vehicleTracker.vehicleData[deviceId];
        if (!vehicle) return;
        
        // Get the cached road name if available
        const cacheKey = `${vehicle.latitude.toFixed(4)},${vehicle.longitude.toFixed(4)}`;
        const cachedData = this.speedLimitCache[cacheKey];
        const roadName = cachedData?.roadName || 'Unknown Road';
        
        // Create the alert element
        const alertDiv = document.createElement('div');
        alertDiv.className = 'speed-alert';
        
        // Create alert content
        alertDiv.innerHTML = `
            <div style="width: 24px; height: 24px; margin-right: 10px; background-color: #FF5722; border-radius: 50%; display: flex; align-items: center; justify-content: center; color: white; font-weight: bold;">
                !
            </div>
            <div style="flex: 1;">
                <strong>Speed Limit Exceeded</strong>
                <span style="display: block; margin-top: 5px;">Device ID: ${deviceId}</span>
                <span style="display: block; font-size: 12px; color: #FF5722; font-weight: bold;">
                    Current: ${Math.round(currentSpeed)} km/h | Limit: ${speedLimit} km/h
                </span>
                <span style="display: block; font-size: 12px; color: #666;">
                    Road: ${roadName}
                </span>
            </div>
            <button style="background: none; border: none; color: #999; font-size: 16px; cursor: pointer;" 
                    onclick="this.parentElement.classList.add('slide-out'); setTimeout(() => this.parentElement.remove(), 500);">×</button>
        `;
        
        // Add to container
        container.appendChild(alertDiv);
        
        // Auto-remove after alert duration
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.classList.add('slide-out');
                setTimeout(() => {
                    if (alertDiv.parentNode) {
                        alertDiv.remove();
                    }
                }, 500);
            }
        }, this.config.alertDuration);
        
        // Also make the vehicle marker pulse
        this._pulseVehicleMarker(deviceId);
    }
    
    /**
     * Make a vehicle marker pulse to highlight it
     * @param {string} deviceId - The vehicle's device ID
     * @private
     */
    _pulseVehicleMarker(deviceId) {
        const marker = this.vehicleTracker.markers[deviceId];
        if (!marker) return;
        
        // Add a pulse animation class to the marker
        const markerElement = marker.getElement();
        if (markerElement) {
            markerElement.classList.add('pulse-marker');
            
            // Remove the class after animation completes
            setTimeout(() => {
                markerElement.classList.remove('pulse-marker');
            }, 2000);
        }
    }
    
    /**
     * Public method to manually check a specific vehicle
     * @param {string} deviceId - The vehicle's device ID
     */
    checkVehicle(deviceId) {
        if (this.config.enabled && deviceId) {
            this._checkVehicleSpeedLimit(deviceId);
        }
    }
    
    /**
     * Public method to enable or disable the checker
     * @param {boolean} enabled - Whether to enable the checker
     */
    setEnabled(enabled) {
        this.config.enabled = !!enabled;
        
        const checkbox = document.getElementById('thinture-speed-enabled');
        if (checkbox) {
            checkbox.checked = this.config.enabled;
        }
    }
    
    /**
     * Public method to set the speed tolerance
     * @param {number} tolerance - The tolerance in km/h
     */
    setTolerance(tolerance) {
        this.config.tolerance = parseInt(tolerance, 10) || 5;
        
        const input = document.getElementById('thinture-speed-tolerance');
        if (input) {
            input.value = this.config.tolerance;
        }
    }
    
    /**
     * Public method to toggle speed limit display on the map
     * @param {boolean} show - Whether to show speed limits
     */
    showSpeedLimits(show) {
        this.config.showSpeedLimitOnMap = !!show;
        
        const checkbox = document.getElementById('thinture-show-speed-limit');
        if (checkbox) {
            checkbox.checked = this.config.showSpeedLimitOnMap;
        }
        
        if (!this.config.showSpeedLimitOnMap) {
            this._clearSpeedLimitMarkers();
        }
    }
    
    /**
     * Public method to clear the speed limit cache
     */
    clearCache() {
        this.speedLimitCache = {};
        console.log('Speed limit cache cleared');
    }
}