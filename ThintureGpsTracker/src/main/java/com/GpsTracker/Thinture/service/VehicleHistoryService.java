package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.dto.DeviceLocation;
import com.GpsTracker.Thinture.dto.PanicAlertDTO;
import com.GpsTracker.Thinture.model.GpsData;
//Import SLF4J Logger
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.GpsTracker.Thinture.model.VehicleHistory;
import com.GpsTracker.Thinture.model.Vehicle;
import com.GpsTracker.Thinture.repository.VehicleHistoryRepository;
import com.GpsTracker.Thinture.repository.VehicleRepository;

import jakarta.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;




/** ───────────✦✧✦───────────
 * Author: 𝙅𝙤𝙩𝙝𝙞𝙚𝙨𝙝 ❖❖❖❖❖❖
 * Senior Developer, R&D 
 *
 * ───────────✦✧✦───────────
 */
@Service
public class VehicleHistoryService {

	@Autowired
	private VehicleHistoryRepository vehicleHistoryRepository;

	private static final Logger logger = LoggerFactory.getLogger(VehicleHistoryService.class);
	private static final double EARTH_RADIUS = 6371; // Radius of the Earth in kilometers
	// ANSI color codes
	private static final String ANSI_RESET = "\u001B[0m";
	private static final String ANSI_GREEN = "\u001B[32m";
	private static final String ANSI_RED = "\u001B[31m";
	private static final String ANSI_BLUE = "\u001B[34m";
	private static final String ANSI_YELLOW = "\u001B[33m";
	private static final String ANSI_CYAN = "\u001B[36m";
	// Method to save vehicle history
	/**
	 * Save vehicle history with colored log outputs for clarity.
	 */
	public void saveVehicleHistory(VehicleHistory history) {
	    // Attempting message in Blue
	    logger.info(ANSI_CYAN + "Attempting to save vehicle history: {}" + ANSI_RESET, history);
	    try {
	        vehicleHistoryRepository.save(history);

	        // Success message in Green
	        logger.info(ANSI_RED + "Vehicle history saved successfully for deviceID: {}" + ANSI_RESET,
	                history.getVehicle().getDeviceID());
	    } catch (Exception e) {
	        // Error message in Red
	        logger.error(ANSI_BLUE + "Error saving vehicle history for deviceID: {} - {}" + ANSI_RESET,
	                history.getVehicle().getDeviceID(), e.getMessage(), e);
	    }
	}

	// Method to fetch all latitude and longitude points for a device within a date
	// range
	public List<DeviceLocation> getLatLngInRange(String deviceID, Timestamp startDate, Timestamp endDate) {
	    logger.info("[]70  Fetching lat/lng points for deviceID: {} from {} to {}", deviceID, startDate, endDate);
	    try {
	        List<VehicleHistory> history = vehicleHistoryRepository.findHistoryByDeviceIDAndDateRangeNative(deviceID,
	                startDate, endDate);

	        if (history.isEmpty()) {
	            logger.warn("No history data found for deviceID: {} in the specified date range.", deviceID);
	            return Collections.emptyList();
	        }

	        List<DeviceLocation> latLngList = history.stream().map(h -> new DeviceLocation(
	                h.getVehicle().getDeviceID(),
	                h.getLatitude(),
	                h.getLongitude(),
	                h.getTimestamp().toString(),
	                h.getIgnition(),
	                h.getVehicleStatus(),
	                h.getSpeed(),
	                h.getAdditionalData(),
	                h.getSequenceNumber()
	        )).collect(Collectors.toList());

	        logger.info("Returning {} lat/lng points for deviceID: {}", latLngList.size(), deviceID);
	        return latLngList;
	    } catch (Exception e) {
	        logger.error("Error fetching latitude/longitude data for deviceID: {} - {}", deviceID, e.getMessage(), e);
	        return Collections.emptyList();
	    }
	}


	
	//THIS IS CODE
	@Transactional
	public List<VehicleHistory> getVehicleHistory(String deviceID, Timestamp startDate, Timestamp endDate) throws Exception {
	    logger.info("[105]Fetching vehicle history for deviceID: {} from {} to {}", deviceID, startDate, endDate);
	    try {
	        // Fetch data using the repository's native query
	        List<VehicleHistory> history = vehicleHistoryRepository.findHistoryByDeviceIDAndDateRangeNative(
	            deviceID, startDate, endDate
	        );
	        logger.info("[111]Fetching vehicle history for deviceID: {} from {} to {}", deviceID, startDate, endDate);
	        if (history == null || history.isEmpty()) {
	            logger.warn("No vehicle history found for deviceID: {} in the specified date range.", deviceID);
	            return Collections.emptyList();
	        }

	        logger.info("Fetched {} records for deviceID: {}", history.size(), deviceID);
	        return history;
	    } catch (Exception e) {
	        // Log detailed error for debugging
	        logger.error("Error fetching vehicle history for deviceID: {} - {}", deviceID, e.getMessage(), e);
	        throw new Exception("Error fetching vehicle history", e);
	    }
	}

	

	

	@Transactional
	public List<VehicleHistory> findHistoryByDeviceIDAndDateRange(String deviceID, Timestamp startDate, Timestamp endDate) {
	    logger.info("[LINE 183] Fetching vehicle history for deviceID: {}, StartDate: {}, EndDate: {}", 
	        deviceID, startDate, endDate);

	    // Fetch history from database
	    List<VehicleHistory> history = vehicleHistoryRepository.findHistoryByDeviceIDAndDateRangeNative(
	        deviceID, startDate, endDate
	    );

	    if (history.isEmpty()) {
	        logger.warn("[LINE 187] No vehicle history found for deviceID: {}, between {} and {}", 
	            deviceID, startDate, endDate);
	    } else {
	        logger.info("[LINE 190] ✅ Fetched {} records for deviceID: {}, between {} and {}", 
	            history.size(), deviceID, startDate, endDate);

	        // Log each record's details
	        for (VehicleHistory record : history) {
	            logger.info("📝 Record - ID: {}, Timestamp: {}, Latitude: {}, Longitude: {}, Speed: {}, Status: {}, Ignition: {}",
	                record.getId(), record.getTimestamp(), record.getLatitude(), record.getLongitude(), 
	                record.getSpeed(), record.getVehicleStatus(), record.getIgnition());
	        }
	    }

	    return history;
	}

	public VehicleHistory findLatestByDeviceID(String deviceID) {
	    logger.info("[LINE 196] Fetching latest vehicle history for deviceID: {}", deviceID);

	    // Fetch latest vehicle history
	    VehicleHistory latestHistory = vehicleHistoryRepository.findTopByVehicle_DeviceIDOrderByTimestampDesc(deviceID);

	    if (latestHistory == null) {
	        logger.warn("[LINE 200] No latest vehicle history found for deviceID: {}", deviceID);
	    } else {
	        logger.info("[LINE 202] ✅ Fetched latest vehicle history for deviceID: {}", deviceID);
	        logger.info("📝 Latest Record - ID: {}, Timestamp: {}, Latitude: {}, Longitude: {}, Speed: {}, Status: {}, Ignition: {}",
	            latestHistory.getId(), latestHistory.getTimestamp(), latestHistory.getLatitude(), 
	            latestHistory.getLongitude(), latestHistory.getSpeed(), latestHistory.getVehicleStatus(), 
	            latestHistory.getIgnition());
	    }

	    return latestHistory;
	}


	/**
	 * Fetch vehicle history by device ID, month, and year.
	 * 
	 * @param deviceID The device ID of the vehicle.
	 * @param month    The month for filtering the history.
	 * @param year     The year for filtering the history.
	 * @return List of VehicleHistory records that match the criteria.
	 */
	

	// new code 26/09/2024
	// Fetch vehicle history logs for a given device within a date range
	public List<VehicleHistory> getHistoryInRange(String deviceID, Timestamp startDate, Timestamp endDate) {
	    logger.info("🔍 [206] Fetching vehicle history for deviceID: {} from {} to {}", deviceID, startDate, endDate);

	    try {
	        // Fetch data from the database
	        List<VehicleHistory> historyLogs = vehicleHistoryRepository.findHistoryByDeviceIDAndDateRangeNative(deviceID, startDate, endDate);

	        if (historyLogs.isEmpty()) {
	            logger.warn("⚠️ No history data found for deviceID: {} in the specified date range.", deviceID);
	            return Collections.emptyList();
	        }

	        // Log total records fetched
	        logger.info("✅ Fetched {} records for deviceID: {} between {} and {}", 
	            historyLogs.size(), deviceID, startDate, endDate);

	        // Log each record's details
	        for (VehicleHistory record : historyLogs) {
	            logger.info("📝 Record - ID: {}, Timestamp: {}, Latitude: {}, Longitude: {}, Speed: {}, Status: {}, Ignition: {}, Additional Data: {}",
	                record.getId(), record.getTimestamp(), record.getLatitude(), record.getLongitude(), 
	                record.getSpeed(), record.getVehicleStatus(), record.getIgnition(), record.getAdditionalData());
	        }

	        return historyLogs;

	    } catch (Exception e) {
	        // Log error details
	        logger.error("❌ Error fetching history data for deviceID: {} - {}", deviceID, e.getMessage(), e);
	        return Collections.emptyList();
	    }
	}

	
	
	
	
	
	
	// Optional method for fetching history for multiple vehicles
		public List<VehicleHistory> getMultipleVehiclesHistory(List<String> deviceIDs, Timestamp startDate,
				Timestamp endDate) {
			logger.info("Fetching history for multiple vehicles: {}", deviceIDs);
			try {
				List<VehicleHistory> history = vehicleHistoryRepository
						.findHistoryByMultipleDeviceIDsAndDateRange(deviceIDs, startDate, endDate);
				if (history.isEmpty()) {
					logger.warn("No history found for the specified vehicles: {} in the date range.", deviceIDs);
				} else {
					logger.info("Fetched {} records for specified vehicles.", history.size());
				}
				return history;
			} catch (Exception e) {
				logger.error("Error fetching history for multiple vehicles - {}", e.getMessage(), e);
				return Collections.emptyList();
			}
		}

		// Haversine formula to calculate the distance between two latitude/longitude
		// points
		private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
			double latDistance = Math.toRadians(lat2 - lat1);
			double lonDistance = Math.toRadians(lon2 - lon1);

			double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
					* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);

			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

			return EARTH_RADIUS * c; // Distance in kilometers
		}

		

		@Transactional
		public List<VehicleHistory> findByDeviceIDMonthYear(String deviceID, int month, int year) {
			logger.info("Attempting to fetch vehicle history for deviceID: {}, month: {}, year: {}", deviceID, month, year);

			try {
				// Calculate start and end date for the given month and year
				YearMonth yearMonth = YearMonth.of(year, month);
				Timestamp startDate = Timestamp.valueOf(LocalDateTime.of(year, month, 1, 0, 0));
				Timestamp endDate = Timestamp.valueOf(LocalDateTime.of(year, month, yearMonth.lengthOfMonth(), 23, 59, 59));

				logger.debug("Calculated startDate: {}, endDate: {}", startDate, endDate);

				List<VehicleHistory> vehicleHistories = vehicleHistoryRepository.findByDeviceIDMonthYear(deviceID, month,
						year);

				if (vehicleHistories.isEmpty()) {
					logger.info("No vehicle history found for deviceID: {}, month: {}, year: {}", deviceID, month, year);
				} else {
					logger.info("Found {} records for deviceID: {}, month: {}, year: {}", vehicleHistories.size(), deviceID,
							month, year);
				}

				return vehicleHistories;
			} catch (Exception e) {
				logger.error(
						"An error occurred while fetching vehicle history for deviceID: {}, month: {}, year: {}. Error: {}",
						deviceID, month, year, e.getMessage(), e);
				throw e; // Rethrow exception for higher-level handling
			}
		}

	// new code 17/10/2024
	// Fetch panic alerts for a specific device
	public List<PanicAlertDTO> getPanicAlertsForDevice(String deviceID) {
		List<VehicleHistory> panicAlerts = vehicleHistoryRepository.findPanicAlertsByDeviceID(deviceID);
		if (panicAlerts.isEmpty()) {
			logger.info("No panic alerts found for deviceID: {}", deviceID);
		} else {
			logger.info("{} panic alert(s) found for deviceID: {}", panicAlerts.size(), deviceID);
		}

		// Convert entities to DTOs
		return panicAlerts.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	// Fetch all panic alerts
	public List<PanicAlertDTO> getAllPanicAlerts() {
		logger.info("Fetching all panic alerts");
		List<VehicleHistory> panicAlerts = vehicleHistoryRepository.findAllPanicAlerts();

		// Convert entities to DTOs
		return panicAlerts.stream().map(this::convertToDTO).collect(Collectors.toList());
	}

	// Helper method to convert VehicleHistory to PanicAlertDTO
	private PanicAlertDTO convertToDTO(VehicleHistory vehicleHistory) {
		PanicAlertDTO dto = new PanicAlertDTO();
		dto.setId(vehicleHistory.getId());
		dto.setVehicleId(vehicleHistory.getVehicle().getDeviceID()); // Assuming this field is properly loaded
		dto.setLatitude(vehicleHistory.getLatitude());
		dto.setLongitude(vehicleHistory.getLongitude());
		dto.setTimestamp(vehicleHistory.getTimestamp());
		dto.setPanic(vehicleHistory.getPanic());
		return dto;
	}

	// Other methods..
	/*
	 * // Fetch vehicle history with optional filters public List<VehicleHistory>
	 * getFilteredVehicleHistory(String vehicleId, Integer month, Integer year) {
	 * logger.
	 * info("Fetching filtered vehicle history for vehicleId: {}, month: {}, year: {}"
	 * , vehicleId, month, year);
	 * 
	 * try { // Check which filters are applied if (vehicleId != null && month !=
	 * null && year != null) { return
	 * vehicleHistoryRepository.findByVehicleIdAndMonthAndYear(vehicleId, month,
	 * year); } else if (vehicleId != null && year != null) { return
	 * vehicleHistoryRepository.findByVehicleIdAndYear(vehicleId, year); } else if
	 * (vehicleId != null) { return
	 * vehicleHistoryRepository.findByVehicleId(vehicleId); } else { return
	 * vehicleHistoryRepository.findAll(); } } catch (Exception e) {
	 * logger.error("Error fetching filtered vehicle history: {}", e.getMessage(),
	 * e); return Collections.emptyList(); } }
	 */

	// Method to fetch the latest vehicle history by deviceID
	public VehicleHistory getLatestLocation(String deviceID) {
		logger.info("Fetching latest vehicle details for deviceID: {}", deviceID);

		VehicleHistory vehicleHistory = vehicleHistoryRepository
				.findTopByVehicle_DeviceIDOrderByTimestampDesc(deviceID);

		if (vehicleHistory != null) {
			logger.info("Found vehicle history for deviceID: {}", vehicleHistory.getVehicle().getDeviceID());
		} else {
			logger.warn("No vehicle history found for deviceID: {}", deviceID);
		}

		return vehicleHistory;
	}

	// new code violetion and vehicle report

	public List<VehicleHistory> findHistoryByDeviceIDMonthYear(String deviceID, int month, int year) {
		logger.info("Fetching vehicle history for deviceID LINE NUMBER 163: {}, month: {}, year: {}", deviceID, month,
				year);
		List<VehicleHistory> history = vehicleHistoryRepository.findByDeviceIDMonthYear(deviceID, month, year);

		if (history.isEmpty()) {
			logger.info("No vehicle history found for deviceID: {}, month: {}, year: {}", deviceID, month, year);
		} else {
			logger.info("Fetched {} records for deviceID: {}, month: {}, year: {}", history.size(), deviceID, month,
					year);
		}

		return history;
	}
	
	// Method to calculate the total distance traveled for a vehicle in a given date
		// range
		public double calculateTotalDistance(String deviceID, Timestamp startDate, Timestamp endDate) {
			logger.info("Calculating total distance for deviceID: {} from {} to {}", deviceID, startDate, endDate);

			try {
				List<VehicleHistory> history = vehicleHistoryRepository.findHistoryByDeviceIDAndDateRangeNative(deviceID,
						startDate, endDate);

				if (history == null || history.size() < 2) {
					logger.warn("Not enough data points to calculate distance for deviceID: {}", deviceID);
					return 0;
				}

				double totalDistance = 0.0;
				for (int i = 0; i < history.size() - 1; i++) {
					VehicleHistory point1 = history.get(i);
					VehicleHistory point2 = history.get(i + 1);

					totalDistance += calculateDistance(point1.getLatitude(), point1.getLongitude(), point2.getLatitude(),
							point2.getLongitude());
				}
				logger.info("Total distance calculated: {}", totalDistance);

				logger.info("Total distance calculated for deviceID: {} is {} km", deviceID, totalDistance);
				return totalDistance;
			} catch (Exception e) {
				logger.error("Error calculating total distance for deviceID: {} - {}", deviceID, e.getMessage(), e);

				return 0;
			}
		}

	  /**
     * Save multiple VehicleHistory records in a batch.
     * 
     * @param historyBatch the list of VehicleHistory objects to save
     */
	public void saveAll(List<VehicleHistory> historyBatch) {
	    if (historyBatch == null || historyBatch.isEmpty()) {
	        logger.warn("No vehicle history records to save. Batch is empty or null.");
	        return;
	    }

	    logger.info("Attempting to save batch of vehicle history records. Batch size: {}", historyBatch.size());
	    try {
	        long startTime = System.currentTimeMillis();
	        vehicleHistoryRepository.saveAll(historyBatch);
	        long endTime = System.currentTimeMillis();
	        logger.info("Batch of {} vehicle history records saved successfully in {} ms.", historyBatch.size(), (endTime - startTime));
	    } catch (Exception e) {
	        logger.error("Error saving batch of vehicle history records: {}", e.getMessage(), e);
	    }
	}
	
	public void save(VehicleHistory history) {
	    vehicleHistoryRepository.save(history);

	    logger.info(ANSI_GREEN + "[SAVE] New vehicle history record saved for Device ID: {} at Timestamp: {}" + ANSI_RESET);
	}


}
