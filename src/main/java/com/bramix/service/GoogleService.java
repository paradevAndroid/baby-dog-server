package com.bramix.service;

import com.bramix.Answer;
import com.bramix.ClientError;
import com.bramix.Properties;
import com.bramix.entities.Account;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class GoogleService implements TokenService {
    @Autowired
    private Validator validator;
    @Autowired
    private UserService userService;
    private GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new JacksonFactory())
            .build();
    @Override
    public Answer getFromToken(String token, HttpSession httpSession) {
        Payload payload = getPayLoad(token);
        if (payload == null)
            return new Answer(false, "Token is not validate");
        if (isExist(payload.getUserId()))
            return new Answer(false, ClientError.UserIsExist);
        String id = payload.getUserId();
        String email = payload.getEmail();
        String name = (String) payload.get("given_name");
        String familyName = (String) payload.get("family_name");
        httpSession.setAttribute(Properties.id.getProperty(), id);
        httpSession.setAttribute(Properties.type.getProperty(), Properties.google.getProperty());
        Account account = new Account(name,familyName, email);
        return new Answer (true, account, "User's info");
    }
    @Override
    public String getId(String token) {
        return getPayLoad(token).getUserId();
    }

    @Override
    public boolean Validate(String token) {
        try {
            verifier.verify(token);
            return true;
        } catch (Exception e) {
            return false; }
    }

    private Payload getPayLoad (String token) {
        GoogleIdToken idToken = null;
        try {
            idToken = verifier.verify(token);
            return idToken.getPayload();
        } catch (Exception e) {
            return null;
        }
    }
    @Override
    public boolean isExist(String token){
        if (userService.getClientRepository().existsClient(token,Properties.google.getProperty()))
            return true;
        return false;
    }

}
