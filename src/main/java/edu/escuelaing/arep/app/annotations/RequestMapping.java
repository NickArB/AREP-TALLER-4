package edu.escuelaing.arep.app.annotations;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public
@interface RequestMapping {
    public String route();
}
