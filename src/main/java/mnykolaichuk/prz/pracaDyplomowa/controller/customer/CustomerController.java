package mnykolaichuk.prz.pracaDyplomowa.controller.customer;

import mnykolaichuk.prz.pracaDyplomowa.exception.*;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CarData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CustomerData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderCustomerData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.ResetPasswordData;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/customer")
public class CustomerController {
    @Autowired
    private CustomerService customerService;

    @Autowired
    private CityService cityService;

    @Autowired
    private CarService carService;

    @Autowired
    private CustomerDetailService customerDetailService;

    @Autowired
    private OrderService orderService;

    @Autowired
    private OrderAnswerService orderAnswerService;

    @Autowired
    private CarMakeService carMakeService;

    @Autowired
    private CarModelService carModelService;

    @Autowired
    private UserAccountService userAccountService;

    @Autowired
    private MessageSource messageSource;

    @GetMapping("/dashboard")
    public String showCustomerDashboard(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        model.addAttribute("customerDetail", customerDetailService.findByCustomerUsername(username));
        model.addAttribute("cityList", cityService.loadCites());
        return "customer/dashboard";
    }

    @GetMapping("/showUpdateForm")
    public String showUpdateForm(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        CustomerData customerData = customerService.getCustomerDataByUsername(username);
        model.addAttribute("customerData", customerData);
        model.addAttribute("oldUsername", customerData.getUsername());
        model.addAttribute("oldEmail", customerData.getEmail());

        return "customer/update-form";
    }

    @PostMapping("/processUpdateForm")
    public ModelAndView processUpdateForm(Model model
            , @Valid @ModelAttribute CustomerData customerData
            , BindingResult bindingResult
            , @ModelAttribute("oldUsername") String oldUsername
            , @ModelAttribute("oldEmail") String oldEmail
            , RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return new ModelAndView("customer/update-form");
        }
        try {
            customerService.update(customerData, oldUsername, oldEmail);
        } catch (UserAlreadyExistException | EmailAlreadyExistException e) {
            model.addAttribute("updateError", e.getMessage());
            return new ModelAndView("customer/update-form").addObject(model);
        }

        if (customerData.getUsername().equals(oldUsername) && customerData.getEmail().equals(oldEmail)) {
            redirectAttributes.addFlashAttribute("message", messageSource.getMessage("user.update.successful"
                    , null, LocaleContextHolder.getLocale()));
            return new ModelAndView(new RedirectView("showOption"));
        }
        if(!(customerData.getUsername().equals(oldUsername) || customerData.getEmail().equals(oldEmail))) {
            redirectAttributes.addFlashAttribute("message",
                    messageSource.getMessage("user.update.username.successful"
                            , null, LocaleContextHolder.getLocale())
                    +
                    messageSource.getMessage("user.update.email.successful"
                            ,null, LocaleContextHolder.getLocale()));
        }
        else {
            if (!customerData.getUsername().equals(oldUsername)) {
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("user.update.username.successful"
                        , null, LocaleContextHolder.getLocale()));
            }
            if (!customerData.getEmail().equals(oldEmail)) {
                redirectAttributes.addFlashAttribute("message", messageSource.getMessage("user.update.email.successful"
                        , null, LocaleContextHolder.getLocale()));
            }
        }

        return new ModelAndView(new RedirectView("/login"));
    }

    @GetMapping("/showChangePasswordForm")
    public String showChangePasswordForm(Model model) {

        model.addAttribute("resetPasswordData", new ResetPasswordData());
        return "customer/change-password-form";
    }

    @PostMapping("/processChangePassword")
    public ModelAndView processChangePasswordForm(Model model
            , @Valid  ResetPasswordData resetPasswordData
            , BindingResult theBindingResult
            , @RequestParam("oldPassword") String oldPassword
            , @CurrentSecurityContext(expression = "authentication.name") String username
            , RedirectAttributes redirectAttributes) {

        String password = customerService.findByUsername(username).getPassword();
        if(userAccountService.comparePassword(oldPassword, password)) {
            // form validation
            if (theBindingResult.hasErrors()) {
                return new ModelAndView("customer/change-password-form");
            }
            customerService.changePasswordByUsername(username, resetPasswordData.getPassword());
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

        customerService.deleteByUsername(username);
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
              @RequestParam("orderId") Integer orderId
            , RedirectAttributes redirectAttributes) {

        orderService.deleteOrderFromCustomerByOrder(orderService.findOrderById(orderId));
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("order.delete.successful"
                , null, LocaleContextHolder.getLocale()));
        return new ModelAndView(new RedirectView("showOrderList"));
    }

    @PostMapping("/deleteCar")
    public ModelAndView deleteCar(
              @RequestParam("carId") Integer carId
            , @CurrentSecurityContext(expression = "authentication.name") String username
            , RedirectAttributes redirectAttributes) {

        customerService.deleteCustomerCarByCarIdAndUsername(carId, username);
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("car.delete.successful"
                , null, LocaleContextHolder.getLocale()));
        return new ModelAndView(new RedirectView("showCarList"));
    }

    @GetMapping("/showAddCarForm")
    public String showCarForm(Model model) {

        CarData carData = new CarData();
        if(model.getAttribute("make") != null) {
            String make = (String) model.getAttribute("make");
            carData.setMake(make);
            model.addAttribute("carModelList", carModelService.loadCarModelList(carMakeService.findByMake(make)));
        }

        model.addAttribute("carData", carData);
        model.addAttribute("carMakeList", carMakeService.loadCarMakeList());
        return "customer/add-car-form";
    }

     @PostMapping("/processCarMakeChoose")
     public ModelAndView processCarMakeChoose(
              @RequestParam("make") String make
            , RedirectAttributes redirectAttributes) {

         redirectAttributes.addFlashAttribute("make", make);
         return new ModelAndView(new RedirectView("showAddCarForm"));
     }

    @PostMapping("/processAddCar")
    public ModelAndView processAddCar(
              @ModelAttribute("carData") @Valid CarData carData
            , BindingResult bindingResult
            , @ModelAttribute("carModelList") ArrayList<String> carModelList
            , @ModelAttribute("carMakeList") ArrayList<String> carMakeList
            , Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username
            , RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            carModelList.addAll(carModelService.loadCarModelList(carMakeService.findByMake(carData.getMake())));
            carMakeList.addAll(carMakeService.loadCarMakeList());
            return new ModelAndView("customer/add-car-form");
        }


        carData.setUsername(username);
        try {
            carService.save(carData);
        } catch (MyCarAlreadyExistException e) {
            carModelList.addAll(carModelService.loadCarModelList(carMakeService.findByMake(carData.getMake())));
            carMakeList.addAll(carMakeService.loadCarMakeList());
            model.addAttribute("addCarError", e.getMessage());
            return new ModelAndView("customer/add-car-form").addObject(model);
        } catch (CarAlreadyHasOwnerException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return new ModelAndView(new RedirectView("showCarList"));
        }
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("car.add.successful"
                , null, LocaleContextHolder.getLocale()));
        return new ModelAndView(new RedirectView("showCarList"));
    }

    @GetMapping("/showCarList")
    public String showCustomerCarList(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        model.addAttribute("carDataList", carService.getCarDataListForCustomerUsername(username));
        return "customer/car-list";
    }

    //Create ordering
    @GetMapping("/showAddCarToOrderForm")
    public String showAddCarToOrderForm(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        CarData carData = new CarData();
        if(model.getAttribute("make") != null) {
            String make = (String) model.getAttribute("make");
            carData.setMake(make);
            model.addAttribute("carModelList", carModelService.loadCarModelList(carMakeService.findByMake(make)));
        }

        model.addAttribute("carDataList", carService.getCarDataListForCustomerUsername(username));
        model.addAttribute("carData", carData);
        model.addAttribute("carMakeList", carMakeService.loadCarMakeList());
        return "customer/add-car-to-order-form";
    }

    @PostMapping("/processCarMakeChooseToOrder")
    public ModelAndView processCarMakeChooseToOrder(
              @RequestParam("make") String make
            , RedirectAttributes redirectAttributes) {


        redirectAttributes.addFlashAttribute("make", make);
        return new ModelAndView(new RedirectView("showAddCarToOrderForm"));
    }

    @PostMapping("/processAddCarToOrder")
    public ModelAndView processAddCarToOrder(
            @ModelAttribute("carData") @Valid CarData carData
            , BindingResult bindingResult
            , @ModelAttribute("carModelList") ArrayList<String> carModelList
            , @ModelAttribute("carMakeList") ArrayList<String> carMakeList
            , RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            carModelList.addAll(carModelService.loadCarModelList(carMakeService.findByMake(carData.getMake())));
            carMakeList.addAll(carMakeService.loadCarMakeList());
            return new ModelAndView("customer/add-car-to-order-form");
        }

        redirectAttributes.addFlashAttribute("carData", carData);
        return new ModelAndView(new RedirectView("showCreateOrderForm"));
    }

    @GetMapping("/showCreateOrderForm")
    public String showCreateOrderForm(Model model) {

        OrderCustomerData orderCustomerData = new OrderCustomerData();
        orderCustomerData.setCarData((CarData) model.getAttribute("carData"));

        model.addAttribute("orderCustomerData", orderCustomerData);
        model.addAttribute("cities", cityService.loadCites());
        model.addAttribute("localDateTime", LocalDateTime.now());

        return "customer/create-order-form";
    }

    @PostMapping("/processCreateOrder")
    public ModelAndView processCreateOrder(@Valid OrderCustomerData orderCustomerData
            , BindingResult bindingResult
            , @ModelAttribute("cities") ArrayList<String> cities
            , @CurrentSecurityContext(expression = "authentication.name") String username
            , RedirectAttributes redirectAttributes) {


        if (bindingResult.hasErrors()) {
            cities.addAll(cityService.loadCites());
            return new ModelAndView("customer/create-order-form").addObject("localDateTime", LocalDateTime.now());
        }
        try {
            orderService.createOrder(username, orderCustomerData);
        } catch (NullWorkshopInCityException e) {
            redirectAttributes.addFlashAttribute("message", e.getMessage());
            return new ModelAndView(new RedirectView("dashboard"));
        }
        redirectAttributes.addFlashAttribute("message", messageSource.getMessage("order.create.successful"
                , null, LocaleContextHolder.getLocale()));
        return new ModelAndView(new RedirectView("showOrderList"));
    }

    @GetMapping("/showOrderList")
    public String showOrderList(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        List<OrderCustomerData> orderCustomerDataList;

        orderCustomerDataList = orderService.getOrderCustomerDataListByUsernameAndStanIsNotCompleted(username);


        model.addAttribute("orderCustomerData", new OrderCustomerData());
        model.addAttribute
                ("orderCustomerDataList", orderCustomerDataList);

        return "customer/order-list";
    }

    @PostMapping("/processOrderChoose")
    public ModelAndView processOrderChoose(Model model
            ,@RequestParam("orderId") Integer orderId) {

        OrderCustomerData orderCustomerData;
        orderCustomerData = orderService.getOrderCustomerDataByOrderAndStanEqualsWorkshopAnswer
                            (orderService.findOrderById(orderId));
        model.addAttribute("orderCustomerData", orderCustomerData);
        return new ModelAndView("customer/workshop-answer-list-for-order");
    }

    @PostMapping("/processOrderForImplementationChoose")
    public ModelAndView processOrderForImplementationChoose(
            @RequestParam("orderAnswerId") Integer orderAnswerId) {

        orderAnswerService.chooseOrderAnswerForImplementation(orderAnswerService.findById(orderAnswerId));
        return new ModelAndView(new RedirectView("showOrderList"));
    }

    @GetMapping("/showCompletedOrderList")
    public String showCompletedOrderList(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username
            , RedirectAttributes redirectAttributes) {
        if(customerService.findByUsername(username) == null) {
            redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("user.not.logged",
                    null, LocaleContextHolder.getLocale()));
            return "redirect:/login";
        }

        List<OrderCustomerData> orderCustomerDataList;
        orderCustomerDataList = orderService.getOrderCustomerDataListByUsernameAndStanEqualsCompleted(username);

        model.addAttribute
                ("orderCustomerDataList", orderCustomerDataList);
        model.addAttribute("orderCustomerData", new OrderCustomerData());
        return "customer/completed-order-list";
    }

    @PostMapping("/showCompletedOrderListOptions")
    public ModelAndView showCompletedOrderListOptions(Model model
            , @RequestParam("orderId") Integer orderId) {

        model.addAttribute("orderCustomerData", orderService.getOrderCustomerDataByOrderId(orderId));

        return new ModelAndView("customer/completed-order-list-options");
    }

    @GetMapping("/showOption")
    public String showShowOption(Model model
            , @CurrentSecurityContext(expression = "authentication.name") String username) {

        model.addAttribute
                ("customerData", customerService.getCustomerDataByUsername(username));
        return "customer/option";
    }

}
