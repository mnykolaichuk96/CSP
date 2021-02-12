package mnykolaichuk.prz.pracaDyplomowa.validation.annotation;

import mnykolaichuk.prz.pracaDyplomowa.validation.UsernameValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = UsernameValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidUsername {
	String message() default "{mnykolaichuk.prz.Username.message}";

	String messageNotEmpty() default "{mnykolaichuk.prz.ValidNotEmpty.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
