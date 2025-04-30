class VehicleTracker {
		    constructor(mapElementId) {
				this.map = L.map(mapElementId, {
				    zoomControl: false  // ← disables the + / – buttons
				}).setView([13.07757, 77.55928], 5);

		        this.markers = {};
		        this.liveDevices = new Set();
		        this.deviceLastUpdate = {};
		        this.previousCourses = {};
		        this.inactivityThreshold = 30000;
		        this.activeAlerts = new Set();
		        this.alertContainer = this.createAlertContainer();

		        L.tileLayer('https://{s}.tile.openstreetmap.org/{z}/{x}/{y}.png', {
		            attribution: '&copy; OpenStreetMap contributors',
		        }).addTo(this.map);

		        this.loadLastKnownPositions();
		        this.initWebSocket();
		        this.pollOfflineDevices();
		        this.checkInactiveDevices();
		    }

			createAlertContainer() {
			   const container = document.createElement('div');
			   container.className = 'alert-container';
			   document.body.appendChild(container);
			   return container;
			}

			showAlert(vehicle, type) {
			   const deviceId = vehicle.deviceId || vehicle.deviceID || 'Unknown';
			   if (this.activeAlerts.has(deviceId)) return;

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
			       }
			   };

			   const alertConfig = config[type];
			   const alertDiv = document.createElement('div');
			   alertDiv.className = alertConfig.class;
			   alertDiv.style.opacity = '0';

			   alertDiv.innerHTML = `
			       <img src="${alertConfig.icon}" class="${type}-icon" alt="${type}">
			       <div class="${type}-info">
			           <strong>${alertConfig.title}</strong>
			           <span>Device ID: ${deviceId}</span>
			           <span>Time: ${new Date().toLocaleTimeString()}</span>
			       </div>
			       <button class="alert-close" onclick="this.parentElement.remove(); window.vehicleTracker.removeAlert('${deviceId}')">×</button>
			   `;

			   void alertDiv.offsetWidth;
			   this.alertContainer.appendChild(alertDiv);
			   alertDiv.style.opacity = '1';
			   
			   this.activeAlerts.add(deviceId);

			   setTimeout(() => {
			       if (alertDiv.parentNode) {
			           alertDiv.classList.add('slide-out');
			           setTimeout(() => {
			               alertDiv.remove();
			               this.removeAlert(deviceId);
			           }, 500);
			       }
			   }, 10000);
			}

			showParkingAlert(vehicle) {
			   this.showAlert(vehicle, 'parking');
			}

			showLiveAlert(vehicle) {
			   this.showAlert(vehicle, 'live'); 
			}

			updateVehicleStatusOnMap(vehicle) {
			    const deviceId = vehicle.deviceId || vehicle.deviceID || 'Unknown';
			    const latitude = parseFloat(vehicle.latitude);
			    const longitude = parseFloat(vehicle.longitude);

			    if (!latitude || !longitude) {
			        console.warn(`Invalid coordinates for Device ID: ${deviceId}`);
			        return;
			    }

			    const status = vehicle.vehicleStatus?.trim().toUpperCase();
			    const ignition = vehicle.ignition?.trim().toUpperCase();

			    console.log(`Processing Vehicle: ${deviceId}`);
			    console.log(`Status: ${status}, Ignition: ${ignition}`);

			    if (status === 'RUNNING' || ignition === 'IGON') {
			        console.log(`Displaying Live Alert for ${deviceId}`);
			        this.showLiveAlert(vehicle);
			    } else if (status === 'PARKED') {
			        console.log(`Displaying Parking Alert for ${deviceId}`);
			        this.showParkingAlert(vehicle);
			    } else {
			        console.log(`No alert triggered for ${deviceId}. Status: ${status}, Ignition: ${ignition}`);
			    }

			    const iconPath = status === 'PARKED'
			        ? '/THINTURE_IMAGE/car/parking-area.png'
			        : (status === 'RUNNING' || ignition === 'IGON') 
			            ? '/THINTURE_IMAGE/car/car_icon4.png'
			            : '/THINTURE_IMAGE/car/car_default.png';

			    console.log(`Updating map icon for ${deviceId}: ${iconPath}`);

			    if (this.markers[deviceId]) {
			        // Update existing marker position and icon
			        this.markers[deviceId]
			            .setLatLng([latitude, longitude])
			            .setIcon(this.createIcon(iconPath));
			    } else {
			        // Create a new marker and add it to the cluster group
			        const newMarker = L.marker([latitude, longitude], {
			            icon: this.createIcon(iconPath)
			        }).bindPopup(`
			            <strong>Device ID:</strong> ${deviceId}<br>
			            <strong>Status:</strong> ${status}
			        `);

			        this.markers[deviceId] = newMarker;
			        this.markerClusterGroup.addLayer(newMarker); // Add marker to cluster group
			    }
			

			this.markerClusterGroup = L.markerClusterGroup({
			    iconCreateFunction: function(cluster) {
			        const count = cluster.getChildCount(); // Number of markers in cluster
			        let className = 'marker-cluster-small';

			        if (count > 10) {
			            className = 'marker-cluster-medium';
			        } else if (count > 50) {
			            className = 'marker-cluster-large';
			        }

			        return new L.DivIcon({
			            html: `<div><span>${count}</span></div>`,
			            className,
			            iconSize: [40, 40]
			        });
			    }
			});

			
			}
			
			
			
			
			
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
				const timeIntervals =vehicle.timeIntervals

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
		                    console.log('Icon load failed for ${deviceId}:', this.src);
		                    if (!this.retryCount) {
		                        this.retryCount = 1;
		                        this.src = '/THINTURE_IMAGE/car/${icon.color}/car0.png';
		                        console.log('Retrying with path:', this.src);
		                    } else {
		                        console.error('All icon load attempts failed for ${deviceId}');
		                        this.style.display = 'none';
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
					                <button style="padding: 5px 10px; background-color: #4CAF50; color: white; border: none; border-radius: 5px; cursor: pointer;" 
					                        onclick="navigateToReplay('${encodeURIComponent(vehicle.deviceId)}')">
					                    Playback
					                </button>
					                <button style="padding: 5px 10px; background-color: red; color: white; border: none; border-radius: 5px; cursor: pointer;" 
					                        onclick="openSideTab()">Live</button>
											
											
											<button style="padding: 5px 10px; background-color: #2196F3; color: white; border: none; border-radius: 5px; cursor: pointer;"
											    onclick="fetchVehicleDetails(this.getAttribute('data-deviceid'))"
											    data-deviceid="${deviceId}">
											    Details
											</button>


					            </div>
					        </div>
					    </div>
					`;


		        if (this.markers[deviceId]) {
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
		            const marker = L.marker([latitude, longitude], {
		                icon: L.divIcon({
		                    className: 'custom-icon',
		                    html: markerHtml,
		                    iconSize: [38, 38],
		                    iconAnchor: [19, 19],
		                    popupAnchor: [0, -19],
		                }),
		            }).bindPopup(popupContent).addTo(this.map);

		            this.markers[deviceId] = marker;
		        }

		        // Check for parking alert condition
		        if (isLive && ignition === 'IGoff' && (vehicleStatus || '').toUpperCase() === 'PARKED') {
		            this.showParkingAlert(vehicle);
		        }

		        this.deviceLastUpdate[deviceId] = Date.now();
		        if (isLive) {
		            this.liveDevices.add(deviceId);
		        }
		    }

		    getVehicleIcon(course, ignition, vehicleStatus) {
		        const basePath = '/THINTURE_IMAGE/car';
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
			
			
			
			
			
			
			

		    loadLastKnownPositions() {
		        console.log('Loading last known positions...');
		        this.fetchAndUpdateVehicleData('/api/vehicles/last-known', false);
		    }
		    
			fetchAndUpdateVehicleData(url, isLive = false) {
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
			                data.forEach(vehicle => this.createOrUpdateMarker(vehicle, isLive));
			            } else if (typeof data === 'object' && data !== null) {
			                this.createOrUpdateMarker(data, isLive);
			            } else {
			                console.warn('No data received for vehicles.');
			            }
			        })
			        .catch(error => console.error('Error fetching vehicle data:', error));
			}


			
			
			
			
		    pollOfflineDevices() {
		        setInterval(() => {
		            console.log('Fetching offline vehicles...');
		            this.fetchAndUpdateVehicleData('/api/vehicles/last-known', false);
		        }, 30000);
		    }

		    checkInactiveDevices() {
		        setInterval(() => {
		            const now = Date.now();
		            this.liveDevices.forEach(deviceId => {
		                if (now - this.deviceLastUpdate[deviceId] > this.inactivityThreshold) {
		                    console.log('Device became inactive:', deviceId);
		                    this.liveDevices.delete(deviceId);
		                    this.fetchAndUpdateVehicleData(`/api/vehicle/latest-location/${deviceId}`, false);
		                }
		            });
		        }, this.inactivityThreshold);
		    }
			
			

		    initWebSocket() {
		        const socket = new SockJS('/gs-guide-websocket');
		        const stompClient = Stomp.over(socket);

		        stompClient.connect({}, () => {
		            console.log('WebSocket connected.');
		            stompClient.subscribe('/topic/location-updates', (message) => {
		                try {
		                    const vehicleData = JSON.parse(message.body);
		                    const standardizedData = {
		                        ...vehicleData,
		                        vehicleStatus: vehicleData.vehicleStatus || vehicleData.VehicleStatus
		                    };
		                    this.createOrUpdateMarker(standardizedData, true);
		                } catch (error) {
		                    console.error('Error processing WebSocket message:', error);
		                    console.error('Message body:', message.body);
		                }
		            });
		        }, (error) => {
		            console.error('WebSocket connection error:', error);
		            setTimeout(() => this.initWebSocket(), 5000);
		        });
		    }
		}

        // Initialize the VehicleTracker when the page is loaded
        document.addEventListener('DOMContentLoaded', () => {
            new VehicleTracker('map');
        });
		
		
		
		
		
  
		document.addEventListener("DOMContentLoaded", function () {
		        const tableBody = document.getElementById("trackerTableBody");
		        const toggleButton = document.getElementById("toggleButton");
		        const sidebar = document.getElementById("sidebar");
		        const searchBar = document.getElementById("searchBar");
		        let activeRow = null;

		        // Function to format timestamp
		        function formatTimestamp(timestamp) {
		            if (!timestamp) return "N/A";
		            const date = new Date(timestamp);
		            const now = new Date();
		            const diffMinutes = Math.floor((now - date) / (1000 * 60));

		            if (diffMinutes < 1) return "Just now";
		            if (diffMinutes < 60) return `${diffMinutes} min ago`;
		            if (diffMinutes < 1440) return `${Math.floor(diffMinutes / 60)} hours ago`;
		            return date.toLocaleString();
		        }

		        // Function to fetch and populate the tracker table
		        function fetchAndPopulateTrackerTable() {
		            fetch("/api/vehicle/last/latests-locations/all")
		                .then(response => {
		                    if (!response.ok) {
		                        throw new Error("Failed to fetch vehicle locations");
		                    }
		                    return response.json();
		                })
		                .then(data => {
		                    if (Array.isArray(data) && data.length > 0) {
		                        populateTable(data);
		                        startAutoRefresh();
		                    } else {
		                        showError("No vehicles found");
		                    }
		                })
		                .catch(error => {
		                    console.error("Error fetching vehicle locations:", error);
		                    showError("Failed to load vehicle data");
		                });
		        }

	        // Function to fetch and populate the tracker table
	        function fetchAndPopulateTrackerTable() {
	            fetch("/api/vehicle/last/latests-locations/all")
	                .then(response => {
	                    if (!response.ok) {
	                        throw new Error("Failed to fetch vehicle locations");
	                    }
	                    return response.json();
	                })
	                .then(data => {
	                    if (Array.isArray(data) && data.length > 0) {
	                        populateTable(data);
	                        startAutoRefresh();
	                    } else {
	                        showError("No vehicles found");
	                    }
	                })
	                .catch(error => {
	                    console.error("Error fetching vehicle locations:", error);
	                    showError("Failed to load vehicle data");
	                });
	        }

	        // Function to show error message
	        function showError(message) {
	            tableBody.innerHTML = `
	                <tr>
	                    <td colspan="4" class="error">
	                        <div class="error-message">
	                            <span>${message}</span>
	                           <button class="trybutton" onclick="fetchAndPopulateTrackerTable()">Retry</button>
	                        </div>
	                    </td>
	                </tr>
	            `;
	        }
			
			// Helper function to determine status class, text, and trigger notifications
			function getStatusInfo(vehicle) {
			    const ignition = vehicle.ignition?.toLowerCase();
			    const status = vehicle.vehicleStatus?.toUpperCase();
			    const deviceId = vehicle.deviceId || "Unknown"; // Extract deviceId from vehicle

			    let statusInfo;

			    if (ignition === "igon") {
			        statusInfo = { class: "status-on", text: "Running", icon: "🚗" };
			        showNotification("Vehicle Status", "The vehicle is Running", "🚗", deviceId);
			    } else if (status === "PARKED") {
			        statusInfo = { class: "status-parked", text: "Parked", icon: "🅿️" };
			        showNotification("Vehicle Status", "The vehicle is Parked", "🅿️", deviceId);
					
			    } else if (ignition === "igoff") {
			        statusInfo = { class: "status-off", text: "Stopped", icon: "⭕" };
			    } else {
			        statusInfo = { class: "status-unknown", text: "Unknown", icon: "❓" };
			    }

			    return statusInfo;
			}


			// Function to show notifications
			

			   

			// Function to populate the table with data and trigger notifications
			function populateTable(data) {
			    tableBody.innerHTML = "";
			    data.sort((a, b) => new Date(b.timestamp) - new Date(a.timestamp)).forEach(vehicle => {
			        const statusInfo = getStatusInfo(vehicle);
			        const row = document.createElement("tr");

			        row.innerHTML = `
			            <td class="${statusInfo.class}">
			                <span class="status-icon">${statusInfo.icon}</span>
			                ${statusInfo.text}
			            </td>
			            <td>${vehicle.deviceId || "N/A"}</td>
			            <td data-timestamp="${vehicle.timestamp}">${formatTimestamp(vehicle.timestamp)}</td>
			            <td class="replay-cell">
			                <button class="replay-button" title="View Route History" onclick="openReplayPage('${vehicle.deviceId}', '${vehicle.startTime}', '${vehicle.endTime}')">
			                    Replay
			                </button>
			            </td>
			        `;

			        // Row click event: Highlight table row and zoom to marker
			        row.addEventListener("click", () => {
			            if (activeRow) activeRow.classList.remove("active-row"); // Remove highlight from previous row
			            row.classList.add("active-row");
			            activeRow = row;

			            zoomToVehicle(vehicle.deviceId, vehicle.latitude, vehicle.longitude);
			        });

			        row.style.cursor = "pointer";
			        tableBody.appendChild(row);
			    });
			}

			// Function to zoom in on the correct vehicle marker and highlight it
			function zoomToVehicle(deviceId, latitude, longitude) {
			    if (!latitude || !longitude) {
			        console.warn(`Invalid coordinates for Device ID: ${deviceId}`);
			        return;
			    }
				function highlightMarker(deviceId) {
				        if (window.vehicleTracker && window.vehicleTracker.markers[deviceId]) {
				            const marker = window.vehicleTracker.markers[deviceId];
				            marker.openPopup();
				            if (marker._icon) {
				                marker._icon.classList.add('highlight-marker');
				                setTimeout(() => marker._icon.classList.remove('highlight-marker'), 1000);
				            }
				        }
				    }

			    const vehicleTracker = window.vehicleTracker;
			    if (!vehicleTracker || !vehicleTracker.map || !vehicleTracker.markers) {
			        console.error("Vehicle tracker or map object not initialized!");
			        return;
			    }

			    // Reset all markers to default icon
			    Object.keys(vehicleTracker.markers).forEach(id => {
			        const prevMarker = vehicleTracker.markers[id];
			        if (prevMarker) {
			            prevMarker.setIcon(L.divIcon({
			                className: 'custom-icon',
			                html: `<img src="/THINTURE_IMAGE/car/car_default.png" style="width: 35px; height: 35px;">`,
			                iconSize: [35, 35],
			                iconAnchor: [17, 17]
			            }));
			        }
			    });

			    // Highlight and zoom in on the selected marker
			    const marker = vehicleTracker.markers[deviceId];
			    if (marker) {
			        marker.setIcon(L.divIcon({
			            className: 'custom-icon',
			            html: `<img src="/THINTURE_IMAGE/car/car_icon1.png" style="width: 50px; height: 50px;">`,
			            iconSize: [50, 50],
			            iconAnchor: [25, 25]
			        }));

			        marker.openPopup(); // Show the popup
			        vehicleTracker.map.setView([latitude, longitude], 18, { animate: true }); // Smooth zoom-in
			    } else {
			        console.warn(`Marker not found for deviceId: ${deviceId}`);
			    }
			}
			

			function showNotification(title, message, icon, deviceId) {
			    // Create notification container
			    const notification = document.createElement("div");
			    notification.classList.add("notifications");

			    // Add notification content
			    notification.innerHTML = `
			        <div class="notification-icon">${icon}</div>
			        <div class="notification-content">
			            <h4>${title}</h4>
			            <p>${message}</p>
			            <small>Device ID: ${deviceId || "Unknown"}</small>
			        </div>
			    `;

			    // Append to the body
			    document.body.appendChild(notification);

			    // Animate notification twice
			    setTimeout(() => {
			        notification.classList.add("animate");
			    }, 100); // First animation starts
			    setTimeout(() => {
			        notification.classList.remove("animate");
			        setTimeout(() => {
			            notification.classList.add("animate");
			        }, 100); // Second animation starts
			    }, 2000); // Delay before repeating the animation

			    // Automatically remove notification after both animations
			    const autoRemoveTimeout = setTimeout(() => {
			        notification.remove();
			    }, 4000);

			    // Hide notification on click
			    notification.addEventListener("click", () => {
			        clearTimeout(autoRemoveTimeout); // Cancel automatic removal
			        notification.remove();
			    });
			}



			// Call the fetch function to populate data and trigger notifications
			document.addEventListener("DOMContentLoaded", fetchAndPopulateTrackerTable);


	        // Toggle button for the sidebar
	        if (toggleButton) {
	            toggleButton.addEventListener("click", e => {
	                e.preventDefault();
	                e.stopPropagation();
	                sidebar.classList.toggle("active");
	                toggleButton.classList.toggle("active");
	            });
	        }

	        // Close sidebar when clicking outside
	        document.addEventListener("click", function (event) {
	            if (!sidebar.contains(event.target) &&
	                !toggleButton.contains(event.target) &&
	                sidebar.classList.contains("active")) {
	                sidebar.classList.remove("active");
	                toggleButton.classList.remove("active");
	            }
	        });

	        // Add search functionality
	        if (searchBar) {
	            searchBar.addEventListener("input", function () {
	                const searchText = this.value.trim().toLowerCase();
	                const rows = tableBody.querySelectorAll("tr");

	                rows.forEach(row => {
	                    const deviceId = row.children[1].textContent.toLowerCase();
	                    const status = row.children[0].textContent.toLowerCase();

	                    if (deviceId.includes(searchText) || status.includes(searchText)) {
	                        row.style.display = "";
	                    } else {
	                        row.style.display = "none";
	                    }
	                });
	            });
	        }

	        // Start auto-refresh
	        function startAutoRefresh() {
	            setInterval(() => {
	                fetchAndPopulateTrackerTable();
	            }, 70000);
	        }

	        // Initialize
	        fetchAndPopulateTrackerTable();
	    });
	
		
		
		
		
	function navigateToReplay(deviceId) {
	    console.log("Navigating with Device ID:", deviceId);
	    if (!deviceId) {
	        alert("Device ID is required to navigate to the replay page.");
	        return;
	    }
	    window.location.href = `/superadmin/playback?deviceId=${encodeURIComponent(deviceId)}`;
	}

	
	
	const vehicle1 = { ignition: "igon", vehicleStatus: "running" };
	const vehicle2 = { ignition: "igoff", vehicleStatus: "PARKED" };

	getStatusInfo(vehicle1); // Notification: Running
	getStatusInfo(vehicle2); // Notification: Parked
	


		document.addEventListener("DOMContentLoaded", function () {
	    fetch("/api/details")
	        .then(response => response.json())
	        .then(data => {
	            console.log("✅ Received Data:", data);

	            if (!data || data.length === 0) {
	                console.error("❌ No vehicles found");
	                document.getElementById("vehiclesTable").innerHTML = "<tr><td colspan='8'>No Data Available</td></tr>";
	                return;
	            }

	            let tableContent = "";
	            data.forEach(vehicle => {
	                tableContent += `
	                    <tr>
	                        <td>${vehicle.deviceID || "N/A"}</td>
	                        <td>${vehicle.vehicleNumber || "N/A"}</td>
	                        <td>${vehicle.imei || "N/A"}</td>
	                        <td>${vehicle.engineNumber || "N/A"}</td>
	                        <td>${vehicle.model || "N/A"}</td>
	                        <td>${vehicle.ownerName || "N/A"}</td>
	                        <td>${vehicle.vehicleType || "N/A"}</td>
	                        <td>
	                            <button onclick="fetchVehicleDetails('${vehicle.deviceID}')">Details</button>
	                        </td>
	                    </tr>`;
	            });

	            document.getElementById("vehiclesTable").innerHTML = tableContent;
	        })
	        .catch(error => console.error("🚨 Error fetching vehicle list:", error));
	});

	// ✅ Function to fetch vehicle details and show in popup
	// ✅ Function to fetch vehicle details and show in popup
	function fetchVehicleDetails(deviceID) {
	    if (!deviceID) {
	        console.error("❌ Device ID is missing!");
	        alert("❌ No Device ID found!");
	        return;
	    }

	    console.log("🚀 Fetching details for Device ID:", deviceID);

	    fetch(`/api/details/${deviceID}`)
	        .then(response => {
	            console.log("📡 Response Status:", response.status);
	            if (!response.ok) {
	                throw new Error("❌ Server returned error " + response.status);
	            }
	            return response.json();
	        })
	        .then(vehicle => {
	            console.log("📡 Vehicle Details Received:", vehicle);

	            if (!vehicle || Object.keys(vehicle).length === 0) {
	                alert("❌ No data found for this vehicle!");
	                return;
	            }

	            // ✅ Populate Table Data Inside Popup
	            let tableContent = `
	                <tr><td><strong>Device ID:</strong></td><td>${vehicle.deviceID || "N/A"}</td></tr>
	                <tr><td><strong>Vehicle Number:</strong></td><td>${vehicle.vehicleNumber || "N/A"}</td></tr>
	                <tr><td><strong>IMEI:</strong></td><td>${vehicle.imei || "N/A"}</td></tr>
	                <tr><td><strong>Engine Number:</strong></td><td>${vehicle.engineNumber || "N/A"}</td></tr>
	                <tr><td><strong>Model:</strong></td><td>${vehicle.model || "N/A"}</td></tr>
	                <tr><td><strong>Owner Name:</strong></td><td>${vehicle.ownerName || "N/A"}</td></tr>
	                <tr><td><strong>Vehicle Type:</strong></td><td>${vehicle.vehicleType || "N/A"}</td></tr>
	            `;

	            document.getElementById("vehicleDetailsTable").innerHTML = tableContent;

	            // ✅ Show Modal
	            document.getElementById("vehicleDetailsModal").style.display = "block";
	            console.log("✅ Table Populated & Modal Opened!");
	        })
	        .catch(error => {
	            console.error("🚨 Error fetching vehicle details:", error);
	            alert("⚠️ Error fetching data. Check console for details.");
	        });
	}

	// ✅ Function to Close Popup
	function closeVehicleDetails() {
	  const modal = document.getElementById("vehicleDetailsModal");
	  if (modal) {
	    modal.style.display = "none";
	  }
	}


	
	function showPopup() {
	    document.getElementById("detailsPopup").style.display = "block";
	    document.getElementById("popupOverlay").style.display = "block";
	}

	function closePopup() {
	    document.getElementById("detailsPopup").style.display = "none";
	    document.getElementById("popupOverlay").style.display = "none";
	}

	// Modify this in fetchVehicleDetails function
	document.getElementById("vehicleDetailsModal").style.display = "block";



