package mnykolaichuk.prz.pracaDyplomowa.service;

import mnykolaichuk.prz.pracaDyplomowa.exception.InvalidTokenException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CustomerDetailData;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerDetail;
import org.springframework.stereotype.Service;

/**
 * Klient przedstawiony w bazie danych za pomocą dwóch tabelek Customer oraz CustomerDetail.
 * Customer przechowuje username oraz hasło
 * CustomerDetail przechowuje dane klienta oraz flagę weryfikacji
 * Jeżeli nie zarejestrowany klient tworze zlecenie, na podstawie przez niego wprowadzonych danych jest tworzony oraz zapisany do bazy danych
 * objekt CustomerDetail bez przepisanego Entity objektu Customer
 * Jeżeli niezarejestrowany klient tworzył wcześniej zlecenia jego dane już są w bazie danych. Do jego danych przypisane są tworzone przez niego zlecenia.
 * Przy rejestracji takiego klienta lub tworzeniu jeszcze jednego zlecenia jako nie zarejestrowany klient jego nowe dane zapisanę do bazy danych a po weryfikacji
 * adresy email stare zlecenia zostaną przypisane do nowych danych a stare dane zostaną usunięte z bazy danych.
 */

@Service
public interface CustomerDetailService {

    /**
     * Dopóki klient, który tworzył zlecenie jako niezarejestrowany, nie zweryfikuje swój email adres w bazie danych są zapisane dwa objekty zawierające dane klienta
     * z jednakowym adresem email. W takim przypadku będie zwrócony Entity objekt danych stworzonech przy wcieśniejszym wykorzystaniu adresu email.
     *
     * @param email
     * @return Entity objekt
     */
    CustomerDetail findByEmail(String email);

    /**
     * Ponieważ CustomerDetail związany z Entity objektem Customer relacją OneToOne, zwraca Entity objekt CustomerDetail na podstawie pola
     * username przechowanego w tabeli Customer
     *
     * @param username Customer username
     * @return Entity objekt
     */
    CustomerDetail findByCustomerUsername(String username);

    /**
     *
     * @param id
     * @return Entity objekt
     */
    CustomerDetail findById(Integer id);

    /**
     * Usunięcie CustomerDetail z bazy danych.
     *
     * @param customerDetail
     */
    void delete(CustomerDetail customerDetail);

    /**
     * Zapisanie CustomerDetail do bazy danych.
     *
     * @param customerDetail
     */
    void save(CustomerDetail customerDetail);

    /**
     * Zwraca objekt służący do wyslania danych klienta z bazy danych na Front End.
     *
     * @param customerDetail Entity objekt
     * @return objekt zawierający dane klienta które są połączeniem objektów CustomerDetail oraz Customer
     */
    CustomerDetailData getCustomerDetailData(CustomerDetail customerDetail);

    /**
     * Wysylanie pisma w celu weryfikacji adresu e-mail.
     *
     * @param customerDetail
     */
    void sendCustomerDetailEmailVerificationEmail(CustomerDetail customerDetail);

    /**
     * Wysylanie pisma w celu weryfikacji adresu e-mail dla CustomerDetail jeżeli dane klienta jyż są w bazie danych.
     *
     * @param customerDetail
     */
    void sendNotNewCustomerDetailEmailVerificationEmail(CustomerDetail customerDetail);

    /**
     * Metod wywołany przy przechodzeniu po linku weryfikacji adresy email, jeżeli dane klienta wcześniej były w bazie danych, stare dane są usunięte,
     * zlecenia przepisane do nowych danych.
     * Sprawdza czy tokien jest jeszcze aktualny oraz poprawny. Zruca wyjątek {@code InvalidTokenException} leżeli tokien jest nie poprawny.
     *
     * @param token
     * @return true or false
     * @throws InvalidTokenException
     */
    boolean verifyCustomer(String token) throws InvalidTokenException;

    boolean isOrderInCustomerDetail(CustomerDetail customerDetail);
    boolean isCustomerInCustomerDetail(CustomerDetail customerDetail);

}
