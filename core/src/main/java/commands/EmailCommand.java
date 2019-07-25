package commands;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import commands.exception.CommandException;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Properties;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
public class EmailCommand implements Command{
    private Logger logger = LogManager.getLogger(EmailCommand.class);
    @Override
    public void execute(HttpServletRequest request, HttpServletResponse response) {
        ObjectMapper mapper = new ObjectMapper();
        JsonNode j = null;
        try {
            j = mapper.readTree(request.getReader());
        } catch (IOException e) {
            throw new CommandException("Error while parsing email", e);
        }
        String[] addresses = j.get("address").asText().split(",");
        String subject = j.get("subject").asText();
        String text = j.get("text").asText();

        Properties properties = new Properties();
        try {
            properties.load(EmailCommand.class.getResourceAsStream("/email.properties"));
            String sender = properties.getProperty("mail.user.name");

            Session session = Session.getDefaultInstance(properties,
                    new javax.mail.Authenticator() {
                        protected PasswordAuthentication getPasswordAuthentication() {
                            return new PasswordAuthentication(sender, properties.getProperty("mail.user.password"));
                        }
                    });

            sendEmail(addresses, subject, text, properties, sender, session);
        } catch (IOException e) {
            throw new CommandException("Error while sending email", e);
        }

    }
    private static void sendEmail(String[] addresses, String subject, String text, Properties properties, String sender, Session session) throws UnsupportedEncodingException {
        try {
            for(String address: addresses){
                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender, properties.getProperty("ADMIN-NAME")));
                message.addRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(address));
                message.setSubject(subject);
                message.setText(text);

                Transport.send(message);
            }

        } catch (MessagingException  e) {
            throw new RuntimeException(e);
        }
    }

}
