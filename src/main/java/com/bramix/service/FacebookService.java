package com.bramix.service;

import com.bramix.Answer;
import com.bramix.ClientError;
import com.bramix.Properties;
import com.bramix.entities.Account;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;

@Service
public class FacebookService implements TokenService {
    @Autowired
    private Validator validator;
    @Autowired
    private UserService userService;
    private Gson gson;
    @Override
    public Answer getFromToken(String token, HttpSession httpSession) {
        String response = validator.getResponse(Properties.facebook_all.getProperty() + token);
        if (response != null) {
            gson = new GsonBuilder().create();
            FacebookForm facebookForm = gson.fromJson(response, FacebookForm.class);
            if (isExist(facebookForm.id)) return new Answer(false, ClientError.UserIsExist);
            httpSession.setAttribute(Properties.id.getProperty(), facebookForm.id);
            httpSession.setAttribute(Properties.type.getProperty(), Properties.faceBook.getProperty());
            Answer answer = new Answer(true, new Account(facebookForm.first_name, facebookForm.last_name, facebookForm.email));
            return answer;
        }
        return new Answer(false, "Token is not validate");
    }
    @Override
    public String getId(String token) {
        String response = validator.getResponse(Properties.facebook_all.getProperty() + token);
        gson = new GsonBuilder().create();
        FacebookForm facebookForm = gson.fromJson(response, FacebookForm.class);
        if (facebookForm != null)
            return facebookForm.id;
        return null;
    }

    @Override
    public boolean Validate(String token) {
        return false;
    }

    @Override
    public boolean isExist(String token) {
        if (userService.getClientRepository().existsClient(token,Properties.faceBook.getProperty()))
            return true;
        return false;
    }

    private class FacebookForm {
        private String id;
        private String first_name;
        private String last_name;
        private String email;
    }
}

