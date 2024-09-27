package com.GpsTracker.Thinture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.GpsTracker.Thinture.model.Dealer;

public interface DealerRepository extends JpaRepository<Dealer, Long> {
	List<Dealer> findByAdminId(Long adminId);
	Dealer findByEmailAndPassword(String email, String password);  // Correctly defining the method
	Dealer findByEmail(String username);
}
