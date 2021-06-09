package com.system.donate.model;

public class User {
    public String fullname="";
    public String email="";
    public String password="";
    public String retypepassword="";
    public User()
    {

    }

    public User(String fullname, String email, String password, String retypepassword) {
        this.fullname = fullname;
        this.email = email;
        this.password = password;
        this.retypepassword = retypepassword;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getRetypepassword() {
        return retypepassword;
    }

    public void setRetypepassword(String retypepassword) {
        this.retypepassword = retypepassword;
    }
}
