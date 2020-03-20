package ml.dreamingfire.group.prod.httpserver.anno;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestMapping {
    // uri, e.g. /test
    String value() default "/";
    // request method, e.g. GET, POST, JSON
    String[] method() default {};
}
