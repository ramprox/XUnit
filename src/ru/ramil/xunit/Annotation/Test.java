package ru.ramil.xunit.Annotation;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Test {
    Priority priority() default Priority.PRIORITY_5;
}
