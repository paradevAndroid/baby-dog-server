package com.bramix.service;

import com.bramix.Answer;
import com.bramix.entities.Client;
import com.bramix.entities.Worker;

import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.ProtocolException;

public interface TokenService {
    Answer getFromToken(String token, HttpSession httpSession) throws IOException;
    String getId(String token);
    boolean Validate(String token);
    boolean isExist(String token);
}
