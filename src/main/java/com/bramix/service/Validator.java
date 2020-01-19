package com.bramix.service;

import com.bramix.Answer;
import com.bramix.ClientError;
import com.bramix.Properties;
import com.bramix.entities.Account;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class Validator {
    @Autowired
    private UserService userService;
    private Answer answer;
    public Answer validatePhone (Account account, String role){
        answer = new Answer(false, ClientError.UserIsExist);
        if (userService.getWorkerRepository().existsByContactPhone1(account.getContactPhone1()) && role.equals(Properties.worker.getProperty()))
            return answer;
        if (userService.getClientRepository().existsByContactPhone1(account.getContactPhone1()) && role.equals(Properties.client.getProperty()))
            return answer;
        return new Answer(true);
    }
    public String getResponse (String token){
        try {
            URL obj = new URL(token);
            HttpURLConnection connection = (HttpURLConnection) obj.openConnection();
            connection.setRequestMethod("GET");
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuffer response = new StringBuffer();
            while ((inputLine = in.readLine()) != null)
                response.append(inputLine);
            in.close();
            return response.toString();
        }
        catch (IOException e){
            return null;
        }
    }


}
