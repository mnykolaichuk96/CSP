package mnykolaichuk.prz.pracaDyplomowa.dao;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.CarMake;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CarMakeRepository extends JpaRepository<CarMake, Integer> {

   @Transactional
   @Modifying
   @Query("delete from CarMake cm where cm.id=:id")
   void deleteCarMakeById(Integer id);

   @Query(value = "FROM CarMake " +
           "ORDER BY make ASC")
   List<CarMake> findAll();

   CarMake findCarMakeByMake(String make);
}
