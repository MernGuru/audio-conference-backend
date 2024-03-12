package com.brh.boundaries.reponses;

import com.brh.entities.AudioFileEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;

public class ConferenceResponse {
    public String getNextBillDate() {
        return nextBillDate;
    }

    public void setNextBillDate(LocalDate date) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

        // Format the LocalDate object
        this.nextBillDate = date.format(formatter);
    }

    public String getActiveStatus() {
        return activeStatus;
    }

    public void setActiveStatus(String activeStatus) {
        this.activeStatus = activeStatus;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public List<AudioFileEntity> getPastSeesions() {
        return pastSeesions;
    }

    public void setPastSeesions(List<AudioFileEntity> pastSeesions) {
        this.pastSeesions = pastSeesions;
    }

    String nextBillDate;
    String activeStatus;
    String status;
    private List<AudioFileEntity> pastSeesions;
    public ConferenceResponse() {

    }
    public void init(LocalDate date, String activeStatus, String status, List<AudioFileEntity> sessions) {
        setNextBillDate(date);
        setActiveStatus(activeStatus);
        setStatus(status);
        setPastSeesions(sessions);
    }

}