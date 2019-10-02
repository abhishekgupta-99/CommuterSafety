package com.example.commutersafety;

public class User {
    String uid,uname,uemail,uphone;

    public User(String uid, String uname, String uemail, String uphone) {
        this.uid = uid;
        this.uname = uname;
        this.uemail = uemail;
        this.uphone = uphone;
    }

    public User() {
    }

    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getUname() {
        return uname;
    }

    public void setUname(String uname) {
        this.uname = uname;
    }

    public String getUemail() {
        return uemail;
    }

    public void setUemail(String uemail) {
        this.uemail = uemail;
    }

    public String getUphone() {
        return uphone;
    }

    public void setUphone(String uphone) {
        this.uphone = uphone;
    }
}
