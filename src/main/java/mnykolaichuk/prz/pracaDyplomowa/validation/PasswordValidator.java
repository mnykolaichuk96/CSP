package mnykolaichuk.prz.pracaDyplomowa.validation;

import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidPassword;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {

	private Pattern pattern;
	private Matcher matcher;
	private String messageNotEmpty;
	private String message;
	//Minimum eight characters, at least one uppercase letter, one lowercase letter and one number
	private static final String PASSWORD_PATTERN = "^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9]).{8,}$";

	@Override
	public void initialize(ValidPassword constraintAnnotation) {
		messageNotEmpty = constraintAnnotation.messageNotEmpty();
		message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(final String password, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		if (password.isEmpty()) {
			context.buildConstraintViolationWithTemplate(messageNotEmpty).addConstraintViolation();
			return false;
		}
		pattern = Pattern.compile(PASSWORD_PATTERN);
		matcher = pattern.matcher(password);
		if(!matcher.matches()) {
			context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
			return false;
		}
		return true;
	}

}