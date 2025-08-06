package com.GpsTracker.Thinture.service;

import com.GpsTracker.Thinture.dto.RFIDDetailsDTO;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.model.Dealer;
import com.GpsTracker.Thinture.model.RFID;
import com.GpsTracker.Thinture.repository.ClientRepository;
import com.GpsTracker.Thinture.repository.DealerRepository;
import com.GpsTracker.Thinture.repository.RFIDRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class RFIDService {

    private static final Logger logger = LoggerFactory.getLogger(RFIDService.class);

    @Autowired
    private RFIDRepository rfidRepository;

    @Autowired
    private DealerRepository dealerRepository;

    @Autowired
    private ClientRepository clientRepository;
    
    // ✅ Enhanced dealer method with duplicate details
    public Map<String, Object> saveRFIDs(List<String> rfidCodes, Long dealerId) {
        logger.info("[RFID API] Saving {} RFIDs for dealerId={}", rfidCodes.size(), dealerId);

        Dealer dealer = dealerRepository.findById(dealerId)
            .orElseThrow(() -> {
                logger.error("[RFID API] Dealer with ID {} not found", dealerId);
                return new RuntimeException("Dealer not found");
            });

        int savedCount = 0;
        int skippedCount = 0;
        List<String> savedRFIDs = new ArrayList<>();
        List<String> duplicateRFIDs = new ArrayList<>();

        for (String code : rfidCodes) {
            if (!rfidRepository.existsByRfidCode(code)) {
                RFID rfid = new RFID();
                rfid.setRfidCode(code);
                rfid.setDealer(dealer);
                rfidRepository.save(rfid);
                logger.debug("[RFID API] Saved RFID: {}", code);
                savedRFIDs.add(code);
                savedCount++;
            } else {
                logger.warn("[RFID API] Duplicate RFID skipped: {}", code);
                duplicateRFIDs.add(code);
                skippedCount++;
            }
        }

        logger.info("[RFID API] Completed saving. Total saved: {}, Skipped: {}", savedCount, skippedCount);
        
        Map<String, Object> result = new HashMap<>();
        result.put("savedCount", savedCount);
        result.put("duplicateCount", skippedCount);
        result.put("savedRFIDs", savedRFIDs);
        result.put("duplicateRFIDs", duplicateRFIDs);
        result.put("success", true);
        result.put("message", savedCount + " RFIDs registered successfully, " + skippedCount + " duplicates skipped.");
        
        return result;
    }

    // ✅ Enhanced client method with duplicate details
    public Map<String, Object> saveRFIDsForClient(List<String> rfidCodes, Long clientId) {
        logger.info("[RFID API] Saving {} RFIDs for clientId={}", rfidCodes.size(), clientId);

        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> {
                logger.error("[RFID API] Client with ID {} not found", clientId);
                return new RuntimeException("Client not found");
            });

        int savedCount = 0;
        int skippedCount = 0;
        List<String> savedRFIDs = new ArrayList<>();
        List<String> duplicateRFIDs = new ArrayList<>();

        for (String code : rfidCodes) {
            if (!rfidRepository.existsByRfidCode(code)) {
                RFID rfid = new RFID();
                rfid.setRfidCode(code);
                rfid.setClient(client);
                rfidRepository.save(rfid);
                logger.debug("[RFID API] Saved RFID: {}", code);
                savedRFIDs.add(code);
                savedCount++;
            } else {
                logger.warn("[RFID API] Duplicate RFID skipped: {}", code);
                duplicateRFIDs.add(code);
                skippedCount++;
            }
        }

        logger.info("[RFID API] Completed saving. Total saved: {}, Skipped: {}", savedCount, skippedCount);
        
        Map<String, Object> result = new HashMap<>();
        result.put("savedCount", savedCount);
        result.put("duplicateCount", skippedCount);
        result.put("savedRFIDs", savedRFIDs);
        result.put("duplicateRFIDs", duplicateRFIDs);
        result.put("success", true);
        result.put("message", savedCount + " RFIDs registered successfully, " + skippedCount + " duplicates skipped.");
        
        return result;
    }
    
    public List<RFIDDetailsDTO> getAllRFIDDetails() {
        List<RFID> rfidList = rfidRepository.findAll();
        return rfidList.stream().map(rfid -> {
            RFIDDetailsDTO dto = new RFIDDetailsDTO();
            dto.setId(rfid.getId());
            dto.setRfidCode(rfid.getRfidCode());

            if (rfid.getDealer() != null) {
                dto.setDealerId(rfid.getDealer().getId());
            }

            if (rfid.getClient() != null) {
                dto.setClientId(rfid.getClient().getId());
            }

            return dto;
        }).toList();
    }
    
 // Add this method to your existing RFIDService class:

    public List<String> getAllRFIDCodes() {
        List<RFID> rfids = rfidRepository.findAll();
        return rfids.stream()
                    .map(RFID::getRfidCode)
                    .collect(Collectors.toList());
    }
}