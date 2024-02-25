package edu.escuelaing.arep.app.web;

import edu.escuelaing.arep.app.annotations.*;

@Component
public class WebComponent {
    @RequestMapping(route = "sample")
    public static String sample(String id){
        return "This is a test for the request mapping, your parameter is " + id;
    }

    @RequestMapping(route = "cos")
    public static String calculateCos(String value){
        return "" + Math.cos(Double.parseDouble(value));
    }
}
