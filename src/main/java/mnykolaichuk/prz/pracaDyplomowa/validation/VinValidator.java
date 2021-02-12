package mnykolaichuk.prz.pracaDyplomowa.validation;

import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidVin;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VinValidator implements ConstraintValidator<ValidVin, String> {

	private Pattern pattern;
	private Matcher matcher;
	private static final String VIN_PATTERN = "^(?=.*[0-9])(?=.*[A-z])[0-9A-z-]{17}$";
	private String messageNotEmpty;
	private String message;

	@Override
	public void initialize(ValidVin constraintAnnotation) {
		messageNotEmpty = constraintAnnotation.messageNotEmpty();
		message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(final String vin, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		if(vin.isEmpty()) {
			context.buildConstraintViolationWithTemplate(messageNotEmpty).addConstraintViolation();
			return false;
		}
		pattern = Pattern.compile(VIN_PATTERN);
		matcher = pattern.matcher(vin);
		if(!matcher.matches()) {
			context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
			return false;
		}
		return true;
	}

}