package com.GpsTracker.Thinture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.GpsTracker.Thinture.model.SuperAdmin;
//AOP filter 

//public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {
@Repository
public interface SuperAdminRepository extends BaseRestrictedRepository<SuperAdmin, Long> {  
// This method will be used to authenticate SuperAdmin by email and password
    SuperAdmin findByEmailAndPassword(String email, String password);
    // Add this method to find by email only
    SuperAdmin findByEmail(String email);
    @Query("SELECT s.id FROM SuperAdmin s WHERE s.email = :email")
    Long findIdByEmail(@Param("email") String email);
	SuperAdmin findByEmailIgnoreCase(String currentUsername);
	  SuperAdmin findByResetToken(String token);
}
