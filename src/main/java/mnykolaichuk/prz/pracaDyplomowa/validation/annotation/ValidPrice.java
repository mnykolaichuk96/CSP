package mnykolaichuk.prz.pracaDyplomowa.validation.annotation;

import mnykolaichuk.prz.pracaDyplomowa.validation.PriceValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = PriceValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPrice {

	String message() default "{mnykolaichuk.prz.ValidNotEmpty.message}";

	String messageNotEmpty() default "{mnykolaichuk.prz.ValidNotEmpty.message}";

	String messageNotMinus() default "{mnykolaichuk.prz.NotMinus.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
