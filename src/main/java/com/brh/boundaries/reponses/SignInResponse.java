package com.brh.boundaries.reponses;

import com.brh.entities.UserEntity;
import com.brh.entities.cores.User;

public class SignInResponse {
    private String status;
    private String msg;
    private User data;

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    private String user_status;
    // Constructors, getters, and setters
    public SignInResponse() {
        user_status = "inactive";
    }
    public void init(String sts, String msg, User data){
        setStatus(sts);
        setMsg(msg);
        setData(data);
    }
    public String getStatus() {
        return status;
    }
    public void setStatus(String str){
        this.status = str;
    }
    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public User getData() {
        return data;
    }

    public void setData(User data) {
        this.data = data;
    }
}
