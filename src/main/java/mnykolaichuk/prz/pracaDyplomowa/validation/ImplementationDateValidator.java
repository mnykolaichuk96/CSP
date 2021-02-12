package mnykolaichuk.prz.pracaDyplomowa.validation;

import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidImplementationDate;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;
import java.time.LocalDate;

public class ImplementationDateValidator implements ConstraintValidator<ValidImplementationDate, LocalDate> {

	private String messageNotEmpty;
	private String messageNotPastDate;

	@Override
	public void initialize(ValidImplementationDate constraintAnnotation) {
		messageNotEmpty = constraintAnnotation.messageNotEmpty();
		messageNotPastDate = constraintAnnotation.messageNotPastDate();
	}

	@Override
	public boolean isValid(final LocalDate implementationDate, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		if (implementationDate == null) {
			context.buildConstraintViolationWithTemplate(messageNotEmpty).addConstraintViolation();
			return false;
		}

		if(implementationDate.getDayOfYear() < LocalDate.now().getDayOfYear()) {
			context.buildConstraintViolationWithTemplate(messageNotPastDate).addConstraintViolation();
				return false;
		}
		return true;
	}

}