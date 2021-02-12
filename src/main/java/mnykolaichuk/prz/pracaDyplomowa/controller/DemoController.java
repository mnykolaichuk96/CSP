package mnykolaichuk.prz.pracaDyplomowa.controller;

import mnykolaichuk.prz.pracaDyplomowa.exception.EmailAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.exception.NullWorkshopInCityException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CarData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CustomerDetailData;
import mnykolaichuk.prz.pracaDyplomowa.model.data.OrderWorkshopData;
import mnykolaichuk.prz.pracaDyplomowa.service.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.time.LocalDateTime;
import java.util.ArrayList;

@Controller
public class DemoController {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private WorkshopService workshopService;

	@Autowired
	private CityService cityService;

	@Autowired
	private OrderService orderService;

	@Autowired
	private CarModelService carModelService;

	@Autowired
	private CarMakeService carMakeService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping("/")
	public String showHome(Model model
			, @CurrentSecurityContext(expression = "authentication.name") String username
			, RedirectAttributes redirectAttributes) {

		if(username != "anonymousUser") {
			redirectAttributes.addFlashAttribute("errorMessage", model.getAttribute("errorMessage"));
			redirectAttributes.addFlashAttribute("message", model.getAttribute("message"));
			if(username.equals("admin")) {return "redirect:/admin/dashboard";}
			if (customerService.findByUsername(username) != null) {return "redirect:/customer/dashboard";}
			if (workshopService.findByUsername(username) != null) {return "redirect:/workshop/dashboard";}
		}
		model.addAttribute("cityList", cityService.loadCites());

		return "home";
	}

	@GetMapping("/showAddCarToOrderForm")
	public String showAddCarToOrderForm(Model model) {

		CarData carData = new CarData();
		if(model.getAttribute("make") != null) {
			String make = (String) model.getAttribute("make");
			carData.setMake(make);
			model.addAttribute("carModelList", carModelService.loadCarModelList(carMakeService.findByMake(make)));
		}

		model.addAttribute("carData", carData);
		model.addAttribute("carMakeList", carMakeService.loadCarMakeList());
		return "unregistered/add-car-to-order-form";
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
			, @ModelAttribute("carMakeList") ArrayList<String> carMakeList
			, @ModelAttribute("carModelList") ArrayList<String> carModelList
			, RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()) {
			carMakeList.addAll(carMakeService.loadCarMakeList());
			carModelList.addAll(carModelService.loadCarModelList(carMakeService.findByMake(carData.getMake())));
			return new ModelAndView("unregistered/add-car-to-order-form");
		}

		redirectAttributes.addFlashAttribute("carData", carData);
		return new ModelAndView(new RedirectView("showCreateOrderForm"));
	}

	@GetMapping("/showCreateOrderForm")
	public String showCreateOrderForm(Model model) {

		OrderWorkshopData orderWorkshopData = new OrderWorkshopData();
		orderWorkshopData.setCarData((CarData) model.getAttribute("carData"));

		model.addAttribute("orderWorkshopData", orderWorkshopData);
		model.addAttribute("customerDetailData", new CustomerDetailData());
		model.addAttribute("cities", cityService.loadCites());
		model.addAttribute("localDateTime", LocalDateTime.now());

		return "unregistered/create-order-form";
	}

	@PostMapping("/processCreateOrder")
	public ModelAndView processCreateOrder(@Valid OrderWorkshopData orderWorkshopData
			, BindingResult bindingResultOrder
			, @Valid CustomerDetailData customerDetailData
			, BindingResult bindingResultCustomer
			, @ModelAttribute("cities") ArrayList<String> cities
			, RedirectAttributes redirectAttributes) {

		if (bindingResultOrder.hasErrors() || bindingResultCustomer.hasErrors()) {
			cities.addAll(cityService.loadCites());
			return new ModelAndView("unregistered/create-order-form").addObject("localDateTime", LocalDateTime.now());
		}
		try {
			orderWorkshopData.setCustomerDetailData(customerDetailData);
			orderService.createOrderFromUnregisteredUser(orderWorkshopData);
		} catch (EmailAlreadyExistException | NullWorkshopInCityException e) {
			redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
			return new ModelAndView(new RedirectView("/"));
		}

		redirectAttributes.addFlashAttribute("message", messageSource.getMessage("order.create.success.unregistered"
				, null, LocaleContextHolder.getLocale()));
		return new ModelAndView(new RedirectView("/"));
	}
}
