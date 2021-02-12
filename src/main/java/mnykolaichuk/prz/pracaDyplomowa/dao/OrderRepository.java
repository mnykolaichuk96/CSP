package mnykolaichuk.prz.pracaDyplomowa.dao;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerDetail;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface OrderRepository extends JpaRepository<Order, Integer> {
    @Transactional
    @Modifying
    @Query("delete from Order o where o.id=:id")
    void deleteOrderById(Integer id);

    List<Order> findAllByCustomerDetail(CustomerDetail customerDetail);
    Order findOrderById(Integer id);
}
