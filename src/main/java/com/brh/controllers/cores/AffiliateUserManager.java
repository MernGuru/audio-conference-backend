package com.brh.controllers.cores;

import com.brh.controllers.GenericDAO;
import com.brh.entities.AffiliateUserEntity;
import com.brh.entities.UserEntity;
import com.brh.entities.cores.AffiliateUser;
import com.brh.entities.cores.User;
import com.brh.security.Argon2Utility;

import jakarta.ejb.EJBException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.validation.constraints.Null;

import java.util.List;
import java.util.Objects;

@Stateless
@LocalBean
public class AffiliateUserManager {
    @Inject
    private EntityManager manager;
    @Inject
    private UserManager userManager;
    public List<AffiliateUser> findAllUsers() {
        List<AffiliateUser> userList = manager.createQuery("SELECT s FROM AffiliateUser s", AffiliateUser.class)
                .getResultList();

        for (int i = 0; i < userList.size(); i++) {
            User u = userManager.findByUseremail(userList.get(i).getEmail());
            if (Objects.equals(u, null)) {
                AffiliateUser user = userList.get(i);
                user.setSubscribed_users_count(0);
                userList.set(i, user);
                continue;
            }
            int user_id = u.getId();
            int referrs_count = (int) userManager.findUsersCountByReferr(user_id);
            AffiliateUser user = userList.get(i);
            user.setSubscribed_users_count(referrs_count);
            userList.set(i, user);
        }
        return userList;
    }
    public AffiliateUser findByUseremail(String useremail) {
        TypedQuery<AffiliateUser> query = manager.createQuery("SELECT u FROM AffiliateUser u WHERE u.email = :email", AffiliateUser.class);
        query.setParameter("email",useremail);
        try {
            return query.getSingleResult();
        } catch (Exception e) {
            // Handle the case when no result is found
            return null; // Or throw a custom exception or return a default value
        }
    }

    public AffiliateUser authenticate(final String email, final String password) throws EJBException {
        AffiliateUser user = findByUseremail(email);
        if (user != null && user.getPassword().equals(password) && user.getStatus().equals("active")) {
            return user;
        }else {
            return null;
        }
    }

    public AffiliateUserEntity findByUseremail4entity(String useremail) {
        AffiliateUserEntity existingUser = manager.createQuery(
                        "SELECT u FROM AffiliateUserEntity u WHERE u.email = :email", AffiliateUserEntity.class)
                .setParameter("email", useremail)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
        return existingUser;
    }
}
