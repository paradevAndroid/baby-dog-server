package com.bramix.controllers;

import com.bramix.Answer;
import com.bramix.ClientError;
import com.bramix.Properties;
import com.bramix.entities.*;
import com.bramix.repos.ClientRepository;
import com.bramix.repos.PetsRepository;
import com.bramix.repos.WorkerRepository;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping ("/info")
public class InfoController {
    @Autowired
    private PetsRepository petsRepository;
    @Autowired
    private WorkerRepository workerRepository;
    @Autowired
    private ClientRepository clientRepository;

    @RequestMapping ("/addPet")
    public Answer addPet (@RequestBody Pet pet, HttpSession httpSession){
        if (httpSession.getAttribute(Properties.id.getProperty()) == null){
            return new Answer (false,"Not permitted");
        }
        String string = (String) httpSession.getAttribute(Properties.id.getProperty());
        Client client = getUserType(string);
        pet.setClient(client);
        petsRepository.save(pet);
        return new Answer(true);
    }

    @RequestMapping ("/getPets")
    public Answer getPets (HttpSession httpSession){
        if(httpSession.getAttribute(Properties.type.getProperty()) == null)
            return new Answer (false,"Not permitted");
        String string = (String) httpSession.getAttribute(Properties.id.getProperty());
        Client client = getUserType(string);
        return new Answer(true, client.getPets());
    }

    @RequestMapping ("/removePet")
    public Answer removePet (@RequestParam int id, HttpSession httpSession){
        if(httpSession.getAttribute(Properties.type.getProperty()) == null)
            return new Answer (false,"Not permitted");
        String string = (String) httpSession.getAttribute(Properties.id.getProperty());
        petsRepository.deleteById(id);
        Client client = getUserType(string);
        return  new Answer (true, client.getPets());
    }
    @RequestMapping (value = "/setAddress", method = RequestMethod.POST)
    public Answer addAdress (@RequestBody Address address, HttpSession httpSession){
        if (httpSession.getAttribute(Properties.type.getProperty()) == null)
            return new Answer (false,"Not permitted");
        String id = (String) httpSession.getAttribute(Properties.id.getProperty());
        String type = (String) httpSession.getAttribute(Properties.type.getProperty());
        setByPhone(id, type, address);
        return new Answer(true);
    }
    @RequestMapping (value = "/user", method = RequestMethod.GET)
    public Answer getAdress (HttpSession httpSession) {
        Address address = null;
        RegistrationEntity registrationEntity;
        String type = (String) httpSession.getAttribute(Properties.type.getProperty());
        if (type == null)
            return new Answer (false,"Not permitted");
        if (type.equals(Properties.client.getProperty())){
            String id = (String) httpSession.getAttribute(Properties.id.getProperty());
            Client client = getUserType(id);
            registrationEntity = new RegistrationEntity( client.getAddress(), client, null);
        }
        else {
            String number = (String) httpSession.getAttribute(Properties.id.getProperty());
            Worker worker = workerRepository.findByContactPhone1(number).get();
            registrationEntity = new RegistrationEntity( worker.getAddress(), null, worker);
        }
        return new Answer(true, registrationEntity);
    }
    @RequestMapping (value = "/setDocument", method = RequestMethod.POST)
    public Answer setDocument (@RequestParam(required = false) String document1, @RequestParam (required = false) String document2, HttpSession httpSession){
        if (httpSession.getAttribute(Properties.type.getProperty()) == null)
            return new Answer (false,"Not permitted");
        String phone = (String) (httpSession.getAttribute(Properties.id.getProperty()));
        if (document1 != null)
            workerRepository.updateByPhone1(document1, phone);
        if (document2 != null)
            workerRepository.updateByPhone2(document2, phone);

        return new Answer (true);
    }

    @PostMapping("/findByPhone")
    @CrossOrigin
    public Answer findByPhone (@RequestParam String phone, HttpSession httpSession){
        Optional <Worker> worker = workerRepository.findByContactPhone1(phone);
        if (worker.isPresent()){
            RegistrationEntity registrationEntity = new RegistrationEntity();
            registrationEntity.setAddress(worker.get().getAddress());
            registrationEntity.setWorker(worker.get());
            return new Answer (true, registrationEntity);
        }
        else return  new Answer(false, ClientError.UserNotFound);
    }

    private Client getUserType (String id){
        if (clientRepository.existsByContactPhone1(id))
            return clientRepository.findByContactPhone1(id).get();
        else if (clientRepository.existsByTokenId(id))
            return clientRepository.findByTokenId(id).get();
        else return null;
    }
    private void setByPhone (String id, String type, Address address){
        if (type.equals(Properties.client.getProperty())){
            Client client = getUserType(id);
            client.setAddress(address);
            clientRepository.saveAndFlush(client);
        }
        else {
            Optional<Worker> worker = workerRepository.findByContactPhone1(id);
            worker.get().setAddress(address);
            workerRepository.removeById(worker.get().getId());
            workerRepository.save(worker.get());
        }
    }
}
