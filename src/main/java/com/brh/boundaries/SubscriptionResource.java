package com.brh.boundaries;

import com.brh.WebRTCEndpoint;
import com.brh.boundaries.reponses.SubscriptionResponse;
import com.brh.controllers.cores.UserManager;
import com.brh.entities.TransactionEntity;
import com.brh.entities.UserEntity;
import jakarta.ejb.EJB;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import net.authorize.Environment;
import net.authorize.api.contract.v1.*;
import net.authorize.api.controller.*;
import net.authorize.api.controller.base.ApiOperationBase;

import jakarta.ejb.Stateless;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Date;
import java.util.logging.Logger;

import static java.math.BigDecimal.valueOf;


@Stateless
@Path("/payment")
public class SubscriptionResource {

//    private static final String API_LOGIN_ID = "5K3jg6F7";
//    private static final String TRANSACTION_KEY = "864JCkjJ2a4D7azy";
//    private static final String ENDPOINT_URL = "https://apitest.authorize.net/xml/v1/request.api";

    // private static final String API_LOGIN_ID = "bizdev05";    //5X77gGeD52ne6dcU
    private static final String API_LOGIN_ID = "3gn78sWXS";
    private static final String TRANSACTION_KEY = "5X77gGeD52ne6dcU";
    private static final String ENDPOINT_URL = "https://apitest.authorize.net/xml/v1/request.api";
    private static final Logger logger = Logger.getLogger(WebRTCEndpoint.class.getName());
    @EJB
    private UserManager userManager;

    @PersistenceContext
    EntityManager entityManager;
    @POST
    @Path("/createSubscription")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional
  /*
    public Response createSubscription(SubscriptionRequest request) {

        System.out.println("Hello, subscription!");

        ApiOperationBase.setEnvironment(Environment.SANDBOX); // Use PRODUCTION when going live

        MerchantAuthenticationType merchantAuth = new MerchantAuthenticationType();
        merchantAuth.setName(API_LOGIN_ID);
        merchantAuth.setTransactionKey(TRANSACTION_KEY);
        ApiOperationBase.setMerchantAuthentication(merchantAuth);

        // Set up payment schedule
        PaymentScheduleType schedule = new PaymentScheduleType();
        PaymentScheduleType.Interval interval = new PaymentScheduleType.Interval();
        interval.setLength((short) request.getIntervalLength());
        interval.setUnit(ARBSubscriptionUnitEnum.valueOf(request.getIntervalUnit()));

        schedule.setInterval(interval);
        try {
            GregorianCalendar startDateCal = new GregorianCalendar();
            startDateCal.setTime(request.getStartDate());
            XMLGregorianCalendar startDate = DatatypeFactory.newInstance().newXMLGregorianCalendar(startDateCal);
            schedule.setStartDate(startDate);
            logger.info("This is CREATE SUBSCRIPTION TEST4"+ startDate.toString());
        } catch (DatatypeConfigurationException e) {
            e.printStackTrace();
            return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Error setting subscription start date.").build();
        }
        schedule.setTotalOccurrences((short) request.getTotalOccurrences());
        schedule.setTrialOccurrences((short) request.getTrialOccurrences());

        // Set up subscription
        ARBSubscriptionType subscription = new ARBSubscriptionType();
        subscription.setPaymentSchedule(schedule);
        subscription.setAmount(valueOf(request.getAmount()));
        subscription.setTrialAmount(BigDecimal.ZERO);

        PaymentType payment = new PaymentType();
        OpaqueDataType opaqueData = new OpaqueDataType();


        opaqueData.setDataDescriptor(request.getDataDescriptor());
        opaqueData.setDataValue(request.getDataValue());


        System.out.println("Hello, world!" + opaqueData);

        payment.setOpaqueData(opaqueData);
        subscription.setPayment(payment);

        ARBCreateSubscriptionRequest apiRequest = new ARBCreateSubscriptionRequest();
        apiRequest.setSubscription(subscription);
        logger.info("This is CREATE SUBSCRIPTION TEST1");
        ARBCreateSubscriptionController controller = new ARBCreateSubscriptionController(apiRequest);
        logger.info("This is CREATE SUBSCRIPTION TEST2");
        controller.execute();
        logger.info("This is CREATE SUBSCRIPTION TEST3");
        ARBCreateSubscriptionResponse response = controller.getApiResponse();
        if (response != null && response.getMessages().getResultCode() == MessageTypeEnum.OK) {
            LocalDate today = LocalDate.now();
            LocalDate endDate;
            if (request.getIntervalUnit().equals("MONTHS")) {
                endDate = today.plusMonths(request.getIntervalLength());
            }else {
                endDate = today.plusDays(request.getIntervalLength());
            }


            TransactionEntity tEntity = new TransactionEntity();
            tEntity.setEmail(request.getEmail());
            tEntity.setInterLength(request.getIntervalLength());
            tEntity.setInterUnit(request.getIntervalUnit());
            tEntity.setPrice(request.getAmount());
            tEntity.setTotalOccurences(request.getTotalOccurrences());
            tEntity.setTrialOccurences(request.getTrialOccurrences());
            tEntity.setEnd_date(endDate);
            tEntity.setStart_date(LocalDate.now());
            tEntity.setCard_number(request.getCard_number());
            tEntity.setCard_cvv(request.getCard_number());
            entityManager.persist(tEntity);
            UserEntity entiy = userManager.findByUseremail4entity(request.getEmail());
            entiy.setStatus("acive");
            entiy.setEnd_date(endDate);
            entityManager.merge(entiy);
            return Response.ok("Subscription ID: " + response.getSubscriptionId()).build();
        } else {

            logger.info("This is CREATE SUBSCRIPTION TEST5");
            return Response.status(Response.Status.BAD_REQUEST).entity("Failed to create subscription").build();
        }
    }
*/
    public Response createSubscription(SubscriptionRequest request) {

        System.out.println("Hello, subscription!");

        ApiOperationBase.setEnvironment(Environment.SANDBOX); // Use PRODUCTION when going live

        MerchantAuthenticationType merchantAuth = new MerchantAuthenticationType();
        merchantAuth.setName(API_LOGIN_ID);
        merchantAuth.setTransactionKey(TRANSACTION_KEY);

        ApiOperationBase.setMerchantAuthentication(merchantAuth);

        PaymentType paymentType = new PaymentType();
        OpaqueDataType opaqueData = new OpaqueDataType();
        opaqueData.setDataDescriptor(request.getDataDescriptor());
        opaqueData.setDataValue(request.getDataValue());

        System.out.println("Hello, request!" + request.getDataDescriptor());
        System.out.println("Hello, request!" + request.getDataValue());
        System.out.println("Hello, opaque!" + opaqueData.getDataDescriptor());
        paymentType.setOpaqueData(opaqueData);

        // Create the payment transaction request
        TransactionRequestType txnRequest = new TransactionRequestType();
        txnRequest.setTransactionType(TransactionTypeEnum.AUTH_CAPTURE_TRANSACTION.value());
        txnRequest.setPayment(paymentType);
        txnRequest.setAmount(new BigDecimal(request.amount).setScale(2, RoundingMode.CEILING));


        // Make the API Request
        CreateTransactionRequest apiRequest = new CreateTransactionRequest();
        apiRequest.setTransactionRequest(txnRequest);
        CreateTransactionController controller = new CreateTransactionController(apiRequest);
        controller.execute();

        CreateTransactionResponse response = controller.getApiResponse();

        if (response!=null) {
            // If API Response is ok, go ahead and check the transaction response
            if (response.getMessages().getResultCode() == MessageTypeEnum.OK) {
                TransactionResponse result = response.getTransactionResponse();
                if (result.getMessages() != null) {
                    UserEntity entiy = userManager.findByUseremail4entity(request.getEmail());
                    LocalDate today = entiy.getEnd_date();
                    LocalDate endDate;
                    if (request.getIntervalUnit().equals("MONTHS")) {
                        endDate = today.plusMonths(request.getIntervalLength());
                    }else {
                        endDate = today.plusDays(request.getIntervalLength());
                    }
                    System.out.println("Successfully created transaction with Transaction ID: " + result.getTransId());
                    System.out.println("Response Code: " + result.getResponseCode());
                    System.out.println("Message Code: " + result.getMessages().getMessage().get(0).getCode());
                    System.out.println("Description: " + result.getMessages().getMessage().get(0).getDescription());
                    System.out.println("Auth Code: " + result.getAuthCode());
                    TransactionEntity tEntity = new TransactionEntity();
                    tEntity.setEmail(request.getEmail());
                    tEntity.setInterLength(request.getIntervalLength());
                    tEntity.setInterUnit(request.getIntervalUnit());
                    tEntity.setPrice(request.getAmount());
                    tEntity.setTotalOccurences(request.getTotalOccurrences());
                    tEntity.setTrialOccurences(request.getTrialOccurrences());
                    tEntity.setEnd_date(endDate);
                    tEntity.setStart_date(entiy.getEnd_date());
                    tEntity.setCard_number(request.getCard_number());
                    tEntity.setCard_cvv(request.getCard_cvv());
                    entityManager.persist(tEntity);
                    entiy.setStatus("active");
                    entiy.setEnd_date(endDate);
                    entityManager.merge(entiy);
                    SubscriptionResponse response1 = new SubscriptionResponse();
                    response1.setStatus("success");
                    return Response.status(Response.Status.OK).entity(response1).build();
                }
                else {
                    System.out.println("Failed Transaction.");
                    if (response.getTransactionResponse().getErrors() != null) {
                        System.out.println("Error Code: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
                        System.out.println("Error message: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
                    }
                }
            }
            else {
                System.out.println("Failed Transaction.");
                if (response.getTransactionResponse() != null && response.getTransactionResponse().getErrors() != null) {
                    System.out.println("Error Code: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorCode());
                    System.out.println("Error message: " + response.getTransactionResponse().getErrors().getError().get(0).getErrorText());
                }
                else {
                    System.out.println("Error Code: " + response.getMessages().getMessage().get(0).getCode());
                    System.out.println("Error message: " + response.getMessages().getMessage().get(0).getText());
                }
            }
        }
        else {
            System.out.println("Null Response.");
        }
        SubscriptionResponse response1 = new SubscriptionResponse();
        response1.setStatus("failure");
        return Response.status(Response.Status.BAD_REQUEST).entity(response1).build();
    }
    // Define a simple POJO for the request payload
    public static class SubscriptionRequest {
        private String dataDescriptor;
        private String dataValue;
        private double amount;
        private int intervalLength;
        private String intervalUnit;
        private Date startDate;
        private int totalOccurrences;
        private int trialOccurrences;

        public String getCard_number() {
            return card_number;
        }

        public void setCard_number(String card_number) {
            this.card_number = card_number;
        }

        public String getCard_cvv() {
            return card_cvv;
        }

        public void setCard_cvv(String card_cvv) {
            this.card_cvv = card_cvv;
        }

        private String card_number;
        private String card_cvv;

        public String getEmail() {
            return email;
        }

        public void setEmail(String email) {
            this.email = email;
        }

        private String email;

        public String getDataDescriptor() {
            return dataDescriptor;
        }

        public void setDataDescriptor(String dataDescriptor) {
            this.dataDescriptor = dataDescriptor;
        }

        public String getDataValue() {
            return dataValue;
        }

        public void setDataValue(String dataValue) {
            this.dataValue = dataValue;
        }

        public double getAmount() {
            return amount;
        }

        public void setAmount(double amount) {
            this.amount = amount;
        }

        public int getIntervalLength() {
            return intervalLength;
        }

        public void setIntervalLength(int intervalLength) {
            this.intervalLength = intervalLength;
        }

        public String getIntervalUnit() {
            return intervalUnit;
        }

        public void setIntervalUnit(String intervalUnit) {
            this.intervalUnit = intervalUnit;
        }

        public Date getStartDate() {
            return startDate;
        }

        public void setStartDate(Date startDate) {
            this.startDate = startDate;
        }

        public int getTotalOccurrences() {
            return totalOccurrences;
        }

        public void setTotalOccurrences(int totalOccurrences) {
            this.totalOccurrences = totalOccurrences;
        }

        public int getTrialOccurrences() {
            return trialOccurrences;
        }

        public void setTrialOccurrences(int trialOccurrences) {
            this.trialOccurrences = trialOccurrences;
        }
    }
}
