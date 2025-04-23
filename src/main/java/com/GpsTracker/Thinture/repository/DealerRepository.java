package com.GpsTracker.Thinture.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Dealer;



public interface DealerRepository extends JpaRepository<Dealer, Long> {
    List<Dealer> findByAdminId(Long adminId);
    Dealer findByEmailAndPassword(String email, String password);
    Dealer findByEmail(String email);
    Dealer findByEmailIgnoreCase(String email);
    @Query("SELECT d.companyName FROM Dealer d")
    List<String> findAllDealerNames();

    Dealer findByCompanyName(String companyName);  // Changed to companyName
    
    
    @Query("SELECT d.id FROM Dealer d WHERE d.email = :email")
    Long findIdByEmail(@Param("email") String email);
    
    @Query("SELECT d FROM Dealer d WHERE d.id = :dealerId")
    Optional<Dealer> findById(@Param("dealerId") Long dealerId);

   Dealer findByResetToken(String token); 
}

    // Admin findByEmailAndPassword(String email, String password);
   


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