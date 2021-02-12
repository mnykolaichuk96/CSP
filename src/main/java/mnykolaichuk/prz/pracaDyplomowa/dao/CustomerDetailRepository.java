package mnykolaichuk.prz.pracaDyplomowa.dao;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CustomerDetailRepository extends JpaRepository<CustomerDetail, Integer> {
    @Transactional
    @Modifying
    @Query("delete from CustomerDetail cd where cd.id=:id")
    void deleteCustomerDetailById(Integer id);

    CustomerDetail findCustomerDetailByEmail(String email);
    List<CustomerDetail> findAllByEmail(String email);
    CustomerDetail findCustomerDetailByCustomerUsername(String username);
    CustomerDetail findCustomerDetailById(Integer id);
}
