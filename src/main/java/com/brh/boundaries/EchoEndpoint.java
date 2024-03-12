package com.brh.boundaries;

import com.brh.EmailService;
import com.brh.WebRTCEndpoint;
import com.brh.boundaries.reponses.ServiceResponse;
import com.brh.boundaries.reponses.SignInResponse;
import com.brh.controllers.cores.AffiliateUserManager;
import com.brh.controllers.cores.InviteTokenManager;
import com.brh.controllers.cores.ServiceManager;
import com.brh.controllers.cores.UserManager;
import com.brh.entities.UserEntity;
import com.brh.entities.cores.AffiliateUser;
import com.brh.entities.cores.Contact;
import com.brh.entities.cores.Service;
import com.brh.entities.cores.User;
import jakarta.ejb.EJB;
import jakarta.ejb.Local;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.LocalDate;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.xml.bind.DatatypeConverter;

import javax.crypto.spec.SecretKeySpec;
import java.util.Properties;
import javax.mail.Session;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import javax.mail.Transport;
import com.fasterxml.jackson.databind.ObjectMapper;


@Path("/echoes")
public class EchoEndpoint {
	@GET
	@Path("/{message}")
	public Response echo(@PathParam("message") String message) {
		return Response.ok().entity("This is your message   : " + message).build();
	}
	@EJB
	private UserManager userManager;
	@EJB
	private ServiceManager serviceManager;
	@EJB
	private AffiliateUserManager affiliateUserManager;
    @EJB
    private InviteTokenManager inviteTokenManager;
	private static final Logger logger = Logger.getLogger(WebRTCEndpoint.class.getName());
	@PersistenceContext
	EntityManager entityManager;
    @Inject
    ObjectMapper objectMapper;

	@POST
	@Path("/signup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional

	public Response signup( User userData ) {
		String forename = userData.getForename();
		String surname = userData.getSurname();
		String username = userData.getUsername();
		String email = userData.getEmail();
		String password = userData.getPassword();

		String refer_code = userData.getRefer_code();
		ReferralCodeManager rcm = new ReferralCodeManager();
		int user_id = rcm.extractUserIdFromReferralCode(refer_code);

		System.out.println("user_id from " + user_id);
		try {

			User user = userManager.findByUsername(email);

			if (user != null) {
				SignInResponse data = new SignInResponse();

				data.init("failed", "User already exists!", null);
				return Response.status(Response.Status.OK).entity(data).build();
			}else {
                boolean isInviteUser = false;
                Contact inviteRow = inviteTokenManager.findContactByToken(userData.getInviteToken());
                if (inviteRow != null && inviteRow.getEmail().equals(userData.getEmail()))
                    isInviteUser = true;
                LocalDate nowTime = LocalDate.now();
				UserEntity userEntity = new UserEntity();
				userEntity.setForename(forename);
				userEntity.setSurname(surname);
				userEntity.setUsername(username);
				userEntity.setEmail(email);
				userEntity.setPassword(password);
				userEntity.setReferr(user_id);
				userEntity.setStatus(isInviteUser? "active": "inactive");
                userEntity.setnewPassword(password);
				userEntity.setcurPassword(password);
				userEntity.setJoin_date(nowTime);
				userEntity.setEnd_date(nowTime.plusMonths(isInviteUser? 1: 0));
                userEntity.setInviteToken(userData.getInviteToken());
				entityManager.persist(userEntity);
				String secretString = "YourSecretKeyForJWTSigning123123123123123123";
				byte[] secretBytes = secretString.getBytes(StandardCharsets.UTF_8);
				Key signingKey = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());

				String token = Jwts.builder()
						.setSubject(email)
						.setIssuedAt(new Date())
						.setExpiration(new Date(System.currentTimeMillis() + 3600000)) // 1 hour expiration
						.signWith(signingKey)
						.compact();
				SignInResponse data = new SignInResponse();
				data.init("success", token, userManager.findByUseremail(email));
				String json = null;
				try {
					json = objectMapper.writeValueAsString(data);
				} catch (Exception e) {
					e.printStackTrace();
					return Response.serverError().build();
				}

				return Response.ok(json, MediaType.APPLICATION_JSON).build();
//				return Response.status(Response.Status.OK).entity(data).build();
			}
		}catch (Exception e){
			e.printStackTrace(); // Log or handle the exception properly
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create user").build();
		}
	}

	@POST
	@Path("/signin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional

	public Response signin( User userData ) {
		String useremail = userData.getUsername();
		String password = userData.getPassword();
		try {

			UserEntity user = userManager.authenticate(useremail, password, 0);
            LocalDate currentDate = LocalDate.now();
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
				data.init("success", token, userManager.findByUseremail(useremail));

                if (user.getEnd_date() != null && currentDate.isBefore(user.getEnd_date())) {
                    data.setUser_status("active");
                    logger.info("active part.");
                }
                else {
                    data.setUser_status("inactive");
                    user.setStatus("inactive");
                    entityManager.merge(user);
                }
				String json = null;
				try {
					json = objectMapper.writeValueAsString(data);
				} catch (Exception e) {
					e.printStackTrace();
					return Response.serverError().build();
				}

				return Response.ok(json, MediaType.APPLICATION_JSON).build();
//				return Response.status(Response.Status.OK).entity(data).build();
			}else {
				logger.info("This is an informational no user part.");
				SignInResponse data = new SignInResponse();
				data.init("failed", "Unknown username or wrong password!", null);
				return Response.status(Response.Status.OK).entity(data).build();
			}
		}catch (Exception e){
			logger.info("This is an informational error part.");
			e.printStackTrace(); // Log or handle the exception properly
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to sign in").build();
		}
	}

	@POST
	@Path("/myaccount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional

	public Response myaccount( User userData ) {
		String email = userData.getEmail();

		try {

			User user = userManager.findByUseremail(email);

			if (user != null) {
				SignInResponse data = new SignInResponse();
				data.init("success", "", user);
                String json = null;
                try {
                    json = objectMapper.writeValueAsString(data);
                } catch (Exception e) {
                    e.printStackTrace();
                    return Response.serverError().build();
                }

                return Response.ok(json, MediaType.APPLICATION_JSON).build();
//				return Response.status(Response.Status.OK).entity(data).build();
			}else {
				SignInResponse data = new SignInResponse();
				data.init("failed", "unknown email address!", null);
				return Response.status(Response.Status.OK).entity(data).build();
			}
		}catch (Exception e){
			e.printStackTrace(); // Log or handle the exception properly
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to sign in").build();
		}
	}

	@POST
	@Path("/validateaccount")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional

	public Response validateaccount( User userData ) {
		String email = userData.getEmail();
		String forename = userData.getForename();
		String surname = userData.getSurname();

		try {

			UserEntity entiy = userManager.findByUseremail4entity(email);

			if (entiy != null) {
				SignInResponse data = new SignInResponse();
				entiy.setForename(forename);
				entiy.setSurname(surname);
				entiy.setUsername(surname);
				entityManager.merge(entiy);
				User user = userManager.findByUseremail(email);
				data.init("success", "Successfully changed.", user);
				String json = null;
				try {
					json = objectMapper.writeValueAsString(data);
				} catch (Exception e) {
					e.printStackTrace();
					return Response.serverError().build();
				}

				return Response.ok(json, MediaType.APPLICATION_JSON).build();
//				return Response.status(Response.Status.OK).entity(data).build();
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
	@Path("/changepassword")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional

	public Response changepassword( User params) {
		String curpassword = params.getCurpassword();
		String newpassword = params.getNewpassword();
		String email = params.getEmail();
		System.out.println("curpassword>>>>>>>>>>>"+ curpassword + "<<email>>>" + email);

		try {
			SignInResponse data = new SignInResponse();
			UserEntity entiy = userManager.findByUseremail4entity(email);
			if (entiy != null) {
				System.out.println("entity password>>>>>>>>>>>"+ entiy.getPassword());
				if(entiy.getPassword().equals(curpassword)){
					entiy.setPassword(newpassword);
					entityManager.merge(entiy);
					User user = userManager.findByUseremail(email);
					data.init("success", "Your password has been successfully changed.", user);
					String json = null;
					try {
						json = objectMapper.writeValueAsString(data);
					} catch (Exception e) {
						e.printStackTrace();
						return Response.serverError().build();
					}

					return Response.ok(json, MediaType.APPLICATION_JSON).build();
//					return Response.status(Response.Status.OK).entity(data).build();
				}else{
					data.init("success", "Wrong password!", null);
					return Response.status(Response.Status.OK).entity(data).build();
				}
			}else {
				data.init("failed", "Unknown email address!", null);
				return Response.status(Response.Status.OK).entity(data).build();
			}
		}catch (Exception e){
			e.printStackTrace(); // Log or handle the exception properly
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to sign in").build();
		}
	}

    @POST
    @Path("/servicelist")
    @Consumes(MediaType.APPLICATION_JSON)
    @Produces(MediaType.APPLICATION_JSON)
    @Transactional

    public Response servicelist( ) {

        try {
            ServiceResponse data = new ServiceResponse();
            List<Service> list = serviceManager.findAllServices();
            if (list != null) {
				data.init("success", "Successfully fetched.", list);
				return Response.status(Response.Status.OK).entity(data).build();
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
	@Path("/contactus")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional

	public Response contactus( Contact contact ) {
		String firsname = contact.getFirstname();
		String lastname = contact.getLastname();
		String email = contact.getEmail();
		String message = contact.getMessage();

		try {
			SignInResponse data = new SignInResponse();
			EmailService emailService = new EmailService();
			emailService.sendEmail(email, "Dear " + firsname + " " + lastname, message);
			data.init("success", "Successfully submited.", null);
			return Response.status(Response.Status.OK).entity(data).build();
		}catch (Exception e){
			e.printStackTrace(); // Log or handle the exception properly
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to api call").build();
		}
	}

	public String getInviteCode(AffiliateUser user) {
		return "generate invite code";
	}

	@POST
	@Path("/affiliatesignup")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional

	public Response affiliatesignup( AffiliateUser userData ) {
		String firstname = userData.getFirstname();
		String lastname = userData.getLastname();
		String email = userData.getEmail();
		String password = userData.getPassword();
		try {

			AffiliateUser user = affiliateUserManager.findByUseremail(email);

			if (user != null) {
				SignInResponse data = new SignInResponse();
				data.init("failed", "User alreay exists!", null);
				return Response.status(Response.Status.OK).entity(data).build();
			}else {
				AffiliateUser nuser = new AffiliateUser();
				nuser.setFirstname(firstname);
				nuser.setLastname(lastname);
				nuser.setEmail(email);
				nuser.setPassword(password);
				nuser.setStatus("active");
				nuser.setJoin_date(LocalDate.now());
				entityManager.persist(nuser);
				SignInResponse data = new SignInResponse();
				data.init("success", "User created successfully", null);
				return Response.status(Response.Status.OK).entity(data).build();
			}
		}catch (Exception e){
			e.printStackTrace(); // Log or handle the exception properly
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to create user").build();
		}
	}

	@POST
	@Path("/affiliatesignin")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	@Transactional

	public Response affiliatesignin( AffiliateUser userData ) {
		String email = userData.getEmail();
		String password = userData.getPassword();

		try {

			AffiliateUser user = affiliateUserManager.authenticate(email, password);

			if (user != null) {
				SignInResponse data = new SignInResponse();
				data.init("success", "Successfully signed in!", null);
				return Response.status(Response.Status.OK).entity(data).build();
			}else {
				SignInResponse data = new SignInResponse();
				data.init("failed", "unknown username or wrong password!", null);
				return Response.status(Response.Status.OK).entity(data).build();
			}
		}catch (Exception e){
			e.printStackTrace(); // Log or handle the exception properly
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).entity("Failed to sign in").build();
		}
	}

	public String createToken(String username, List<String> roles) {
		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary("YourSecretKey");

		return Jwts.builder()
				.setSubject(username)
				.claim("roles", roles)
				.setIssuedAt(now)
				.setExpiration(new Date(nowMillis + 3600000)) // 1 hour expiration
				.signWith(SignatureAlgorithm.HS256, apiKeySecretBytes)
				.compact();
	}
}