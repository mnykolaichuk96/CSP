package mnykolaichuk.prz.pracaDyplomowa.service.impl;

import mnykolaichuk.prz.pracaDyplomowa.dao.OrderRepository;
import mnykolaichuk.prz.pracaDyplomowa.exception.EmailAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.exception.NullWorkshopInCityException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CarData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderAnswerData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderCustomerData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderWorkshopData;
import mnykolaichuk.prz.pracaDyplomowa.model.email.CreateOrderEmailContext;
import mnykolaichuk.prz.pracaDyplomowa.model.email.InformationAboutAccountDeletingEmailContext;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerDetail;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Order;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.OrderAnswer;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Workshop;
import mnykolaichuk.prz.pracaDyplomowa.model.enums.Stan;
import mnykolaichuk.prz.pracaDyplomowa.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@Service
public class OrderServiceImpl implements OrderService {
    @Autowired
    private CustomerDetailService customerDetailService;

    @Autowired
    private CarService carService;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private EmailService emailService;

    @Autowired
    private OrderAnswerService orderAnswerService;

    @Autowired
    private CityService cityService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private MessageSource messageSource;

    @Value("http://localhost:8080/workshop/showCreatedOrderList")
    private String SHOW_CREATED_ORDER_LIST_URL;

    @Value("http://localhost:8080/workshop/showImplementationOrderList")
    private String SHOW_IMPLEMENTATION_ORDER_LIST_URL;

    @Value("${site.base.url.http}")
    private String baseURL;

    @Override
    public void createOrder(String username, OrderCustomerData orderCustomerData) throws NullWorkshopInCityException {
        if(workshopService.findAllByCity(cityService.findByCityName(orderCustomerData.getCityName())).size() == 0) {
            throw new NullWorkshopInCityException(messageSource.getMessage("null.workshop.in.city.exception"
                    , null, LocaleContextHolder.getLocale()));
        }
        else {
            Order order = new Order();
            order.setDescription(orderCustomerData.getDescription());
            order.setCreationDate(orderCustomerData.getCreationDate());
            order.setCustomerDetail(customerDetailService.findByCustomerUsername(username));
            order.setCity(cityService.findByCityName(orderCustomerData.getCityName()));
            order.setCar(carService.saveForOrder(orderCustomerData.getCarData()));

            orderRepository.save(order);
            for (Workshop workshop : workshopService.findAllByCity(order.getCity())) {
                OrderAnswer tempOrderAnswer = new OrderAnswer();
                tempOrderAnswer.setStan(Stan.CREATED);
                tempOrderAnswer.setWorkshop(workshop);
                tempOrderAnswer.setOrder(order);
                orderAnswerService.save(tempOrderAnswer);
                sendToWorkshopListCreateOrderInformationEmail(tempOrderAnswer);
            }
        }
    }

    @Override
    public void sendToWorkshopListCreateOrderInformationEmail(OrderAnswer orderAnswer) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { createOrderInformationEmail(orderAnswer);

            }
        });
    }

    private void createOrderInformationEmail(OrderAnswer orderAnswer) {
        CreateOrderEmailContext emailContext = new CreateOrderEmailContext();
        emailContext.init(getOrderWorkshopDataByOrderAnswerId(orderAnswer.getId()));
        emailContext.setInformationUrl(SHOW_CREATED_ORDER_LIST_URL);
        try {
            emailService.sendMail(emailContext);
        }
        catch (MessagingException e) {
                e.printStackTrace();
        }
    }

    private OrderCustomerData getOrderCustomerDataByOrder(Order order) {
        OrderCustomerData orderCustomerData = new OrderCustomerData();
        orderCustomerData.setOrderId(order.getId());
        orderCustomerData.setDescription(order.getDescription());
        orderCustomerData.setCreationDate(order.getCreationDate());
        orderCustomerData.setCityName(order.getCity().getCityName());
        if(order.getOrderAnswers() == null) {
            order.setOrderAnswers(null);
        } else {
            orderCustomerData.setOrderAnswerDataList
                    (orderAnswerService.getOrderAnswerDataListByOrderAnswerList(order.getOrderAnswers()));
        }
        orderCustomerData.setCarData(carService.getCarData(order.getCar()));

        return orderCustomerData;
    }

    @Override
    public void createOrderFromUnregisteredUser(OrderWorkshopData orderWorkshopData) throws EmailAlreadyExistException, NullWorkshopInCityException {
        boolean notNew = false;

        if(workshopService.findAllByCity(cityService.findByCityName(orderWorkshopData.getCityName())).size() == 0) {
            throw new NullWorkshopInCityException(messageSource.getMessage("null.workshop.in.city.exception"
                    , null, LocaleContextHolder.getLocale()));
        }
        if(customerDetailService.findByEmail(orderWorkshopData.getCustomerDetailData().getEmail()) != null) {
            if (customerDetailService.isCustomerInCustomerDetail(customerDetailService.findByEmail(orderWorkshopData.getCustomerDetailData().getEmail()))) {
                throw new EmailAlreadyExistException(messageSource.getMessage("email.already.exist.exception.account.exist"
                        , null, LocaleContextHolder.getLocale()));
            }
        }
        CustomerDetail customerDetail = new CustomerDetail();
        customerDetail.setFirstName(orderWorkshopData.getCustomerDetailData().getFirstName());
        customerDetail.setLastName(orderWorkshopData.getCustomerDetailData().getLastName());
        customerDetail.setEmail(orderWorkshopData.getCustomerDetailData().getEmail());
        customerDetail.setPhoneNumber(orderWorkshopData.getCustomerDetailData().getPhoneNumber());

        CarData carData = new CarData();
        carData.setMake(orderWorkshopData.getCarData().getMake());
        carData.setModel(orderWorkshopData.getCarData().getModel());
        carData.setYear(orderWorkshopData.getCarData().getYear());
        carData.setEngineType(orderWorkshopData.getCarData().getEngineType());
        carData.setRegistrationNumber(orderWorkshopData.getCarData().getRegistrationNumber());
        carData.setVin(orderWorkshopData.getCarData().getVin());

        carService.saveForOrder(carData);

        Order order = new Order();
        order.setDescription(orderWorkshopData.getDescription());
        order.setCreationDate(orderWorkshopData.getCreationDate());
        order.setCustomerDetail(customerDetail);
        order.setCity(cityService.findByCityName(orderWorkshopData.getCityName()));
        order.setCar(carService.findCarByVinAndRegistrationNumberAndCustomersIsNull(carData.getVin(), carData.getRegistrationNumber()));

        if(customerDetailService.findByEmail(orderWorkshopData.getCustomerDetailData().getEmail()) != null) {
            notNew = true;
        }

        customerDetail.setOrders(Arrays.asList(order));
        customerDetailService.save(customerDetail);
        orderRepository.save(order);

        if(notNew) {
            customerDetailService.sendNotNewCustomerDetailEmailVerificationEmail(customerDetail);
        } else {
            customerDetailService.sendCustomerDetailEmailVerificationEmail(customerDetail);
        }
        for(Workshop workshop : workshopService.findAllByCity(order.getCity())) {
            OrderAnswer tempOrderAnswer = new OrderAnswer();
            tempOrderAnswer.setStan(Stan.UNREGISTERED);
            tempOrderAnswer.setWorkshop(workshop);
            tempOrderAnswer.setOrder(order);
            orderAnswerService.save(tempOrderAnswer);
        }
    }

    @Override
    public List<OrderCustomerData> getOrderCustomerDataListByUsernameAndStanIsNotCompleted(String username) {
        return getOrderCustomerDataListByUsernameAndStan(username, false);
    }

    @Override
    public OrderCustomerData getOrderCustomerDataByOrderAndStanEqualsWorkshopAnswer(Order order) {
        OrderCustomerData orderCustomerData = getOrderCustomerDataByOrder(order);
        List<OrderAnswerData> orderAnswerDataList = new ArrayList<>();
        for(OrderAnswerData orderAnswerData : orderCustomerData.getOrderAnswerDataList()){
            if(orderAnswerData.getStan() == Stan.WORKSHOP_ANSWER) {
                orderAnswerDataList.add(orderAnswerData);
            }
        }
        orderCustomerData.setOrderAnswerDataList(orderAnswerDataList);
        return orderCustomerData;
    }

    @Override
    public List<OrderCustomerData> getOrderCustomerDataListByUsernameAndStanEqualsCompleted(String username) {
        return getOrderCustomerDataListByUsernameAndStan(username, true);
    }

    private List<OrderCustomerData> getOrderCustomerDataListByUsernameAndStan(String username, boolean ifOrderCompleted) {
        CustomerDetail customerDetail =
                customerDetailService.findByCustomerUsername(username);
        List<Order> orders = orderRepository.findAllByCustomerDetail(customerDetail);
        List<OrderCustomerData> orderCustomerDataList = new ArrayList<>();
        for(Order order : orders) {
            if(!(ifOrderCompleted ^ checkIfOrderCompleted(order))) {
                orderCustomerDataList.add(getOrderCustomerDataByOrder(order));
            }
        }
        return orderCustomerDataList;
    }

    private boolean checkIfOrderCompleted(Order order) {
        if(!isOrderAnswerInOrder(order)){
            return false;
        }
        for(OrderAnswer orderAnswer : order.getOrderAnswers()) {
            if(orderAnswer.getStan() == Stan.COMPLETED){
                return true;
            }
        }
        return false;
    }

    @Override
    public OrderCustomerData getOrderCustomerDataByOrderId(Integer id) {
        OrderCustomerData orderCustomerData = new OrderCustomerData();
        Order order = findOrderById(id);
        OrderAnswer orderAnswer = order.getOrderAnswers().get(0);
        orderCustomerData.setDescription(order.getDescription());
        orderCustomerData.setCreationDate(order.getCreationDate());
        orderCustomerData.setCityName(order.getCity().getCityName());
        orderCustomerData.setOrderId(id);
        orderCustomerData.setOrderAnswerDataList(Arrays.asList(orderAnswerService.getOrderAnswerData(orderAnswer)));
        orderCustomerData.setCarData(carService.getCarData(order.getCar()));

        return orderCustomerData;
    }

    @Override
    public List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStanEqualsCreated(String username) {
        return getOrderWorkshopDataListByUsernameAndStan(username, Stan.CREATED);
    }

    @Override
    public List<OrderWorkshopData>
    getOrderWorkshopDataListByUsernameAndStanEqualsImplementation(String username) {
        return getOrderWorkshopDataListByUsernameAndStan(username, Stan.IMPLEMENTATION);
    }

    @Override
    public List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStanEqualsCompleted(String username) {
        return getOrderWorkshopDataListByUsernameAndStan(username, Stan.COMPLETED);
    }

    private List<OrderWorkshopData> getOrderWorkshopDataListByUsernameAndStan(String username, Stan stan) {
        boolean isUnregistered = false;
        List<OrderAnswer> orderAnswers = orderAnswerService.findAllByWorkshopUsername(username);
        List<OrderWorkshopData> orderWorkshopDataList = new ArrayList<>();
        OrderWorkshopData orderWorkshopData;
        for(OrderAnswer orderAnswer : orderAnswers) {
            if(stan == Stan.CREATED && orderAnswer.getStan() == Stan.UNREGISTERED) {
                isUnregistered = true;
            }
            if(isCustomerDetailInOrder(orderAnswer.getOrder())) {
                if (orderAnswer.getStan() == stan || (isUnregistered && orderAnswer.getOrder().getCustomerDetail().isAccountVerified())) {
                    orderWorkshopData = new OrderWorkshopData();
                    orderWorkshopData.setOrderAnswerId(orderAnswer.getId());
                    orderWorkshopData.setDescription(orderAnswer.getOrder().getDescription());
                    orderWorkshopData.setCreationDate(orderAnswer.getOrder().getCreationDate());
                    orderWorkshopData.setCityName(orderAnswer.getOrder().getCity().getCityName());
                    orderWorkshopData.setCarData(carService.getCarData(orderAnswer.getOrder().getCar()));
                    orderWorkshopData.setCustomerDetailData(customerDetailService
                            .getCustomerDetailData(orderAnswer.getOrder().getCustomerDetail()));
                    orderWorkshopData.setOrderAnswerData(orderAnswerService.getOrderAnswerData(orderAnswer));

                    orderWorkshopDataList.add(orderWorkshopData);
                }
            }
            else {
                if (orderAnswer.getStan() == stan || isUnregistered) {
                    orderWorkshopData = new OrderWorkshopData();
                    orderWorkshopData.setOrderAnswerId(orderAnswer.getId());
                    orderWorkshopData.setDescription(orderAnswer.getOrder().getDescription());
                    orderWorkshopData.setCreationDate(orderAnswer.getOrder().getCreationDate());
                    orderWorkshopData.setCityName(orderAnswer.getOrder().getCity().getCityName());
                    orderWorkshopData.setCarData(carService.getCarData(orderAnswer.getOrder().getCar()));
                    orderWorkshopData.setOrderAnswerData(orderAnswerService.getOrderAnswerData(orderAnswer));

                    orderWorkshopDataList.add(orderWorkshopData);
                }
            }
            isUnregistered = false;
        }
        return orderWorkshopDataList;
    }

    @Override
    public OrderWorkshopData getOrderWorkshopDataByOrderAnswerId(Integer id) {
        OrderWorkshopData orderWorkshopData = new OrderWorkshopData();
        OrderAnswer orderAnswer = orderAnswerService.findById(id);
        Order order = orderAnswer.getOrder();
        orderWorkshopData.setDescription(order.getDescription());
        orderWorkshopData.setCreationDate(order.getCreationDate());
        orderWorkshopData.setOrderAnswerId(id);
        orderWorkshopData.setCityName(order.getCity().getCityName());
        orderWorkshopData.setCustomerDetailData(customerDetailService.getCustomerDetailData(order.getCustomerDetail()));
        orderWorkshopData.setCarData(carService.getCarData(order.getCar()));
        orderWorkshopData.setOrderAnswerData(orderAnswerService.getOrderAnswerData(orderAnswer));
        return orderWorkshopData;
    }

    @Override
    public Order findOrderById(Integer id) {
        try {
            return orderRepository.findOrderById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void save(Order order) {
        orderRepository.save(order);
    }

    @Override
    public void delete(Order order) {
        orderRepository.deleteOrderById(order.getId());
    }

    @Override
    public void deleteOrderFromCustomerByOrder(Order order) {

        //jeżeli wszystkie warsztaty usuną swoje odpowiedzi to liczba OrderAnswer będzie równa null oraz jest to możliwe tylko w przypadku 'CREATED' oraz 'WORKSHOP_ANSWER'
        // nie będzie przypisanych warsztatów i takie zlecenie usuwa się z bazy danych
        if(!isOrderAnswerInOrder(order)) {
        carService.deleteByOrder(order);
        orderRepository.deleteOrderById(order.getId());
        } else if(order.getOrderAnswers().size() == 0) {
            carService.deleteByOrder(order);
            orderRepository.deleteOrderById(order.getId());
        }

        else {
            //Jeżeli stan zlecenia jest 'IMPLEMENTATION' lub 'CREATED' on ma jeden przypisany objekt OrderAnswer
            if (order.getOrderAnswers().size() == 1
                    && order.getOrderAnswers().get(0).getStan() != Stan.CREATED && order.getOrderAnswers().get(0).getStan() != Stan.WORKSHOP_ANSWER
                    && order.getOrderAnswers().get(0).getStan() != Stan.UNREGISTERED) {
                switch (order.getOrderAnswers().get(0).getStan()) {
                    case IMPLEMENTATION:
                        //admin może usunąć warsztat, chociaż on ma zlecenie w stanie 'IMPLEMENTATION'
                        //sprawdzenie czy zlecenie nie zostało w taki sposób usunięte
                        if (!orderAnswerService.isWorkshopInOrderAnswer(order.getOrderAnswers().get(0))) {
                            carService.deleteByOrder(order);
                            orderAnswerService.delete(order.getOrderAnswers().get(0));
                            orderRepository.deleteOrderById(order.getId());
                        }
                        //jeżeli jest przypisany warsztat, wysyłamy na jego adres elektroniczny informację o usunięcu konta klienta
                        //zerujemy relację Zlecenie-Klient
                        else {
                            sendInformationAboutAccountDeletingEmail(order, order.getCustomerDetail());
                            order.setCustomerDetail(null);
                            orderRepository.save(order);
                        }
                        break;
                    case COMPLETED:
                        //jeżeli nie ma przypisanego warsztatu usuwamy zlecenie z bazy danych
                        if (!orderAnswerService.isWorkshopInOrderAnswer(order.getOrderAnswers().get(0))) {
                            carService.deleteByOrder(order);
                            orderAnswerService.delete(order.getOrderAnswers().get(0));
                            orderRepository.deleteOrderById(order.getId());
                        }
                        //jeżeli jest przypisany warsztat, zeruje relacje Klient-Zlecenie
                        else {
                            order.setCustomerDetail(null);
                            orderRepository.save(order);
                        }
                        break;
                }

            }
            //jeżeli zlecenie ma więcej jednego przypisanego OrderAnser stan równy 'CREATED' lub 'WORKSHOP_ANSWER' lub 'UNREGISTERED'
            //jeżeli wszystkie warsztaty usuną swoje odpowiedzi to nie będzie przypisanych warsztatów i takie zlecenie usuwa się z bazy danych

            //zostały zlecenie o stanie równym 'CREATED', 'UNREGISTERED' lub 'WORKSHOP_ANSWER'. Oni są usuwane z bazy danych
            else {
                carService.deleteByOrder(order);
                for (OrderAnswer orderAnswer : order.getOrderAnswers()) {
                    orderAnswerService.delete(orderAnswer);
                }
                orderRepository.deleteOrderById(order.getId());
            }
        }
    }

    private void sendInformationAboutAccountDeletingEmail(Order order, CustomerDetail customerDetail) {
        InformationAboutAccountDeletingEmailContext emailContext = new InformationAboutAccountDeletingEmailContext();
        emailContext.init(getOrderCustomerDataByOrder(order), customerDetailService.getCustomerDetailData(customerDetail));
        emailContext.setInformationUrl(SHOW_IMPLEMENTATION_ORDER_LIST_URL);
        try {
            emailService.sendMail(emailContext);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isCarInOrder(Order order) {
        return !Objects.isNull(order.getCar());
    }

    @Override
    public boolean isOrderAnswerInOrder(Order order) {
        return !Objects.isNull(order.getOrderAnswers());
    }

    @Override
    public boolean isCustomerDetailInOrder(Order order) {
        return !Objects.isNull(order.getCustomerDetail());
    }
}
