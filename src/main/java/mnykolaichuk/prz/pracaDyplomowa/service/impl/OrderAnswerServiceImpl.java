package mnykolaichuk.prz.pracaDyplomowa.service.impl;

import mnykolaichuk.prz.pracaDyplomowa.dao.OrderAnswerRepository;
import mnykolaichuk.prz.pracaDyplomowa.exception.CantDeleteWorkshopWhileImplementationExistException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderAnswerData;
import mnykolaichuk.prz.pracaDyplomowa.model.email.CompletedEmailContext;
import mnykolaichuk.prz.pracaDyplomowa.model.email.ImplementationEmailContext;
import mnykolaichuk.prz.pracaDyplomowa.model.email.NoMoreOrderAnswerEmailContext;
import mnykolaichuk.prz.pracaDyplomowa.model.email.WorkshopAnswerEmailContext;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Order;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.OrderAnswer;
import mnykolaichuk.prz.pracaDyplomowa.model.enums.Stan;
import mnykolaichuk.prz.pracaDyplomowa.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class OrderAnswerServiceImpl implements OrderAnswerService {

    @Autowired
    private OrderAnswerRepository orderAnswerRepository;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private CustomerService customerService;

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
        OrderAnswer orderAnswer =
                orderAnswerRepository.findOrderAnswerById(orderAnswerData.getOrderAnswerId());
        orderAnswer.setImplementationDate(orderAnswerData.getImplementationDate());
        orderAnswer.setPrice(orderAnswerData.getPrice());
        orderAnswer.setStan(Stan.WORKSHOP_ANSWER);
        orderAnswerRepository.save(orderAnswer);
        sendStanChangeEmail(orderAnswer);
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
        sendStanChangeEmail(orderAnswer);
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
        }
        if(orderAnswer.getStan() == Stan.IMPLEMENTATION && !isAdmin) {
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


