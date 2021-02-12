package mnykolaichuk.prz.pracaDyplomowa.dao;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.CarMake;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CarModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CarModelRepository extends JpaRepository<CarModel, Integer> {

   @Transactional
   @Modifying
   @Query("delete from CarModel cm where cm.id=:id")
   void deleteCarModelById(Integer id);

   List<CarModel> findAllByCarMakeOrderByModelAsc(CarMake carMake);

   @Query(value = "FROM CarModel " +
                  "ORDER BY carMake.make ASC, model")
   List<CarModel> findAll();

   CarModel findCarModelByCarMakeIdAndModel(Integer carMakeId, String model);
   CarModel findCarModelById(Integer id);
}
