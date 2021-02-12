package mnykolaichuk.prz.pracaDyplomowa.validation.annotation;

import mnykolaichuk.prz.pracaDyplomowa.validation.NameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = NameValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidName {
	String message() default "{mnykolaichuk.prz.Name.message}";

	String messageNotEmpty() default "{mnykolaichuk.prz.ValidNotEmpty.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
