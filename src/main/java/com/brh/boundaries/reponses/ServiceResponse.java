package com.brh.boundaries.reponses;

import com.brh.entities.UserEntity;
import com.brh.entities.cores.Service;
import com.brh.entities.cores.User;

import java.util.List;

public class ServiceResponse {
    private String status;
    private String msg;
    private List<Service> data;
    // Constructors, getters, and setters
    public ServiceResponse() {

    }
    public void init(String sts, String msg, List<Service> data){
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

    public List<Service> getData() {
        return data;
    }

    public void setData(List<Service> data) {
        this.data = data;
    }
}
