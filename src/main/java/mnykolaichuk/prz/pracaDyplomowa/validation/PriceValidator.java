package mnykolaichuk.prz.pracaDyplomowa.validation;

import mnykolaichuk.prz.pracaDyplomowa.validation.annotation.ValidPrice;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class PriceValidator implements ConstraintValidator<ValidPrice, Double> {

	private String messageNotEmpty;
	private String messageNotMinus;

	@Override
	public void initialize(ValidPrice constraintAnnotation) {
		messageNotEmpty = constraintAnnotation.messageNotEmpty();
		messageNotMinus = constraintAnnotation.messageNotMinus();
	}

	@Override
	public boolean isValid(final Double price, final ConstraintValidatorContext context) {
		context.disableDefaultConstraintViolation();
		if (price == null) {
			context.buildConstraintViolationWithTemplate(messageNotEmpty).addConstraintViolation();
			return false;
		}

		if(price < 0) {
			context.buildConstraintViolationWithTemplate(messageNotMinus).addConstraintViolation();
				return false;
		}
		return true;
	}

}