package me.yoruichi.mis;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @Author: Yoruichi
 * @Date: 2019/5/8 3:04 PM
 */
public class CommonUtil {

    public static Object[] getFieldValue(Field f, Object[] v) throws InvocationTargetException, IllegalAccessException {
        Alias a = f.getAnnotation(Alias.class);
        if (a != null) {
            return CommonUtil.getFieldValueByAlias(a, f, v);
        }
        AsJson aj = f.getAnnotation(AsJson.class);
        if (aj != null) {
            Object[] res = new Object[v.length];
            for (int i = 0; i < v.length; i++) {
                res[i] = JSON.toJSONString(v);
            }
            return res;
        }
        Class<?>[] clazzArray = f.getType().getInterfaces();
        for (int j = 0; j < clazzArray.length; j++) {
            if (f.getType().equals(JSONObject.class)) {
                Object[] res = new Object[v.length];
                for (int i = 0; i < v.length; i++) {
                    res[i] = JSON.toJSONString(v);
                }
                return res;
            }
        }
        return v;
    }

    public static Object getFieldValue(Field f, Object v) throws InvocationTargetException, IllegalAccessException {
        AsJson aj = f.getAnnotation(AsJson.class);
        if (aj != null) {
            return JSON.toJSONString(v);
        }
        Alias a = f.getAnnotation(Alias.class);
        if (a != null) {
            return CommonUtil.getFieldValueByAlias(a, f, v);
        }
        if (f.getType().equals(JSONObject.class)) {
            return JSON.toJSONString(v);
        }
        Class<?>[] clazzArray = f.getType().getInterfaces();
        for (int j = 0; j < clazzArray.length; j++) {
            if (clazzArray[j].equals(GenericType.class)) {
                return ((GenericType) v).getCode();
            }

        }
        return v;
    }

    public static <T> Object[] getFieldValueByAlias(Alias a, Field f, T[] v) throws InvocationTargetException, IllegalAccessException {
        Method[] methods = f.getType().getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (methods[i].getName().equalsIgnoreCase(a.name())) {
                Object[] res = new Object[v.length];
                for (int j = 0; j < v.length; j++) {
                    res[j] = methods[i].invoke(v[j], new Object[] {});
                }
                return res;
            }
        }
        throw new RuntimeException("No method of name " + a.name());
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
        AsJson aj = f.getAnnotation(AsJson.class);
        if (aj != null && v instanceof String) {
            return JSON.parseObject((String) v, f.getType());
        }
        Alias a = f.getAnnotation(Alias.class);
        if (a != null) {
            return CommonUtil.setFieldValueByAlias(a, f, v);
        }
        if (f.getType().equals(JSONObject.class) && v instanceof String) {
            return JSON.parseObject((String) v);
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
