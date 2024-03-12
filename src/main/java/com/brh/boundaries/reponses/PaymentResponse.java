package com.brh.boundaries.reponses;

import com.brh.entities.AudioFileEntity;
import com.brh.entities.TransactionEntity;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Locale;


public class PaymentResponse {

    String nextBillDate;
    String activeStatus;
    String status;

    public List<TransactionEntity> getTransactionLogs() {
        return transactionLogs;
    }

    public void setTransactionLogs(List<TransactionEntity> transactionLogs) {
        this.transactionLogs = transactionLogs;
    }

    private List<TransactionEntity> transactionLogs;
    public PaymentResponse() {

    }

    public String getNextBillDate() {
        return nextBillDate;
    }

    public void setNextBillDate(LocalDate nextBillDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd MMMM yyyy", Locale.ENGLISH);

        // Format the LocalDate object
        String formattedDate = nextBillDate.format(formatter);
        this.nextBillDate = formattedDate;
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
    public void init(LocalDate date, String activeStatus, String status, List<TransactionEntity> sessions) {
        setNextBillDate(date);
        setActiveStatus(activeStatus);
        setStatus(status);
        setTransactionLogs(sessions);
    }

}