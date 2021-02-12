package mnykolaichuk.prz.pracaDyplomowa.service.impl;

import mnykolaichuk.prz.pracaDyplomowa.dao.AuthorityRepository;
import mnykolaichuk.prz.pracaDyplomowa.dao.WorkshopRepository;
import mnykolaichuk.prz.pracaDyplomowa.exception.CantDeleteWorkshopWhileImplementationExistException;
import mnykolaichuk.prz.pracaDyplomowa.exception.EmailAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.exception.InvalidTokenException;
import mnykolaichuk.prz.pracaDyplomowa.exception.UserAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.WorkshopData;
import mnykolaichuk.prz.pracaDyplomowa.model.email.AccountVerificationEmailContext;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.City;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.OrderAnswer;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.SecureToken;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Workshop;
import mnykolaichuk.prz.pracaDyplomowa.model.enums.AuthorityEnum;
import mnykolaichuk.prz.pracaDyplomowa.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.core.task.TaskExecutor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import javax.mail.MessagingException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class WorkshopServiceImpl implements WorkshopService {

    @Autowired
    private WorkshopRepository workshopRepository;

    @Autowired
    private AuthorityRepository authorityRepository;

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CityService cityService;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private OrderAnswerService orderAnswerService;

    @Autowired
    private TaskExecutor taskExecutor;

    @Autowired
    private EmailService emailService;

    @Autowired
    private MessageSource messageSource;

    @Value("${site.base.url.http}")
    private String baseURL;


    @Override
    public void register(WorkshopData workshopData) throws UserAlreadyExistException, EmailAlreadyExistException {
        if(checkIfUsernameExist(workshopData.getUsername())) {
            workshopData.setUsername(null);
            throw new UserAlreadyExistException(messageSource.getMessage("user.already.exist.exception"
                    , null, LocaleContextHolder.getLocale()));
        }
        if(checkIfEmailExist(workshopData.getEmail())) {
            workshopData.setEmail(null);
            throw new EmailAlreadyExistException();
        }
        Workshop workshop = new Workshop();
        BeanUtils.copyProperties(workshopData, workshop);
        encodePassword(workshopData, workshop);
        workshop.setAuthorities(Stream.of
                (authorityRepository.findByAuthority
                (AuthorityEnum.ROLE_WORKSHOP)).collect(Collectors.toSet()));
        workshop.setCity(cityService.findByCityName(workshopData.getCityName()));
        workshopRepository.save(workshop);
        sendWorkshopRegistrationConfirmationEmail(workshop);

    }

    private void encodePassword(WorkshopData source, Workshop target) {
        target.setPassword(passwordEncoder.encode(source.getPassword()));
    }

    @Override
    public boolean verifyWorkshop(String token) throws InvalidTokenException {
        SecureToken secureToken = secureTokenService.findByToken(token);
        if(Objects.isNull(secureToken) || !StringUtils.equals(token, secureToken.getToken()) || secureToken.isExpired()){
            throw new InvalidTokenException(messageSource.getMessage("invalid.token.exception"
                    , null, LocaleContextHolder.getLocale()));
        }
        Workshop workshop = workshopRepository.getOne(secureToken.getWorkshop().getId());
        if(Objects.isNull(workshop)){
            return false;
        }
        workshop.setAccountVerified(true);
        workshopRepository.save(workshop); // let's same user details

        // we don't need invalid password now
        secureTokenService.removeToken(secureToken);
        return true;
    }

    @Override
    public void update(WorkshopData workshopData, String oldUsername, String oldEmail) throws UserAlreadyExistException, EmailAlreadyExistException {
        boolean isEmailChange = false;
        if (!workshopData.getUsername().equals(oldUsername)) {
            if (checkIfUsernameExist(workshopData.getUsername())) {
                workshopData.setUsername(null);
                throw new UserAlreadyExistException(messageSource.getMessage("user.already.exist.exception"
                        , null, LocaleContextHolder.getLocale()));
            }
        }
        if (!workshopData.getEmail().equals(oldEmail)) {
            isEmailChange = true;
            if (checkIfEmailExist(workshopData.getEmail())) {
                workshopData.setEmail(null);
                throw new EmailAlreadyExistException(messageSource.getMessage("email.already.exist.exception"
                        , null, LocaleContextHolder.getLocale()));
            }
        }

        Workshop workshop = workshopRepository.findWorkshopByUsername(oldUsername);
        BeanUtils.copyProperties(workshopData, workshop);
        workshop.setCity(cityService.findByCityName(workshopData.getCityName()));
        if (isEmailChange) {
            workshop.setAccountVerified(false);
            workshopRepository.save(workshop);
            sendWorkshopRegistrationConfirmationEmail(workshop);
        } else {
            workshopRepository.save(workshop);
        }
    }

    @Override
    public WorkshopData getWorkshopDataByUsername(String username) {
        WorkshopData workshopData = new WorkshopData();
        Workshop workshop = workshopRepository.findWorkshopByUsername(username);
        if(workshop == null) {
            return null;
        }
        BeanUtils.copyProperties(workshop, workshopData);
        workshopData.setMatchingPassword(workshop.getPassword());
        workshopData.setCityName(workshop.getCity().getCityName());
        workshopData.setWorkshopId(workshop.getId());

        return workshopData;
    }

    @Override
    public List<WorkshopData> getAllWorkshopDataList() {
        List<WorkshopData> allWorkshopDataList = new ArrayList<>();
        for(Workshop workshop : workshopRepository.findAll()) {
            allWorkshopDataList.add(getWorkshopDataByUsername(workshop.getUsername()));
        }
        return allWorkshopDataList;
    }

    @Override
    public void deleteByUsernameAndIsAdmin(String username, Boolean isAdmin)
            throws CantDeleteWorkshopWhileImplementationExistException {
        Workshop workshop = workshopRepository.findWorkshopByUsername(username);
        //usuwa wszystkie zlecenia warsztatu
        if(isOrderAnswerInWorkshop(workshop)) {
            for (OrderAnswer orderAnswer : workshop.getOrderAnswers()) {
                orderAnswerService.deleteOrderAnswerFromWorkshopByOrderAnswerAndIsAdmin(orderAnswer, isAdmin);
            }
        }
        workshopRepository.deleteById(workshop.getId());
    }

    @Override
    public void save(Workshop workshop) {
        workshopRepository.save(workshop);
    }

    @Override
    public Workshop findByUsername(String username) {
        try {
            return workshopRepository.findWorkshopByUsername(username);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public Workshop findById(Integer id) {
        try {
            return workshopRepository.findWorkshopById(id);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public List<Workshop> findAllByCity(City city) {
        List<Workshop> workshops = workshopRepository.findAllByCity(city);
        for(Workshop workshop : workshopRepository.findAllByCity(city)) {
            if(!workshop.isAccountVerified()) {
                workshops.remove(workshop);
            }
        }
        return workshops;
    }

    @Override
    public List<Workshop> findAllWithUnActiveByCity(City city) {
        try {
            return workshopRepository.findAllByCity(city);
        } catch (NullPointerException e) {
            return null;
        }
    }

    @Override
    public void changePasswordByUsername(String username, String password) {
        Workshop workshop = findByUsername(username);
        workshop.setPassword(passwordEncoder.encode(password));
        save(workshop);
    }

    private boolean checkIfUsernameExist(String username) {
        return workshopRepository.findWorkshopByUsername(username) != null || customerService.findByUsername(username) != null ? true : false;
    }

    private boolean checkIfEmailExist(String email) {
        return workshopRepository.findWorkshopByEmail(email) != null ? true : false;
    }

    private void sendWorkshopRegistrationConfirmationEmail(Workshop workshop) {

        taskExecutor.execute(new Runnable() {
            @Override
            public void run() { workshopRegistrationConfirmationEmail(workshop);
            }
        });
    }

    private void workshopRegistrationConfirmationEmail(Workshop workshop) {
        SecureToken secureToken = secureTokenService.createSecureToken();
        secureToken.setWorkshop(workshop);
        secureTokenService.saveSecureToken(secureToken);
        AccountVerificationEmailContext emailContext = new AccountVerificationEmailContext();
        emailContext.init(getWorkshopDataByUsername(workshop.getUsername()));
        emailContext.setToken(secureToken.getToken());
        emailContext.buildVerificationUrl(baseURL, secureToken.getToken());
        try {
            emailService.sendMail(emailContext);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean isOrderAnswerInWorkshop(Workshop workshop) {
        return !Objects.isNull(workshop.getOrderAnswers());
    }
}
