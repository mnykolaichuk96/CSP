package mnykolaichuk.prz.pracaDyplomowa.service.impl;

import mnykolaichuk.prz.pracaDyplomowa.dao.CarMakeRepository;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CarMake;
import mnykolaichuk.prz.pracaDyplomowa.service.CarMakeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CarMakeServiceImpl implements CarMakeService {

    @Autowired
    private CarMakeRepository carMakeRepository;

    @Override
    public List<String> loadCarMakeList() {
        List<String> carMakeList = new ArrayList<>();
        for (CarMake carMake : carMakeRepository.findAll()) {
            carMakeList.add(carMake.getMake());
        }
        return carMakeList;
    }

    @Override
    public CarMake findByMake(String make) {
        return carMakeRepository.findCarMakeByMake(make);
    }

    @Override
    public void delete(CarMake carMake) {
        carMakeRepository.deleteCarMakeById(carMake.getId());
    }

    @Override
    public void save(CarMake carMake) {
        carMakeRepository.save(carMake);
    }
}
