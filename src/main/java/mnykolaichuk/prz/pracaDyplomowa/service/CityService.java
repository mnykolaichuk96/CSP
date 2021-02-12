package mnykolaichuk.prz.pracaDyplomowa.service;

import mnykolaichuk.prz.pracaDyplomowa.exception.CantDeleteCityException;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.City;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CityData;

import java.util.List;

/**
 * Klienty mają dostep do wyboru miasta przy tworzeniu zlecenia.
 * Warsztaty mają dostęp do wyboru miasta przy rejestracji.
 * Admin moze dodawać lub usuwać, jeżeli u nich nie ma zarejestrowanych warsztatów, miasta.
 */

public interface CityService {
    /**
     *
     * @param cityName
     * @return Entity objekt
     */
    City findByCityName(String cityName);

    /**
     * Zwraca listę wszystkich dostępnych miast.
     *
     * @return List Entity objektów
     */
    List<String> loadCites();

    List<CityData> getAllCityDataList();

    void deleteCityById(Integer id) throws CantDeleteCityException;

    void addCity(City city);
}
