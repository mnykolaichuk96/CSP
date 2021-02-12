package mnykolaichuk.prz.pracaDyplomowa.service;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.CarMake;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Klient może wybierać z listy dostępnych producentów samochodów.
 * Klient nie może dodawać lub usuwać z bazy danych producentów samochodów.
 * Admin ma możliwość dodawania lub usunięcia producentów samochodów.
 */

@Service
public interface CarMakeService {

    /**
     *@return List dostępnych do wyboru producentów samochów
    */
    List<String> loadCarMakeList();

    /**
     * @param make producent samochodów
     * @return Entity object
     */
    CarMake findByMake(String make);

    void delete(CarMake carMake);

    void save(CarMake carMake);
}
