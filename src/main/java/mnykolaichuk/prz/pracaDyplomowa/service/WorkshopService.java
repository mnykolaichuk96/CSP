package mnykolaichuk.prz.pracaDyplomowa.service;

import mnykolaichuk.prz.pracaDyplomowa.exception.CantDeleteWorkshopWhileImplementationExistException;
import mnykolaichuk.prz.pracaDyplomowa.exception.EmailAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.exception.InvalidTokenException;
import mnykolaichuk.prz.pracaDyplomowa.exception.UserAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.WorkshopData;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.City;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Workshop;

import java.util.List;

/**
 * Warsztat może zarejestrować swoje konto, które będzie dostępne po veryfikacji email adresy.
 * Warsztat ma możliwość zmieniać dane, username oraz password.
 * Warsztat ma możliwość usunięcia konta jeśli nie ma zleceń w stanie 'IMPLEMENTATION'
 */
public interface WorkshopService {

    /**
     * Rejestracja warsztatu na podstawie objektu zawierającego dane, zwróconego z Front End.
     * Zruca wyjątki {@code UsernameAlreadyExistException jeżeli warsztat albo klient z podanum username już istnieje w bazie danych}
     *               {@code EmailAlreadyExistException jeżeli warsztat z podanym username już istnieje w bazie danych}
     *
     * @param workshopData
     * @throws UserAlreadyExistException
     * @throws EmailAlreadyExistException
     */
    void register(WorkshopData workshopData)
            throws UserAlreadyExistException, EmailAlreadyExistException;

    /**
     * Metod wywołany przy przechodzeniu po linku weryfikacji adresy email. W przypadku poprawnej walidacji konto warsztatu zostanie aktywne.
     * Sprawdza czy tokien jest jeszcze aktualny oraz poprawny. Zruca wyjątek {@code InvalidTokenException} leżeli tokien jest nie poprawny.
     *
     * @param token
     * @return true or false
     * @throws InvalidTokenException
     */
    boolean verifyWorkshop(String token) throws InvalidTokenException;

    /**
     *  Zmiana danych warsztatu na podstawie objektu zawierającego nowe dane po akceptacji zmian, zwróconego z Front End, username oraz email adresy przed zmianą.
     *  Po zmianie username następuje automatyczne wylogowanie.
     *  Po zmianie email adresy następuje automatyczne wylogowanie i warsztat będzie mógł się zalogować dopiero po weryfikacji email adresy.
     *  Zruca wyjątki {@code UsernameAlreadyExistException jeżeli, przy próbie akceptacji zmian, warsztat albo klient z podanum username już istnieje w bazie danych}
     *                {@code EmailAlreadyExistException jeżeli, przy próbie akceptacji zmian, warsztat z podanym username już istnieje w bazie danych}
     *
     * @param workshopData objekt zawierający nowe dane warsztatu
     * @param oldUsername username warsztatu przed rozpoczęciem aktualizacji danych
     * @param oldEmail email warsztatu przed rozpoczęciem aktualizacji danych
     * @throws UserAlreadyExistException
     * @throws EmailAlreadyExistException
     */
    void update(WorkshopData workshopData, String oldUsername, String oldEmail)
            throws UserAlreadyExistException, EmailAlreadyExistException;

    /**
     * Zwraca objekt służący do komunikacji Entity objektu Workshop(warsztat) oraz Front End.
     *
     * @param username Workshop username
     * @return WorkshopData
     */
    WorkshopData getWorkshopDataByUsername(String username);

    List<WorkshopData> getAllWorkshopDataList();

    /**
     * Usunięcie konta warsztatu, jeżeli warsztat ma zlecenia w stanie równym 'IMPLEMENTATION' usunięcie nie możliwe.
     * Rzuca wujątki {@code CantDeleteWorkshopWhileImplementationExistException} jeżeli są zlecenia w stanie 'IMPLEMENTATION'
     *
     * @param username Workshop username
     * @param isAdmin flaga ustalona na true w przypadku wywołania metody administratorem
     * @throws CantDeleteWorkshopWhileImplementationExistException
     */
    void deleteByUsernameAndIsAdmin(String username, Boolean isAdmin) throws CantDeleteWorkshopWhileImplementationExistException;

    void save(Workshop workshop);

    /**
     * Zwraca warsztat na podstawie go username
     *
     * @param username Warsztat username
     * @return Entity objekt
     */
    Workshop findByUsername(String username);

    Workshop findById(Integer id);

    /**
     * Zwraca listę wszystkich warsztatów dostępnych w mieście.
     *
     * @param city Entity objekt miasto
     * @return List Entity objektów
     */
    List<Workshop> findAllByCity(City city);

    List<Workshop> findAllWithUnActiveByCity(City city);

    void changePasswordByUsername(String username, String password);

    boolean isOrderAnswerInWorkshop(Workshop workshop);

}
