
package com.brh.controllers.cores;

import com.brh.controllers.GenericDAO;
import com.brh.entities.AffiliateUserEntity;
import com.brh.entities.AudioFileEntity;
import com.brh.entities.TransactionEntity;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Stateless
@LocalBean
public class TransactionManager {
    @Inject
    private EntityManager manager;
    @Inject
    private TransactionManager transactionManager;
    public List<TransactionEntity> findPastSessions(String email) {
        List<TransactionEntity> resultList = manager.createQuery("SELECT s FROM TransactionEntity s WHERE s.email = :email ORDER BY s.start_date ASC ", TransactionEntity.class)
                .setParameter("email", email)
                .getResultList();
        return resultList;
    }
}
