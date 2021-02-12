package mnykolaichuk.prz.pracaDyplomowa.controller.workshop;

import mnykolaichuk.prz.pracaDyplomowa.exception.EmailAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.exception.UserAlreadyExistException;
import mnykolaichuk.prz.pracaDyplomowa.model.data.WorkshopData;
import mnykolaichuk.prz.pracaDyplomowa.service.CityService;
import mnykolaichuk.prz.pracaDyplomowa.service.WorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.ArrayList;

@Controller
@RequestMapping("/register/workshop")
public class WorkshopRegistrationController {

    @Autowired
    private WorkshopService workshopService;

    @Autowired
	private CityService cityService;

    @Autowired
	private MessageSource messageSource;

	@GetMapping("/showRegistrationForm")
	public String showRegistrationForm(Model model) {

		model.addAttribute("workshopData", new WorkshopData());
		model.addAttribute("cities", cityService.loadCites());
		return "workshop/registration-form";
	}

	@PostMapping("/processRegistration")
	public ModelAndView processRegistration(Model model
			, @Valid WorkshopData workshopData
			, BindingResult bindingResult
			, @ModelAttribute("cities") ArrayList<String> cityList
			, RedirectAttributes redirectAttributes) {

		if (bindingResult.hasErrors()){
			cityList.addAll(cityService.loadCites());
			 return new ModelAndView("workshop/registration-form");
		}
		try {
			workshopService.register(workshopData);
		}
		catch (UserAlreadyExistException | EmailAlreadyExistException e){
			model.addAttribute("registrationError", e.getMessage());
			cityList.addAll(cityService.loadCites());
			return new ModelAndView("workshop/registration-form").addObject(model);
		}
		redirectAttributes.addFlashAttribute("message", messageSource.getMessage("user.registration.successful"
				, null, LocaleContextHolder.getLocale()));
		return new ModelAndView(new RedirectView("/login"));
	}
}
