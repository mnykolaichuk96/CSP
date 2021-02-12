package mnykolaichuk.prz.pracaDyplomowa.dao;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.Car;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface CarRepository extends JpaRepository<Car, Integer> {

    @Transactional
    @Modifying
    @Query("delete from Car c where c.id=:id")
    void deleteCarById(Integer id);

    Car findCarById(int id);
    Car findCarByVinAndRegistrationNumberAndCustomersIsNotNull(String vin, String registrationNumber);
    Car findCarByVinAndRegistrationNumberAndCustomersIsNull(String vin, String registrationNumber);
}
