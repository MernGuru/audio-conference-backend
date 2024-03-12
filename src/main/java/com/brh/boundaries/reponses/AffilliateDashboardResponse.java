package com.brh.boundaries.reponses;

import com.brh.entities.cores.User;
import com.brh.entities.UserEntity;
import com.brh.entities.cores.User;

public class AffilliateDashboardResponse {
    private String status;
    private String msg;

    public long getGeneratedRevenue() {
        return generatedRevenue;
    }

    public void setGeneratedRevenue(long generatedRevenue) {
        this.generatedRevenue = generatedRevenue;
    }

    private long generatedRevenue;
    private long subscribledCount;

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

    private String user_status;
    // Constructors, getters, and setters
    public AffilliateDashboardResponse() {
        user_status = "inactive";
    }
    public void init(String sts, String msg, long data, long subscribledCount){
        setStatus(sts);
        setMsg(msg);
        setGeneratedRevenue(data);
        setSubscribledCount(subscribledCount);
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

    public long getSubscribledCount() {
        return subscribledCount;
    }

    public void setSubscribledCount(long subscribledCount) {
        this.subscribledCount = subscribledCount;
    }
}
