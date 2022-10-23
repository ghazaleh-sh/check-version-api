package ir.sadad.co.checkversionapi.commons.validators;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.ElementType.TYPE_USE;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

@Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
@Retention(RUNTIME)
@Repeatable(CheckSsnCrudUser.List.class)
@Documented
@Constraint(validatedBy = {CheckSsnCrudUserValidator.class})
public @interface CheckSsnCrudUser {
    String message() default "ebp.exceptions.validator.EBP40000028";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    String value() default "";

    @Target({METHOD, FIELD, ANNOTATION_TYPE, CONSTRUCTOR, PARAMETER, TYPE_USE})
    @Retention(RUNTIME)
    @Documented
    @interface List {
        CheckSsnCrudUser[] value();
    }
}
