package mnykolaichuk.prz.pracaDyplomowa.validation;

import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidEmail;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class EmailValidator implements ConstraintValidator<ValidEmail, String> {

	private Pattern pattern;
	private Matcher matcher;
	private String messageNotNull;
	private String message;
	private static final String EMAIL_PATTERN = "^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@"
			+ "[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";

	@Override
	public void initialize(ValidEmail constraintAnnotation) {
		messageNotNull = constraintAnnotation.messageNotEmpty();
		message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(final String name, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		if (name.isEmpty()) {
			context.buildConstraintViolationWithTemplate(messageNotNull).addConstraintViolation();
			return false;
		}
		pattern = Pattern.compile(EMAIL_PATTERN);
		matcher = pattern.matcher(name);
		if(!matcher.matches()) {
			context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
			return false;
		}
		return true;
	}

}