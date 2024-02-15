package edu.escuelaing.arep.app;

import edu.escuelaing.arep.app.Component;

@Component
public class WebComponent {
    @GetMapping(route = "sample")
    public static String sample(String id){
        System.out.println("it works!");
        return id;
    }
}
