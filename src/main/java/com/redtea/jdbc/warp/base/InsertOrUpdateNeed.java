package com.redtea.jdbc.warp.base;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by yoruichi on 16/10/25.
 */
public class InsertOrUpdateNeed {
    public String sql;
    public Object[] args;

    public InsertOrUpdateNeed(String sql, Object[] args) {
        this.sql = sql;
        this.args = args;
    }

    public static InsertOrUpdateNeed getInsertOrUpdateNeed(BasePo o) throws Exception {
        Class<? extends BasePo> clazz = o.getClass();
        Field[] fs = clazz.getDeclaredFields();
        List<Field> inc = Lists.newLinkedList();
        List<Object> obs = Lists.newLinkedList();
        List<Object> obss = Lists.newLinkedList();
        for (int i = 0; i < fs.length; i++) {
            fs[i].setAccessible(true);
            Object v = fs[i].get(o);
            if (v != null) {
                inc.add(fs[i]);
                obs.add(v);
                obss.add(v);
            }
        }
        obs.addAll(obss);
        if (inc.size() == 0) throw new Exception("Object has no valid value,please check.");
        return new InsertOrUpdateNeed(SqlBuilder.getInsertOrUpdateSql(clazz, inc), obs.toArray());
    }
}
