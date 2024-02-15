package edu.escuelaing.arep.app;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;

public class InvokeMain {
    public static void main(String... args) {
        try {
            Class<?> c = Class.forName(args[0]);
            Class[] argTypes = new Class[] { int.class, int.class };
            Method suma = c.getDeclaredMethod("suma", argTypes);
            String[] sumaArgs = Arrays.copyOfRange(args, 1, args.length);
            System.out.format("invoking %s.suma()%n", c.getName());
            suma.invoke(null, Integer.parseInt(sumaArgs[0]), Integer.parseInt(sumaArgs[1]));
            // production code should handle these exceptions more gracefully
        } catch (ClassNotFoundException x) {
            x.printStackTrace();
        } catch (NoSuchMethodException x) {
            x.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}
