package mnykolaichuk.prz.pracaDyplomowa.config;

import mnykolaichuk.prz.pracaDyplomowa.security.CustomerUserDetailService;
import mnykolaichuk.prz.pracaDyplomowa.security.WorkshopUserDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;

import javax.annotation.Resource;
import javax.sql.DataSource;

@Configuration
@EnableWebSecurity
public class AppSecurityConfig extends WebSecurityConfigurerAdapter{

	@Resource
	CustomerUserDetailService customerUserDetailService;

	@Resource
    WorkshopUserDetailService workshopUserDetailService;

	@Autowired
	private CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	private DataSource dataSource;


		@Override
		protected void configure(HttpSecurity http) throws Exception {    //HttpSecurity використовується для конфігурації spraing security

			http.authorizeRequests()    // обмежуємо доступ на основі входящого httpServletRequest
					// тут ми вказуємо до яких мепінгів будуть мати доступ які ролі
					.antMatchers("/register/**", "/home", "/login", "/password").permitAll()
					.antMatchers("/admin/**").hasRole("ADMIN")
					.antMatchers("/employee/**").hasRole("EMPLOYEE")    // /** -- all sub-directories
					.antMatchers("/workshop/**").hasRole("WORKSHOP")
					//.hasAnyAuthority()	дозволяє перечислити ролі для доступу
					.and()
					.rememberMe()
								.userDetailsService(customerUserDetailService)
								.userDetailsService(workshopUserDetailService)
						.tokenRepository(persistentTokenRepository())
					.and()
					.formLogin()		//customise log in proces видозмінюємо його під наші потреби
						.defaultSuccessUrl("/home")    // якщо все пройшло успішно редірект на сукцес page
						.loginPage("/login")    // показуємо логування по request mapping	//для цього мапінгу треба писати контроллер
						.failureUrl("/login?error=true")

							//	.loginProcessingUrl("/authenticateTheUser") // логування постить дпні на цей url
					// для цього контроллера не треба він сам перевіряє чи сходяться user id oraz paasword
					.successHandler(customAuthenticationSuccessHandler)
					.permitAll()    // дозволяємо бачити цю форму всім користувачам включно з незареєстрованими
					.and()
					.logout().permitAll();	//дозволяється доступ до фкнкціоналу log out для всіх користувачів

		}

	@Bean
	public DaoAuthenticationProvider authenticationProviderWorkshop() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(workshopUserDetailService); //set the custom user details service
		//ми вказуєм метод кодування щоб при логуванні спрінг знав як закодувати пароль і міг порівняти з тим що в базі
		auth.setPasswordEncoder(passwordEncoder); //set the password encoder - bcrypt
		return auth;
	}

	@Bean
	public DaoAuthenticationProvider authenticationProviderEmployee() {
		DaoAuthenticationProvider auth = new DaoAuthenticationProvider();
		auth.setUserDetailsService(customerUserDetailService); //set the custom user details service
		auth.setPasswordEncoder(passwordEncoder); //set the password encoder - bcrypt
		return auth;
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth) {
		auth.authenticationProvider(authenticationProviderEmployee())
				.authenticationProvider(authenticationProviderWorkshop());
	}

	@Bean
	public PersistentTokenRepository persistentTokenRepository() {
		JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
		db.setDataSource(dataSource);
		return db;
	}
}






