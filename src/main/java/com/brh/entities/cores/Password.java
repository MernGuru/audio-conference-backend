package com.brh.entities.cores;

public class Password {
    private String pass;
    private String npass;
    private String email;
    public Password(){
    }
    public String getEmail() {
        return email;
    }

    public void setEmail(String useremail) {
        this.email = useremail;
    }
    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }
    public String getNpass() {
        return npass;
    }

    public void setNpass(String npass) {
        this.npass = npass;
    }
}