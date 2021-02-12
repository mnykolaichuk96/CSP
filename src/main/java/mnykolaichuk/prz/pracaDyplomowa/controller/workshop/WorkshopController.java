package mnykolaichuk.prz.pracaDyplomowa.controller.workshop;

import mnykolaichuk.prz.pracaDyplomowa.exception.CantDeleteWorkshopWhileImplementationExistException;
import mnykolaichuk.prz.pracaDyplomowa.exception.EmailAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.exception.UserAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderAnswerData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderWorkshopData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.ResetPasswordData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.WorkshopData;
import mnykolaichuk.prz.pracaDyplomowa.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;

@Controller
@RequestMapping("/workshop")
public class WorkshopController {
    @Autowired
    private WorkshopService workshopService;

    @Autowired
    private CityService cityService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderAnswerService orderAnswerService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/dashboard")
    public String showWorkshopDashboard(Model model
                                    , @CurrentSecurityContext(expression = "authentication.name") String username) {

        model.addAttribute("workshopData",workshopService.getWorkshopDataByUsername(username));
        return "workshop/dashboard";
    }

    @GetMapping("/showUpdateForm")
    public String showUpdateForm(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        model.addAttribute("workshopData", workshopService.getWorkshopDataByUsername(username));
        model.addAttribute("cities", cityService.loadCites());
        return "workshop/update-form";
    }

    @PostMapping("/processUpdateForm")
    public ModelAndView processUpdateForm(Model model
            , @Valid WorkshopData workshopData
            , BindingResult bindingResult
            , @RequestParam("oldUsername") String oldUsername
            , @RequestParam("oldEmail") String oldEmail
            , RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("workshop/update-form");
        }

        try {
            workshopService.update(workshopData, oldUsername, oldEmail);
        }
        catch (UserAlreadyExistException | EmailAlreadyExistException e){
            model.addAttribute("updateError", e.getMessage());
            return new ModelAndView("workshop/update-form").addObject(model);
        }

        if (workshopData.getUsername().equals(oldUsername) && workshopData.getEmail().equals(oldEmail)) {
            return new ModelAndView(new RedirectView("showOption"));
        }
        if(!(workshopData.getUsername().equals(oldUsername) || workshopData.getEmail().equals(oldEmail))) {
            redirectAttributes.addFlashAttribute("message",
                    messageSource.getMessage("user.update.username.successful"
                            , null, LocaleContextHolder.getLocale())
                    +
                     messageSource.getMessage("user.update.email.successful"
                             , null, LocaleContextHolder.getLocale()));
        }
        else {
            if (!workshopData.getUsername().equals(oldUsername)) {
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("user.update.username.successful"
                        , null, LocaleContextHolder.getLocale()));
            }
            if (!workshopData.getEmail().equals(oldEmail)) {
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("user.update.email.successful"
                        , null, LocaleContextHolder.getLocale()));
            }
        }
        return new ModelAndView(new RedirectView("/login"));
    }

    @GetMapping("/showChangePasswordForm")
    public String showChangePasswordForm(Model model) {

        model.addAttribute("resetPasswordData", new ResetPasswordData());
        return "workshop/change-password-form";
    }

    @PostMapping("/processChangePassword")
    public ModelAndView processChangePasswordForm(Model model
            , @Valid  ResetPasswordData resetPasswordData
            , BindingResult theBindingResult
            , @RequestParam("oldPassword") String oldPassword
            , @CurrentSecurityContext(expression = "authentication.name") String username
            , RedirectAttributes redirectAttributes) {

        String password = workshopService.findByUsername(username).getPassword();
        if(userAccountService.comparePassword(oldPassword, password)) {
            // form validation
            if (theBindingResult.hasErrors()) {
                return new ModelAndView("workshop/change-password-form");
            }
            workshopService.changePasswordByUsername(username, resetPasswordData.getPassword());
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("user.password.change"
                    , null, LocaleContextHolder.getLocale()));
            return new ModelAndView(new RedirectView("showOption"));
        }
        else {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("user.password.error"
                    , null, LocaleContextHolder.getLocale()));
            return new ModelAndView(new RedirectView("showChangePasswordForm"));
        }
    }

    @GetMapping("/deleteAccount")
    public ModelAndView deleteAccount(
            @CurrentSecurityContext(expression = "authentication.name") String username
            , RedirectAttributes redirectAttributes
            , HttpServletRequest httpServletRequest) {

        try {
            workshopService.deleteByUsernameAndIsAdmin(username, false);
        } catch (CantDeleteWorkshopWhileImplementationExistException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
            return new ModelAndView(new RedirectView("showImplementationOrderList"));
        }
        try {
            httpServletRequest.logout();
        } catch (ServletException e) {
            e.printStackTrace();
        }

        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("user.delete.account.successful"
                , null, LocaleContextHolder.getLocale()));
        return new ModelAndView(new RedirectView("/"));
    }

    @PostMapping("/deleteOrder")
    public ModelAndView deleteOrder(
              @RequestParam("orderAnswerId") Integer orderAnswerId
            , RedirectAttributes redirectAttributes) {

        try {
            orderAnswerService.deleteOrderAnswerFromWorkshopByOrderAnswerAndIsAdmin
                    (orderAnswerService.findById(orderAnswerId), false);
        } catch (CantDeleteWorkshopWhileImplementationExistException e) {
            e.getStackTrace();
        }
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("order.delete.successful"
                , null, LocaleContextHolder.getLocale()));
        return new ModelAndView(new RedirectView("showCreatedOrderList"));
    }

    @GetMapping("/showCreatedOrderList")
    public String showCreatedOrderList(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username
            , RedirectAttributes redirectAttributes) {

        if(workshopService.findByUsername(username) == null) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("user.not.logged",
                    null, LocaleContextHolder.getLocale()));
            return "redirect:/login";
        }

        List<OrderWorkshopData> orderWorkshopDataList
                = orderService.getOrderWorkshopDataListByUsernameAndStanEqualsCreated(username);
        if (model.getAttribute("orderAnswerData") == null) {
            model.addAttribute("orderAnswerData", new OrderAnswerData());
        }
        model.addAttribute
                ("orderWorkshopDataList", orderWorkshopDataList);

        return "workshop/created-order-list";
    }

    @PostMapping("/processOrderChoose")
    public ModelAndView processOrderChoose(
            @Valid @ModelAttribute("orderAnswerData") OrderAnswerData orderAnswerData
            , BindingResult bindingResult
            , RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("org.springframework.validation.BindingResult.orderAnswerData", bindingResult);
            redirectAttributes.addFlashAttribute("orderAnswerData", orderAnswerData);
            return new ModelAndView(new RedirectView("showCreatedOrderList"));
        }
        orderAnswerService.createWorkshopAnswerByOrderAnswerData(orderAnswerData);
        return new ModelAndView(new RedirectView("showCreatedOrderList"));
    }

    @PostMapping("/showCreatedOrderListOptions")
    public ModelAndView showCreatedOrderListOptions(Model model
            , @RequestParam("orderAnswerId") Integer orderAnswerId) {

        model.addAttribute("orderWorkshopData", orderService.getOrderWorkshopDataByOrderAnswerId(orderAnswerId));
        return new ModelAndView("workshop/created-order-list-options");
    }

    @GetMapping("/showImplementationOrderList")
    public String showImplementationOrderList(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username
            , RedirectAttributes redirectAttributes) {

        if(workshopService.findByUsername(username) == null) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("user.not.logged",
                    null, LocaleContextHolder.getLocale()));
            return "redirect:/login";
        }

        List<OrderWorkshopData> orderWorkshopDataList
                = orderService.getOrderWorkshopDataListByUsernameAndStanEqualsImplementation(username);
        model.addAttribute("orderAnswerData", new OrderAnswerData());
        model.addAttribute
                ("orderWorkshopDataList", orderWorkshopDataList);

        return "workshop/implementation-order-list";
    }

    @PostMapping("/showImplementationOrderListOptions")
    public ModelAndView showImplementationOrderListOptions(Model model
            , @RequestParam("orderAnswerId") Integer orderAnswerId) {

        model.addAttribute("orderWorkshopData", orderService.getOrderWorkshopDataByOrderAnswerId(orderAnswerId));
        return new ModelAndView("workshop/implementation-order-list-options");
    }

    @PostMapping("/processOrderCompleted")
    public ModelAndView processOrderCompleted(@RequestParam("orderAnswerId") Integer orderAnswerId) {

        orderAnswerService.chooseOrderAnswerForCompleted(orderAnswerService.findById(orderAnswerId));
        return new ModelAndView(new RedirectView("showCreatedOrderList"));
    }

    @GetMapping("/showCompletedOrderList")
    public String showCompletedOrderList(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        List<OrderWorkshopData> orderWorkshopDataList
                = orderService.getOrderWorkshopDataListByUsernameAndStanEqualsCompleted(username);
        model.addAttribute
                ("orderWorkshopDataList", orderWorkshopDataList);
        return "workshop/completed-order-list";
    }

    @PostMapping("/showCompletedOrderListOptions")
    public ModelAndView showCompletedOrderListOptions(Model model
            , @RequestParam("orderAnswerId") Integer orderAnswerId) {

        model.addAttribute("orderWorkshopData", orderService.getOrderWorkshopDataByOrderAnswerId(orderAnswerId));

        return new ModelAndView("workshop/completed-order-list-options");
    }

    @GetMapping("/showOption")
    public String showShowOption(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        model.addAttribute
                ("workshopData", workshopService.getWorkshopDataByUsername(username));
        return "workshop/option";
    }

}
