package com.bramix.service;

import com.bramix.entities.Client;
import com.bramix.entities.Worker;
import com.bramix.repos.ClientRepository;
import com.bramix.repos.WorkerRepository;
import org.springframework.beans.factory.annotation.Autowired;

@org.springframework.stereotype.Service
public class UserService {
    @Autowired
    private ClientRepository clientRepository;
    @Autowired
    private WorkerRepository workerRepository;

    public void save(Client client) {
        clientRepository.save(client);
    }
    public void save(Worker worker) {
        workerRepository.save (worker);
    }
    public ClientRepository getClientRepository() {
        return clientRepository;
    }

    public WorkerRepository getWorkerRepository() {
        return workerRepository;
    }

    public Client getClientType (String id){
        if (clientRepository.existsByContactPhone1(id))
            return clientRepository.findByContactPhone1(id).get();
        else if (clientRepository.existsByTokenId(id))
            return clientRepository.findByTokenId(id).get();
        else return null;
    }
}
