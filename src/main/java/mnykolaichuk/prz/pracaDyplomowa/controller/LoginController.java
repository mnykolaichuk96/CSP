package mnykolaichuk.prz.pracaDyplomowa.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.security.core.annotation.CurrentSecurityContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/login")
public class LoginController {

	@Autowired
	private MessageSource messageSource;

	@GetMapping()
	public String login(Model model
			, @CurrentSecurityContext(expression = "authentication.name") String username
			, RedirectAttributes redirectAttributes) {
		if(username != "anonymousUser") {
			redirectAttributes.addFlashAttribute("errorMessage", messageSource.getMessage("user.already.logged"
					,null, LocaleContextHolder.getLocale()));
			return "redirect:/";
		}

		return "login";
	}
	
}









