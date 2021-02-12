package mnykolaichuk.prz.pracaDyplomowa.controller.customer;

import mnykolaichuk.prz.pracaDyplomowa.exception.EmailAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.exception.UserAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.CustomerData;
import mnykolaichuk.prz.pracaDyplomowa.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;

@Controller
@RequestMapping("/register/customer")
public class CustomerRegistrationController {

    @Autowired
    private CustomerService customerService;

	@Autowired
	private MessageSource messageSource;

	@GetMapping("/showRegistrationForm")
	public String showRegistrationForm(Model model) {

		model.addAttribute("customerData", new CustomerData());
		return "customer/registration-form";
	}

	@PostMapping("/processRegistration")
	public ModelAndView processRegistration(Model model
			, @Valid CustomerData customerData
			, BindingResult bindingResult
			, RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()){
		 	return new ModelAndView("customer/registration-form");
		}
        try {
		    customerService.register(customerData);
	    }
        catch (UserAlreadyExistException | EmailAlreadyExistException e){
		    model.addAttribute("registrationError", e.getMessage());
		    return new ModelAndView("customer/registration-form").addObject(model);
       }
		redirectAttributes.addFlashAttribute("message", messageSource.getMessage("user.registration.successful"
				, null, LocaleContextHolder.getLocale()));
       return new ModelAndView(new RedirectView("/login"));
	}
}
