package com.brh;

import jakarta.ejb.Singleton;
import jakarta.websocket.Session;
import java.util.Collections;
import java.util.Set;
import java.util.HashMap;
import java.util.Map;


@Singleton
public class WebRTCManager {

    private static final Map<String, Session> adminSessions = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, Session> userSessions = Collections.synchronizedMap(new HashMap<>());
    private static final Map<String, Session> listenerSessions = Collections.synchronizedMap(new HashMap<>());

    public void addAdminSession(Session session) {
        adminSessions.put(session.getId(), session);
    }

    public void addUserSession(Session session) {
        userSessions.put(session.getId(), session);
    }

    public void addListenerSession(Session session) {
        listenerSessions.put(session.getId(), session);
    }

    public void removeUserSession(String id) {
        userSessions.remove(id);
    }

    public void removeAdminSession(String id) {
        adminSessions.remove(id);
    }
    public void removeListenerSession(String id) {
        listenerSessions.remove(id);
    }

    public Map<String, Session> getUserSessions() {
        return userSessions;
    }
    public Map<String, Session> getAdminSessions() { return adminSessions; }
    public Map<String, Session> getListenerSessions() { return listenerSessions; }
}
