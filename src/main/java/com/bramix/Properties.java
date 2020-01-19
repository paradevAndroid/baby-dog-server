package com.bramix;

public enum Properties {
    android_google_id("1048873970751-e0p85dfcjehgusai2t76hk1mvmr9ogpa.apps.googleusercontent.com"),
    google ("Google"),
    faceBook("FaceBook"),
    logIn ("logIn"),
    phone("phone"),
    instagram ("Instagram"),
    id ("id"),
    type ("type"),
    worker ("Worker"),
    client ("Client"),
    facebook_all("https://graph.facebook.com/v2.12/me?fields=id,first_name,last_name,email&access_token="),
    instagram_all("https://api.instagram.com/v1/users/self?access_token=");

    private String property;

    public final String getProperty() {
        return this.property;
    }
    Properties(){}
    Properties(String property) {
        this.property = property;
    }

    public static String findProperty (String property){
        for (Properties prop : Properties.values()) {
            if (property.equals(prop.getProperty()))
                return prop.getProperty();
        }
        return null;
    }
}
