package com.bramix.controllers;

import com.bramix.Answer;
import com.bramix.repos.ClientRepository;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping ("/client")
public class ClientDataController {
    private final ClientRepository clientRepository;

    public ClientDataController(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    @GetMapping ("/getReviews")
    public Answer getClientReviews (@RequestParam Integer id){
        if (!clientRepository.existsById(id)){
            return new Answer (false, "User with such id is not exist");
        }
        return new Answer(true, clientRepository.getOne(id).getReviews())  ;
    }
}
