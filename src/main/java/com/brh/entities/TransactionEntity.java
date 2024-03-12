package com.brh.entities;

import jakarta.persistence.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "transaction_log")
public class TransactionEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private  Integer interLength;

    @Column(nullable = false)
    private  String interUnit;

    @Column(nullable = false)
    private Integer totalOccurences;


    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    @Column(nullable = false)
    private LocalDate end_date;

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    @Column(nullable = false)
    private Double price;

    public String getCard_number() {
        return card_number;
    }

    public void setCard_number(String card_number) {
        this.card_number = card_number;
    }

    public String getCard_cvv() {
        return card_cvv;
    }

    public void setCard_cvv(String card_cvv) {
        this.card_cvv = card_cvv;
    }

    @Column(nullable = false)
    private String card_number;

    @Column(nullable = false)
    private String card_cvv;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Integer getInterLength() {
        return interLength;
    }

    public void setInterLength(Integer interLength) {
        this.interLength = interLength;
    }

    public String getInterUnit() {
        return interUnit;
    }

    public void setInterUnit(String interUnit) {
        this.interUnit = interUnit;
    }

    public Integer getTotalOccurences() {
        return totalOccurences;
    }

    public void setTotalOccurences(Integer totalOccurences) {
        this.totalOccurences = totalOccurences;
    }

    public Integer getTrialOccurences() {
        return trialOccurences;
    }

    public void setTrialOccurences(Integer trialOccurences) {
        this.trialOccurences = trialOccurences;
    }

    @Column(nullable = false)
    private Integer trialOccurences;

    public LocalDate getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDate start_date) {
        this.start_date = start_date;
    }

    @Column(nullable = false)
    private LocalDate start_date;

}

