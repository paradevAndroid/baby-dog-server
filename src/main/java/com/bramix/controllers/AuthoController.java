package com.bramix.controllers;


/*
    This class implements users authorisation
*/

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
import java.util.Optional;

@Slf4j
@RestController
@RequestMapping(path = "/auth")// This means that this class is a Controller
public class AuthoController {
    private Gson gson;
    @Autowired
    private GoogleService googleService;
    @Autowired
    FacebookService facebookService;
    @Autowired
    InstagramService instagramService;
    @Autowired
    private Validator validator;
    @Autowired
    private UserService userService;
    @Autowired
    private SmsService smsService;
    private Answer answer;

    @RequestMapping(value = "/googleIn", method = RequestMethod.POST)
    public Answer googleAutorisation(@RequestParam String token, HttpSession httpSession) {
        return getAnswer(token, Properties.google.getProperty(), httpSession);
    }

    @RequestMapping(value = "/facebookIn", method = RequestMethod.POST)
    public Answer faceBookAutorisation(@RequestParam String token, HttpSession httpSession) {
        return getAnswer(token, Properties.faceBook.getProperty(), httpSession);
    }
    @RequestMapping(value = "/instagramIn", method = RequestMethod.POST)
    public Answer instagramAutorisation(@RequestParam String token, HttpSession httpSession) {
        return getAnswer(token, Properties.instagram.getProperty(), httpSession);
    }

    @RequestMapping(value = "phone/first", method = RequestMethod.POST)
    public Answer phoneCLient(@RequestParam String phone, HttpSession httpSession) throws MessagingException {

        if (userService.getClientRepository().existsByContactPhone1(phone)) {
            log.info("phone authorized = {}", phone);
            httpSession.setAttribute(Properties.id.getProperty(), phone);
            httpSession.setAttribute(Properties.type.getProperty(), Properties.client.getProperty());
            return smsService.sendSms(phone);
        }
        else if (userService.getWorkerRepository().existsByContactPhone1(phone)){
            log.info("phone authorized = {}", phone);
            httpSession.setAttribute(Properties.id.getProperty(), phone);
            httpSession.setAttribute(Properties.type.getProperty(), Properties.worker.getProperty());
            return smsService.sendSms(phone);
        }
        return new Answer(false, ClientError.UserNotFound);
    }

    @RequestMapping(value = "phone/second", method = RequestMethod.POST)
    public Answer clientIn(@RequestParam String code, HttpSession httpSession) {
        return getByPhone(code, httpSession);
    }

    @RequestMapping(value = "/signOut", method = RequestMethod.POST)
    public Answer signOut(HttpSession httpSession) {
            httpSession.removeAttribute(Properties.id.getProperty());
            httpSession.removeAttribute(Properties.type.getProperty());
            return new Answer(true,  "User was logout" );
    }

    private Answer getAnswer(String token, String type, HttpSession httpSession) {
        String id = null;
        if (type.equals(Properties.google.getProperty())) id = googleService.getId(token);
        if (type.equals(Properties.faceBook.getProperty())) id = facebookService.getId(token);
        if (type.equals(Properties.instagram.getProperty())) id = instagramService.getId(token);
        if (!userService.getClientRepository().existsClient(id, type))
            return new Answer(false, ClientError.UserNotFound);
        //httpSession.setAttribute(Properties.logIn.getProperty(), Properties.logIn.getProperty());
        Client client = userService.getClientRepository().getClient(id, type);
        httpSession.setAttribute(Properties.type.getProperty(), Properties.client.getProperty());
        httpSession.setAttribute(Properties.id.getProperty(), client.getTokenId());
        RegistrationEntity registationEntity = new RegistrationEntity();
        registationEntity.setAddress(client.getAddress());
        registationEntity.setClient(client);
        //httpSession.setAttribute(Properties.id.getProperty(), client.getId());
        return new Answer(true, registationEntity , "Client's info");
    }

    public Answer getByPhone(String code, HttpSession httpSession) {
        String number = httpSession.getAttribute(Properties.id.getProperty()).toString();
        if (number != null) {
            if ((smsService.ValidateCode(number, code).getStatus())) {
                String typeOfUser = httpSession.getAttribute(Properties.type.getProperty()).toString();
                if (typeOfUser.equals(Properties.client.getProperty()) ){
                    Optional<Client> client = userService.getClientRepository().findByContactPhone1(number);
                    RegistrationEntity registationEntity = new RegistrationEntity();
                    registationEntity.setClient(client.get());
                    registationEntity.setAddress(client.get().getAddress());
                    return new Answer(true, registationEntity, "Client's info");
                }

                else{
                    Worker worker = userService.getWorkerRepository().findByContactPhone1(number).get();
                    RegistrationEntity registrationEntity = new RegistrationEntity();
                    registrationEntity.setWorker(worker);
                    registrationEntity.setAddress(worker.getAddress());
                    return new Answer(true, registrationEntity, "Worker's info");
                }

            }
            else {
                httpSession.removeAttribute(Properties.id.getProperty());
                httpSession.removeAttribute(Properties.type.getProperty());
                return smsService.ValidateCode(number, code);
            }
        }
        return new Answer(false,  "Not Permitted");
    }
    @GetMapping("/authorize")
    public void auto (HttpSession httpSession) {
        httpSession.setAttribute(Properties.type.getProperty(), Properties.phone.getProperty());
        httpSession.setAttribute(Properties.id.getProperty(), "380500395871");
    }

}
