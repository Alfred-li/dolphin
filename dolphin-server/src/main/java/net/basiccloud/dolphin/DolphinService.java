package net.basiccloud.dolphin;

import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * dolphin service annotation
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Service
public @interface DolphinService {

    /**
     * service version
     * @return version
     */
    String versions() default "";

    /**
     * proto class
     * @return class
     */
    Class<?> protoClass();

    /**
     * set this service is a mock service
     * @return true or false
     */
    boolean mock() default false;
}
