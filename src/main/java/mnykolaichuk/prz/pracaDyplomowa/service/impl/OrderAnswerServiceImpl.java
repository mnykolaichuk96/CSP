package mnykolaichuk.prz.pracaDyplomowa.service.impl;

import mnykolaichuk.prz.pracaDyplomowa.dao.OrderAnswerRepository;
import mnykolaichuk.prz.pracaDyplomowa.exception.CantDeleteWorkshopWhileImplementationExistException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderAnswerData;
import mnykolaichuk.prz.pracaDyplomowa.model.email.*;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Order;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.OrderAnswer;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.SecureToken;
import mnykolaichuk.prz.pracaDyplomowa.model.enums.Stan;
import mnykolaichuk.prz.pracaDyplomowa.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.TaskExecutor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.*;

@Service
public class OrderAnswerServiceImpl implements OrderAnswerService {

    @Autowired
    private OrderAnswerRepository orderAnswerRepository;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private CarService carService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private MessageSource messageSource;

    @Value("http://localhost:8080/customer/showOrderList")
    private String SHOW_ORDER_LIST_URL;

    @Value("http://localhost:8080/workshop/showImplementationOrderList")
    private String SHOW_IMPLEMENTATION_ORDER_LIST_URL;

    @Value("http://localhost:8080/customer/showCompletedOrderList")
    private String SHOW_COMPLETED_ORDER_LIST_URL;

    @Value("http://localhost:8080")
    private String BASE_URL;

    @Autowired
    private KafkaTemplate<String, Object> kafkaTemplate;
    private final String sendOfferTopic = "sendOffer";
    private final String choseOfferTopic = "choseOffer";

    @Override
    public OrderAnswerData getOrderAnswerData(OrderAnswer orderAnswer) {
        OrderAnswerData orderAnswerData = new OrderAnswerData();
        BeanUtils.copyProperties(orderAnswer, orderAnswerData);
        orderAnswerData.setOrderAnswerId(orderAnswer.getId());
        if(isWorkshopInOrderAnswer(orderAnswer)) {
            orderAnswerData.setWorkshopData
                    (workshopService.getWorkshopDataByUsername(orderAnswer.getWorkshop().getUsername()));
        }
        return orderAnswerData;
    }

    @Override
    public List<OrderAnswerData> getOrderAnswerDataListByOrderAnswerList(List<OrderAnswer> orderAnswerList) {
        List<OrderAnswerData> orderAnswerDataList = new ArrayList<>();
        for(OrderAnswer orderAnswer : orderAnswerList) {
            orderAnswerDataList.add(getOrderAnswerData(orderAnswer));
        }
        return orderAnswerDataList;
    }

    @Override
    public OrderAnswer findById(Integer id) {
        try {
            return orderAnswerRepository.findOrderAnswerById(id);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<OrderAnswer> findAllByWorkshopUsername(String username) {
        try {
            return orderAnswerRepository.findAllByWorkshopUsername(username);
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public void createWorkshopAnswerByOrderAnswerData(OrderAnswerData orderAnswerData) {
        OrderAnswer orderAnswer = findById(orderAnswerData.getOrderAnswerId());
        boolean isUnregistered = orderAnswer.getStan() == Stan.UNREGISTERED ? true : false;
        orderAnswer.setImplementationDate(orderAnswerData.getImplementationDate());
        orderAnswer.setPrice(orderAnswerData.getPrice());
        orderAnswer.setStan(Stan.WORKSHOP_ANSWER);
        orderAnswerRepository.save(orderAnswer);
        if(isUnregistered) {
            sendStanWorkshopAnswerUnregistered(orderAnswer);
        } else {
            // Sending email with offer
            Map<String, String> messageData = new HashMap<>();

            messageData.put("WorkshopEmail", orderAnswer.getWorkshop().getEmail());
            messageData.put("WorkshopPhoneNumber", orderAnswer.getWorkshop().getPhoneNumber());
            messageData.put("WorkshopAddress", orderAnswer.getWorkshop().getAddress());

            messageData.put("CarRegistry", orderAnswer.getOrder().getCar().getRegistrationNumber());
            messageData.put("ImplementationDate", orderAnswer.getImplementationDate().toString());
            messageData.put("Price", String.valueOf(orderAnswer.getPrice()));

            messageData.put("CustomerEmail", orderAnswer.getOrder().getCustomerDetail().getEmail());
            messageData.put("CustomerPhoneNumber", orderAnswer.getOrder().getCustomerDetail().getPhoneNumber());

            kafkaTemplate.send(sendOfferTopic, messageData);
            sendStanChangeEmail(orderAnswer);
        }
    }

    @Override
    public void chooseOrderAnswerForImplementation(OrderAnswer orderAnswer) {
        orderAnswer.setStan(Stan.IMPLEMENTATION);
        orderAnswerRepository.save(orderAnswer);
        for(OrderAnswer tempOrderAnswer : orderAnswer.getOrder().getOrderAnswers()){
            if(tempOrderAnswer.getId() != orderAnswer.getId()) {
                orderAnswerRepository.deleteOrderAnswerById(tempOrderAnswer.getId());
            }
        }
        // Customer chose offer
        Map<String, String> messageData = new HashMap<>();

        messageData.put("CustomerEmail", orderAnswer.getOrder().getCustomerDetail().getEmail());
        messageData.put("CustomerPhoneNumber", orderAnswer.getOrder().getCustomerDetail().getPhoneNumber());

        messageData.put("CarRegistration", orderAnswer.getOrder().getCar().getRegistrationNumber());
        messageData.put("CarMake", orderAnswer.getOrder().getCar().getCarModel().getCarMake().getMake());
        messageData.put("CarModel", orderAnswer.getOrder().getCar().getCarModel().getModel());
        messageData.put("CarYear", orderAnswer.getOrder().getCar().getYear().toString());

        messageData.put("WorkshopEmail", orderAnswer.getWorkshop().getEmail());
        messageData.put("WorkshopPhoneNumber", orderAnswer.getWorkshop().getPhoneNumber());

        kafkaTemplate.send(choseOfferTopic, messageData);
        sendStanChangeEmail(orderAnswer);
    }

    @Override
    public void chooseOrderAnswerForCompleted(OrderAnswer orderAnswer) {
        orderAnswer.setStan(Stan.COMPLETED);
        orderAnswerRepository.save(orderAnswer);
        for (OrderAnswer tempOrderAnswer : orderAnswer.getOrder().getOrderAnswers()) {
            if(tempOrderAnswer.getId() != orderAnswer.getId()) {
                orderAnswerRepository.deleteOrderAnswerById(tempOrderAnswer.getId());
            }
        }
    }

    private void sendStanWorkshopAnswerUnregistered(OrderAnswer orderAnswer) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                stanWorkshopAnswerUnregistered(orderAnswer);
            }
        });
    }

    private void stanWorkshopAnswerUnregistered(OrderAnswer orderAnswer) {
        SecureToken secureToken = secureTokenService.createSecureToken();
        //wykorzystamy pole verificationCarId w celu zapisania orderAnswerId
        secureToken.setVerificationCarId(orderAnswer.getId());
        secureTokenService.saveSecureToken(secureToken);
        WorkshopAnswerUnregisteredEmailContext emailContext = new WorkshopAnswerUnregisteredEmailContext();
        emailContext.init(orderService.getOrderWorkshopDataByOrderAnswerId(orderAnswer.getId()));
        emailContext.buildOfferChooseUrl(BASE_URL, secureToken.getToken());

        try {
            emailService.sendMail(emailContext);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void sendStanChangeEmail(OrderAnswer orderAnswer) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() {
                switch (orderAnswer.getStan()) {
                    case WORKSHOP_ANSWER:stanWorkshopAnswerEmail(orderAnswer);
                        break;
                    case IMPLEMENTATION:stanImplementationEmail(orderAnswer);
                        break;
                    case COMPLETED:stanCompletedEmail(orderAnswer);
                        break;
                }
            }
        });
    }

    private void stanWorkshopAnswerEmail(OrderAnswer orderAnswer) {
        WorkshopAnswerEmailContext emailContext = new WorkshopAnswerEmailContext();
        emailContext.init(orderService.getOrderWorkshopDataByOrderAnswerId(orderAnswer.getId()));
        emailContext.setInformationUrl(SHOW_ORDER_LIST_URL);

        try {
            emailService.sendMail(emailContext);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void stanImplementationEmail(OrderAnswer orderAnswer) {
        ImplementationEmailContext emailContext = new ImplementationEmailContext();
        emailContext.init(orderService.getOrderWorkshopDataByOrderAnswerId(orderAnswer.getId()));
        emailContext.setInformationUrl(SHOW_IMPLEMENTATION_ORDER_LIST_URL);

        try {
            emailService.sendMail(emailContext);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    private void stanCompletedEmail(OrderAnswer orderAnswer) {
        if(!Objects.isNull(orderAnswer.getOrder().getCustomerDetail())) {
            CompletedEmailContext emailContext = new CompletedEmailContext();
            emailContext.init(
                    orderService.getOrderWorkshopDataByOrderAnswerId(orderAnswer.getId())
                    , workshopService.getWorkshopDataByUsername(orderAnswer.getWorkshop().getUsername()));
            emailContext.setInformationUrl(SHOW_COMPLETED_ORDER_LIST_URL);

            try {
                emailService.sendMail(emailContext);
            } catch (MessagingException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void save(OrderAnswer orderAnswer) {
        orderAnswerRepository.save(orderAnswer);
    }

    @Override
    public void delete(OrderAnswer orderAnswer) {
        orderAnswerRepository.deleteOrderAnswerById(orderAnswer.getId());
    }

    @Override
    public void deleteOrderAnswerFromWorkshopByOrderAnswerAndIsAdmin(OrderAnswer orderAnswer, Boolean isAdmin) throws CantDeleteWorkshopWhileImplementationExistException {
        if(orderAnswer.getStan() == Stan.CREATED || orderAnswer.getStan() == Stan.UNREGISTERED || orderAnswer.getStan() == Stan.WORKSHOP_ANSWER) {
            //jeżeli tylko aktualnie zalogowany warsztat ma to zlecenie
            if(orderAnswer.getOrder().getOrderAnswers().size() == 1) {
                sendNoMoreOrderAnswerEmail(orderAnswer.getOrder());
                orderAnswer.setStan(Stan.COMPLETED);
                orderAnswer.setWorkshop(null);
                save(orderAnswer);
            } else {
                orderAnswer.setOrder(null);
                save(orderAnswer);
                delete(orderAnswer);
            }
        } else if(orderAnswer.getStan() == Stan.IMPLEMENTATION && !isAdmin) {
            throw new CantDeleteWorkshopWhileImplementationExistException(messageSource.getMessage("cant.delete.workshop.while.implementation.exist.exception"
                    , null, LocaleContextHolder.getLocale()));
            } else {
                deleteImplementationOrCompletedByOrderAnswer(orderAnswer);
            }
        if(orderAnswer.getStan() == Stan.COMPLETED) {
            deleteImplementationOrCompletedByOrderAnswer(orderAnswer);
        }
    }

    private void deleteImplementationOrCompletedByOrderAnswer(OrderAnswer orderAnswer) {
        //jeżeli nie ma przypisanego CustomerDetail usuwamy z bazy danych
        if(!orderService.isCustomerDetailInOrder(orderAnswer.getOrder())) {
            carService.deleteByOrder(orderAnswer.getOrder());
            Order order = orderAnswer.getOrder();
            delete(orderAnswer);
            orderService.delete(order);
        }
        else {
            orderAnswer.setWorkshop(null);
            save(orderAnswer);
        }
    }

    private void sendNoMoreOrderAnswerEmail(Order order) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { noMoreOrderAnswerEmail(order);

            }
        });
    }

    private void noMoreOrderAnswerEmail(Order order) {
        NoMoreOrderAnswerEmailContext emailContext = new NoMoreOrderAnswerEmailContext();
        emailContext.init(orderService.getOrderWorkshopDataByOrderAnswerId(order.getOrderAnswers().get(0).getId()));
        emailContext.setInformationUrl(SHOW_COMPLETED_ORDER_LIST_URL);
        try {
            emailService.sendMail(emailContext);
        }
        catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isWorkshopInOrderAnswer(OrderAnswer orderAnswer) {
        return !Objects.isNull(orderAnswer.getWorkshop());
    }
}


