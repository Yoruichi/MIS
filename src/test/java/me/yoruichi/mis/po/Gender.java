package me.yoruichi.mis.po;

import me.yoruichi.mis.GenericType;

/**
 * @Author: Yoruichi
 * @Date: 2019/5/8 2:00 PM
 */
public enum Gender  {
    M(0),
    F(1);

    Gender(int code) {
        this.code = code;
    }

    private int code;

    public int getCode() {
        return code;
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
