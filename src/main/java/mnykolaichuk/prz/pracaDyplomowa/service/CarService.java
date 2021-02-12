package mnykolaichuk.prz.pracaDyplomowa.service;

import mnykolaichuk.prz.pracaDyplomowa.exception.CarAlreadyHasOwnerException;
import mnykolaichuk.prz.pracaDyplomowa.exception.InvalidTokenException;
import mnykolaichuk.prz.pracaDyplomowa.exception.MyCarAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Car;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Customer;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Order;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CarData;

import java.util.List;

/**
 * Samochód może być stworzony przez klienta albo dla zlecenie.
 * Samochód stworzony przez klienta zapisany w bazie danych z przypisanym do niego klientem(ów).
 * Samochód stworzony dla zlecenia(ń) nie ma przypisanego klienta.
 * Samochód uważa sie unikatowym jeżeli w bazie danych nie ma innego z takim samym VIN numerem oraz numerem rejestracyjnym.
 * Jeżeli samochód już jest przypisany do klienta(ów) i nowy klint chcę go dodać do już przypisanego klienta(ów) będą wysyłane
 * pisma zawierającę dane oraz dane kontaktowe klienta chcącego dodać samochód oraz link weryfikacji. Samochód będzie dodany do listy
 * samochodów klienta po weryfikacji przez wszystkich już przypisanych klientów.
 */

public interface CarService {

    /**
     *Zwraca samochód z podanym numerem VIN oraz numerem rejestracyjnym będący przypisanym do zlecenia(ń).
     *
     * @param vin VIN muner samochodu
     * @param registrationNumber numer rejestracyjny samochodu
     * @return Entity oblect
     */
    Car findCarByVinAndRegistrationNumberAndCustomersIsNull(String vin, String registrationNumber);

    /**
     *Zwraca samochód z podanym numerem VIN oraz numerem rejestracyjnym będącego przypisanym do klienta(ów).
     *
     * @param vin VIN muner samochodu
     * @param registrationNumber numer rejestracyjny samochodu
     * @return Entity objekt
     */
    Car findCarByVinAndRegistrationNumberAndCustomersIsNotNull(String vin, String registrationNumber);

    /**
     *
     * @param id
     * @return Entity object
     */
    Car findById(Integer id);

    /**
     * Zwraca objekt służący do wyslania danych samochodu z bazy danych na Front End.
     *
     * @param car Entity object
     * @return CarData object zawirający dane samochodu
     */
    CarData getCarData(Car car);

    /**
     * Zwraca listę objektów służący do wyslania danych samochodu z bazy danych na Front End
     * wszystkich samochodów przypisanych do aktualnie zalogowanego klienta.
     *
     * @param username aktualnie zalogowanego klienta
     * @return List CarData objektów zawierających dane samochodu
     */
    List<CarData> getCarDataListForCustomerUsername(String username);

    /**
     * Zapisuje nowy samochód z przypisanym aktualnie zalogowanym klientem do bazy danych.
     * Rzuca wyjątek {@code MyCarAlreadyExistException} jeżeli samochód już jest w liście moich samochodów klienta.
     *
     * @param carData objekt zawierający pobrane z Front End dane samochodu
     * @throws MyCarAlreadyExistException
     */
    void save(CarData carData) throws MyCarAlreadyExistException, CarAlreadyHasOwnerException;

    /**
     * Zapisuje do bazy danych nowy samochód dla zlecenia bez przypisanego klienta.
     * Zwraca zapisany objekt.
     * Nie wypełnia związek OneToMany Samochód-Zlecenie
     *
     * @param carData objekt zawierajacy pobrany z Front End dane samochodu
     * @return Entity objekt
     */
    Car saveForOrder(CarData carData);

    /**
     * Metod wywołany przy przechodzeniu po linku weryfikacji dodania samochodu.
     * Sprawdza czy tokien jest jeszcze aktualny oraz poprawny. Zruca wyjątek {@code InvalidTokenException} leżeli tokien jest nie poprawny.
     * @param token
     * @return true or false
     * @throws InvalidTokenException
     */
    boolean verifyCar(String token) throws InvalidTokenException;

    /**
     * Zwraca listę wszystkich przypisanych do klienta samochodów
     *
     * @param userName
     * @return List Entity objektów
     */
    List<Car> findAllCarsByUsername(String userName);

    /**
     * Jeżeli samochód jest przypisany do jednego klienta, usuwa samochód z bazy danych.
     * Jeżeli samochód jest przypisany do listy klientów, usuwa relację między samochodem a sourceCustomer.
     *
     * @param car Entity objekt
     * @param sourceCustomer Entity objekt
     */
    void deleteByCarAndCustomer(Car car, Customer sourceCustomer);

    /**
     * Jeżeli samochód jest przypisany do jednego zlecenia, usuwa samochód z bazy danych.
     * Jeżeli samochód jest przypisany do listy zleceń, usuwa relację między samochodem a sourceCustomer.
     *
     * @param sourceOrder Entity objekt
     */
    void deleteByOrder(Order sourceOrder);

    boolean isCustomerInCar(Car car);
}
