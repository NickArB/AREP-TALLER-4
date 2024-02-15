package edu.escuelaing.arep.app;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)

@interface GetMapping {
    public String route();
}
