package mnykolaichuk.prz.pracaDyplomowa.service.impl;

import mnykolaichuk.prz.pracaDyplomowa.dao.AuthorityRepository;
import mnykolaichuk.prz.pracaDyplomowa.dao.CustomerRepository;
import mnykolaichuk.prz.pracaDyplomowa.exception.EmailAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.exception.UserAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CustomerData;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Car;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Customer;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerDetail;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Order;
import mnykolaichuk.prz.pracaDyplomowa.model.enums.AuthorityEnum;
import mnykolaichuk.prz.pracaDyplomowa.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class CustomerServiceImpl implements CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private CarService carService;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private CustomerDetailService customerDetailService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private MessageSource messageSource;

   @Override
    public void register(CustomerData customerData) throws UserAlreadyExistException, EmailAlreadyExistException {
       boolean isCustomerDetailNotNew = false;
       //sprawdza czy istnieje klient albo warsztat z takim username
       if(checkIfUsernameExist(customerData.getUsername())) {
           //zeruje pole username objektu który będzie wysłany na Front End
            customerData.setUsername(null);
            throw new UserAlreadyExistException(messageSource.getMessage("user.already.exist.exception"
                    , null, LocaleContextHolder.getLocale()));
        }
       //sprawdza czy istnieje klient z takim email
       if(checkIfEmailExist(customerData.getEmail())) {
           if (customerDetailService.isCustomerInCustomerDetail(customerDetailService.findByEmail(customerData.getEmail()))) {
               customerData.setEmail(null);
               throw new EmailAlreadyExistException(messageSource.getMessage("email.already.exist.exception"
                       , null, LocaleContextHolder.getLocale()));
           } else {
               isCustomerDetailNotNew = true;
           }
       }
        Customer customer = new Customer();
        BeanUtils.copyProperties(customerData, customer);
        CustomerDetail customerDetail = new CustomerDetail();
        BeanUtils.copyProperties(customerData, customerDetail);
        encodePassword(customerData, customer);
        customer.setAuthorities
                (Stream.of(authorityRepository.findByAuthority(AuthorityEnum.ROLE_CUSTOMER))
                .collect(Collectors.toSet()));
        customer.setCustomerDetail(customerDetail);
        customerDetailService.save(customerDetail);
        customerRepository.save(customer);
        if(isCustomerDetailNotNew) {
            customerDetailService.sendNotNewCustomerDetailEmailVerificationEmail(customerDetail);
        }
        else {
            customerDetailService.sendCustomerDetailEmailVerificationEmail(customerDetail);
        }
    }

    private void encodePassword(CustomerData source, Customer target) {
        target.setPassword(passwordEncoder.encode(source.getPassword()));
    }

    @Override
    public void update(CustomerData customerData, String oldUsername, String oldEmail)
            throws UserAlreadyExistException, EmailAlreadyExistException {
        boolean isEmailChange = false;
        boolean isUsernameChange = false;
        //jeżeli username został zmieniony
        if(!customerData.getUsername().equals(oldUsername)) {
            isUsernameChange = true;
            //sprawdza czy nowy username nie jest zajęty przez innego użytkownika
            if(checkIfUsernameExist(customerData.getUsername())) {
                customerData.setUsername(null);
                throw new UserAlreadyExistException(messageSource.getMessage("user.already.exist.exception"
                        , null, LocaleContextHolder.getLocale()));
            }
        }
        //jeżeli email został zmieniony
        if(!customerData.getEmail().equals(oldEmail)) {
            isEmailChange = true;
            //sprawdza czy email adres nie jest wykorzystany przez innego klienta
            if (checkIfEmailExist(customerData.getEmail())) {
                customerData.setEmail(null);
                throw new EmailAlreadyExistException(messageSource.getMessage("email.already.exist.exception"
                        , null, LocaleContextHolder.getLocale()));
            }
        }
        //jeżeli username został zmieniony aktualizuje nowy username w bazie danych
        if(isUsernameChange) {
            Customer customer = customerRepository.findCustomerByUsername(oldUsername);
            customer.setUsername(customerData.getUsername());
            customerRepository.save(customer);
        }
        //aktualizuje wszystkie dane klienta
        CustomerDetail customerDetail =
                customerRepository.findCustomerByUsername(customerData.getUsername()).getCustomerDetail();
        BeanUtils.copyProperties(customerData, customerDetail);
        //jeżeli email został zmieniony, konto nie będzie ważne do weryfikacji nowego adresu email
        if(isEmailChange) {
            customerDetail.setAccountVerified(false);
            customerDetailService.save(customerDetail);
            customerDetailService.sendCustomerDetailEmailVerificationEmail(customerDetail);
        }
        else {
            customerDetailService.save(customerDetail);
        }
    }

    @Override
    public CustomerData getCustomerDataByUsername(String username) {
        CustomerData customerData = new CustomerData();
        Customer customer = customerRepository.findCustomerByUsername(username);
        BeanUtils.copyProperties(customer, customerData);
        BeanUtils.copyProperties(customerDetailService.findByCustomerUsername(username), customerData);
        customerData.setMatchingPassword(customer.getPassword());
        customerData.setCustomerId(customer.getId());
        return customerData;
    }

    @Override
    public List<CustomerData> getAllCustomerDataList() {
       List<CustomerData> allCustomerDataList = new ArrayList<>();
       List<Customer> allCustomerList = customerRepository.findAll();
       allCustomerList.remove(customerRepository.findCustomerById(1));
       for(Customer customer : allCustomerList) {
           allCustomerDataList.add(getCustomerDataByUsername(customer.getUsername()));
       }
       return allCustomerDataList;
    }

    @Override
    public void deleteByUsername(String username) {
       Customer customer = customerRepository.findCustomerByUsername(username);
       //usuwa wszystkie samochody przypisane do klienta
       if(isCarInCustomer(customer)) {
           for (Car car : customer.getCars()) {
               carService.deleteByCarAndCustomer(car, customer);
           }
       }

       //usuwa wszystkie zlecenia klienta
        if(customerDetailService.isOrderInCustomerDetail(customer.getCustomerDetail())) {
            for (Order order : customer.getCustomerDetail().getOrders()) {
                orderService.deleteOrderFromCustomerByOrder(order);
            }
        }
        CustomerDetail customerDetail = customer.getCustomerDetail();
        customerRepository.save(customer);
        customerRepository.deleteCustomerById(customer.getId());
        customerDetailService.delete(customerDetail);

    }

    @Override
    public void deleteCustomerCarByCarIdAndUsername(Integer carId, String username) {
        Car car = carService.findById(carId);
        carService.deleteByCarAndCustomer(car, findByUsername(username));
    }

    @Override
    public void save(Customer customer) {
        customerRepository.save(customer);
    }

    @Override
    public Customer findByUsername(String username) {
        Customer customer = customerRepository.findCustomerByUsername(username);

        return customer;
    }

    @Override
    public Customer findById(Integer id) {
        Customer customer = customerRepository.findCustomerById(id);

        return customer;
    }

    @Override
    public void changePasswordByUsername(String username, String password) {
        Customer customer = findByUsername(username);
        customer.setPassword(passwordEncoder.encode(password));
        save(customer);
    }

    private boolean checkIfUsernameExist(String username) {
        return customerRepository.findCustomerByUsername(username) != null || workshopService.findByUsername(username) != null ? true : false;
    }

    private boolean checkIfEmailExist(String email) {
        return customerDetailService.findByEmail(email) != null ? true : false;
    }

    @Override
    public boolean isCarInCustomer(Customer customer) {
       return !Objects.isNull(customer.getCars());
    }

    @Override
    public boolean isCustomerInCustomerDetail(CustomerDetail customerDetail) {
       return !Objects.isNull(customerDetail.getCustomer());
    }
}
