package com.brh.boundaries.reponses;

import com.brh.entities.cores.AffiliateUser;

import java.util.List;

public class AffiliateUserResponse {
    private String status;
    private String msg;
    private List<AffiliateUser> data;
    // Constructors, getters, and setters
    public AffiliateUserResponse() {

    }
    public void init(String sts, String msg, List<AffiliateUser> data){
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

    public List<AffiliateUser> getData() {
        return data;
    }

    public void setData(List<AffiliateUser> data) {
        this.data = data;
    }
}
