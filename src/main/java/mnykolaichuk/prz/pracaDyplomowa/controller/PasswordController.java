package mnykolaichuk.prz.pracaDyplomowa.controller;

import mnykolaichuk.prz.pracaDyplomowa.exception.InvalidTokenException;
import mnykolaichuk.prz.pracaDyplomowa.exception.UnknowIdentifierException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.ResetPasswordData;
import mnykolaichuk.prz.pracaDyplomowa.service.UserAccountService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@Controller
@RequestMapping("/password")
public class PasswordController {

    @Autowired
    private MessageSource messageSource;

    @Autowired
    private UserAccountService userAccountService;

    private static final String MSG = "resetPasswordMsg";

    @GetMapping("/showForgotPasswordForm")
    public String showForgotPasswordForm() {
        return "account/forgot-password-form";
    }

    @PostMapping("/processResetPassword")
    public ModelAndView resetPassword(
              @RequestParam(value = "username",required = false) String username
            , RedirectAttributes redirectAttributes) {

        if(StringUtils.isEmpty(username)) {
            return new ModelAndView("account/forgot-password-form").addObject("errorMessage", "is required");
        }
        try {
            userAccountService.forgottenPassword(username);
        } catch (UnknowIdentifierException e) {

        }
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("user.forgot.password"
                        , null, LocaleContextHolder.getLocale()));
        return new ModelAndView(new RedirectView("/login"));
    }

    @GetMapping("/showChangePasswordForm")
    public String showChangePasswordForm(Model model
            , @RequestParam(required = false) String token
            , RedirectAttributes redirAttr) {

        if (StringUtils.isEmpty(token)) {
            redirAttr.addFlashAttribute("tokenError", messageSource.getMessage("user.registration.verification.missing.token"
                    , null, LocaleContextHolder.getLocale()));
            return "redirect:/login";
        }

        ResetPasswordData resetPasswordData = new ResetPasswordData();
        resetPasswordData.setToken(token);
        model.addAttribute("resetPasswordData", resetPasswordData);

        return "account/change-password-form";
    }

    @PostMapping("/processChange")
    public ModelAndView processChange(Model model
            , @Valid ResetPasswordData resetPasswordData
            , BindingResult bindingResult
            , RedirectAttributes redirectAttributes) {

        if(bindingResult.hasErrors()) {
            return new ModelAndView("account/change-password-form");
        }
        try {
            userAccountService.updatePassword(resetPasswordData.getPassword(), resetPasswordData.getToken());
        } catch (InvalidTokenException | UnknowIdentifierException e) {
            model.addAttribute("errorMessage", messageSource.getMessage("user.registration.verification.invalid.token"
                    , null, LocaleContextHolder.getLocale()));
            return new ModelAndView("account/change-password-form");
        }

        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("user.password.update.msg"
                , null, LocaleContextHolder.getLocale()));
        return new ModelAndView(new RedirectView("/login"));
    }
}
