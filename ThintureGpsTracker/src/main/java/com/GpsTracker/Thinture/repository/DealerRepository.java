package com.GpsTracker.Thinture.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.GpsTracker.Thinture.model.Dealer;



public interface DealerRepository extends JpaRepository<Dealer, Long> {
    List<Dealer> findByAdminId(Long adminId);
    Dealer findByEmailAndPassword(String email, String password);
    Dealer findByEmail(String email);

    @Query("SELECT d.companyName FROM Dealer d")
    List<String> findAllDealerNames();

    Dealer findByCompanyName(String companyName);  // Changed to companyName
}


/*
public interface DealerRepository extends JpaRepository<Dealer, Long> {
	List<Dealer> findByAdminId(Long adminId);
	Dealer findByEmailAndPassword(String email, String password);  // Correctly defining the method
	Dealer findByEmail(String username);
	  @Query("SELECT d.name FROM Dealer d")
	    List<String> findAllDealerNames();

	    Dealer findByName(String name);
}
*/