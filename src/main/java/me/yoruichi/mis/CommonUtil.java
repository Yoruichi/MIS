package me.yoruichi.mis;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @Author: Yoruichi
 * @Date: 2019/5/8 3:04 PM
 */
public class CommonUtil {

    public static Object getFieldValue(Field f, Object v) throws InvocationTargetException, IllegalAccessException {
        Alias a = f.getAnnotation(Alias.class);
        if (a != null) {
            return CommonUtil.getFieldValueByAlias(a, f, v);
        }

        Class<?>[] clazzArray = f.getType().getInterfaces();
        for (int j = 0; j < clazzArray.length; j++) {
            if (clazzArray[j].equals(GenericType.class)) {
                return ((GenericType) v).getCode();
            }
        }
        return v;
    }

    public static <T, R> R getFieldValueByAlias(Alias a, Field f, T v) throws InvocationTargetException, IllegalAccessException {
        Method[] methods = f.getType().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equalsIgnoreCase(a.name())) {
                return (R) methods[i].invoke(v, new Object[] {});
            }
        }
        throw new RuntimeException("No method of name " + a.name());
    }

    public static Object setFieldValue(Field f, Object v) throws InvocationTargetException, IllegalAccessException {
        Alias a = f.getAnnotation(Alias.class);
        if (a != null) {
            return CommonUtil.setFieldValueByAlias(a, f, v);
        }

        Class<?>[] clazzArray = f.getType().getInterfaces();
        for (int j = 0; j < clazzArray.length; j++) {
            if (clazzArray[j].equals(GenericType.class)) {
                Method[] methods = f.getType().getMethods();
                for (int i = 0; i < methods.length; i++) {
                    if (methods[i].getName().equalsIgnoreCase("codeOf")) {
                        return methods[i].invoke(null, new Object[] { v });
                    }
                }
            }
        }
        return v;
    }

    public static <T, R> R setFieldValueByAlias(Alias a, Field f, T v) throws InvocationTargetException, IllegalAccessException {
        Method[] methods = f.getType().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equalsIgnoreCase(a.decode())) {
                return (R) methods[i].invoke(null, new Object[] { v });
            }
        }
        throw new RuntimeException("No method of name " + a.decode());
    }
}
