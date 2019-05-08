package me.yoruichi.mis;

import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;

/**
 * @Author: Yoruichi
 * @Date: 2019/5/8 2:35 PM
 */
public interface GenericType {
    default int getCode() {
        Field codeField = ReflectionUtils.findField(this.getClass(), "code");
        if (codeField != null) {
            codeField.setAccessible(true);

            try {
                return codeField.getInt(this);
            } catch (IllegalAccessException var3) {
                return -2147483648;
            }
        } else {
            return -2147483648;
        }
    }
}
