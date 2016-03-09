package ru.cdecl.pub.iota.annotations;

import org.glassfish.hk2.api.AnnotationLiteral;
import org.glassfish.jersey.message.filtering.EntityFiltering;

import java.lang.annotation.*;

@Target({ElementType.TYPE, ElementType.METHOD, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
@Documented
@EntityFiltering
public @interface UserProfileIdView {

    @SuppressWarnings("ClassExplicitlyAnnotation")
    public static final class Factory extends AnnotationLiteral<UserProfileIdView> implements UserProfileIdView {

        private Factory() {
        }

        public static UserProfileIdView getInstance() {
            return new Factory();
        }

    }

}
