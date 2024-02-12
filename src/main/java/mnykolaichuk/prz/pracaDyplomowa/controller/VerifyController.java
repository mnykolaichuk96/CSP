package mnykolaichuk.prz.pracaDyplomowa.controller;

import mnykolaichuk.prz.pracaDyplomowa.exception.InvalidTokenException;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.CustomerDetail;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.SecureToken;
import mnykolaichuk.prz.pracaDyplomowa.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/verify")
public class VerifyController {

    @Autowired
    private CarService carService;

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private OrderAnswerService orderAnswerService;

    @Autowired
    private SecureTokenService secureTokenService;

    @Autowired
    private CustomerDetailService customerDetailService;

    private static final String REDIRECT_LOGIN= "redirect:/login";
    private static final String REDIRECT_HOME= "redirect:/";


    @GetMapping("/user")
    public String verifyUser(@RequestParam(required = false) String token, final Model model, RedirectAttributes redirAttr){

        if(StringUtils.isEmpty(token)){
            redirAttr.addFlashAttribute("errorMessage", messageSource.getMessage("user.registration.verification.missing.token"
                    , null, LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;
        }
        CustomerDetail customerDetail = null;
        try {
            customerDetail = secureTokenService.findByToken(token).getCustomerDetail();
        } catch (NullPointerException e) {
        }
        try {
            if(customerDetail != null) {
                customerDetailService.verifyCustomer(token);
            }
            else {
                workshopService.verifyWorkshop(token);
            }
        } catch (InvalidTokenException e) {
            redirAttr.addFlashAttribute("errorMessage", messageSource.getMessage("user.registration.verification.invalid.token"
                    , null,LocaleContextHolder.getLocale()));
            return REDIRECT_LOGIN;
        }

        redirAttr.addFlashAttribute("message", messageSource.getMessage("user.registration.verification.success"
                , null,LocaleContextHolder.getLocale()));
        return REDIRECT_LOGIN;
    }

    @GetMapping("/car")
    public String verifyCar(@RequestParam(required = false) String token, final Model model, RedirectAttributes redirAttr){

        if(StringUtils.isEmpty(token)){
            redirAttr.addFlashAttribute("errorMessage", messageSource.getMessage("user.registration.verification.missing.token"
                    , null, LocaleContextHolder.getLocale()));
            return REDIRECT_HOME;
        }
        try {
            carService.verifyCar(token);
        } catch (InvalidTokenException e) {
            redirAttr.addFlashAttribute("errorMessage", messageSource.getMessage("user.registration.verification.invalid.token"
                    , null,LocaleContextHolder.getLocale()));
            return REDIRECT_HOME;
        }

        redirAttr.addFlashAttribute("message", messageSource.getMessage("car.verification.success"
                , null,LocaleContextHolder.getLocale()));
        return REDIRECT_HOME;
    }

    @GetMapping("/offerChoose")
    public String offerChoose(@RequestParam(required = false) String token, final Model model, RedirectAttributes redirAttr){
        if(StringUtils.isEmpty(token)){
            redirAttr.addFlashAttribute("errorMessage", messageSource.getMessage("user.registration.verification.missing.token"
                    , null, LocaleContextHolder.getLocale()));
            return REDIRECT_HOME;
        }

        SecureToken secureToken = secureTokenService.findByToken(token);
        //orderAnswerId zapisany do pola verificationCarId tablicy secure_token
        orderAnswerService.chooseOrderAnswerForImplementation(orderAnswerService.findById(secureToken.getVerificationCarId()));
        secureTokenService.removeToken(secureToken);
        redirAttr.addFlashAttribute("message", messageSource.getMessage("order.choose.success"
                , null,LocaleContextHolder.getLocale()));
        return REDIRECT_HOME;
    }

}
