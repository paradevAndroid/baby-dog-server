package com.bramix.service;

import com.bramix.sms.SmsService;
import org.springframework.beans.factory.annotation.Autowired;

public class Servises {
    @Autowired
    public static SmsService smsService;
    @Autowired
    public static GoogleService googleService;
    @Autowired
    public static Validator validator;
    @Autowired
    public static UserService userService;
    @Autowired
    public static FacebookService facebookService;
    @Autowired
    public static InstagramService instagramService;
}
