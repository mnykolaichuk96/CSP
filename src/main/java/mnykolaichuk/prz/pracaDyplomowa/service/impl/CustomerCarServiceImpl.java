package mnykolaichuk.prz.pracaDyplomowa.service.impl;

import mnykolaichuk.prz.pracaDyplomowa.dao.CustomerCarRepository;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerCar;
import mnykolaichuk.prz.pracaDyplomowa.service.CustomerCarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerCarServiceImpl implements CustomerCarService {

    @Autowired
    private CustomerCarRepository customerCarRepository;

    @Override
    public List<CustomerCar> findAllByCarId(Integer carId) {
        try {
            return customerCarRepository.findAllByCarId(carId);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public List<CustomerCar> findAllByCustomerId(Integer customerId) {
        try {
            return customerCarRepository.findAllByCustomerId(customerId);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public CustomerCar findByCustomerIdAndCarId(Integer customerId, Integer carId) {
        try {
            return customerCarRepository.findCustomerCarByCustomerIdAndCarId(customerId, carId);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public void delete(CustomerCar customerCar) {
        customerCarRepository.deleteCustomerCar(customerCar);
    }

    @Override
    public void save(CustomerCar customerCar) {
        customerCarRepository.save(customerCar);
    }
}
