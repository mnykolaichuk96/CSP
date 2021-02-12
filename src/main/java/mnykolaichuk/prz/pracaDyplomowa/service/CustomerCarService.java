package mnykolaichuk.prz.pracaDyplomowa.service;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerCar;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * W celu umożliwienia weryfikacji relacji ManyToMany Klient-Samochód jest utworzony serwis.
 */

@Service
public interface CustomerCarService {

    /**
     * Zwraca listę wszystkich relacji Klient-Samochód dla samochodu.
     *
     * @param carId
     * @return List Entity objektów
     */
    List<CustomerCar> findAllByCarId(Integer carId);

    /**
     * Zwraca listę wszystkich relacji
     *
     * @param customerId
     * @return
     */
    List<CustomerCar> findAllByCustomerId(Integer customerId);

    /**
     * Żeby zwrócić unikatowy Entity objekt potrzebne są id klienta oraz samochodu.
     *
     * @param customerId
     * @param carId
     * @return Entity objekt
     */
    CustomerCar findByCustomerIdAndCarId(Integer customerId, Integer carId);

    /**
     * Usuwa z bazy danych.
     *
     * @param customerCar Entity objekt
     */
    void delete(CustomerCar customerCar);

    /**
     * Zapisuje Entity objekt do bazy danych
     *
     * @param customerCar
     */
    void save(CustomerCar customerCar);
}
