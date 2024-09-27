package com.GpsTracker.Thinture.repository;



import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.GpsTracker.Thinture.model.Admin;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    // Additional query methods can be added here
	 Admin findByEmailIgnoreCase(String email);
    
    // Admin findByEmailAndPassword(String email, String password);
    @Query("SELECT s.id FROM Admin s WHERE s.email = :email")
    Long findIdByEmail(@Param("email") String email);// Correctly defining the method
}


