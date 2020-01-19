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
import java.util.Map;

@Service
public class InstagramService implements TokenService {
    @Autowired
    Validator validator;
    @Autowired
    UserService userService;
    private Gson gson;

    @Override
    public Answer getFromToken(String token, HttpSession httpSession) {
        Map<String, Object> parameters = getMap(token);
        if (parameters == null) return new Answer(false, "Token is not validate");
        if (isExist((String)parameters.get("id"))) return new Answer(false, ClientError.UserIsExist);
            Account account = new Account((String) parameters.get("full_name"));
        httpSession.setAttribute(Properties.id.getProperty(), parameters.get("id"));
        httpSession.setAttribute(Properties.type.getProperty(), Properties.instagram.getProperty());
        return new Answer(true, account);

    }

    @Override
    public String getId(String token) {
        Map<String, Object> parameters = getMap(token);
        return parameters.get("id").toString();
    }

    @Override
    public boolean Validate(String token) {
        return false;
    }

    @Override
    public boolean isExist(String token){
        if (userService.getClientRepository().existsClient(token ,Properties.instagram.getProperty()))
            return true;
        return false;
    }
    private Map<String, Object> getMap(String token) {
        gson = new GsonBuilder().create();
        String resp = validator.getResponse(Properties.instagram_all.getProperty() + token);
        if (resp == null) return null;
        Map<String, Object> parameters = (Map<String, Object>) gson.fromJson(resp, Map.class).get("data");
        return parameters;
    }
}
