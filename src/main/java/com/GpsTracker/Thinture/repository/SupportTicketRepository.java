package com.GpsTracker.Thinture.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;


import com.GpsTracker.Thinture.model.SupportTicket;




import com.GpsTracker.Thinture.model.SupportTicket;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SupportTicketRepository extends JpaRepository<SupportTicket, Long> {
	  List<SupportTicket> findByAdminId(Long id);
	    List<SupportTicket> findBySuperadminId(Long id);
	    List<SupportTicket> findByDealerId(Long id);
	    List<SupportTicket> findByClientId(Long id);
	    List<SupportTicket> findByUserId(Long id);
	    List<SupportTicket> findByDriverId(Long id);
}

