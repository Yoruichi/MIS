package me.yoruichi.mis;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author yoruichi
 * @date 16/10/25
 */
public class InsertManyNeed {
    public String sql;
    public List<Object[]> args;

    public InsertManyNeed(String sql, List<Object[]> args) {
        this.sql = sql;
        this.args = args;
    }

    public static InsertManyNeed getInsertManyNeed(List<? extends BasePo> os) throws Exception {
        if (null == os || os.size() == 0) {
            throw new Exception("There is no value to process.");
        }
        List<Object[]> args = Lists.newLinkedList();
        List<Field> inc = Lists.newLinkedList();
        Class<? extends BasePo> clazz = os.get(0).getClass();
        Field[] fs = clazz.getDeclaredFields();
        processInc(os, inc, fs);
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
                        arg.add(CommonUtil.getFieldValue(fs[i], v));
                    }
                }
                args.add(arg.toArray());
            } else {
                throw new Exception("Wrong class type in parameter given,please check.");
            }
        }
        return new InsertManyNeed(SqlBuilder.getInsertSql(clazz, inc, os.get(0).getTableName()), args);
    }

    public static void processInc(List<? extends BasePo> os, List<Field> inc, Field[] fs) throws IllegalAccessException {
        for (int i = 0; i < fs.length; i++) {
            if (fs[i].isAnnotationPresent(Exclude.class)) {
                continue;
            }
            fs[i].setAccessible(true);
            Object v = fs[i].get(os.get(0));
            if (v != null) {
                inc.add(fs[i]);
            }
        }
    }
}
