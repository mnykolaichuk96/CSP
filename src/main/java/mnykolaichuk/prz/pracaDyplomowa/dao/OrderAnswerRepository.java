package mnykolaichuk.prz.pracaDyplomowa.dao;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.OrderAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderAnswerRepository extends JpaRepository<OrderAnswer, Integer> {
    @Transactional
    @Modifying
    @Query("delete from OrderAnswer oa where oa.id=:id")
    void deleteOrderAnswerById(Integer id);

    List<OrderAnswer> findAllByWorkshopUsername(String username);
    OrderAnswer findOrderAnswerById(Integer id);
}
