package mnykolaichuk.prz.pracaDyplomowa.validation.annotation;

import mnykolaichuk.prz.pracaDyplomowa.validation.PasswordValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = PasswordValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidPassword {
	String message() default "{mnykolaichuk.prz.Password.message}";

	String messageNotEmpty() default "{mnykolaichuk.prz.ValidNotEmpty.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
