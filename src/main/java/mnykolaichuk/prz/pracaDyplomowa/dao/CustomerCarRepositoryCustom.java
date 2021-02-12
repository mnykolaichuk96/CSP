package mnykolaichuk.prz.pracaDyplomowa.dao;

import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerCar;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerCarRepositoryCustom {
    void deleteCustomerCar(CustomerCar customerCar);
}
