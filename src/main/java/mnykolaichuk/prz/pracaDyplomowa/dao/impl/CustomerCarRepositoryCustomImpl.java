package mnykolaichuk.prz.pracaDyplomowa.dao.impl;

import mnykolaichuk.prz.pracaDyplomowa.dao.CustomerCarRepositoryCustom;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerCar;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.Query;
@Repository
public class CustomerCarRepositoryCustomImpl implements CustomerCarRepositoryCustom {

    @Autowired
    private EntityManager entityManager;

    @Transactional
    @Modifying
    @Override
    public void deleteCustomerCar(CustomerCar customerCar) {
        Query query = entityManager.createQuery("delete from CustomerCar cc where cc.carId=:carId and cc.customerId=:customerId");
        query.setParameter("carId", customerCar.getCarId());
        query.setParameter("customerId", customerCar.getCustomerId());

        try {
            query.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

