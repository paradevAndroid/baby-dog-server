package com.bramix;

public class Answer {
    private boolean status;
    private Object response;
    private String details;
    private int userInfo;


    public Answer(boolean status, Object response, String details) {
        this.status = status;
        this.response = response;
        this.details = details;
    }

    public Answer(boolean status, String details) {
        this.status = status;
        this.details = details;
    }

    public Answer(boolean status, int userInfo) {
        this.status = status;
        this.userInfo = userInfo;
    }

    public Answer(boolean status, Object response) {
        this.status = status;
        this.response = response;
    }

    public int getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(int userInfo) {
        this.userInfo = userInfo;
    }

    public Answer(boolean status) {
        this.status = status;
    }

    public boolean getStatus() {
        return status;
    }

    public void setStatus(boolean status) {
        this.status = status;
    }

    public Object getResponse() {
        return response;
    }

    public void setResponse(Object response) {
        this.response = response;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
