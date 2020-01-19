package com.bramix.controllers;

import com.bramix.Answer;
import com.bramix.ClientError;
import com.bramix.Properties;
import com.bramix.entities.Client;
import com.bramix.entities.RegistrationEntity;
import com.bramix.entities.Worker;
import com.bramix.service.*;
import com.bramix.sms.SmsService;
import com.google.gson.Gson;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.mail.MessagingException;
import javax.servlet.http.HttpSession;

//import javax.servlet.http.HttpSession;

/*
    This class implements users registration
*/

@Slf4j
@RestController
@RequestMapping(path = "/reg")// This means that this class is a Controller
public class RegistryController {
    private Gson gson;
    @Autowired
    private SmsService smsService;
    @Autowired
    private GoogleService googleService;
    @Autowired
    private Validator validator;
    @Autowired
    private UserService userService;
    @Autowired
    private FacebookService facebookService;
    @Autowired
    private InstagramService instagramService;

    @RequestMapping(value = "/phone", method  = RequestMethod.POST)
    public Answer registratePhone (@RequestParam String phone,@RequestParam String type, HttpSession httpSession) throws MessagingException {
        if(userService.getWorkerRepository().existsByContactPhone1(phone) || userService.getClientRepository().existsByContactPhone1(phone))
            return new Answer(false, ClientError.UserIsExist);

        httpSession.setAttribute(Properties.phone.getProperty(), phone);
        smsService.sendSms(phone);
            return new Answer(true, "Number need to activate") ;
    }
    @RequestMapping(value = "/phone/validate", method  = RequestMethod.POST)
    public Answer validatePhone (@RequestParam String code, HttpSession httpSession){
        if (httpSession.getAttribute(Properties.phone.getProperty()) != null)
            return smsService.ValidateCode(httpSession.getAttribute(Properties.phone.getProperty()).toString(), code);
        else return new Answer(false, "Not permitted");
    }
    @RequestMapping(value = "/google", method = RequestMethod.POST)
     public Answer getGoogleToken (@RequestParam String token, HttpSession httpSession){
        return googleService.getFromToken(token, httpSession);
    }
    @RequestMapping (value = "/facebook", method = RequestMethod.POST)
    public Answer getFacebookToken (@RequestParam String token, HttpSession httpSession) {
        return facebookService.getFromToken(token, httpSession);
    }
    @RequestMapping (value = "/instagram", method = RequestMethod.POST)
    public Answer getInstagramToken (@RequestParam String token, HttpSession httpSession){
        return instagramService.getFromToken(token,httpSession);
    }
    @RequestMapping(value = "/google/finishClient", method = RequestMethod.POST)
    public Answer googleRegistateClient(@RequestBody RegistrationEntity registationEntity, HttpSession httpSession) {
        return getFinishNetwork(registationEntity, Properties.google.getProperty(), httpSession,Properties.client.getProperty());
    }
    @RequestMapping(value = "/facebook/finishClient", method = RequestMethod.POST)
    public Answer faceBookRegistateClient(@RequestBody RegistrationEntity registationEntity, HttpSession httpSession) {
        return getFinishNetwork(registationEntity, Properties.faceBook.getProperty(), httpSession,Properties.client.getProperty());
    }
    @RequestMapping(value = "/instagram/finishClient", method = RequestMethod.POST)
    public Answer instagramRegistateClient(@RequestBody RegistrationEntity registationEntity, HttpSession httpSession) {
        return getFinishNetwork(registationEntity, Properties.instagram.getProperty(), httpSession,Properties.client.getProperty());
    }
    @RequestMapping(value = "/phone/finishClient", method = RequestMethod.POST)
    public Answer phoneRegistateClient(@RequestBody RegistrationEntity registationEntity, HttpSession httpSession) {
        if (httpSession.getAttribute(Properties.phone.getProperty()) != null){
            Client client = registationEntity.getClient();
            client.setAddress(registationEntity.getAddress());
            client.setType(Properties.phone.getProperty());
            client.setContactPhone1(httpSession.getAttribute(Properties.phone.getProperty()).toString());
            httpSession.removeAttribute(Properties.phone.getProperty());
            httpSession.setAttribute(Properties.type.getProperty(), Properties.client.getProperty());
            httpSession.setAttribute(Properties.id.getProperty() ,  client.getContactPhone1());
            userService.save(client);
            return new Answer(true, registationEntity) ;
        }
        else return new Answer(false, "Not permitted");
    }
    @RequestMapping(value = "/phone/finishWorker", method = RequestMethod.POST)
    public Answer phoneRegistateWorker(@RequestBody RegistrationEntity registationEntity, HttpSession httpSession) {


        if (httpSession.getAttribute(Properties.phone.getProperty()) != null){

            Worker worker = registationEntity.getWorker();
            worker.setAddress(registationEntity.getAddress());
            worker.setType(Properties.phone.getProperty());
            worker.setContactPhone1(httpSession.getAttribute(Properties.phone.getProperty()).toString());
            httpSession.removeAttribute(Properties.phone.getProperty());
            httpSession.setAttribute(Properties.id.getProperty(), worker.getContactPhone1());
            httpSession.setAttribute(Properties.type.getProperty(), Properties.worker.getProperty());
            //httpSession.setAttribute(Properties.logIn.getProperty(), Properties.logIn.getProperty());
            userService.save(worker);
            log.info("Worker is registrated", worker);
            return new Answer(true, registationEntity);
        }
        else return new Answer(false, "Not permitted");
    }
    private Answer getFinishNetwork (RegistrationEntity registationEntity, String type, HttpSession httpSession, String typeOfUser){
        if (httpSession.getAttribute(Properties.id.getProperty()) != null ){
            Client client = registationEntity.getClient();
            client.setAddress(registationEntity.getAddress());
            client.setType(type);
            client.setTokenId(httpSession.getAttribute(Properties.id.getProperty()).toString());
            httpSession.setAttribute(Properties.id.getProperty(), client.getTokenId());
            httpSession.setAttribute(Properties.type.getProperty(), Properties.client.getProperty());
            //httpSession.setAttribute(Properties.logIn.getProperty(), Properties.logIn.getProperty());
            userService.save(client);
            log.info("Client is registated = {}", client);
            return new Answer(true, registationEntity);
        }
        return new Answer(false, "Not permitted");
    }

}

