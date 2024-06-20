package com.example.firebaseprojectapp.model;

public class Users {
    String profilepic, userName, mail, password,dob, userId;

    public Users(String mail, String password) {
        this.mail = mail;
        this.password = password;
    }

    public Users(String userName, String mail, String password, String dob) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.dob = dob;
    }

    public Users( String userName, String mail, String password, String dob,String profilepic) {
        this.userName = userName;
        this.mail = mail;
        this.password = password;
        this.dob = dob;
        this.profilepic = profilepic;
    }

    public Users() {
    }

    public String getProfilepic() {
        return profilepic;
    }

    public void setProfilepic(String profilepic) {
        this.profilepic = profilepic;
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

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
