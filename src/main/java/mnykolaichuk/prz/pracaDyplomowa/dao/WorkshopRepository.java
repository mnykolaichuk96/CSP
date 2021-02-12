package mnykolaichuk.prz.pracaDyplomowa.dao;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.City;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Workshop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface WorkshopRepository  extends JpaRepository<Workshop, Integer> {
    @Transactional
    @Modifying
    @Query("delete from Workshop w where w.id=:id")
    void deleteWorkshopById(Integer id);

    Workshop findWorkshopByUsername(String username);
    Workshop findWorkshopByEmail(String email);
    Workshop findWorkshopById(Integer id);
    List<Workshop> findAllByCity(City city);
    List<Workshop> findAll();

}
