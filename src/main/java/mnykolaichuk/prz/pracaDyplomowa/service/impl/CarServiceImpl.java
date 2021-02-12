package mnykolaichuk.prz.pracaDyplomowa.service.impl;

import mnykolaichuk.prz.pracaDyplomowa.dao.CarRepository;
import mnykolaichuk.prz.pracaDyplomowa.exception.CarAlreadyHasOwnerException;
import mnykolaichuk.prz.pracaDyplomowa.exception.InvalidTokenException;
import mnykolaichuk.prz.pracaDyplomowa.exception.MyCarAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CarData;
import mnykolaichuk.prz.pracaDyplomowa.model.email.CarVerificationEmailContext;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.*;
import mnykolaichuk.prz.pracaDyplomowa.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.mail.MessagingException;
import java.time.Year;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class CarServiceImpl implements CarService {

    @Autowired
    private CarRepository carRepository;

    @Autowired
    private CustomerCarService customerCarService;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerDetailService customerDetailService;

    @Autowired
    private CarService carService;

    @Autowired
    private CarModelService carModelService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private MessageSource messageSource;

    @Value("${site.base.url.http}")
    private String baseURL;

    @Override
    public Car findById(Integer id) {
        try {
            return carRepository.findCarById(id);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public CarData getCarData(Car car) {
        CarData carData = new CarData();
        BeanUtils.copyProperties(car,carData);
        carData.setMake(car.getCarModel().getCarMake().getMake());
        carData.setModel(car.getCarModel().getModel());
        carData.setYear(car.getYear().toString());
        return carData;
    }

    @Override
    public List<CarData> getCarDataListForCustomerUsername(String username) {
        List<CarData> carDataList = new ArrayList<>();
        if(customerService.isCarInCustomer(customerService.findByUsername(username))) {
            for (CustomerCar customerCar : customerCarService.findAllByCustomerId(customerService.findByUsername(username).getId())) {
                if(customerCar.getCarVerified() == 1) {
                    carDataList.add(getCarData(findById(customerCar.getCarId())));
                }
            }
            return carDataList;
        }
        return null;
    }

    @Override
    public void save(CarData carData) throws MyCarAlreadyExistException, CarAlreadyHasOwnerException {
        List<Customer> customers = new ArrayList<>();
        //zapisuje id klienta aktualnie dodającego samochód
        Integer currentUserId = customerService.findByUsername(carData.getUsername()).getId();
        //ustawia boolean flagę czy istnieje w BD dodawany samochód, z już przypisamym klientem.
        boolean isCarExist = checkIfVinAndRegistrationNumberExistCustomerIsNotNull(carData.getVin(), carData.getRegistrationNumber());

        //liczba przypisanych do samochodu klientów
        int count = 1;

        if(isCarExist){
            //zwraca samochód z przypisanym klientem oraz podanym numerem VIN, numerem rejestracyjnym z bazy danych
            Car car = findCarByVinAndRegistrationNumberAndCustomersIsNotNull(carData.getVin(), carData.getRegistrationNumber());
            //wszystkie Entity objekty z polami id samochodu, id klienta zawierające id aktualnie dodawanego samochodu
            for(CustomerCar customerCar : customerCarService.findAllByCarId(car.getId())) {
                //jeżeli aktualnie zalogowany klient już jest przypisany do samochodu
                if(customerCar.getCustomerId() == currentUserId) {
                    //rzuca wyjątek że samochód już jest w liście samochodów klienta
                    throw new MyCarAlreadyExistException(messageSource.getMessage("my.car.already.exist.exception"
                            , null, LocaleContextHolder.getLocale()));
                }
                //jeżeli samochód nie ma przypisanego aktualnie zalogowanego klienta
                else {
                    //dodaje do listy wszystrkich przypisanych do samochodu klientów
                    customers.add(customerService.findById(customerCar.getCustomerId()));
                    //liczniek ilości przypisanych klientów
                    count++;
                }
            }
            //tworzy Entity object służący do związku @ManyToMany Klient-Samochód
            // @param currentUserId id aktualnie zalogowanego klienta
            //@param carId id samochodu
            //@param carVerified liczba przypisanych do samochodu klientów. Kiedy liczba będzie równa '1' samochód będzie dostępny dla
            //  aktualnie zalogowanego klienta
            CustomerCar customerCar = new CustomerCar(currentUserId, car.getId(), count);

            for(Customer customer : customers){
                //wysylanie do wszystkich klientów przypisanych do samochody pisem z informacją o dodaniu ich samochodu do listy
                // własnych samochodów przez klienta oraz możliwością weryfikacji tego dodania
                sendCarConfirmationEmail(customerService.findByUsername(carData.getUsername()), customer.getCustomerDetail(), car);
            }
            //zapisanie do bazy danych nowego przypisanego do samochodu klienta
            customerCarService.save(customerCar);
            throw new CarAlreadyHasOwnerException(messageSource.getMessage("car.already.has.owner.exception"
                    , null, LocaleContextHolder.getLocale()));
        }

        //jeżeli samochód nie isnieje w bazie danych
        if(!isCarExist) {
            //Entity object model samochodu z referencją na producenta samochodu
            CarModel carModel = carModelService.findByMakeAndModel(carData.getMake(), carData.getModel());
            Car car = new Car();
            //kopiowanie wszystkich danych z formy do Entity objektu
            BeanUtils.copyProperties(carData, car);
            //przypisanie producenta oraz modeli samochodu
            car.setCarModel(carModel);
            car.setYear(Year.of(Integer.parseInt(carData.getYear())));
            //przypisanie do samochodu aktualnie zalogowanego klienta
            car.setCustomers(Arrays.asList(customerService.findByUsername(carData.getUsername())));

            //zapisanie samochodu do bazy danych
            carRepository.save(car);
            //zapisanie do bazy danych związku Klient-Samochód
            CustomerCar customerCar;
            customerCar = customerCarService.findByCustomerIdAndCarId
                    (currentUserId,
                            findCarByVinAndRegistrationNumberAndCustomersIsNotNull(car.getVin(), car.getRegistrationNumber())
                            .getId());
            customerCar.setCarVerified(1);
            customerCarService.save(customerCar);
        }
    }

    /**
     * Wysyłanie pisem na pocztę elektroniczną wykonuje się w nowym wątku.
     *
     * @param sourceCustomer aktualnie zalogowany klient
     * @param targetCustomerDetail Entity objekt zawierający dane klienta do którego będzie wysłane pismo
     * @param car Entity object samochód
     */
    private void sendCarConfirmationEmail(Customer sourceCustomer, CustomerDetail targetCustomerDetail, Car car) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { carConfirmationEmail(sourceCustomer, targetCustomerDetail, car);

            }
        });
    }

    private void carConfirmationEmail(Customer sourceCustomer, CustomerDetail targetCustomerDetail, Car car) {
        SecureToken secureToken = secureTokenService.createSecureTokenForCar(car, sourceCustomer);
        secureToken.setCustomerDetail(targetCustomerDetail);
        secureTokenService.saveSecureToken(secureToken);
        CarVerificationEmailContext emailContext = new CarVerificationEmailContext();
        emailContext.init(customerDetailService.getCustomerDetailData(sourceCustomer.getCustomerDetail())
                , customerDetailService.getCustomerDetailData(targetCustomerDetail)
                , carService.getCarData(car));
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        try {
            emailService.sendMail(emailContext);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Car saveForOrder(CarData carData) {
        //ustawia boolean flagę czy istnieje w BD dodawany samochód, z już przypisamym zleceniem
        boolean isCarExist = checkIfVinAndRegistrationNumberExistCustomerIsNull(carData.getVin(), carData.getRegistrationNumber());
        //jeżeli takie samochody są w bazie danych
        if(isCarExist){
            //zwraca samochód z przypisanym zleceniem oraz podanym numerem VIN, numerem rejestracyjnym z bazy danych
            Car car = findCarByVinAndRegistrationNumberAndCustomersIsNull(carData.getVin(), carData.getRegistrationNumber());
            //ponieważ samochód już jest w barzie danych, zwracamy Entity objekt samochodu
            return car;
        }
        //tworzony nowy samochód
        CarModel carModel = carModelService.findByMakeAndModel(carData.getMake(), carData.getModel());
        Car car = new Car();
        car.setCarModel(carModel);
        BeanUtils.copyProperties(carData, car);
        try {
            car.setYear(Year.of(Integer.parseInt(carData.getYear())));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return carRepository.save(car);

    }

    private boolean checkIfVinAndRegistrationNumberExistCustomerIsNotNull(String vin, String registrationNumber) {
        return carRepository.findCarByVinAndRegistrationNumberAndCustomersIsNotNull(vin, registrationNumber) != null ? true : false;
    }

    private boolean checkIfVinAndRegistrationNumberExistCustomerIsNull(String vin, String registrationNumber) {
        return carRepository.findCarByVinAndRegistrationNumberAndCustomersIsNull(vin, registrationNumber) != null ? true : false;
    }

    @Override
    public Car findCarByVinAndRegistrationNumberAndCustomersIsNull(String vin, String registrationNumber) {
        try {
            return carRepository.findCarByVinAndRegistrationNumberAndCustomersIsNull(vin, registrationNumber);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public Car findCarByVinAndRegistrationNumberAndCustomersIsNotNull(String vin, String registrationNumber) {
        try {
            return carRepository
                    .findCarByVinAndRegistrationNumberAndCustomersIsNotNull(vin, registrationNumber);
        }
        catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public List<Car> findAllCarsByUsername(String username) {
        List<Car> cars = new ArrayList<>();
        List<CustomerCar> customerCars = customerCarService.findAllByCustomerId(customerService.findByUsername(username).getId());
        for(CustomerCar customerCar : customerCars) {
            if(customerCar.getCarVerified() == 1) {
                cars.add(carRepository.findCarById(customerCar.getCarId()));
            }
        }
        return cars;
    }

    @Override
    public boolean verifyCar(String token) throws InvalidTokenException {
        //Zwraca z bazy danych Entity objekt token
        SecureToken secureToken = secureTokenService.findByToken(token);
        //jeżeli token jest równy null albo nie jest równy do żądanego tokiena albo nie jest aktualny
        if(Objects.isNull(secureToken) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()){
            //rzyca wyjątek o nie poprawności tokiena
            throw new InvalidTokenException(messageSource.getMessage("invalid.token.exception"
                    , null, LocaleContextHolder.getLocale()));
        }
        //zwraca Entity objekt klienta do którego było wysłane pismo na podstawie zapisanych do tokiena danych klienta
        Customer customer = customerService.findById(secureToken.getCustomerDetail().getCustomer().getId());
        //jeżeli takiego klienta nie istnieje zwraca false
        if(Objects.isNull(customer)){
            return false;
        }
        //zwraca id samochodu na podstawie zapisanych do tokiena danych
        Integer carId = secureToken.getVerificationCarId();
        //zwraca id klienta chcącego dodać samochód do swojej listy
        Integer customerId = secureToken.getVerificationCarCustomerId();
        //zwraca Entity objekt odpowiadający za relację ManyToMany Klient-Samochód, zawierający pole weryfikacji samochodu
        CustomerCar customerCar = customerCarService.findByCustomerIdAndCarId(customerId, carId);
        //kiedy wszystkie już przypisane i zweryfikowane do samochodu klienty zweryfikują dodanie samochodu przez nowego klienta,
        //liczba w polu carVerified będzie równa '1' i samochód będzie dostępny do korzystania przez nowego klienta
        customerCar.setCarVerified(customerCar.getCarVerified() - 1);
        customerCarService.save(customerCar);

        // usuwa wykorzystany tokien z bazy danych
        secureTokenService.removeToken(secureToken);
        return true;
    }

    @Override
    public void deleteByCarAndCustomer(Car car, Customer sourceCustomer) {
        //jeżeli samochód ma przypisanego jednego klienta, usuwa z bazy danych
        if(isCustomerInCar(car)) {
            if (car.getCustomers().size() == 1) {
                carRepository.deleteCarById(car.getId());
            }
            //jeżeli samochód ma listę przypisanych klientów, usuwa relację Samochód-sourceCustomer
            else {
                for (CustomerCar customerCar : customerCarService.findAllByCarId(car.getId())) {
                    if (customerCar.getCustomerId() == sourceCustomer.getId()) {
                        customerCarService.delete(customerCar);
                    }
                }
            }
        }
    }

    @Override
    public void deleteByOrder(Order sourceOrder) {
        //jeżeli samochód ma przypisane jedno zlecenie, usuwa z bazy danych
        if(orderService.isCarInOrder(sourceOrder)) {
            Car car = sourceOrder.getCar();
            if (car.getOrders().size() == 1) {
                sourceOrder.setCar(null);
                orderService.save(sourceOrder);
                carRepository.deleteCarById(car.getId());
            }
            //jeżeli samochód ma listę przypisanych zleceń, usuwa relację Samochód-sourceCustomer
            else {
                for (Order order : car.getOrders()) {
                    if (order.getId() == sourceOrder.getId()) {
                        order.setCar(null);
                        orderService.save(order);
                    }
                }
            }
        }
    }

    @Override
    public boolean isCustomerInCar(Car car) {
        return !Objects.isNull(car.getCustomers());
    }

}
