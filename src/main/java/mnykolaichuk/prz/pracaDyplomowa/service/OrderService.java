package mnykolaichuk.prz.pracaDyplomowa.service;

import mnykolaichuk.prz.pracaDyplomowa.exception.EmailAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.exception.NullWorkshopInCityException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderCustomerData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderWorkshopData;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Order;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.OrderAnswer;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Zlecenie przedstawione w bazie danych za pomocą dwóch tabeli Order oraz OrderAnswer.
 * Order przechowuje głowne dane wspólne dla wszystkich warsztatów:Relacje z CustomerDetail, Car oraz City.
 *      *opis zlecenia;
 *      *data stworzenia;
 *      *relacja z danymi samochodu
 *      *relacja z danymi klienta
 *      *relacja z wybranym miastem
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
public interface OrderService {

    /**
     * Tworzy zlecenie(jeden Order oraz OrderAnswer dla każdego warsztatu) na podstawie Customer username
     *  oraz objektu OrderCustomerData(objekt do wysłania oraz odebrania danych z Fromt End). Objekt służy do komunikazji Klient-Zlecenie.
     *  Rzuca wyjątek{@code NullWorkshopInCityException} jeżeli w wybranym dla zlecenia mieście nie ma warsztatów.
     *
     * @param username Customer
     * @param orderCustomerData objekt do komunikacji z Front End
     * @throws NullWorkshopInCityException
     */
    void createOrder(String username, OrderCustomerData orderCustomerData) throws NullWorkshopInCityException;

    /**
     * Wysyła pisma na adres elektroniczny listy warsztatów znajdujących sie w mieście stworzenia zlecenia.
     * Każde pismo wysłane w nowym wątku.
     *
     * @param orderAnswer Entity objekt.
     */
    void sendToWorkshopListCreateOrderInformationEmail(OrderAnswer orderAnswer);

    /**
     * Zapisuje do bazy danych dane klienta do tabeli CustomerDetail, dane samochodu do tabeli Car, oraz dane zlecenia do tabel Order oraz OrderAnswer.
     * Warsztaty będą poinformowane o stworzeniu nowego zlecenia i będą mieć dostęp do jego danych po weryfikacji adresy email.
     * Rzuca wyjątko {@code EmailAlreadyExistException} jeżeli podany przez klienta email już zajęty innym klientem,
     *               {@code NullWorkshopInCityException} jeżeli u wybranym mieście nie ma warsztatów.
     *
     * @param orderWorkshopData objekt do komunikacji z Front End
     * @throws EmailAlreadyExistException
     * @throws NullWorkshopInCityException
     */
    void createOrderFromUnregisteredUser(OrderWorkshopData orderWorkshopData) throws EmailAlreadyExistException, NullWorkshopInCityException;

    /**
     * Zwraca listę objektów do wysłania danych o zleceniu dla klienta. Będą zwrócone tylko zlecenia z stanem nie równym 'COMPLETED'.
     * Żeby wyświetlić stany 'CREATED', 'UNREGISTERED', 'IMPLEMENTATION'
     *
     * @param username Customer username
     * @return List objektów do komunikacji z FrontEnd(Klient-Zlecenie) Stan != 'COMPLETED'
     */
    List<OrderCustomerData> getOrderCustomerDataListByUsernameAndStanIsNotCompleted(String username);

    /**
     * Zwraca objekt do wysłania danych o zleceniu dla klienta na podstawie Entity objektu Order.
     * Będą zwrócone tylko zlecenia z stanem równym 'WORKSHOP_ANSWER'(odpowiedzi z ofertami od warsztatów).
     * Rzuca wyjątek {@code NullOrderAnswerForOrderException} jeżeli wszystkie warsztatu usunęli OrderAnswer.
     *
     * @param order Entity objekt
     * @return OrderCustomerData objekt do komunikacji z FrontEnd(Klient-Zlecenie) Stan = 'WORKSHOP_ANSWER'
     */
    OrderCustomerData getOrderCustomerDataByOrderAndStanEqualsWorkshopAnswer(Order order);

    /**
     * Zwraca listę objektów OrderCustomerData(komunikacja z Front End Klient-Zlecenie) na podstawie username klienta.
     * Będą zwrócone tylko zlecenia z stanem równym 'COMPLETED'(zrealizowane zlecenia).
     *
     * @param username Customer username
     * @return List OrderCustomerData objekt do komunikacji z FrontEnd(Klient-Zlecenie) Stan = 'COMPLETED'
     */
    List<OrderCustomerData> getOrderCustomerDataListByUsernameAndStanEqualsCompleted(String username);

    OrderCustomerData getOrderCustomerDataByOrderId(Integer id);

    /**
     * Zwraca listę objektów OrderWorkshopData(komunikacja z Front End Warsztat-Zlecenie) na podstawie username warsztatu.
     * Będą zwrócone tylko zlecenia z stanem równym 'CREATED'(stworzone zlecenia).
     *
     * @param username Workshop username
     * @return List OrderWorkshopData objekt do komunikacji z FrontEnd(Klient-Zlecenie) Stan = 'CREATED'
     */
    List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStanEqualsCreated(String username);

    /**
     * Zwraca listę objektów OrderWorkshopData(komunikacja z Front End Warsztat-Zlecenie) na podstawie username warsztatu.
     * Będą zwrócone tylko zlecenia z stanem równym 'IMPLEMENTATION'(w realizacji).
     *
     * @param username Workshop username
     * @return List OrderWorkshopData objekt do komunikacji z FrontEnd(Klient-Zlecenie) Stan = 'IMPLEMENTATION'
     */
    List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStanEqualsImplementation(String username);

    /**
     * Zwraca listę objektów OrderWorkshopData(komunikacja z Front End Warsztat-Zlecenie) na podstawie username warsztatu.
     * Będą zwrócone tylko zlecenia z stanem równym 'COMPLETED'(zrealizowane zlecenia).
     *
     * @param username Workshop username
     * @return List OrderWorkshopData objekt do komunikacji z FrontEnd(Klient-Zlecenie) Stan = 'COMPLETED'
     */
    List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStanEqualsCompleted(String username);

    OrderWorkshopData getOrderWorkshopDataByOrderAnswerId(Integer id);

    /**
     * Zwraca Entity Order na podstawie go id.
     *
     * @param id Order id
     * @return Entity objekt
     */
    Order findOrderById(Integer id);

    /**
     * Zapisuje zlecenie do bazy danych
     *
     * @param order Entity objekt
     */
    void save(Order order);

    void delete(Order order);

    /**
     * Usunięcie zlecenia od starony klienta.
     * Jeżeli samochód jest przypisany do innych zleceń nie będzie usunięty z bazy danych.
     * Jeżeli klient ma zlecenia w stanie CREATED lub WORKSHOP_ANSWER będą oni usunięte z bazy danych.
     * Jeżeli klient ma zlecenia w stanie IMPLEMENTATION on nie ma dostępu do usunięcia takich zleceń. Będą usunięte w przypadku usunięcia konta.
     * Jeżeli klient ma zlecenie w stanie UNREGISTERED ono będzie usunięte z bazy danych jeżeli nie ma przypisanych warsztatów.
     *  W przeciwnym przypadku będzie usunięte relację Klient-Zlecenie.
     * Jeżeli klient ma zlecenie w stanie COMPLETED ono będzie usunięte z bazy danych jeżeli nie ma przypisanych warsztatów.
     *  W przeciwnym przypadku będzie usunięte relację Klient-Zlecenie.
     *
     * @param order Entity objekt
     */
    void deleteOrderFromCustomerByOrder(Order order);

    boolean isCarInOrder(Order order);
    boolean isOrderAnswerInOrder(Order order);
    boolean isCustomerDetailInOrder(Order order);

}
