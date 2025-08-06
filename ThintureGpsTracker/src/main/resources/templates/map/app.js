/**
 * Thinture GPS Application - Main initialization file
 * This file initializes the entire application and connects all modules
 */

// Initialize the application when DOM is fully loaded
document.addEventListener('DOMContentLoaded', () => {
    // Create the global app object
    window.thintureApp = new ThintureApp();
});

/**
 * ThintureApp - Main Application Class
 * Central point that connects all modules and provides public API
 */
class ThintureApp {
    constructor() {
        console.log('Initializing Thinture GPS Application...');
        
        // Initialize the vehicle tracker (core module)
        this.vehicleTracker = new VehicleTracker('map');
        
        // Initialize the SpeedLimiter if available
        if (typeof ThinTureSpeedLimiter !== 'undefined') {
            this.speedLimiter = new ThinTureSpeedLimiter(this.vehicleTracker);
            console.log('Speed Limiter module initialized');
        }
        
        // Set up event listeners for global elements
        this.setupGlobalEventHandlers();
        
        console.log('Thinture GPS Application initialized successfully');
    }
    
    /**
     * Set up event handlers for global elements
     */
    setupGlobalEventHandlers() {
        // Example: Global error handler
        window.addEventListener('error', (event) => {
            console.error('Global error:', event.message);
            this.showNotification('An error occurred. Please check console for details.', 'error');
        });
        
        // Set up keyboard shortcuts if needed
        document.addEventListener('keydown', (event) => {
            // Example: Ctrl+F to focus on filter
            if (event.ctrlKey && event.key === 'f') {
                event.preventDefault();
                const filterInput = document.getElementById('vehicle-filter');
                if (filterInput) filterInput.focus();
            }
        });
    }
    
    /**
     * Public API methods for external access
     * These methods are called from HTML onclick attributes
     */
    
    // Center map on a specific vehicle
    centerOnVehicle(deviceId) {
        this.vehicleTracker.centerOnVehicle(deviceId);
    }
    
    // Show speedometer for a vehicle
    showSpeedometer(deviceId, speed) {
        this.vehicleTracker.speedometer.showForVehicle(deviceId, speed);
    }
    
    // Show an alert for a vehicle
    showAlert(deviceId, type) {
        const vehicle = this.vehicleTracker.vehicleData[deviceId];
        if (vehicle) {
            this.vehicleTracker.uiManager.showAlert(vehicle, type);
        }
    }
    
    // Remove an alert from the UI
    removeAlert(alertDiv, deviceId) {
        this.vehicleTracker.uiManager.removeAlert(alertDiv, deviceId);
    }
    
    // Show a notification message
    showNotification(message, type = 'info') {
        // Create notification element
        const notification = document.createElement('div');
        notification.className = `notification ${type}`;
        notification.innerHTML = `
            <span>${message}</span>
            <button class="close-btn" onclick="this.parentElement.remove()">×</button>
        `;
        
        // Add to notification container
        const container = document.getElementById('notification-container');
        if (container) {
            container.appendChild(notification);
            
            // Auto-remove after a delay
            setTimeout(() => {
                if (notification.parentNode) {
                    notification.classList.add('fade-out');
                    setTimeout(() => notification.remove(), 500);
                }
            }, 5000);
        }
    }
}