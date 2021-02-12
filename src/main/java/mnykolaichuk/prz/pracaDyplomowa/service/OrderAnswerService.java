package mnykolaichuk.prz.pracaDyplomowa.service;

import mnykolaichuk.prz.pracaDyplomowa.exception.CantDeleteWorkshopWhileImplementationExistException;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.OrderAnswer;
import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderAnswerData;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Zlecenie przedstawione w bazie danych za pomocą dwóch tabeli Order oraz OrderAnswer.
 * Order przechowuje głowne dane wspólne dla wszystkich warsztatów. Relacje z CustomerDetail oraz Car.
 * OrderAnswer jest tworzony dla każdego warsztatu inny. Relacje z Warsztat.
 * Zawiera Enum pole 'stan', które przedstawia na jakim etapie jest zlecenie:
 *      'CREATED' - zlecenie stworzone, wszystkie warsztatu wybranego miasta widzą go w liście nowych zleceń oraz otrzymaki wiadomość email,
 *          że zostało stworzone nowe zlecenie. Do wypełnienia przez warsztat dostępne są 2 pola: cena, data realizacji.
 *      'WORKSHOP_ANSWER' - oferta warsztatu, odpowiedż zawierająca cenę oraz date realizacji. Klient otrzymuje wiadomośc email,
 *          zawierającą informację o ofercie warsztatu.
 *      'IMPLEMENTATION' - klient wybrał jedną ofertę. Teraz klient od daje samochód do warsztatu. Warsztat dostaje wiadomość o tym,
 *          że został wybrany do realizacji zlecenia.
 *      'CREATED' - warsztat zrealizował zlecenie. Klient otrzymuje wiadomośc mail, że samochód gotowy do odebrania.
 *      'UNREGISTERED' - nie zarejestrowany klient tworzy wiadomość. Po weryfikacji email adresy, warsztat otrzymuje email z informacją o nowym zleceniu.
 *          Zlecenie będzie dostępny dla warsztatu bez możliwości odpowiedzi.
 *
 * Jeżeli warsztat usuwa zlecenie usuwa się jego kopia OrderAnswer
 */

@Service
public interface OrderAnswerService {

    /**
     * Zwraca objekt służący do wyslania danych które są różne dla każdego warsztatu z bazy danych na Front End.
     * Objekt OrderAnswerData zawiera pola:
     *      cena;
     *      data realizacji;
     *      relacja z warsztatem.
     *
     * @param orderAnswer Entity objekt
     * @return OrderAnswerData
     */
    OrderAnswerData getOrderAnswerData(OrderAnswer orderAnswer);

    /**
     * Zwraca liste objektów OrderAnswerData na podstawie listy Entity objektów OrderAnswer
     *
     * @param orderAnswerList List entity objektów
     * @return List OrderAnswerData
     */
    List<OrderAnswerData> getOrderAnswerDataListByOrderAnswerList(List<OrderAnswer> orderAnswerList);

    /**
     * Tworzony objekt OrderAnswer który jest polączony z Entity objektem Order i razem przedstawiają zlecenie kiedy warsztat odpowiada klientu.
     * Oferuje swoją cenę oraz termin wykonania.
     *
     * @param orderAnswerData objekt służący do wyslania oraz odebrania danych które są różne dla każdego warsztatu
     *                        z bazy danych na Front End oraz z Front End do bazy danych.
     */
    void createWorkshopAnswerByOrderAnswerData(OrderAnswerData orderAnswerData);

    /**
     * Klient wybiera jeden OrderAnswer(oferta od warsztatu na stworzone zlecenie).
     * Warsztat otrzymuje wiadomość email o tym, że został wybrany do realizacji zlecenia
     *
     * @param orderAnswer Entity objekt
     */
    void chooseOrderAnswerForImplementation(OrderAnswer orderAnswer);

    /**
     * Warsztat oznacza zlecenie jako zrealizowane.
     * Klient otrzymuje wiadomość email o tym, że zlecenie zostało zrealizowane i samochód gotowy do odbioru.
     *
     * @param orderAnswer Entity objekt
     */
    void chooseOrderAnswerForCompleted(OrderAnswer orderAnswer);

    /**
     *
     * @param id
     * @return Entity objekt
     */
    OrderAnswer findById(Integer id);

    /**
     *
     * @param username Workshop username
     * @return List Entity objektów
     */
    List<OrderAnswer> findAllByWorkshopUsername(String username);

    /**
     * Zapisuje Entity objekt do bazy danych.
     *
     * @param orderAnswer
     */
    void save(OrderAnswer orderAnswer);

    /**
     * Usuwa Entity objekt z bazy danych.
     *
     * @param orderAnswer
     */
    void delete(OrderAnswer orderAnswer);

    /**
     * Usunięcie zlecenia od starony warsztatu.
     * Jeżeli samochód jest przypisany do innych zleceń nie będzie usunięty z bazy danych.
     * Jeżeli warsztat ma zlecenia w stanie CREATED OrderAnswer będzie usunięty. Jeżeli usunięty ostatni OrderAnswer,
     *  klient dostanie wiadomość email, że nikt nie odpowiedział na zlecenie.
     * Jeżeli warsztat ma zlecenia w stanie IMPLEMENTATION on nie ma dostępu do usunięcia takich zleceń. Dopóki są zlecenia w takim stanie,
     *  funkcja usunięcia konta nie dostępna. Usunięcie jest możliwe tylko w przypadku usunięcia konta przez administratora.
     * Jeżeli warsztat ma zlecenie w stanie UNREGISTERED OrderAnswer będzie usunięty. Jeżeli usunięty ostatni OrderAnswer,
     *  klient dostanie wiadomość email, że nikt nie odpowiedział na zlecenie.
     * Jeżeli warsztat ma zlecenie w stanie COMPLETED ono będzie usunięte z bazy danych jeżeli nie ma przypisanych klientów.
     *  W przeciwnym przypadku będzie usunięte relację Warsztat-Zlecenie.
     *  Rzuca wyjątek {@code CantDeleteWorkshopWhileImplementationExistException} jeżeli warsztat usuwa konto zawierające zlecenia w stanie 'IMPLEMENTATION'
     *
     * @param orderAnswer Entity objekt
     * @param isAdmin flaga ustalona na true w przypadku wywołania metody administratorem
     */
    void deleteOrderAnswerFromWorkshopByOrderAnswerAndIsAdmin(OrderAnswer orderAnswer, Boolean isAdmin)throws CantDeleteWorkshopWhileImplementationExistException;

    boolean isWorkshopInOrderAnswer(OrderAnswer orderAnswer);

}
