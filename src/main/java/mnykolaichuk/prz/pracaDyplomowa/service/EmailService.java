package mnykolaichuk.prz.pracaDyplomowa.service;

import mnykolaichuk.prz.pracaDyplomowa.model.email.AbstractEmailContext;

import javax.mail.MessagingException;

public interface EmailService {
    /**
     * Email jest wysyłany na podstawie danych zamieszczonych w objekcie dzidziczącym od AbstractEmailContext.
     * Rzuca wyjątek {@code MessagingException} jeżeli pismo nie zostało prawidłowo wysłane.
     *
     * @param email objekt zawierający dane pisma
     * @throws MessagingException
     */
    void sendMail(final AbstractEmailContext email) throws MessagingException;

}
