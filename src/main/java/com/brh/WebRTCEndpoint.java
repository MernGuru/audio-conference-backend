// WebRTCEndpoint.java
package com.brh;

import com.brh.controllers.cores.ReserveCallManager;
import com.brh.entities.AudioFileEntity;
import com.brh.entities.ReserveCallEntity;
import com.brh.entities.UserEntity;
import jakarta.annotation.Resource;
import jakarta.ejb.EJB;
import jakarta.inject.Inject;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.PersistenceContextType;
import jakarta.transaction.*;
import jakarta.websocket.*;
import jakarta.websocket.server.PathParam;
import jakarta.websocket.server.ServerEndpoint;

import jakarta.ws.rs.DefaultValue;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.logging.Logger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import jakarta.json.Json;
import jakarta.json.JsonObject;
import jakarta.json.JsonReader;
import java.io.StringReader;
@ServerEndpoint("/stream/user/{role}")
public class WebRTCEndpoint {
    @EJB
    private ReserveCallManager reserveCallManager;
    @Inject
    private WebRTCManager webRTCManager;
    @PersistenceContext(unitName = "default", type = PersistenceContextType.EXTENDED)
    EntityManager entityManager;

    @Resource
    private UserTransaction userTransaction;
    private static final Logger logger = Logger.getLogger(WebRTCEndpoint.class.getName());


    @OnOpen
    public void onOpen(Session session, @PathParam("role") @DefaultValue("user") String role) {
        logger.info("This is an informational message." + role);
        if ("admin".equals(role)) {
            webRTCManager.addAdminSession(session);
            sendJsonData(session, "login", session.getId());
        } else if ("user".equals(role)) {
            webRTCManager.addUserSession(session);
            sendJsonData(session, "login", session.getId());
            sendToAdmin("new-user", session.getId());
            if (webRTCManager.getAdminSessions().size() == 0) {
                LocalDateTime currentTimeUTC = LocalDateTime.now(ZoneOffset.UTC);

                // Convert the current time to the Mountain timezone
                ZoneId mountainZone = ZoneId.of("America/Denver");
                ZonedDateTime currentTime = currentTimeUTC.atZone(mountainZone);
                // Format the current time to Y-m-d H:00:00
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("Y-MM-dd H:00:00");
                String formattedTime = currentTime.format(formatter);
                logger.info("This is an informational message." + formattedTime);
                List<ReserveCallEntity> reserveCallEntityList = reserveCallManager.findReserveByTime(formattedTime);
                logger.info("This is an informational message111." + formattedTime);
                if (reserveCallEntityList.size() != 0) {
                    ReserveCallEntity entity = reserveCallEntityList.getFirst();
                    sendJsonData(session, "request-play", entity.getFilename());
                }else {
                    sendJsonData(session, "admin-disconnected", "");
                }
            }
        } else if ("listener".equals(role)) {
            webRTCManager.addListenerSession(session);
            sendJsonData(session, "login", session.getId());
            sendJsonData(session, "request-play", "");
        }
        else {
            // Invalid role, close the session if necessary`
            try {
                session.close(new CloseReason(CloseReason.CloseCodes.CANNOT_ACCEPT, "Invalid role" + role));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @OnMessage
    public void onMessage(String message, Session session) {
        if (message.equals("ping"))
            return;

        System.out.println("Message from " + session.getId() + ": " + message);
        if (webRTCManager.getAdminSessions().containsKey(session.getId())) {
            handleAdminMessage(message);
        } else if (webRTCManager.getUserSessions().containsKey(session.getId())) {
            handleUserMessage(message, session);
        } else if (webRTCManager.getListenerSessions().containsKey(session.getId())) {
            handleListenerMessage(message, session);
        }
    }

    @OnMessage
    public void onMessage(byte[] audioData, Session session) {
        logger.info("byte data receiving......");
        if (webRTCManager.getAdminSessions().containsKey(session.getId())) {
            try {
                ZonedDateTime currentTimeUTC = ZonedDateTime.now(ZoneOffset.UTC);

                logger.info("byte data receiving111......" + currentTimeUTC.toString() + LocalDateTime.now().toString());
                // Convert the current time to the Mountain timezone
                ZoneId mountainZone = ZoneId.of("America/Denver");
                ZonedDateTime currentTimeMountain = currentTimeUTC.withZoneSameInstant(mountainZone);
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("Y-MM-dd H:00:00");
                String formattedTime = currentTimeMountain.format(formatter);
                int hour = currentTimeMountain.getHour();
                String firstFileName  = hour + ".wav";
                Path path = Paths.get(firstFileName);

                logger.info("byte data receiving.222.....");
                path = Files.write(Paths.get(firstFileName), audioData, StandardOpenOption.CREATE, StandardOpenOption.APPEND);

                logger.info("byte data receiving..333....");
                AudioFileEntity audioFileEntity = new AudioFileEntity();
                audioFileEntity.setFilename(firstFileName);
                audioFileEntity.setSavetime(formattedTime);
                logger.info("byte data receiving..444...." + firstFileName + formattedTime);
                userTransaction.begin();
                entityManager.persist(audioFileEntity);
                userTransaction.commit();
                logger.info("saved file path is " + path.getRoot() + "/" + path.toString());
            } catch (IOException | NotSupportedException | SystemException | RollbackException |
                     HeuristicMixedException | HeuristicRollbackException e) {
                logger.info("byte data receiving..444....fdsfds" + e.toString());
                throw new RuntimeException(e);
            }
        } else {
            // Handle user messages or other logic
        }
    }

    @OnClose
    public void onClose(Session session) {
        if (webRTCManager.getAdminSessions().containsKey(session.getId())) {
            webRTCManager.removeAdminSession(session.getId());
            System.out.println("Admin disconnected: " + session.getId());
            webRTCManager.getUserSessions().forEach((userId, userSession) -> {
                try {
                    logger.info("This is an informational message." + userId);
                    sendJsonData(userSession, "admin-disconnected", "");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } else if (webRTCManager.getUserSessions().containsKey(session.getId())) {
            webRTCManager.removeUserSession(session.getId());
            System.out.println("User disconnected: " + session.getId());
            // Notify admin about the disconnected user
            sendToAdmin("user-disconnected", session.getId());
        } else if (webRTCManager.getListenerSessions().containsKey(session.getId())) {
            webRTCManager.removeListenerSession(session.getId());
            System.out.println("User disconnected: " + session.getId());
        }
    }

    @OnError
    public void onError(Throwable error) {
        System.err.println("WebSocket error: " + error.getMessage());
    }

    private void sendToAdmin(String type, String data) {
        logger.info("This is notifying new user to admins" + type + ":" + data);
        webRTCManager.getAdminSessions().forEach((adminId, adminSession) -> {
            try {
                logger.info("This is an informational message." + adminId);
                sendJsonData(adminSession, type, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void sendJsonData(Session session, String type, String data) {
        try {
            session.getBasicRemote().sendText(String.format("{\"type\":\"%s\",\"data\":\"%s\"}", type, data));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return;
    }

    private void handleAdminMessage(String message) {
        JsonObject messageObject = parseJsonString(message);
        JsonObject messageData = messageObject.get("data").asJsonObject();
        String target = messageData.getString("target");
        webRTCManager.getUserSessions().forEach((userId, userSession) -> {
            if (!target.equals(userId))
                return;
            try {
                userSession.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleUserMessage(String message, Session userSession) {
        JsonObject messageObject = parseJsonString(message);
        String messageType = messageObject.getString("type");
        if (messageType.equals("request-play")) {
            try {
                JsonObject data = messageObject.getJsonObject("data");
                String fileName = data.getString("file");
                Path path = Paths.get(fileName);
                byte[] audioData = Files.readAllBytes(path);
                userSession.getBasicRemote().sendBinary(ByteBuffer.wrap(audioData));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }

        webRTCManager.getAdminSessions().forEach((adminId, adminSession) -> {
            try {
                adminSession.getBasicRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    private void handleListenerMessage(String message, Session listenerSession) {
        JsonObject messageObject = parseJsonString(message);
        String messageType = messageObject.getString("type");
        if (messageType.equals("request-play")) {
            try {
                JsonObject data = messageObject.getJsonObject("data");
                String fileName = data.getString("file");
                Path path = Paths.get(fileName);
                byte[] audioData = Files.readAllBytes(path);
                listenerSession.getBasicRemote().sendBinary(ByteBuffer.wrap(audioData));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            return;
        }
    }

    private static JsonObject parseJsonString(String jsonString) {
        JsonReader jsonReader = Json.createReader(new StringReader(jsonString));
        JsonObject jsonObject = jsonReader.readObject();
        jsonReader.close();
        return jsonObject;
    }

}
