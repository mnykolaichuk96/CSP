package mnykolaichuk.prz.pracaDyplomowa.dao;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerCar;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CustomerCarRepository extends JpaRepository<CustomerCar, Integer>, CustomerCarRepositoryCustom {
    List<CustomerCar> findAllByCarId(Integer carId);
    List<CustomerCar> findAllByCustomerId(Integer customerId);
    CustomerCar findCustomerCarByCustomerIdAndCarId(Integer customerId, Integer carId);

}
