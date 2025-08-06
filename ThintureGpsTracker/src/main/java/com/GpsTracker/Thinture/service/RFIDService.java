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



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class RFIDService {

    private static final Logger logger = LoggerFactory.getLogger(RFIDService.class);

    @Autowired
    private RFIDRepository rfidRepository;

    @Autowired
    private DealerRepository dealerRepository;

     @Autowired
     private ClientRepository clientRepository;
    
    //dealer
    public void saveRFIDs(List<String> rfidCodes, Long dealerId) {
        logger.info("[RFID API] Saving {} RFIDs for dealerId={}", rfidCodes.size(), dealerId);

        Dealer dealer = dealerRepository.findById(dealerId)
            .orElseThrow(() -> {
                logger.error("[RFID API] Dealer with ID {} not found", dealerId);
                return new RuntimeException("Dealer not found");
            });

        int savedCount = 0;
        int skippedCount = 0;

        for (String code : rfidCodes) {
            if (!rfidRepository.existsByRfidCode(code)) {
                RFID rfid = new RFID();
                rfid.setRfidCode(code);
                rfid.setDealer(dealer);
                rfidRepository.save(rfid);
                logger.debug("[RFID API] Saved RFID: {}", code);
                savedCount++;
            } else {
                logger.warn("[RFID API] Duplicate RFID skipped: {}", code);
                skippedCount++;
            }
        }

        logger.info("[RFID API] Completed saving. Total saved: {}, Skipped: {}", savedCount, skippedCount);
    }
 // Client method (new)
    public void saveRFIDsForClient(List<String> rfidCodes, Long clientId) {
        logger.info("[RFID API] Saving {} RFIDs for clientId={}", rfidCodes.size(), clientId);

        Client client = clientRepository.findById(clientId)
            .orElseThrow(() -> {
                logger.error("[RFID API] Client with ID {} not found", clientId);
                return new RuntimeException("Client not found");
            });

        int savedCount = 0;
        int skippedCount = 0;

        for (String code : rfidCodes) {
            if (!rfidRepository.existsByRfidCode(code)) {
                RFID rfid = new RFID();
                rfid.setRfidCode(code);
                rfid.setClient(client);  // Set the client here
                rfidRepository.save(rfid);
                logger.debug("[RFID API] Saved RFID: {}", code);
                savedCount++;
            } else {
                logger.warn("[RFID API] Duplicate RFID skipped: {}", code);
                skippedCount++;
            }
        }

        logger.info("[RFID API] Completed saving. Total saved: {}, Skipped: {}", savedCount, skippedCount);
    }
    
    
    public List<RFIDDetailsDTO> getAllRFIDDetails() {
        List<RFID> rfidList = rfidRepository.findAll();
        return rfidList.stream().map(rfid -> {
            RFIDDetailsDTO dto = new RFIDDetailsDTO();
            dto.setId(rfid.getId());
            dto.setRfidCode(rfid.getRfidCode());

            if (rfid.getDealer() != null) {
                dto.setDealerId(rfid.getDealer().getId());
             //   dto.setDealerName(rfid.getDealer().getName()); // assumes Dealer has getName()
            }

            if (rfid.getClient() != null) {
                dto.setClientId(rfid.getClient().getId());
              //  dto.setClientName(rfid.getClient().getName()); // assumes Client has getName()
            }

            return dto;
        }).toList();
    }
    
}



