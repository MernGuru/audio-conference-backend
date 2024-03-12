package com.brh.boundaries;

import com.brh.WebRTCEndpoint;
import com.brh.boundaries.reponses.AffilliateDashboardResponse;
import com.brh.boundaries.reponses.ConferenceResponse;
import com.brh.boundaries.reponses.PaymentResponse;
import com.brh.boundaries.reponses.SignInResponse;
import com.brh.controllers.cores.*;
import com.brh.entities.UserEntity;
import com.brh.entities.cores.AffiliateUser;
import com.brh.entities.cores.Contact;
import com.brh.entities.cores.User;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.json.JsonObject;
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

import java.time.*;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.logging.Logger;

@Path("/user")
public class UserEndpoint {
    @Inject
    EntityManager entityManager;
    @EJB
    private AffiliateUserManager affiliateUserManager;
    @EJB
    private UserManager userManager;
    @EJB
    private AudioFileManager audioFileManager;
    @EJB
    private TransactionManager transactionManager;
    @EJB
    private InviteTokenManager inviteTokenManager;
    @Inject
    ObjectMapper objectMapper;
    private static final Logger logger = Logger.getLogger(WebRTCEndpoint.class.getName());
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
    @Path("/referral-code")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response getReferralCode(User userData) {

        String useremail = userData.getUsername();
        try {
            AffiliateUser user = affiliateUserManager.findByUseremail(useremail);
            ReferralCodeManager rcm = new ReferralCodeManager();
            String referCode = rcm.generateReferralCode(user.getId());
            long genratedRevenue = userManager.getGeneratedRevenueByReferr(user.getId());
            long subscribedUsersCount = userManager.findUsersCountByReferr(user.getId());
            AffilliateDashboardResponse data = new AffilliateDashboardResponse();
            data.init("success", referCode, genratedRevenue, subscribedUsersCount);
            return Response.status(Response.Status.OK).entity(data).build();
        } catch (Exception e) {
            e.printStackTrace(); // Log or handle the exception properly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Incorrect credential").build();
        }
    }

    @POST
    @Path("/conference-info")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response getConferenceInfo(User userData) {

        String useremail = userData.getEmail();
        ConferenceResponse response = new ConferenceResponse();
        try {
            LocalDateTime currentTimeUTC = LocalDateTime.now(ZoneOffset.UTC);

            // Convert the current time to the Mountain timezone
            ZoneId mountainZone = ZoneId.of("America/Denver");
            ZonedDateTime currentTimeMountain = currentTimeUTC.atZone(mountainZone);
            User user = userManager.findByUseremail(useremail);
            LocalDate endDate = user.getEnd_date();
            response.init(endDate.plusDays(1), user.getStatus(), "success", audioFileManager.findPastSessions(LocalDateTime.from(currentTimeMountain)));

            logger.info("......" + response.getNextBillDate());
            return Response.status(Response.Status.OK).entity(response).build();
        } catch (Exception e) {
            e.printStackTrace(); // Log or handle the exception properly
            response.init(LocalDate.now(), "", "failure", null);
            return Response.status(Response.Status.OK).entity(response).build();
        }
    }

    @POST
    @Path("/payment-info")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response getPaymentInfo(User userData) {

        String useremail = userData.getEmail();
        try {
            User user = userManager.findByUseremail(useremail);
            LocalDate endDate = user.getEnd_date();
            PaymentResponse response = new PaymentResponse();
            long differenceInDays = ChronoUnit.DAYS.between(LocalDate.now(), endDate);
            String status = "active";
            if (differenceInDays <= 10) {
                status = "inactive";
            }
            logger.info("......" + differenceInDays);
            response.init(endDate.plusDays(1), status, "success", transactionManager.findPastSessions(useremail));
            String json = null;
            try {
                json = objectMapper.writeValueAsString(response);
            } catch (Exception e) {
                e.printStackTrace();
                return Response.serverError().build();
            }

            return Response.ok(json, MediaType.APPLICATION_JSON).build();
//            return Response.status(Response.Status.OK).entity(response).build();
        } catch (Exception e) {
            e.printStackTrace(); // Log or handle the exception properly
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Incorrect credential").build();
        }
    }

    @POST
    @Path("/validate-invite-token")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response validateInviteToken(Contact contact) {

        String token = contact.getToken();
        ConferenceResponse response = new ConferenceResponse();
        try {
            Contact contact1 = inviteTokenManager.findContactByToken(token);
            ZoneId jakartaZone = ZoneId.of("Asia/Jakarta");

            // Convert LocalDateTime objects to ZonedDateTime objects with Jakarta time zone
            ZonedDateTime jakartaDateTime1 = contact1.getCreate_at().atZone(jakartaZone);
            ZonedDateTime jakartaDateTime2 = LocalDateTime.now().atZone(jakartaZone);

            // Calculate the difference between the two ZonedDateTime objects
            long minutesDifference = ChronoUnit.MINUTES.between(jakartaDateTime1, jakartaDateTime2);
            if (minutesDifference > 15) {
                response.init(contact1.getCreate_at().toLocalDate(), "inactive", "success", null);
            }else {
                response.init(contact1.getCreate_at().toLocalDate(), "active", "success", null);
            }

            return Response.status(Response.Status.OK).entity(response).build();
        } catch (Exception e) {
            e.printStackTrace(); // Log or handle the exception properly
            response.init(LocalDate.now(), "", "failure", null);
            return Response.status(Response.Status.OK).entity(response).build();
        }
    }
}




