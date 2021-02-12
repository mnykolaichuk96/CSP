package mnykolaichuk.prz.pracaDyplomowa.validation;

import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidPhoneNumber;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PhoneNumberValidator implements ConstraintValidator<ValidPhoneNumber, String> {

	private Pattern pattern;
	private Matcher matcher;
	private String messageNotNull;
	private String message;
	//TO DO настроїти добру регулярку
	private static final String PHONE_NUMBER_PATTERN = "^(\\+\\d{1,2}[\\s\\-]?)?(\\d{3})([\\s\\-]?\\d{3})[\\s\\-]?\\d{3}$";

	@Override
	public void initialize(ValidPhoneNumber constraintAnnotation) {
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
		pattern = Pattern.compile(PHONE_NUMBER_PATTERN);
		matcher = pattern.matcher(name);
		if(!matcher.matches()) {
			context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
			return false;
		}
		return true;
	}

}