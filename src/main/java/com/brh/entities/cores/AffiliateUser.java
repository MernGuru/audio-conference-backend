package com.brh.entities.cores;

import jakarta.ejb.Local;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Objects;

@Entity
@Table(name = "affiliate_users")
@SuppressWarnings("unused")
public class AffiliateUser {
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    @Column(unique = true,nullable = false, length = 32)
    private String firstname;

    @Column(nullable = false)
    private String lastname;
    @Column(nullable = false)
    private String email;
    @Column(nullable = false)
    private String password;

    public LocalDate getJoin_date() {
        return join_date;
    }

    public void setJoin_date(LocalDate join_date) {
        this.join_date = join_date;
    }

    @Column(nullable = false)
    private LocalDate join_date;


    public Integer getSubscribed_users_count() {
        return subscribed_users_count;
    }

    public void setSubscribed_users_count(Integer subscribed_users_count) {
        this.subscribed_users_count = subscribed_users_count;
    }

    private Integer subscribed_users_count;
    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(nullable = false)
    private String status;




    public AffiliateUser(){

    }

    public String getFirstname() {
        return firstname;
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }
    public String getLastname() {
        return lastname;
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        AffiliateUser contact = (AffiliateUser) o;
        return firstname.equals(contact.firstname) && lastname.equals(contact.lastname) && email.equals(contact.email);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), firstname, lastname, email);
    }

    @Override
    public String toString() {
        return "{\"super\":" + super.toString() +
                ",\"firstname\":\"" + firstname + "\"" +
                ",\"lastname\":\"" + lastname + "\"" +
                ",\"email\":" + email +
                "}";
    }

}

