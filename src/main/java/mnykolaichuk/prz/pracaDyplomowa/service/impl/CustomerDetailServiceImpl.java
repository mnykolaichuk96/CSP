package mnykolaichuk.prz.pracaDyplomowa.service.impl;

import mnykolaichuk.prz.pracaDyplomowa.dao.CustomerDetailRepository;
import mnykolaichuk.prz.pracaDyplomowa.exception.InvalidTokenException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CustomerDetailData;
import mnykolaichuk.prz.pracaDyplomowa.model.email.AccountVerificationEmailContext;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerDetail;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Order;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.OrderAnswer;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.SecureToken;
import mnykolaichuk.prz.pracaDyplomowa.service.EmailService;
import mnykolaichuk.prz.pracaDyplomowa.service.CustomerDetailService;
import mnykolaichuk.prz.pracaDyplomowa.service.OrderService;
import mnykolaichuk.prz.pracaDyplomowa.service.SecureTokenService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.TaskExecutor;
import org.springframework.dao.IncorrectResultSizeDataAccessException;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.mail.MessagingException;
import javax.persistence.NonUniqueResultException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class CustomerDetailServiceImpl implements CustomerDetailService {

    @Autowired
    private CustomerDetailRepository customerDetailRepository;

    @Autowired
    private OrderService orderService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private EmailService emailService;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private MessageSource messageSource;

    @Value("${site.base.url.http}")
    private String baseURL;

    @Override
    public CustomerDetail findByEmail(String email) {
        CustomerDetail customerDetail;
        try {
            customerDetail = customerDetailRepository.findCustomerDetailByEmail(email);
            return customerDetail;
        } catch (NullPointerException e) {
            return null;
            //jeżeli więcej niż jeden wynik wracamy CustomerDetail danych dodanych przy wcześniejszym tworzeniu zlecenia
        } catch (IncorrectResultSizeDataAccessException | NonUniqueResultException e) {
            List <CustomerDetail> customerDetails = customerDetailRepository.findAllByEmail(email);
            for(CustomerDetail customerDetailWithUnregisteredStan : customerDetails) {
                if(isOrderInCustomerDetail(customerDetailWithUnregisteredStan) && (!isCustomerInCustomerDetail(customerDetailWithUnregisteredStan))) {
                    return customerDetailWithUnregisteredStan;
                }
            }
        }
        return null;
    }

    @Override
    public CustomerDetail findByCustomerUsername(String username) {
        try {
            return customerDetailRepository.findCustomerDetailByCustomerUsername(username);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public CustomerDetail findById(Integer id) {
        try {
            return customerDetailRepository.findCustomerDetailById(id);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public void delete(CustomerDetail customerDetail) {
        SecureToken secureToken = secureTokenService.findByCustomerDetail(customerDetail);
        if(secureToken != null) {
            secureToken.setCustomerDetail(null);
            secureTokenService.saveSecureToken(secureToken);
        }
        customerDetailRepository.deleteCustomerDetailById(customerDetail.getId());
    }

    @Override
    public void save(CustomerDetail customerDetail) {
        customerDetailRepository.save(customerDetail);
    }

    @Override
    public CustomerDetailData getCustomerDetailData(CustomerDetail customerDetail) {
        CustomerDetailData customerDetailData = new CustomerDetailData();
        if(customerDetail == null) {
            return null;
        }
        BeanUtils.copyProperties(customerDetail, customerDetailData);
        return customerDetailData;
    }

    @Override
    public void sendCustomerDetailEmailVerificationEmail(CustomerDetail customerDetail) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { customerDetailEmailVerificationEmail(customerDetail);

            }
        });
    }

    private void customerDetailEmailVerificationEmail(CustomerDetail customerDetail) {
        SecureToken secureToken = secureTokenService.createSecureToken();
        //przypisuje do SecurityToken nowe objekt CustomerDetail
        secureToken.setCustomerDetail(customerDetail);
        secureToken.setNotNewCustomerDetail(false);
        if(isOrderInCustomerDetail(customerDetail)) {
            if (customerDetail.getOrders().size() != 0) {
                //w pole carId zapisany orderId zeby wiedzieć jaki order będzie stworzony po veryfikacji email
                secureToken.setVerificationCarId(customerDetail.getOrders().get(0).getId());
            }
        }
        secureTokenService.saveSecureToken(secureToken);
        AccountVerificationEmailContext emailContext = new AccountVerificationEmailContext();
        emailContext.init(getCustomerDetailData(customerDetail));
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        try {
            emailService.sendMail(emailContext);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void sendNotNewCustomerDetailEmailVerificationEmail(CustomerDetail customerDetail) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { notNewCustomerDetailEmailVerificationEmail(customerDetail);

            }
        });
    }

    private void notNewCustomerDetailEmailVerificationEmail(CustomerDetail customerDetail) {
        SecureToken secureToken = secureTokenService.createSecureToken();
        //przypisuje do SecurityToken nowe objekt CustomerDetail
        secureToken.setCustomerDetail(customerDetail);
        secureToken.setNotNewCustomerDetail(true);
        if(isOrderInCustomerDetail(customerDetail)) {
            //w pole carId zapisany orderId zeby wiedzieć jaki order będzie stworzony po veryfikacji email
            secureToken.setVerificationCarId(customerDetail.getOrders().get(0).getId());
        }
        secureTokenService.saveSecureToken(secureToken);
        AccountVerificationEmailContext emailContext = new AccountVerificationEmailContext();
        emailContext.init(getCustomerDetailData(customerDetail));
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        try {
            emailService.sendMail(emailContext);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean verifyCustomer(String token) throws InvalidTokenException {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if(Objects.isNull(secureToken) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()){
            throw new InvalidTokenException(messageSource.getMessage("invalid.token.exception"
                    , null, LocaleContextHolder.getLocale()));
        }
        CustomerDetail customerDetail = findById(secureToken.getCustomerDetail().getId());
        if(Objects.isNull(customerDetail)){
            return false;
        }
        //jeżeli tokien zawiera orderId, zlecenie stworzone przez niezarejestrowanego klienta. Ono teraz będzie dostępne
        //dla warsztatów oraz na ich pocztę będzie wysłana wiadomość o nowym zleceniu.
        Integer orderId = secureToken.getVerificationCarId();

        if(orderId != null) {
            if(orderService.isOrderAnswerInOrder(orderService.findOrderById(orderId))) {
                for (OrderAnswer orderAnswer : orderService.findOrderById(orderId).getOrderAnswers()) {
                    orderService.sendToWorkshopListCreateOrderInformationEmail(orderAnswer);
                }
            }
        }
        if(secureToken.isNotNewCustomerDetail()) {
            //przepisanie dotych czas stworzonych zleceń do aktualnego CustomerDetail
            List<Order> orderList = new ArrayList<>();
            CustomerDetail oldCustomerDetail = findByEmail(customerDetail.getEmail());
            if(isOrderInCustomerDetail(oldCustomerDetail)) {
                for (Order order : oldCustomerDetail.getOrders()) {
                    order.setCustomerDetail(null);
                    orderService.save(order);
                    orderList.add(order);
                }
            }
            delete(oldCustomerDetail);
            save(customerDetail);
            for (Order order : orderList) {
                order.setCustomerDetail(customerDetail);
                orderService.save(order);
            }
        }
        customerDetail.setAccountVerified(true);

        save(customerDetail);

        secureTokenService.removeToken(secureToken);
        return true;
    }

    @Override
    public boolean isOrderInCustomerDetail(CustomerDetail customerDetail) {
       return !Objects.isNull(customerDetail.getOrders());
    }

    @Override
    public boolean isCustomerInCustomerDetail(CustomerDetail customerDetail) {
        return !Objects.isNull(customerDetail.getCustomer());
    }
}
