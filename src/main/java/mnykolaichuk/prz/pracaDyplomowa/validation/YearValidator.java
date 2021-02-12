package mnykolaichuk.prz.pracaDyplomowa.validation;

import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidYear;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.Year;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class YearValidator implements ConstraintValidator<ValidYear, String> {

	private Pattern pattern;
	private Matcher matcher;
	private String messageNotEmpty;
	private String messageNotFutureYear;
	private String message;
	//year 1900-2099
	private static final String YEAR_PATTERN = "^(19|20)\\d{2}$";

	@Override
	public void initialize(ValidYear constraintAnnotation) {
		messageNotEmpty = constraintAnnotation.messageNotEmpty();
		messageNotFutureYear = constraintAnnotation.messageNotFutureYear();
		message = constraintAnnotation.message();
	}

	@Override
	public boolean isValid(final String year, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		if (year.isEmpty()) {
			context.buildConstraintViolationWithTemplate(messageNotEmpty).addConstraintViolation();
			return false;
		}
		pattern = Pattern.compile(YEAR_PATTERN);
		matcher = pattern.matcher(year);
		if(matcher.matches()) {
			//year <= current year
			if(Year.now().compareTo(Year.of(Integer.parseInt(year))) < 0) {
				context.buildConstraintViolationWithTemplate(messageNotFutureYear).addConstraintViolation();
				return false;
			}
			return true;
		}
		context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
		return false;
	}

}