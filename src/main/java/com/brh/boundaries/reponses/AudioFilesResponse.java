package com.brh.boundaries.reponses;

import com.brh.entities.AudioFileEntity;
import com.brh.entities.ReserveCallEntity;
import com.brh.entities.cores.AffiliateUser;

import java.util.List;

public class AudioFilesResponse {
    private String status;
    private String msg;

    public List<ReserveCallEntity> getReserve_date() {
        return reserve_data;
    }

    public void setReserve_date(List<ReserveCallEntity> reserve_date) {
        this.reserve_data = reserve_date;
    }

    private List<ReserveCallEntity> reserve_data;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public List<AudioFileEntity> getData() {
        return data;
    }

    public void setData(List<AudioFileEntity> data) {
        this.data = data;
    }

    private List<AudioFileEntity> data;
    // Constructors, getters, and setters
    public AudioFilesResponse() {

    }
    public void init(String sts, String msg, List<AudioFileEntity> data, List<ReserveCallEntity> reserveData){
        setStatus(sts);
        setMsg(msg);
        setData(data);
        setReserve_date(reserveData);
    }
}
