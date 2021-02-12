package mnykolaichuk.prz.pracaDyplomowa.config;


import mnykolaichuk.prz.pracaDyplomowa.model.entity.Customer;
import mnykolaichuk.prz.pracaDyplomowa.model.entity.Workshop;
import mnykolaichuk.prz.pracaDyplomowa.service.CustomerService;
import mnykolaichuk.prz.pracaDyplomowa.service.WorkshopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Collection;

@Component
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Autowired
	private CustomerService customerService;

	@Autowired
	private WorkshopService workshopService;

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication)
			throws IOException, ServletException {

		String redirectUrl = null; 		//створюємо урл по яких буде перекеровуватися юзер а по яких автосервіс після успішного логування
		String username = authentication.getName();

		Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();
		for(GrantedAuthority grantedAuthority : authorities) {
			if (grantedAuthority.getAuthority().equals("ROLE_CUSTOMER")) {
				redirectUrl = "/customer/dashboard";
				Customer tempCustomer = customerService.findByUsername(username);

				// now place in the session
				HttpSession session = request.getSession();
				session.setAttribute("customer", tempCustomer);

				break;
			} else if (grantedAuthority.getAuthority().equals("ROLE_WORKSHOP")) {
				redirectUrl = "/workshop/dashboard";
				Workshop tempWorkshop = workshopService.findByUsername(username);

				// now place in the session
				HttpSession session = request.getSession();
				session.setAttribute("workshop", tempWorkshop);
				break;
			} else if (grantedAuthority.getAuthority().equals("ROLE_ADMIN")) {
				redirectUrl = "/admin/dashboard";
				Customer customer = customerService.findByUsername(username);

				// now place in the session
				HttpSession session = request.getSession();
				session.setAttribute("admin", customer);
				break;
			}

		}
		//forward to page page for role
		response.sendRedirect(request.getContextPath() + redirectUrl);
		}
}
