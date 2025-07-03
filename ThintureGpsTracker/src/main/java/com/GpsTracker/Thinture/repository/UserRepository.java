package com.GpsTracker.Thinture.repository;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.model.User;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

//AOP filter 
//public interface UserRepository extends JpaRepository<User, Long> {
    // No additional methods required for basic CRUD

@Repository
public interface UserRepository extends BaseRestrictedRepository<User, Long> {
	List<User> findByAdminId(Long adminId); // Example custom query method
	User findByEmailAndPassword(String email, String password);
	
	   User findByUsername(String username);
   
	 User findByEmail(String username);
	 User findByResetToken(String token);   
	 User findByEmailIgnoreCase(String email); 
	 
	 
}
