package me.yoruichi.mis.po;

import me.yoruichi.mis.GenericType;

/**
 * @Author: Yoruichi
 * @Date: 2019/5/8 2:00 PM
 */
public enum Gender {
//        implements GenericType {
    M(0),
    F(1);

    Gender(int code) {
        this.code = code;
    }

    private int code;

    public String getName() {
        return this.name();
    }

    public int getCode() {
        return code;
    }

    public static Gender nameOf(String name) {
        Gender[] values = Gender.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].name().equals(name)) {
                return values[i];
            }
        }
        return M;
    }

    public static Gender codeOf(int code) {
        Gender[] values = Gender.values();
        for (int i = 0; i < values.length; i++) {
            if (values[i].code == code) {
                return values[i];
            }
        }
        return M;
    }
}
