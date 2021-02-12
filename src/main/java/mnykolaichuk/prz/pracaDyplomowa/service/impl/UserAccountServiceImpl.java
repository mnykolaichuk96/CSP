package mnykolaichuk.prz.pracaDyplomowa.service.impl;

import mnykolaichuk.prz.pracaDyplomowa.exception.InvalidTokenException;
import mnykolaichuk.prz.pracaDyplomowa.exception.UnknowIdentifierException;
import mnykolaichuk.prz.pracaDyplomowa.model.email.ForgotPasswordEmailContext;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Customer;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.SecureToken;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Workshop;
import mnykolaichuk.prz.pracaDyplomowa.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.mail.MessagingException;
import java.util.Objects;

@Service
public class UserAccountServiceImpl implements UserAccountService {

    @Autowired
    private CustomerService customerService;

    @Autowired
    private CustomerDetailService customerDetailService;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    MessageSource messageSource;

    @Value("${site.base.url.http}")
    private String baseURL;

    @Override
    public void forgottenPassword(String username) throws UnknowIdentifierException {
        Customer customer = null;
        Workshop workshop = null;
        if(customerService.findByUsername(username) != null) {
            customer = customerService.findByUsername(username);
            sendResetPasswordEmail(customer);
        }
        else if(workshopService.findByUsername(username) != null) {
            workshop = workshopService.findByUsername(username);
            sendResetPasswordEmail(workshop);
        }
        else {
            throw new UnknowIdentifierException(messageSource.getMessage("unknow.identifier.exception"
                    , null, LocaleContextHolder.getLocale()));
        }

    }

    @Override
    public void updatePassword(String password, String token) throws InvalidTokenException, UnknowIdentifierException {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if(Objects.isNull(secureToken) || !StringUtils.pathEquals(token, secureToken.getToken()) || secureToken.isExpired()) {
            throw new InvalidTokenException(messageSource.getMessage("invalid.token.exception"
                    , null, LocaleContextHolder.getLocale()));
        }
        if(!Objects.isNull(secureToken.getCustomerDetail())) {
            if(!secureToken.getCustomerDetail().isAccountVerified()) {
                throw new UnknowIdentifierException(messageSource.getMessage("unknow.identifier.exception"
                        , null, LocaleContextHolder.getLocale()));
            }
            Customer customer = secureToken.getCustomerDetail().getCustomer();
            customer.setPassword(passwordEncoder.encode(password));
            customerService.save(customer);
        }
        else if (!Objects.isNull(secureToken.getWorkshop())) {
            if(!secureToken.getWorkshop().isAccountVerified()) {
                throw new UnknowIdentifierException(messageSource.getMessage("unknow.identifier.exception"
                        , null, LocaleContextHolder.getLocale()));
            }
            Workshop workshop = secureToken.getWorkshop();
            workshop.setPassword(passwordEncoder.encode(password));
            workshopService.save(workshop);
        }
        secureTokenService.removeToken(secureToken);
    }

    @Override
    public boolean comparePassword(String password, String encodePassword) {
        passwordEncoder = new BCryptPasswordEncoder();
        boolean isPasswordMatches = passwordEncoder.matches(password, encodePassword);
        if (isPasswordMatches)
            return true;
        return false;
    }

    private void sendResetPasswordEmail(Object user) {
        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { resetPasswordEmail(user);

            }
        });
    }

    private void resetPasswordEmail(Object user) {
        ForgotPasswordEmailContext emailContext = new ForgotPasswordEmailContext();
        SecureToken secureToken = secureTokenService.createSecureToken();
        if(user instanceof Customer) {
            Customer customer = (Customer) user;
            secureToken.setCustomerDetail(customer.getCustomerDetail());
            emailContext.init(customerDetailService.getCustomerDetailData(customer.getCustomerDetail()));
        }
        if (user instanceof Workshop) {
            Workshop workshop = (Workshop) user;
            secureToken.setWorkshop(workshop);
            emailContext.init(workshopService.getWorkshopDataByUsername(workshop.getUsername()));
        }

        secureTokenService.saveSecureToken(secureToken);
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        try {
            emailService.sendMail(emailContext);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
