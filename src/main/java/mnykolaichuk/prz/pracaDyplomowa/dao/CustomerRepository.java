package mnykolaichuk.prz.pracaDyplomowa.dao;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Integer> {
    @Transactional
    @Modifying
    @Query("delete from Customer c where c.id=:id")
    void deleteCustomerById(Integer id);

    Customer findCustomerByUsername(String username);
    Customer findCustomerById(Integer id);
    List<Customer> findAll();
}
