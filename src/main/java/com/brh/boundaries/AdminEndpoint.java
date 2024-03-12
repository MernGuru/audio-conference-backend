package com.brh.boundaries;

import com.brh.EmailService;
import com.brh.WebRTCEndpoint;
import com.brh.boundaries.reponses.*;
import com.brh.controllers.cores.*;
import com.brh.entities.AffiliateUserEntity;
import com.brh.entities.AudioFileEntity;
import com.brh.entities.ReserveCallEntity;
import com.brh.entities.UserEntity;
import com.brh.entities.cores.*;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.ejb.EJB;
import jakarta.json.JsonObject;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;

import javax.crypto.spec.SecretKeySpec;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.Random;
import java.util.logging.Logger;

import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import com.fasterxml.jackson.databind.ObjectMapper;

@Path("/admin")
public class AdminEndpoint {
    @EJB
    private UserManager userManager;
    @EJB
    private AffiliateUserManager affiliateUserManager;
    @EJB
    private AudioFileManager audioFileManager;
    @EJB
    private ReserveCallManager reserveCallManager;
    private static final Logger logger = Logger.getLogger(WebRTCEndpoint.class.getName());
    @PersistenceContext
    EntityManager entityManager;
    @Inject
    ObjectMapper objectMapper;
    @POST
    @Path("/signin")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response signin( User userData ) {

        String useremail = userData.getUsername();
        String password = userData.getPassword();

        try {

            UserEntity user = userManager.authenticate(useremail, password, 1);

            if (user != null) {
                SignInResponse data = new SignInResponse();
                String secretString = "YourSecretKeyForJWTSigning123123123123123123";
                byte[] secretBytes = secretString.getBytes(StandardCharsets.UTF_8);
                Key signingKey = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());

                String token = Jwts.builder()
                        .setSubject(useremail)
                        .setIssuedAt(new Date())
                        .setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiration
                        .signWith(signingKey)
                        .compact();
                logger.info("This is an informational token after part.");
                data.init("success", token, null);
                return Response.status(Response.Status.OK).entity(data).build();
            }else {
                logger.info("This is an informational no user part.");
                SignInResponse data = new SignInResponse();
                data.init("failed", "unknown username or wrong password!", null);
                return Response.status(Response.Status.OK).entity(data).build();
            }
        }catch (Exception e){
            logger.info("This is an informational error part.");
            e.printStackTrace(); // Log or handle the exception properly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to sign in").build();
        }
    }

    @POST
    @Path("/validate-token")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response validateToken() {

        try {
            return Response.status(Response.Status.OK).entity("Correct credential").build();
        } catch (Exception e) {
            e.printStackTrace(); // Log or handle the exception properly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Incorrect credential").build();
        }
    }

    @POST
    @Path("/dashboard")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response dashboard() {
        try {
            long activeUsersCount = userManager.findActiveUsersCount();
            long subscribedUsersCount = userManager.findSubscribedCount();
            long generatedRevenue = userManager.getGeneratedRevenue();

            DashboardResponse data = new DashboardResponse(activeUsersCount, subscribedUsersCount, generatedRevenue);
            data.setStatus("success");
            return Response.status(Response.Status.OK).entity(data).build();
        }catch (Exception e){
            e.printStackTrace(); // Log or handle the exception properly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to api call").build();
        }
    }


    @POST
    @Path("/dashboard-revenue")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response dashboard(DurationData durationData) {
        try {
            long generatedRevenue = userManager.getGeneratedRevenue(durationData);

            DashboardResponse data = new DashboardResponse(0, 0, generatedRevenue);
            data.setStatus("success");
            return Response.status(Response.Status.OK).entity(data).build();
        }catch (Exception e){
            e.printStackTrace(); // Log or handle the exception properly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to api call").build();
        }
    }

    @POST
    @Path("/users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response users( ) {

        try {
            UserResponse data = new UserResponse();
            List<User> list = userManager.findAllUsers();
            if (list != null) {
                data.init("success", "Successfully fetched.", list);
                String json = null;
                try {
                    json = objectMapper.writeValueAsString(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Response.serverError().build();
                }

                return Response.ok(json, MediaType.APPLICATION_JSON).build();
//                return Response.status(Response.Status.OK).entity(data).build();
            }else {
                data.init("failed", "Filed to get list.", null);
                return Response.status(Response.Status.OK).entity(data).build();
            }
        }catch (Exception e){
            e.printStackTrace(); // Log or handle the exception properly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to api call").build();
        }
    }

    @POST
    @Path("/afilliate-users")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response affiliate_users( ) {

        try {
            AffiliateUserResponse data = new AffiliateUserResponse();
            List<AffiliateUser> list = affiliateUserManager.findAllUsers();
            if (list != null) {
                data.init("success", "Successfully fetched.", list);
                String json = null;
                try {
                    json = objectMapper.writeValueAsString(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Response.serverError().build();
                }

                return Response.ok(json, MediaType.APPLICATION_JSON).build();
//                return Response.status(Response.Status.OK).entity(data).build();
            }else {
                data.init("failed", "Filed to get list.", null);
                return Response.status(Response.Status.OK).entity(data).build();
            }
        }catch (Exception e){
            e.printStackTrace(); // Log or handle the exception properly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to api call").build();
        }
    }

    @POST
    @Path("/invite")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response invite( Contact contact ) {
        String email = contact.getEmail();

        try {
//            entityManager.merge(contact);
            EmailService emailService = new EmailService();
            ReferralCodeManager rcm = new ReferralCodeManager();
            String token = rcm.generateReferralCode(Math.abs(new Random(System.currentTimeMillis()).nextInt()));
            contact.setToken(token);
            contact.setCreate_at(LocalDateTime.now());
            entityManager.persist(contact);
            emailService.sendEmail(email, "Hi, dear", "Please visit here: https://brh.lcom:8443/join-session/invite/"+ token);

            SignInResponse data = new SignInResponse();
            data.init("success", "Successfully submited.", null);
            return Response.status(Response.Status.OK).entity(data).build();
        }catch (Exception e){
            e.printStackTrace(); // Log or handle the exception properly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to api call").build();
        }
    }

    @POST
    @Path("/affiliate-user-statistics")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response affiliate_user_statistics(AffiliateUser userData) {
        String email = userData.getEmail();
        User referr = userManager.findByUseremail(email);
        try {
            long subscribedUsersCount = userManager.findUsersCountByReferr(referr.getId());
            long generatedRevenue = userManager.getGeneratedRevenueByReferr(referr.getId());

            DashboardResponse data = new DashboardResponse(0, subscribedUsersCount, generatedRevenue);
            data.setStatus("success");
            return Response.status(Response.Status.OK).entity(data).build();
        }catch (Exception e){
            DashboardResponse data = new DashboardResponse(0, 0, 0);
            data.setStatus("success");
            return Response.status(Response.Status.OK).entity(data).build();
        }
    }
    @POST
    @Path("/affiliate-status")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response affiliate_status( AffiliateUser userData ) {
        String email = userData.getEmail();
        String status = userData.getStatus();

        try {

            AffiliateUserEntity entiy = affiliateUserManager.findByUseremail4entity(email);

            if (entiy != null) {
                SignInResponse data = new SignInResponse();
                entiy.setEmail(email);
                entiy.setStatus(status);
                entityManager.merge(entiy);
                data.init("success", "Successfully changed.", null);
                return Response.status(Response.Status.OK).entity(data).build();
            }else {
                SignInResponse data = new SignInResponse();
                data.init("failed", "Unknown email address!", null);
                return Response.status(Response.Status.OK).entity(data).build();
            }
        }catch (Exception e){
            e.printStackTrace(); // Log or handle the exception properly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to sign in").build();
        }
    }

    @POST
    @Path("/audio-files")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response audio_files( ) {

        try {
            AudioFilesResponse data = new AudioFilesResponse();
            List<AudioFileEntity> list = audioFileManager.findAllFiles();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("Y-MM-dd");
            List<ReserveCallEntity> reserveList = reserveCallManager.findReserveByDate(LocalDate.now().format(formatter));
            if (list != null) {
                data.init("success", "Successfully fetched.", list, reserveList);
                return Response.status(Response.Status.OK).entity(data).build();
            }else {
                data.init("failed", "Filed to get list.", null, null);
                return Response.status(Response.Status.OK).entity(data).build();
            }
        }catch (Exception e){
            e.printStackTrace(); // Log or handle the exception properly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to api call").build();
        }
    }

    @POST
    @Path("/reserve-call")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response reserve_call(ReserveCallEntity reserveData) {

        try {
            SignInResponse data = new SignInResponse();
            int len = reserveCallManager.findReserve(reserveData.getReserve_time());

            if (len > 0)
                reserveCallManager.removeReserve(reserveData.getReserve_time());
            entityManager.persist(reserveData);
            data.init("success", "Successfully reserved.", null);
            return Response.status(Response.Status.OK).entity(data).build();
        }catch (Exception e){
            e.printStackTrace(); // Log or handle the exception properly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to api call").build();
        }
    }

    @POST
    @Path("/cancel-reserve-call")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response cancel_reserve_call(ReserveCallEntity reserveData) {

        try {
            SignInResponse data = new SignInResponse();
            reserveCallManager.removeReserve(reserveData.getReserve_time());
            data.init("success", "Successfully canceled.", null);
            return Response.status(Response.Status.OK).entity(data).build();
        }catch (Exception e){
            e.printStackTrace(); // Log or handle the exception properly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to api call").build();
        }
    }
}