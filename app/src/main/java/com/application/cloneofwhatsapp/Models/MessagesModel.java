package com.application.cloneofwhatsapp.Models;

public class MessagesModel {
    String uId,message;
    Long timeStamp;

    //Constructors
    public MessagesModel(String uId, String message, Long timeStamp) {
        this.uId = uId;
        this.message = message;
        this.timeStamp = timeStamp;
    }
    public MessagesModel(String uId, String message) {
        this.uId = uId;
        this.message = message;
    }
    public MessagesModel(){
        //Empty constructor for firebase
    }

    public String getuId() {
        return uId;
    }

    public void setuId(String uId) {
        this.uId = uId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Long getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(long timeStamp) {
        this.timeStamp = timeStamp;
    }
}
