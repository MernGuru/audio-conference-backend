package com.brh.controllers.cores;

import com.brh.WebRTCEndpoint;
import com.brh.controllers.GenericDAO;
import com.brh.entities.AffiliateUserEntity;
import com.brh.entities.AudioFileEntity;
import com.brh.entities.ReserveCallEntity;
import com.brh.entities.UserEntity;
import com.brh.entities.cores.AffiliateUser;
import com.brh.entities.cores.Contact;
import com.brh.entities.cores.User;
import com.brh.security.Argon2Utility;

import jakarta.ejb.EJBException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.validation.constraints.Null;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class InviteTokenManager {
    @Inject
    private EntityManager manager;
    public Contact findContactByToken(String token) {
        Contact existingInvite = manager.createQuery(
                        "SELECT u FROM Contact u WHERE u.token = :token", Contact.class)
                .setParameter("token", token)
                .getResultList()
                .stream()
                .findFirst()
                .orElse(null);
        return existingInvite;
    }
    public void removeInvite(String token) {
        List<Contact> fileList = manager.createQuery("SELECT s FROM Contact s WHERE s.token = :token ", Contact.class)
                .setParameter("token", token)
                .getResultList();
        for (int i = 0; i < fileList.size(); i++) {
            Contact entity = fileList.get(i);
            manager.remove(entity);
        }
        return;
    }
}

