package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.model.Userr;
import com.GpsTracker.Thinture.repository.UserrRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserrService {

    private static final Logger logger = LoggerFactory.getLogger(UserrService.class);

    @Autowired
    private UserrRepository userrRepository;

    public void saveUserr(Userr userr) {
        logger.info("Saving userr: {}", userr);
        userrRepository.save(userr);
        logger.info("Userr saved successfully.");
    }

    public void deleteUserr(Long id) {
        logger.info("Deleting userr with ID: {}", id);
        userrRepository.deleteById(id);
        logger.info("Userr deleted successfully.");
    }

    // Add any other business logic or methods as needed
}
