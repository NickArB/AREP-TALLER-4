package edu.escuelaing.arep.app.reflex;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import edu.escuelaing.arep.app.annotations.*;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.*;
import java.net.URL;

public class ReflexiveManager {
    static Map<String, Method> methods = new HashMap<>();

    public static void defineAllComponents() throws ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        List<Class<?>> components = getAllProjectClasses();

        for(Class<?> c: components){
            if (c.isAnnotationPresent(Component.class)){
                for (Method m : c.getMethods()) {
                    if (m.isAnnotationPresent(RequestMapping.class)) {
                        methods.put(m.getAnnotation(RequestMapping.class).route(), m);
                    }
                }
            }
        }
    }

    private static Set<String> getMethodsName(){
        return methods.keySet();
    }

    public static String invokeMethod(String methodName, String arg) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {
        if(getMethodsName().contains(methodName)){
            return methods.get(methodName).invoke(null, arg).toString();
        }else{
            throw new NoSuchMethodException();
        }
    }

    private static List<Class<?>> getAllProjectClasses() {
        List<Class<?>> classes = new ArrayList<>();
        ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

        try {
            Enumeration<URL> resources = classLoader.getResources("");
            while (resources.hasMoreElements()) {
                URL resource = resources.nextElement();
                if (resource.getProtocol().equals("file")) {
                    File file = new File(resource.getFile());
                    classes.addAll(classLookUpInDirectory(file, ""));
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    private static List<Class<?>> classLookUpInDirectory(File dir, String packageName) {
        List<Class<?>> classes = new ArrayList<>();
        if (dir.exists() && dir.isDirectory()) {
            for (File file : dir.listFiles()) {
                if (file.isDirectory()) {
                    classes.addAll(classLookUpInDirectory(file, packageName + file.getName() + "."));
                } else if (file.getName().endsWith(".class")) {
                    try {
                        String className = packageName + file.getName().replace(".class", "");
                        Class<?> clazz = Class.forName(className);
                        classes.add(clazz);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return classes;
    }

}
