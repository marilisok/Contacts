package util;

import Service.ContactService;
import Service.ServiceFactory;
import model.Contact;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public enum CheckBirthdayUtil {
    INSTANCE;

    public static CheckBirthdayUtil getInstance() {
        return  INSTANCE;
    }
    private ContactService contactService = ServiceFactory.getContactService();
    private final String ADDRESS = "mari.lisok@mail.ru";
    private final String SUBJECT = "Birthday contacts";

    private final ScheduledExecutorService scheduler =
            Executors.newScheduledThreadPool(1);

    public void startService() {
        scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                try {
                    sendEmailToAdmin();
                } catch (Exception e) {
                   e.printStackTrace();
                }
            }
        }, 0, 1, TimeUnit.DAYS);
    }

    public void stopService() {
        scheduler.shutdown();
    }

    private void sendEmailToAdmin() {
        String text = makeMessage();

        Properties properties = new Properties();
        try {
            properties.load(CheckBirthdayUtil.class.getResourceAsStream("/email.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        String sender = properties.getProperty("mail.user.name");

        Session session = Session.getDefaultInstance(properties,
                new javax.mail.Authenticator() {
                    protected PasswordAuthentication getPasswordAuthentication() {
                        return new PasswordAuthentication(sender, properties.getProperty("mail.user.password"));
                    }
                });

        try {
            sendEmail(ADDRESS, SUBJECT, text, properties, sender, session);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("Error while sending email about birthday",e);
        }

    }
    private static void sendEmail(String addresses, String subject, String text, Properties properties, String sender, Session session) throws UnsupportedEncodingException {
        try {

                Message message = new MimeMessage(session);
                message.setFrom(new InternetAddress(sender, properties.getProperty("ADMIN-NAME")));
                message.addRecipients(Message.RecipientType.BCC,
                        InternetAddress.parse(addresses));
                message.setSubject(subject);
                message.setText(text);

                Transport.send(message);


        } catch (MessagingException  e) {
            throw new RuntimeException(e);
        }
    }
    private List<Contact> whoHave() {
        List<Contact> contacts = contactService.getBirthdayContacts();
        List<Contact> birtdayContacts = new ArrayList<>();
        for(Contact contact: contacts) {
            if ( contact.getBirthday() != null && isBirthday(contact)) {
                birtdayContacts.add(contact);
            }
        }
        return birtdayContacts;
    }

    private boolean isBirthday(Contact contact) {
        Date birthday = new Date(contact.getBirthday().getTime());
        Calendar cal = Calendar.getInstance();
        int nowMonth = cal.get(Calendar.MONTH);
        int nowDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        cal.setTime(birthday);
        int birthMonth = cal.get(Calendar.MONTH);
        int birthDayOfMonth = cal.get(Calendar.DAY_OF_MONTH);
        return (nowDayOfMonth == birthDayOfMonth) && (nowMonth == birthMonth);
    }

    private String makeMessage() {
        List<Contact> contacts = whoHave();
        if(contacts.size() == 0) {
            return "Nobody has birthday today.";
        }
        String message = "Birthday contacts:\n";
        int count = 0;
        for (Contact contact: contacts) {
            count ++;
            message  += count + ". " + contact.getFirstName()+"_"+contact.getLastName() + "\n";
        }
        return  message;
    }
}
