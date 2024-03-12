package com.brh.controllers.cores;

import com.brh.controllers.GenericDAO;
import com.brh.entities.UserEntity;
import com.brh.entities.cores.Service;
import com.brh.entities.cores.User;
import com.brh.security.Argon2Utility;

import jakarta.ejb.EJBException;
import jakarta.ejb.LocalBean;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.ws.rs.core.GenericEntity;
import jakarta.ws.rs.core.Response;

import java.util.List;

@Stateless
@LocalBean
public class ServiceManager {
    @Inject
    private EntityManager manager;

    public List<Service> findAllServices() {
        return manager.createQuery("SELECT s FROM Service s", Service.class)
                .getResultList();
    }
}
