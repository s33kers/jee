package jee.mif.bl.services;

import javax.mail.MessagingException;

/**
 * Created by Tadas.
 */
public interface EmailSender {

    void sendMail(String to, String subject, String body) throws MessagingException;
}
