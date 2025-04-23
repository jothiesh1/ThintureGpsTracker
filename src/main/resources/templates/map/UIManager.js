/**
 * UIManager - Handles all UI elements and user interactions
 */
class UIManager {
    constructor(vehicleTracker) {
        this.vehicleTracker = vehicleTracker;
        this.filterTimeout = null;
        
        // Initialize sidebar
        this.initializeSidebar();
        
        // Initialize filters and controls
        this.initializeControls();
    }
    
    /**
     * Initialize sidebar behavior
     */
    initializeSidebar() {
        const sidebar = document.querySelector('.sidebar');
        const toggleBtn = document.getElementById('sidebar-toggle');
        
        if (sidebar && toggleBtn) {
            // Hide sidebar on load
            sidebar.classList.add('hidden');
            toggleBtn.textContent = '☰ Open Sidebar';
            toggleBtn.style.right = '10px';
    
            toggleBtn.addEventListener('click', () => {
                sidebar.classList.toggle('hidden');
    
                const isHidden = sidebar.classList.contains('hidden');
    
                // Update button position and label
                toggleBtn.textContent = isHidden ? '☰ Open Sidebar' : '✖ Close Sidebar';
                toggleBtn.style.right = isHidden ? '10px' : '320px';
            });
        }
        
        // Set up user info
        this.setupUserInfo();
    }
    
    /**
     * Initialize filters and control buttons
     */
    initializeControls() {
        // Set up vehicle filter input
        const filterInput = document.getElementById('vehicle-filter');
        if (filterInput) {
            filterInput.addEventListener('input', () => {
                // Debounce filter to avoid excessive filtering
                clearTimeout(this.filterTimeout);
                this.filterTimeout = setTimeout(() => {
                    this.vehicleTracker.filterVehicles();
                }, 300);
            });
        }
        
        // Set up status filter dropdown
        const statusFilter = document.getElementById('status-filter');
        if (statusFilter) {
            statusFilter.addEventListener('change', () => {
                this.vehicleTracker.filterVehicles();
            });
        }
        
        // Set up show all vehicles button
        const showAllBtn = document.getElementById('show-all-btn');
        if (showAllBtn) {
            showAllBtn.addEventListener('click', () => {
                this.vehicleTracker.showAllVehicles();
            });
        }
        
        // Set up refresh button
        const refreshBtn = document.getElementById('refresh-btn');
        if (refreshBtn) {
            refreshBtn.addEventListener('click', () => {
                this.vehicleTracker.communicationManager.refreshData();
            });
        }
        
        // Initialize select boxes
        const selects = document.querySelectorAll('select');
        selects.forEach(select => {
            if (select.value === '') {
                const firstOption = select.querySelector('option');
                if (firstOption) select.value = firstOption.value;
            }
        });
    }
    
    /**
     * Update the vehicle list in the sidebar
     */
    updateVehicleList() {
        const vehicleList = document.getElementById('vehicle-list');
        if (!vehicleList) return;
        
        // Clear existing list
        vehicleList.innerHTML = '';
        
        // Get all vehicles from markers
        const vehicles = Object.keys(this.vehicleTracker.markers).map(deviceId => {
            return {
                deviceId: deviceId,
                ...this.vehicleTracker.vehicleData[deviceId],
                isLive: this.vehicleTracker.liveDevices.has(deviceId),
                lastUpdate: this.vehicleTracker.deviceLastUpdate[deviceId] || 0
            };
        });
        
        // Sort vehicles: live ones first, then by last update time
        vehicles.sort((a, b) => {
            if (a.isLive && !b.isLive) return -1;
            if (!a.isLive && b.isLive) return 1;
            return b.lastUpdate - a.lastUpdate;
        });
        
        // Apply filters if set
        const filterText = document.getElementById('vehicle-filter')?.value?.toLowerCase() || '';
        const statusFilter = document.getElementById('status-filter')?.value?.toUpperCase() || '';
        
        const filteredVehicles = vehicles.filter(vehicle => {
            // Filter by text (device ID or status)
            const matchesText = !filterText || 
                vehicle.deviceId?.toLowerCase().includes(filterText) || 
                vehicle.vehicleStatus?.toLowerCase().includes(filterText);
            
            // Filter by status
            const matchesStatus = !statusFilter || 
                vehicle.vehicleStatus?.toUpperCase() === statusFilter;
            
            return matchesText && matchesStatus;
        });
        
        // Add filtered vehicles to list
        filteredVehicles.forEach(vehicle => {
            const vehicleItem = document.createElement('div');
            vehicleItem.className = 'vehicle-item';
            vehicleItem.dataset.deviceId = vehicle.deviceId;
            
            // Determine status indicator class
            let statusClass = 'status-offline';
            if (vehicle.isLive) {
                statusClass = 'status-online';
            } else if ((vehicle.vehicleStatus || '').toUpperCase() === 'PARKED') {
                statusClass = 'status-idle';
            }
            
            vehicleItem.innerHTML = `
                <span class="vehicle-status ${statusClass}"></span>
                <strong>${vehicle.deviceId}</strong>
                <div><small>${vehicle.vehicleStatus || 'Unknown'} - ${vehicle.timestamp ? new Date(vehicle.timestamp).toLocaleTimeString() : 'N/A'}</small></div>
            `;
            
            vehicleItem.addEventListener('click', () => {
                this.vehicleTracker.centerOnVehicle(vehicle.deviceId);
            });
            
            vehicleList.appendChild(vehicleItem);
        });
        
        // Show message if no vehicles match filter
        if (filteredVehicles.length === 0) {
            const noVehicles = document.createElement('div');
            noVehicles.className = 'vehicle-item';
            noVehicles.textContent = filterText || statusFilter 
                ? 'No vehicles match current filters'
                : 'No vehicles available';
            vehicleList.appendChild(noVehicles);
        }
    }
    
    /**
     * Setup user info section
     */
    setupUserInfo() {
        // Get user info from hidden fields
        const userRole = document.getElementById('userRole')?.value || 'ROLE_USER';
        
        // Display user role badge
        const roleBadge = document.getElementById('role-badge');
        if (roleBadge) {
            let roleText = userRole.replace('ROLE_', '');
            roleBadge.textContent = roleText;
            
            // Remove all role classes
            roleBadge.classList.remove('role-superadmin', 'role-admin', 'role-dealer', 'role-client', 'role-user');
            
            // Add appropriate role class
            switch (userRole) {
                case 'ROLE_SUPERADMIN':
                    roleBadge.classList.add('role-superadmin');
                    break;
                case 'ROLE_ADMIN':
                    roleBadge.classList.add('role-admin');
                    break;
                case 'ROLE_DEALER':
                    roleBadge.classList.add('role-dealer');
                    break;
                case 'ROLE_CLIENT':
                    roleBadge.classList.add('role-client');
                    break;
                case 'ROLE_USER':
                    roleBadge.classList.add('role-user');
                    break;
            }
        }
        
        // Set up username display
        const username = document.getElementById('username');
        if (username) {
            const displayRole = userRole.replace('ROLE_', '');
            
            // Set sample username based on role
            switch (userRole) {
                case 'ROLE_SUPERADMIN':
                    username.textContent = 'Super Admin';
                    break;
                case 'ROLE_ADMIN':
                    username.textContent = 'Admin User';
                    break;
                case 'ROLE_DEALER':
                    username.textContent = 'Dealer';
                    break;
                case 'ROLE_CLIENT':
                    username.textContent = 'Client';
                    break;
                case 'ROLE_USER':
                    username.textContent = 'Regular User';
                    break;
                default:
                    username.textContent = 'User';
            }
        }
    }
    
    /**
     * Show an alert notification
     */
    showAlert(vehicle, type) {
        const deviceId = vehicle.deviceId || vehicle.deviceID || 'Unknown';
        if (this.vehicleTracker.activeAlerts.has(deviceId)) return;
        
        const container = document.getElementById('alert-container');
        if (!container) return;
        
        const config = {
            parking: {
                class: 'parking-alert',
                icon: '/THINTURE_IMAGE/car/parking-area.png',
                title: 'Vehicle Parked'
            },
            live: {
                class: 'live-alert',
                icon: '/THINTURE_IMAGE/car/car_icon4.png',
                title: 'Live Message Alert'
            },
            speed: {
                class: 'speed-alert',
                icon: '/THINTURE_IMAGE/car/speed-limit.png',
                title: 'Speed Limit Exceeded'
            }
        };
        
        const alertConfig = config[type];
        const alertDiv = document.createElement('div');
        alertDiv.className = alertConfig.class;
        alertDiv.style.opacity = '0';
        
        // Create alert content
        alertDiv.innerHTML = `
            <img src="${alertConfig.icon}" class="${type}-icon" alt="${type}" 
                 onerror="this.src='data:image/svg+xml;utf8,<svg xmlns=\\'http://www.w3.org/2000/svg\\' width=\\'24\\' height=\\'24\\'><circle cx=\\'12\\' cy=\\'12\\' r=\\'10\\' fill=\\'orange\\'/></svg>'">
            <div class="${type}-info">
                <strong>${alertConfig.title}</strong>
                <span>Device ID: ${deviceId}</span>
                <span>Time: ${new Date().toLocaleTimeString()}</span>
            </div>
            <button class="alert-close" onclick="thintureApp.removeAlert(this.parentElement, '${deviceId}')">×</button>
        `;
        
        // Force reflow to enable transition
        void alertDiv.offsetWidth;
        container.appendChild(alertDiv);
        alertDiv.style.opacity = '1';
        
        this.vehicleTracker.activeAlerts.add(deviceId);
        
        // Auto-remove after 10 seconds
        setTimeout(() => {
            if (alertDiv.parentNode) {
                alertDiv.classList.add('slide-out');
                setTimeout(() => {
                    this.removeAlert(alertDiv, deviceId);
                }, 500);
            }
        }, 10000);
    }
    
    /**
     * Remove an alert element
     */
    removeAlert(alertDiv, deviceId) {
        if (alertDiv && alertDiv.parentNode) {
            alertDiv.remove();
        }
        this.vehicleTracker.activeAlerts.delete(deviceId);
    }
}