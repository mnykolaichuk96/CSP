package mnykolaichuk.prz.pracaDyplomowa.service.impl;

import mnykolaichuk.prz.pracaDyplomowa.dao.CarModelRepository;
import mnykolaichuk.prz.pracaDyplomowa.exception.CantDeleteCarModelException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CarModelData;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CarMake;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CarModel;
import mnykolaichuk.prz.pracaDyplomowa.service.CarMakeService;
import mnykolaichuk.prz.pracaDyplomowa.service.CarModelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CarModelServiceImpl implements CarModelService {

    @Autowired
    private CarModelRepository carModelRepository;

    @Autowired
    private CarMakeService carMakeService;

    @Autowired
    private MessageSource messageSource;

    @Override
    public List<String> loadCarModelList(CarMake carMake) {
        List<String> carModelList = new ArrayList<>();
        for(CarModel carModel : carModelRepository.findAllByCarMakeOrderByModelAsc(carMake)) {
            carModelList.add(carModel.getModel());
        }
        return carModelList;
    }

    @Override
    public CarModel findByMakeAndModel(String make, String model) {
        Integer carMakeId = carMakeService.findByMake(make).getId();
        return carModelRepository.findCarModelByCarMakeIdAndModel(carMakeId, model);
    }

    @Override
    public List<CarModelData> getAllCarModelDataList() {
        List<CarModelData> allCarModelDataList = new ArrayList<>();
        CarModelData carModelData;
        for(CarModel carModel : carModelRepository.findAll()) {
            carModelData = new CarModelData();
            carModelData.setModelId(carModel.getId());
            carModelData.setMake(carModel.getCarMake().getMake());
            carModelData.setModel(carModel.getModel());
            if(isCarInCarModel(carModel)) {
                carModelData.setCarInSystemCount(carModel.getCars().size());
            }
            else {
                carModelData.setCarInSystemCount(0);
            }
            allCarModelDataList.add(carModelData);
        }
        return allCarModelDataList;
    }

    @Override
    public void deleteByCarModelId(Integer id) throws CantDeleteCarModelException {
        CarModel carModel = carModelRepository.findCarModelById(id);
        if(isCarInCarModel(carModel)) {
            if(carModel.getCars().size() != 0)
            throw new CantDeleteCarModelException(messageSource.getMessage("cant.delete.car.model.exception"
                     ,null, LocaleContextHolder.getLocale()));
        }
        CarMake carMake = carModel.getCarMake();
        if(carMake.getCarModels().size() == 1) {
            carModelRepository.deleteCarModelById(id);
            carMakeService.delete(carMake);
        }
        else {
            carModelRepository.deleteCarModelById(id);
        }
    }

    @Override
    public void addCarModel(String make, String model) {
        if(carMakeService.findByMake(make) == null) {
            carMakeService.save(new CarMake(make));
        }
        CarModel carModel = new CarModel(model);
        carModel.setCarMake(carMakeService.findByMake(make));
        carModelRepository.save(carModel);
    }

    @Override
    public boolean isCarInCarModel(CarModel carModel) {
        return !Objects.isNull(carModel.getCars());
    }
}
