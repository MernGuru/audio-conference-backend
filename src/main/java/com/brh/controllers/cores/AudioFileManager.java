package com.brh.controllers.cores;

import com.brh.controllers.GenericDAO;
import com.brh.entities.AffiliateUserEntity;
import com.brh.entities.AudioFileEntity;
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
public class AudioFileManager {
    @Inject
    private EntityManager manager;
    @Inject
    private AudioFileManager audioFileManager;

    public List<AudioFileEntity> findAllFiles() {
        List<AudioFileEntity> fileList = manager.createQuery("SELECT s FROM AudioFileEntity s WHERE 1=1 ORDER BY s.savetime DESC ", AudioFileEntity.class)
                .getResultList();
        List<AudioFileEntity> resultList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i < fileList.size())
                resultList.add(fileList.get(i));
        }
        return resultList;
    }

    public List<AudioFileEntity> findPastSessions(LocalDateTime now) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("Y-MM-dd hh:00:00");

        // Format the LocalDate object
        String formattedDate = now.format(formatter);
        List<AudioFileEntity> fileList = manager.createQuery("SELECT s FROM AudioFileEntity s WHERE s.savetime <= :today ORDER BY s.savetime DESC ", AudioFileEntity.class)
                .setParameter("today", formattedDate)
                .getResultList();
        List<AudioFileEntity> resultList = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            if (i < fileList.size())
                resultList.add(fileList.get(i));
        }
        return resultList;
    }
}
