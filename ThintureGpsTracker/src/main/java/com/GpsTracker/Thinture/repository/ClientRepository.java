package com.GpsTracker.Thinture.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Dealer;

import java.util.List;
import java.util.Optional;

public interface ClientRepository extends JpaRepository<Client, Long> {

    // Fetch all clients
    @Query("SELECT c FROM Client c")
    List<Client> getAllClients();

    // Find Client ID by email
    @Query("SELECT c.id FROM Client c WHERE c.email = :email")
    Long findIdByEmail(@Param("email") String email);

 //   Optional<Client> findByEmail(String email);
    
    
    
    @Query("SELECT c FROM Client c WHERE c.dealer.id = :dealerId")
    Optional<Client> getClientByDealerId(@Param("dealerId") Long dealerId);

    @Query("SELECT c FROM Client c WHERE LOWER(c.companyName) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Client> findClientsByQuery(@Param("query") String query);
    
    @Query("SELECT c FROM Client c WHERE LOWER(c.companyName) LIKE LOWER(CONCAT('%', :query, '%')) OR CAST(c.id AS string) LIKE CONCAT('%', :query, '%')")
    List<Client> findClientsByIdOrName(@Param("query") String query);


    @Query("SELECT c FROM Client c WHERE c.id = :clientId")
    Optional<Client> findById(@Param("clientId") Long clientId);
    
    // Additional query methods can be added here
	 Admin findByEmailIgnoreCase(String email);
   
	  Client findByEmail(String email);
	  Client findByResetToken(String token); 
  
    
    
    
}



