package edu.escuelaing.arep.app;

import java.util.HashMap;
import java.util.Map;
import java.lang.reflect.*;

public class HTTPServer {
    static Map<String, Method> components = new HashMap<>();
    public static void main(String[] args) throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        Class c = Class.forName(args[0]);

        if (c.isAnnotationPresent(Component.class)){
            for (Method m : c.getMethods()) {
                if (m.isAnnotationPresent(GetMapping.class)) {
                    components.put(m.getAnnotation(GetMapping.class).route(), m);
                }
            }
        }

        String sample = "/component/sample";
        Method m = components.get(sample.substring(11));

        String queryValue = "Sample";

        if(m != null){
            System.out.println(m.invoke(null, queryValue));
        }
    }
}
