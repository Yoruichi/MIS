package me.yoruichi.mis;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author yoruichi
 * @date 16/10/25
 */
public class InsertOneNeed {
    public String sql;
    public Object[] args;

    public InsertOneNeed(String sql, Object[] args) {
        this.sql = sql;
        this.args = args;
    }

    public static InsertOneNeed getInsertOneNeed(BasePo o) throws Exception {
        if (null == o) {
            throw new Exception("There is no value to process.");
        }
        Class<? extends BasePo> clazz = o.getClass();
        Field[] fs = clazz.getDeclaredFields();
        List<Field> inc = Lists.newLinkedList();
        List<Object> obs = Lists.newLinkedList();
        for (int i = 0; i < fs.length; i++) {
            fs[i].setAccessible(true);
            Object v = fs[i].get(o);
            if (v != null) {
                inc.add(fs[i]);
                boolean seated = false;
                Class<?>[] clazzArray = fs[i].getType().getInterfaces();
                for (int j = 0; j < clazzArray.length; j++) {
                    if (clazzArray[j].equals(GenericType.class)) {
                        obs.add(((GenericType) v).getCode());
                        seated = true;
                        break;
                    }
                }
                if (!seated) {
                    obs.add(v);
                }
            }
        }
        if (inc.size() == 0) {
            throw new Exception("Object has no valid value,please check.");
        }
        return new InsertOneNeed(SqlBuilder.getInsertSql(clazz, inc, o.getTableName()), obs.toArray());
    }
}
