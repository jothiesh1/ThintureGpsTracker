package com.GpsTracker.Thinture.repository;
import com.GpsTracker.Thinture.model.SuperAdmin;
import com.GpsTracker.Thinture.model.User;
import com.GpsTracker.Thinture.model.Userr;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserrRepository extends JpaRepository<Userr, Long> {
    // No additional methods required for basic CRUD
	List<User> findByAdminId(Long adminId); // Example custom query method
	Userr findByEmailAndPassword(String email, String password);
}