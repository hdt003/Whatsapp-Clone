package com.application.cloneofwhatsapp.Models;

public class Users {
    String profilePic,userName,mail,password,userId,lastMessage;

    public String getUserId() {
        return userId;
    }

//    public String getUserId(String userId) {
//        return userId;

//    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    //constructor  //alt+shift+insert(0)
    public Users(String profilePic, String userName, String mail, String password, String userId, String lastMessage) {
        this.profilePic = profilePic;
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.userId = userId;
        this.lastMessage = lastMessage;
    }



    public Users(String userName, String mail, String password) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
    }
    public Users()
    {

    }
    public String getProfilePic() {
        return profilePic;
    }

    public void setProfilePic(String profilePic) {
        this.profilePic = profilePic;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }



    public String getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(String lastMessage) {
        this.lastMessage = lastMessage;
    }
}
