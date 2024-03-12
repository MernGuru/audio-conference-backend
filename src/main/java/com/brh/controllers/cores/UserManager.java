package com.brh.controllers.cores;

import com.brh.WebRTCEndpoint;
import com.brh.controllers.GenericDAO;
import com.brh.entities.TransactionEntity;
import com.brh.entities.UserEntity;
import com.brh.entities.cores.DurationData;
import com.brh.entities.cores.Service;
import com.brh.entities.cores.User;
import com.brh.security.Argon2Utility;

import jakarta.ejb.EJBException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class UserManager {
    @Inject
    private EntityManager manager;
    private static final Logger logger = Logger.getLogger(WebRTCEndpoint.class.getName());

    public User findByUsername(String username) {
        TypedQuery<User> query = manager.createQuery("SELECT u FROM User u WHERE u.username = :username", User.class);
        query.setParameter("username",username);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            // Handle the case when no result is found
            return null; // Or throw a custom exception or return a default value
        }
    }
    public User findByUseremail(String useremail) {
        TypedQuery<User> query = manager.createQuery("SELECT u FROM User u WHERE u.email = :email", User.class);
        query.setParameter("email",useremail);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            // Handle the case when no result is found
            return null; // Or throw a custom exception or return a default value
        }
    }
    public UserEntity findByUseremail4entity(String useremail) {
        UserEntity existingUser = manager.createQuery(
                        "SELECT u FROM UserEntity u WHERE u.email = :email", UserEntity.class)
                .setParameter("email", useremail)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
        return existingUser;
    }
    public long findUsersCountByReferr(int referr_id) {
        return manager.createQuery(
                        "SELECT u FROM UserEntity u WHERE u.referr = :referr", UserEntity.class)
                .setParameter("referr", referr_id)
                .getResultList()
                .stream()
                .count();
    }
    public long getGeneratedRevenue() {
        double totalSum = 0;
        List<TransactionEntity> priceList = manager.createQuery("SELECT s FROM TransactionEntity s ", TransactionEntity.class)
                .getResultList();
        for (int j = 0; j < priceList.size(); j++) {
            totalSum += priceList.get(j).getPrice();
        }
        return Math.round(totalSum * 0.3);
    }

    public long getGeneratedRevenue(DurationData durationData) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("EEE MMM dd yyyy");

        // Parse the input string into a LocalDate object
        LocalDate from_date = LocalDate.parse(durationData.getFrom_date(), formatter);
        LocalDate end_date = LocalDate.parse(durationData.getEnd_date(), formatter);

        double totalSum = 0;
        List<TransactionEntity> priceList = manager.createQuery("SELECT s FROM TransactionEntity s WHERE s.start_date >= :startDate and s.start_date <= :endDate", TransactionEntity.class)
                .setParameter("startDate", from_date)
                .setParameter("endDate", end_date)
                .getResultList();
        for (int j = 0; j < priceList.size(); j++) {
            totalSum += priceList.get(j).getPrice();
        }
        return Math.round(totalSum * 0.3);
    }
    public  long getGeneratedRevenueByReferr(int referr_id) {
        List<User> userList = manager.createQuery("SELECT s FROM User s WHERE  s.referr = :referr", User.class)
                .setParameter("referr", referr_id)
                .getResultList();
        double totalSum = 0;
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            List<TransactionEntity> priceList = manager.createQuery("SELECT s FROM TransactionEntity s WHERE  s.email = :email", TransactionEntity.class)
                    .setParameter("email", user.getEmail())
                    .getResultList();
            for (int j = 0; j < priceList.size(); j++) {
                totalSum += priceList.get(j).getPrice();
            }
        }
        return Math.round(totalSum);
    }
    public long findActiveUsersCount() {
        return manager.createQuery(
                        "SELECT u FROM UserEntity u WHERE u.status = :status and u.is_admin = 0", UserEntity.class)
                .setParameter("status", "active")
                .getResultList()
                .stream()
                .count();
    }

    public long findSubscribedCount() {
        String sql = "SELECT u FROM UserEntity u WHERE u.is_admin = 0 and u.end_date not like u.join_date";
        return manager.createQuery(
                        sql, UserEntity.class)
                .getResultList()
                .stream()
                .count();
    }

    public List<User> findAllUsers() {
        return manager.createQuery("SELECT s FROM User s where s.is_admin = 0", User.class)
                .getResultList();
    }

    public UserEntity authenticate(final String email, final String password, final int type) throws EJBException {
        // type => 0: user, 1: admin
        UserEntity user = findByUseremail4entity(email);
        if (user != null) {
            if (!user.getPassword().equals(password)) {
                return null;
            }
            if (type == 0) /// need to update
                return user;
            if (type == 1 && user.getIs_admin() == 1) /// need to update
                return user;
            else return null;

        }else {
            return null;
        }
    }
}
