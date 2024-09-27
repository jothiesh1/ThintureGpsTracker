package com.GpsTracker.Thinture.repository;

import com.GpsTracker.Thinture.model.User;
import com.GpsTracker.Thinture.model.Userr;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
    List<User> findByAdminId(Long adminId); 
    User findByEmailAndPassword(String email, String password);
	 User findByEmail(String username);
}
//