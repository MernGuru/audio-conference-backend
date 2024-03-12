package com.brh.boundaries.reponses;

import com.brh.entities.UserEntity;
import com.brh.entities.cores.User;

import java.util.List;

public class UserResponse {
    private String status;
    private String msg;
    private List<User> data;
    // Constructors, getters, and setters
    public UserResponse() {

    }
    public void init(String sts, String msg, List<User> data){
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

    public List<User> getData() {
        return data;
    }

    public void setData(List<User> data) {
        this.data = data;
    }
}
