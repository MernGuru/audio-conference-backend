package com.brh.entities;
import jakarta.ejb.Local;
import jakarta.persistence.*;
import jakarta.persistence.criteria.CriteriaBuilder;

import java.time.LocalDate;

@Entity
@Table(name = "users")
public class UserEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(nullable = false)
    private String username;
    @Column(nullable = false)
    private String forename;
    @Column(nullable = false)
    private String surname;
    @Column(unique = true,nullable = false, length = 32)
    private String email;
    @Column(nullable = false)
    private String password;
    @Column(nullable = true)
    private String curpassword;
    @Column(nullable = true)
    private String newpassword;

    public int getIs_admin() {
        return is_admin;
    }

    public void setIs_admin(int is_admin) {
        this.is_admin = is_admin;
    }

    @Column(nullable = false)
    private int is_admin;

    @Column(nullable = true)
    private String inviteToken;

    public String getInviteToken() {
        return inviteToken;
    }

    public void setInviteToken(String inviteToken) {
        this.inviteToken = inviteToken;
    }

    public LocalDate getJoin_date() {
        return join_date;
    }

    public void setJoin_date(LocalDate join_date) {
        this.join_date = join_date;
    }

    @Column(nullable = false)
    private LocalDate join_date;


    public LocalDate getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDate end_date) {
        this.end_date = end_date;
    }

    @Column(nullable = false)
    private LocalDate end_date;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Column(nullable = false)
    private String status;

    public Integer getReferr() {
        return referr;
    }

    public void setReferr(Integer referr) {
        this.referr = referr;
    }

    @Column(nullable = false)
    private Integer referr;

    // Getters and setters...
    // Getters and setters...

    public String getUsername() {
        return username;
    }
    public void setForename(String forename) {
        this.forename = forename;
    }
    public void setSurname(String surname) {
        this.surname = surname;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }


    public String getcurPassword() {
        return curpassword;
    }

    public void setcurPassword(String curpassword) {
        this.curpassword = curpassword;
    }
    public String getnewPassword() {
        return newpassword;
    }

    public void setnewPassword(String newpassword) {
        this.newpassword = newpassword;
    }

}
