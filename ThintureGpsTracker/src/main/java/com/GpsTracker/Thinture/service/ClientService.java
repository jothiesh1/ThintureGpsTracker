package com.GpsTracker.Thinture.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.GpsTracker.Thinture.model.Admin;
import com.GpsTracker.Thinture.model.Client;
import com.GpsTracker.Thinture.repository.ClientRepository;
@Service
public class ClientService {

    @Autowired
    private ClientRepository clientRepository;

    public Client saveClient(Client client) {
        return clientRepository.save(client);
    }

    public Optional<Client> findByEmail(String email) {
        return clientRepository.findByEmail(email);
    }

    public List<Client> getAllClients() {
        return clientRepository.findAll();
    }
}
