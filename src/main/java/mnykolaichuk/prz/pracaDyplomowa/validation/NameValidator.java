package mnykolaichuk.prz.pracaDyplomowa.validation;

import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidName;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class NameValidator implements ConstraintValidator<ValidName, String> {

	private Pattern pattern;
	private Matcher matcher;
	private String messageNotNull;
	private String message;
	//перша літера велика решто маленькі
	private static final String NAME_PATTERN = "^[A-Z][a-z]+$";

	@Override
	public void initialize(ValidName constraintAnnotation) {
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
		pattern = Pattern.compile(NAME_PATTERN);
		matcher = pattern.matcher(name);
		if(!matcher.matches()) {
			context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
			return false;
		}
		return true;
	}

}