package com.redtea.jdbc.warp.base;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by yoruichi on 16/10/25.
 */
public class InsertManyNeed {
    public String sql;
    public List<Object[]> args;

    public InsertManyNeed(String sql, List<Object[]> args) {
        this.sql = sql;
        this.args = args;
    }

    public static InsertManyNeed getInsertManyNeed(List<? extends BasePo> os) throws Exception {
        List<Object[]> args = Lists.newLinkedList();
        List<Field> inc = Lists.newLinkedList();
        Class<? extends BasePo> clazz = os.get(0).getClass();
        Field[] fs = clazz.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            fs[i].setAccessible(true);
            Object v = fs[i].get(os.get(0));
            if (v != null) {
                inc.add(fs[i]);
            }
        }
        if (inc.size() == 0) {
            throw new Exception("Object has no valid value,please check.");
        }
        for (BasePo o : os) {
            if (o.getClass().equals(clazz)) {
                List<Object> arg = Lists.newLinkedList();
                for (int i = 0; i < fs.length; i++) {
                    fs[i].setAccessible(true);
                    Object v = fs[i].get(o);
                    if (v != null) {
                        arg.add(v);
                    }
                }
                args.add(arg.toArray());
            } else {
                throw new Exception("Wrong class type in parameter given,please check.");
            }
        }
        return new InsertManyNeed(SqlBuilder.getInsertSql(clazz, inc), args);
    }
}
