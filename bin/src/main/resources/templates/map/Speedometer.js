/**
 * Speedometer - Handles the speedometer visualization
 */
class Speedometer {
    constructor() {
        this.canvas = document.getElementById("speedometer");
        this.ctx = null;
        this.centerX = 0;
        this.centerY = 0;
        this.radius = 200;
        this.maxSpeed = 240;
        this.currentVehicleId = null;
        
        this.initialize();
    }
    
    /**
     * Initialize the speedometer
     */
    initialize() {
        // Initialize canvas and context
        if (!this.canvas) return;
        
        this.ctx = this.canvas.getContext("2d");
        this.centerX = this.canvas.width / 2;
        this.centerY = this.canvas.height;
        
        // Draw initial speedometer
        this.draw(0);
        
        // Set up toggle control
        this.setupToggle();
    }
    
    /**
     * Set up the speedometer toggle button
     */
    setupToggle() {
        const toggleIcon = document.getElementById("toggleSpeedometer");
        const container = document.getElementById("speedometer-container");
        
        if (toggleIcon && container) {
            toggleIcon.addEventListener("click", () => {
                const isVisible = container.classList.contains("visible");
                if (isVisible) {
                    // Hide speedometer
                    container.classList.remove("visible");
                    // Reset tracking
                    this.currentVehicleId = null;
                } else {
                    // Show speedometer with 0 speed initially
                    container.classList.add("visible");
                    this.draw(0);
                }
            });
        }
    }
    
    /**
     * Draw the speedometer with the given speed
     */
    draw(speed) {
        if (!this.ctx) return;
        
        // Clear canvas
        this.ctx.clearRect(0, 0, this.canvas.width, this.canvas.height);

        // Draw speedometer arc
        const grad = this.ctx.createLinearGradient(0, 0, this.canvas.width, 0);
        grad.addColorStop(0, "#4caf50");
        grad.addColorStop(0.5, "#ffeb3b");
        grad.addColorStop(1, "#f44336");

        this.ctx.lineWidth = 14;
        this.ctx.strokeStyle = grad;
        this.ctx.beginPath();
        this.ctx.arc(this.centerX, this.centerY, this.radius, Math.PI, 2 * Math.PI);
        this.ctx.stroke();

        // Draw speed markings
        this.ctx.font = "bold 18px sans-serif";
        this.ctx.textAlign = "center";
        this.ctx.textBaseline = "middle";

        for (let i = 0; i <= this.maxSpeed; i += 20) {
            const angle = Math.PI + (i / this.maxSpeed) * Math.PI;
            const outerX = this.centerX + this.radius * Math.cos(angle);
            const outerY = this.centerY + this.radius * Math.sin(angle);
            const innerX = this.centerX + (this.radius - 10) * Math.cos(angle);
            const innerY = this.centerY + (this.radius - 10) * Math.sin(angle);

            this.ctx.strokeStyle = "#888";
            this.ctx.lineWidth = 1.5;
            this.ctx.beginPath();
            this.ctx.moveTo(innerX, innerY);
            this.ctx.lineTo(outerX, outerY);
            this.ctx.stroke();

            const textX = this.centerX + (this.radius - 25) * Math.cos(angle);
            const textY = this.centerY + (this.radius - 25) * Math.sin(angle);
            this.ctx.fillStyle = "black";
            this.ctx.fillText(i.toString(), textX, textY);
        }

        // Draw needle
        const needleAngle = Math.PI + (speed / this.maxSpeed) * Math.PI;
        const needleLength = this.radius - 40;
        const needleX = this.centerX + needleLength * Math.cos(needleAngle);
        const needleY = this.centerY + needleLength * Math.sin(needleAngle);

        this.ctx.strokeStyle = "#e53935";
        this.ctx.lineWidth = 3;
        this.ctx.beginPath();
        this.ctx.moveTo(this.centerX, this.centerY);
        this.ctx.lineTo(needleX, needleY);
        this.ctx.stroke();

        // Draw needle center
        this.ctx.beginPath();
        this.ctx.arc(this.centerX, this.centerY, 6, 0, 2 * Math.PI);
        this.ctx.fillStyle = "#ddd";
        this.ctx.fill();

        // Update speed display
        document.getElementById("speedDisplay").innerHTML = `<b>Speed: ${Math.round(speed)} km/h</b>`;
    }
    
    /**
     * Show the speedometer for a specific vehicle
     */
    showForVehicle(deviceId, speed) {
        // Show the speedometer container
        const container = document.getElementById("speedometer-container");
        if (container) {
            container.classList.add("visible");
        }
        
        // Update speedometer with the vehicle's speed
        this.draw(parseFloat(speed) || 0);
        
        // Store the current vehicle ID
        this.currentVehicleId = deviceId;
        
        console.log(`Showing speedometer for vehicle ${deviceId} with speed ${speed}`);
    }
    
    /**
     * Update the speedometer with a new speed value
     */
    update(speed) {
        this.draw(parseFloat(speed) || 0);
    }
}