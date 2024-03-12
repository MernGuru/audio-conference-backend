package com.brh.controllers.cores;

import com.brh.WebRTCEndpoint;
import com.brh.controllers.GenericDAO;
import com.brh.entities.AffiliateUserEntity;
import com.brh.entities.AudioFileEntity;
import com.brh.entities.ReserveCallEntity;
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

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.logging.Logger;

@Stateless
@LocalBean
public class ReserveCallManager {
    @Inject
    private EntityManager manager;
    @Inject
    private AudioFileManager audioFileManager;
    private static final Logger logger = Logger.getLogger(WebRTCEndpoint.class.getName());

    public int findReserve(String time) {
        List<ReserveCallEntity> fileList = manager.createQuery("SELECT s FROM ReserveCallEntity s WHERE s.reserve_time = :reserve_time ", ReserveCallEntity.class)
                .setParameter("reserve_time", time)
                .getResultList();
        return fileList.size();
    }

    public List<ReserveCallEntity> findReserveByTime(String time) {
        List<ReserveCallEntity> fileList = manager.createQuery("SELECT s FROM ReserveCallEntity s WHERE s.reserve_time = :reserve_time ", ReserveCallEntity.class)
                .setParameter("reserve_time", time)
                .getResultList();

        logger.info("This is an informational message2222." + time);
        return fileList;
    }

    public List<ReserveCallEntity> findReserveByDate(String date) {
        List<ReserveCallEntity> fileList = manager.createQuery("SELECT s FROM ReserveCallEntity s WHERE s.reserve_time like :reserve_time ", ReserveCallEntity.class)
                .setParameter("reserve_time", date + "%")
                .getResultList();

        logger.info("This is an informational message2222." + date);
        return fileList;
    }

    public void removeReserve(String time) {
        List<ReserveCallEntity> fileList = manager.createQuery("SELECT s FROM ReserveCallEntity s WHERE s.reserve_time = :reserve_time ", ReserveCallEntity.class)
                .setParameter("reserve_time", time)
                .getResultList();
        for (int i = 0; i < fileList.size(); i++) {
            ReserveCallEntity entity = fileList.get(i);
            manager.remove(entity);
        }
        return;
    }
}

