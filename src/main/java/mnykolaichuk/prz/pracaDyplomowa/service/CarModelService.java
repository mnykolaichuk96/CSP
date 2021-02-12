package mnykolaichuk.prz.pracaDyplomowa.service;

import mnykolaichuk.prz.pracaDyplomowa.exception.CantDeleteCarModelException;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CarMake;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CarModel;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CarModelData;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Modeli samochodów są dostępne na podstawie wybranego producenta samochodów
 * Klient może wybierać z listy dostępnych modeli samochodów.
 * Klient nie może dodawać lub usuwać z bazy danych modelisamochodów.
 * Admin ma możliwość dodawania lub usunięcia modeli samochodów.
 */

@Service
public interface CarModelService {

    /**
     * Zwraca listę dostępnych do wyboru modeli samochodu na podstawie producenta.
     *
     * @param carMake Entity object producent samochodów
     * @return List dostępnych modeli na podstawie wybranego producenta
     */
    List<String> loadCarModelList(CarMake carMake);

    /**
     * Może zwrócić pojedynczy obiekt tylko na podstawie dwóch @param.
     *
     * @param make producent samochodów
     * @param model model samochodu
     * @return Entity object
     */
    CarModel findByMakeAndModel(String make, String model);

    List<CarModelData> getAllCarModelDataList();

    void deleteByCarModelId(Integer id) throws CantDeleteCarModelException;

    void addCarModel(String make, String model);

    boolean isCarInCarModel(CarModel carModel);
}
