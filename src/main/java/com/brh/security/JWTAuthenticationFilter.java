package com.brh.security;

import com.brh.WebRTCEndpoint;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.Priority;
import jakarta.ws.rs.Priorities;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.Provider;
import jakarta.ws.rs.core.SecurityContext;

import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.security.Principal;
import java.util.logging.Logger;

@Provider
@Priority(Priorities.AUTHENTICATION)
public class JWTAuthenticationFilter implements ContainerRequestFilter {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final Logger logger = Logger.getLogger(WebRTCEndpoint.class.getName());

    @Override
    public void filter(ContainerRequestContext requestContext) throws IOException {
        String path = requestContext.getUriInfo().getPath();
        logger.info("This is an informational message." + path);
        if (path.equals("/echoes/signin") || path.equals("/admin/signin") || path.equals("/echoes/servicelist") || path.equals("/echoes/signup") || path.equals("/echoes/contactus") || path.equals("/user/referral-code") || path.equals("/echoes/affiliatesignin") || path.equals("/echoes/affiliatesignup") || path.equals("/user/validate-invite-token")) {
            logger.info("This is an informational messa1111ge." + path);
            return; // Skip filtering, proceed without authentication
        }
        String authorizationHeader = requestContext.getHeaderString(AUTHORIZATION_HEADER);
        logger.info("This is an informational messa2222ge." + path);
        if (authorizationHeader == null || !authorizationHeader.startsWith(BEARER_PREFIX)) {
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
            return;
        }
        logger.info("This is an informational messa3333ge." + path);
        String token = authorizationHeader.substring(BEARER_PREFIX.length());
        try {
            Claims claims = validateToken(token);
            logger.info("This is an informational messa4444ge." + path);
            setSecurityContext(requestContext, claims);
        } catch (Exception e) {
            logger.info("This is an informational messa5555ge." + e.toString());
            requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED).build());
        }
    }

    private Claims validateToken(String token) {
        String secretString = "YourSecretKeyForJWTSigning123123123123123123";
        byte[] secretBytes = secretString.getBytes(StandardCharsets.UTF_8);
        Key signingKey = new SecretKeySpec(secretBytes, SignatureAlgorithm.HS256.getJcaName());
        return Jwts.parser()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

    private void setSecurityContext(ContainerRequestContext requestContext, Claims claims) {
        SecurityContext originalContext = requestContext.getSecurityContext();
        requestContext.setSecurityContext(new SecurityContext() {
            @Override
            public Principal getUserPrincipal() {
                return () -> claims.getSubject();
            }

            @Override
            public boolean isUserInRole(String role) {
                // This should be adjusted based on how roles are stored in your JWT claims
                return claims.get("roles", String.class).contains(role);
            }

            @Override
            public boolean isSecure() {
                return originalContext.isSecure();
            }

            @Override
            public String getAuthenticationScheme() {
                return SecurityContext.BASIC_AUTH;
            }
        });
    }
}

