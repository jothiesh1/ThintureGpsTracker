package com.GpsTracker.Thinture.repository;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import com.GpsTracker.Thinture.model.Vehicle;
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

	void deleteByDeviceID(String deviceID);

	// long countByCreatedDateAfter(LocalDate firstDayOfMonth);
	Optional<Vehicle> findByDeviceID(String deviceID);
	Optional<Vehicle> findByVehicleNumber(String vehicleNumber);

    // You can add custom query methods if needed
}
//