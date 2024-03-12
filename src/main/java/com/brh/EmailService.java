package com.brh;
import jakarta.annotation.Resource;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.Message;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
import java.util.Properties;
public class EmailService {

    public void sendEmail(String to, String subject, String content) throws MessagingException {
        // Mailtrap SMTP server properties
        Properties props = new Properties();
        props.put("mail.smtp.host", "sandbox.smtp.mailtrap.io"); // Replace with Mailtrap's hostname
        props.put("mail.smtp.port", "2525"); // Replace with Mailtrap's port
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // Use TLS

        // Mailtrap's SMTP server credentials
        final String username = "d7621784103f74"; // Replace with your Mailtrap username
        final String password = "1554b67966468c"; // Replace with your Mailtrap password

        // Create a session with authenticator
        Session session = Session.getInstance(props, new jakarta.mail.Authenticator() {
            protected jakarta.mail.PasswordAuthentication getPasswordAuthentication() {
                return new jakarta.mail.PasswordAuthentication(username, password);
            }
        });

        // Create and send the email
        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress("globalgrahicsjpstar@gmail.com")); // Sender's email address
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
        message.setSubject(subject);
        message.setText(content);

        Transport.send(message);
    }
}
