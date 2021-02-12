package mnykolaichuk.prz.pracaDyplomowa.validation.annotation;

import mnykolaichuk.prz.pracaDyplomowa.validation.PhoneNumberValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = PhoneNumberValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPhoneNumber {
	String message() default "{mnykolaichuk.prz.PhoneNumber.message}";

	String messageNotEmpty() default "{mnykolaichuk.prz.ValidNotEmpty.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
