/**
 * CommunicationManager - Handles all server communication
 * Responsible for WebSocket connections and API calls
 */
class CommunicationManager {
    constructor(vehicleTracker) {
        this.vehicleTracker = vehicleTracker;
        this.stompClient = null;
        this.isUsingWebSocket = true;
        this.apiRefreshInterval = null;
        
        // Status display element
        this.statusElement = document.getElementById('connection-status');
        
        // Set up data source toggle
        const dataSourceToggle = document.getElementById('data-source-toggle');
        if (dataSourceToggle) {
            dataSourceToggle.addEventListener('click', () => this.toggleDataSource());
        }
    }
    
    /**
     * Initialize WebSocket connection
     */
    initWebSocket() {
        // Skip if not using WebSocket
        if (!this.isUsingWebSocket) return;
        
        const socket = new SockJS('/gs-guide-websocket');
        const stompClient = Stomp.over(socket);
        
        // Disable STOMP debug logging in production
        stompClient.debug = null;
        
        // Display connecting status
        this.displayStatus('Connecting to WebSocket...', false);
        
        stompClient.connect({}, (frame) => {
            console.log('WebSocket connected.');
            this.displayStatus('Connected (WebSocket)', true);
            
            // Subscribe to global topic
            stompClient.subscribe('/topic/location-updates', (message) => {
                this.handleWebSocketMessage(message);
            });
            
            // Also subscribe to user-specific queue
            stompClient.subscribe('/user/queue/location-updates', (message) => {
                this.handleWebSocketMessage(message);
            });
            
            // Request initial data
            stompClient.send("/app/fetch-all-locations", {}, {});
        }, (error) => {
            console.error('WebSocket connection error:', error);
            this.displayStatus('Connection failed - retrying...', false);
            
            // Attempt to reconnect after delay
            setTimeout(() => this.initWebSocket(), 5000);
        });
        
        // Store client for potential future use
        this.stompClient = stompClient;
    }
    
    /**
     * Handle incoming WebSocket messages
     */
    handleWebSocketMessage(message) {
        try {
            const vehicleData = JSON.parse(message.body);
            const standardizedData = {
                ...vehicleData,
                vehicleStatus: vehicleData.vehicleStatus || vehicleData.VehicleStatus || 'Unknown',
                deviceID: vehicleData.deviceId || vehicleData.deviceID || 'Unknown'
            };
            
            // Update vehicle tracker with new data
            this.vehicleTracker.updateVehicle(standardizedData, true);
        } catch (error) {
            console.error('Error processing WebSocket message:', error);
            console.error('Message body:', message.body);
        }
    }
    
    /**
     * Toggle between WebSocket and API data sources
     */
    toggleDataSource() {
        this.isUsingWebSocket = !this.isUsingWebSocket;
        const toggleBtn = document.getElementById('data-source-toggle');
        
        if (this.isUsingWebSocket) {
            // Switch to WebSocket
            toggleBtn.textContent = "Data Source: WebSocket";
            toggleBtn.classList.remove('api');
            this.initWebSocket();
            
            // Clear API polling interval
            if (this.apiRefreshInterval) {
                clearInterval(this.apiRefreshInterval);
                this.apiRefreshInterval = null;
            }
        } else {
            // Switch to API
            toggleBtn.textContent = "Data Source: API";
            toggleBtn.classList.add('api');
            
            // Disconnect WebSocket
            if (this.stompClient) {
                this.stompClient.disconnect();
                this.displayStatus('Switched to API', true);
            }
            
            // Fetch data from API and set up polling
            this.fetchLocations();
            this.apiRefreshInterval = setInterval(() => {
                this.fetchLocations();
            }, 10000);
        }
    }
    
    /**
     * Fetch locations from API
     */
    fetchLocations() {
        this.displayStatus('Fetching from API...', true);
        
        fetch('/api/live/locations')
            .then(response => {
                if (!response.ok) {
                    throw new Error(`API request failed with status ${response.status}`);
                }
                return response.json();
            })
            .then(locations => {
                console.log('API Data received:', locations);
                this.displayStatus('Connected (API)', true);
                
                if (Array.isArray(locations)) {
                    locations.forEach(location => {
                        // Standardize data format
                        const standardizedData = {
                            ...location,
                            vehicleStatus: location.vehicleStatus || location.VehicleStatus || 'Unknown',
                            deviceID: location.deviceId || location.deviceID || 'Unknown'
                        };
                        
                        // Update vehicle tracker with new data
                        this.vehicleTracker.updateVehicle(standardizedData, false);
                    });
                    
                    // Fit map to show all vehicles if first load
                    if (Object.keys(this.vehicleTracker.markers).length === locations.length) {
                        this.vehicleTracker.showAllVehicles();
                    }
                }
            })
            .catch(error => {
                console.error('Error fetching from API:', error);
                this.displayStatus('API Error - retrying...', false);
            });
    }
    
    /**
     * Fetch last known positions for all vehicles
     */
    fetchLastKnownPositions() {
        console.log('Loading last known positions...');
        this.fetchData('/api/vehicles/last-known', false);
    }
    
    /**
     * Fetch data from a specific API endpoint
     */
    fetchData(url, isLive = false) {
        fetch(url)
            .then(response => {
                if (!response.ok) {
                    console.error(`API call failed with status ${response.status}`);
                    throw new Error(`Failed to fetch data: ${response.statusText}`);
                }
                return response.json();
            })
            .then(data => {
                console.log("Data received from API:", data);
                if (Array.isArray(data) && data.length > 0) {
                    data.forEach(vehicle => {
                        // Standardize data format
                        const standardizedData = {
                            ...vehicle,
                            vehicleStatus: vehicle.vehicleStatus || vehicle.VehicleStatus || 'Unknown',
                            deviceID: vehicle.deviceId || vehicle.deviceID || 'Unknown'
                        };
                        
                        // Update vehicle
                        this.vehicleTracker.updateVehicle(standardizedData, isLive);
                    });
                    
                    // Fit map to show all vehicles if first load
                    if (!isLive) {
                        this.vehicleTracker.showAllVehicles();
                    }
                } else if (typeof data === 'object' && data !== null) {
                    // Single vehicle data
                    const standardizedData = {
                        ...data,
                        vehicleStatus: data.vehicleStatus || data.VehicleStatus || 'Unknown',
                        deviceID: data.deviceId || data.deviceID || 'Unknown'
                    };
                    
                    this.vehicleTracker.updateVehicle(standardizedData, isLive);
                } else {
                    console.warn('No data received for vehicles.');
                }
            })
            .catch(error => console.error('Error fetching vehicle data:', error));
    }
    
    /**
     * Display connection status
     */
    displayStatus(message, isConnected) {
        if (this.statusElement) {
            this.statusElement.textContent = message;
            this.statusElement.className = isConnected 
                ? 'connection-status connected' 
                : 'connection-status disconnected';
        }
    }
    
    /**
     * Trigger a data refresh
     */
    refreshData() {
        if (this.isUsingWebSocket) {
            // If using WebSocket, just reload last known positions
            this.fetchLastKnownPositions();
            // Also trigger a broadcast via API
            fetch('/api/live/broadcast', { method: 'POST' })
                .then(response => response.text())
                .then(result => console.log('Broadcast result:', result))
                .catch(error => console.error('Error triggering broadcast:', error));
        } else {
            // If using API, fetch from API endpoint
            this.fetchLocations();
        }
    }
    
    /**
     * Start polling for offline devices
     */
    startPolling() {
        // Poll for offline devices
        setInterval(() => {
            if (this.isUsingWebSocket) {
                console.log('Fetching offline vehicles...');
                this.fetchLastKnownPositions();
            }
        }, 30000);
    }
}