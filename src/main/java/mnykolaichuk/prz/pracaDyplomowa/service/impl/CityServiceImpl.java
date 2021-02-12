package mnykolaichuk.prz.pracaDyplomowa.service.impl;

import mnykolaichuk.prz.pracaDyplomowa.dao.CityRepository;
import mnykolaichuk.prz.pracaDyplomowa.exception.CantDeleteCityException;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.City;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CityData;
import mnykolaichuk.prz.pracaDyplomowa.service.CityService;
import mnykolaichuk.prz.pracaDyplomowa.service.WorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CityServiceImpl implements CityService {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private MessageSource messageSource;

    @Override
    public City findByCityName(String cityName) {
        return cityRepository.findCityByCityName(cityName);
    }

    @Override
    public List<String> loadCites() {
        List<String> cities = new ArrayList<>();
        for(City city : cityRepository.findAll()){
            cities.add(city.getCityName());
        }
        return cities;
    }

    @Override
    public List<CityData> getAllCityDataList() {
        List<CityData> allCityDataList = new ArrayList<>();
        CityData cityData;
        for(City city : cityRepository.findAll()) {
            cityData = new CityData();
            cityData.setCityId(city.getId());
            cityData.setCityName(city.getCityName());
            cityData.setWorkshopCount(workshopService.findAllWithUnActiveByCity(city).size());
            allCityDataList.add(cityData);
        }
        return allCityDataList;
    }

    @Override
    public void deleteCityById(Integer id) throws CantDeleteCityException {
        if(workshopService.findAllWithUnActiveByCity(cityRepository.findCityById(id)).size() != 0) {
            throw new CantDeleteCityException(messageSource.getMessage("cant.delete.city.exception"
                    , null, LocaleContextHolder.getLocale()));
        }
        else {
            cityRepository.deleteCityById(id);
        }
    }

    @Override
    public void addCity(City city) {
        cityRepository.save(city);
    }
}
