package mnykolaichuk.prz.pracaDyplomowa.service;

import mnykolaichuk.prz.pracaDyplomowa.exception.EmailAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.exception.UserAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Customer;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerDetail;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CustomerData;

import java.util.List;

/**
 * Klient przedstawiony w bazie danych za pomocą dwóch tabeli Customer oraz CustomerDetail.
 * Customer przechowuje username oraz hasło
 * CustomerDetail przechowuje dane klienta oraz flagę weryfikacji
 * Klient ma możliwość zmiany danych osobistych
 * Jeżeli nie zarejestrowany klient tworze zlecenie, na podstawie przez niego wprowadzonych danych jest tworzony oraz zapisany do bazy danych
 * objekt CustomerDetail bez przepisanego Entity objektu Customer
 * Jeżeli niezarejestrowany klient tworzył wcześniej zlecenia jego dane już są w bazie danych. Do jego danych przypisane są tworzone przez niego zlecenia.
 * Przy rejestracji takiego klienta lub tworzeniu jeszcze jednego zlecenia jako nie zarejestrowany klient jego nowe dane zapisanę do bazy danych a po weryfikacji
 * adresy email stare zlecenia zostaną przypisane do nowych danych a stare dane zostaną usunięte z bazy danych.
 */

public interface CustomerService {

    /**
     * Rejestracja klienta na podstawie objektu zawierającego dane, zwróconego z Front End.
     * Zruca wyjątki {@code UsernameAlreadyExistException jeżeli klient albo warsztat z podanum username już istnieje w bazie danych}
     *              {@code EmailAlreadyExistException jeżeli klient z podanym username już istnieje w bazie danych}
     *
     * @param customerData
     * @throws UserAlreadyExistException
     * @throws EmailAlreadyExistException
     */
    void register(CustomerData customerData)
            throws UserAlreadyExistException, EmailAlreadyExistException;

    /**
     * Zmiana danych klienta na podstawie objektu zawierającego nowe dane po akceptacji zmian, zwróconego z Front End, username oraz email adresy przed zmianą.
     * Po zmianie username następuje automatyczne wylogowanie.
     * Po zmianie email adresy następuje automatyczne wylogowanie i klient będzie mógł się zalogować dopiero po weryfikacji email adresy.
     * Zruca wyjątki {@code UsernameAlreadyExistException jeżeli, przy próbie akceptacji zmian, klient albo warsztat z podanum username już istnieje w bazie danych}
     *               {@code EmailAlreadyExistException jeżeli, przy próbie akceptacji zmian, klient z podanym username już istnieje w bazie danych}
     *
     * @param customerData objekt zawierający nowe dane klienta
     * @param oldUsername username klienta przed rozpoczęciem aktualizacji danych
     * @param oldEmail email klienta przed rozpoczęciem aktualizacji danych
     * @throws UserAlreadyExistException
     * @throws EmailAlreadyExistException
     */
    void update(CustomerData customerData, String oldUsername, String oldEmail)
            throws UserAlreadyExistException, EmailAlreadyExistException;

    /**
     * Zwraca objekt służący do wyslania danych klienta z bazy danych na Front End.
     *
     * @param username Customer username
     * @return objekt zawierający dane klienta które są połączeniem objektów CustomerDetail oraz Customer
     */
    CustomerData getCustomerDataByUsername(String username);

    List<CustomerData> getAllCustomerDataList();

    /**
     * Usunięcie konta klienta.
     * Jeżeli samochód klienta jest przypisany do innych klientów nie będzie usunięty z bazy danych.
     * Jeżeli klient ma zlecenia w stanie CREATED lub WORKSHOP_ANSWER będą oni usunięte z bazy danych.
     * Jeżeli klient ma zlecenia w stanie IMPLEMENTATION nie będą oni usunięte z bazy danych dopóki będzie relacja z warsztatem. Od razu po usunięciu konta
     *  warsztat do którego przypisane zlecenie o stanie IMPLEMENTATION dostanie pismo email z informacją że klient usunął konto oraz dane kontaktowe klienta
     * Jeżeli klient ma zlecenie w stanie UNREGISTERED ono będzie usunięte z bazy danych jeżeli nie ma przypisanych warsztatów.
     *  W przeciwnym przypadku będzie usunięte relację Klient-Zlecenie.
     * Jeżeli klient ma zlecenie w stanie COMPLETED ono będzie usunięte z bazy danych jeżeli nie ma przypisanych warsztatów.
     *  W przeciwnym przypadku będzie usunięte relację Klient-Zlecenie.
     *
     * @param username Customer username
     */
    void deleteByUsername(String username);

    /**
     * Usunięcie samochodu z bazy danych, jeżeli do niego przypisany jeden klient.
     * Usunięcie relacji Klient-Samochód kiedy di samochodu przypisano więcej klientów.
     *
     * @param carId id usuwanego samochodu
     * @param username Customer username
     */
    void deleteCustomerCarByCarIdAndUsername(Integer carId, String username);

    void save(Customer customer);

    /**
     *
     * @param username
     * @return Entity objekt
     */
    Customer findByUsername(String username);

    /**
     *
     * @param id
     * @return Entity objekt
     */
    Customer findById(Integer id);

    void changePasswordByUsername(String username, String password);

    boolean isCarInCustomer(Customer customer);

    boolean isCustomerInCustomerDetail(CustomerDetail customerDetail);









}
