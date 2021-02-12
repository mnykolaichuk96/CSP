package mnykolaichuk.prz.pracaDyplomowa.dao;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CityRepository  extends JpaRepository<City, Integer> {

    @Transactional
    @Modifying
    @Query("delete from City c where c.id=:id")
    void deleteCityById(Integer id);

    City findCityByCityName(String cityName);
    City findCityById(Integer id);
}
