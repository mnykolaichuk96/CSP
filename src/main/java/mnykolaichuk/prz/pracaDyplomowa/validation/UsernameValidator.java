package mnykolaichuk.prz.pracaDyplomowa.validation;

import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidUsername;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

	private Pattern pattern;
	private Matcher matcher;
	private String messageNotEmpty;
	private String message;
	//username is 8-20 characters, no _ or . at the beginning, no __ or _. or ._ or .. inside, allowed characters
	//, no _ or . at the end (за кожен розділ відповідає 1 пара() в регулярці)
	private static final String USERNAME_PATTERN = "^(?=.{8,20}$)(?![_.])(?!.*[_.]{2})[a-zA-Z0-9._]+(?<![_.])$";

	@Override
	public void initialize(ValidUsername constraintAnnotation) {
		messageNotEmpty = constraintAnnotation.messageNotEmpty();
		message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(final String username, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		if (username.isEmpty()) {
			context.buildConstraintViolationWithTemplate(messageNotEmpty).addConstraintViolation();
			return false;
		}
		pattern = Pattern.compile(USERNAME_PATTERN);
		matcher = pattern.matcher(username);
		if(!matcher.matches()) {
			context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
			return false;
		}
		return true;
	}

}