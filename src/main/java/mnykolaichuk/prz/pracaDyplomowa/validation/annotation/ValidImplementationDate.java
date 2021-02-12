package mnykolaichuk.prz.pracaDyplomowa.validation.annotation;

import mnykolaichuk.prz.pracaDyplomowa.validation.ImplementationDateValidator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Constraint(validatedBy = ImplementationDateValidator.class)
@Target({ ElementType.TYPE, ElementType.FIELD, ElementType.ANNOTATION_TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ValidImplementationDate {

	String message() default "{mnykolaichuk.prz.ValidNotEmpty.message}";

	String messageNotEmpty() default "{mnykolaichuk.prz.ValidNotEmpty.message}";

	String messageNotPastDate() default "{mnykolaichuk.prz.NotPastDate.message}";

	Class<?>[] groups() default {};

	Class<? extends Payload>[] payload() default {};
}
