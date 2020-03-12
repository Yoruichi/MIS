package me.yoruichi.mis;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;

/**
 * @author yoruichi
 * @date 16/12/27
 */
public class InsertOrUpdateNeedMany {
    public String sql;
    public List<Object[]> args;

    public InsertOrUpdateNeedMany(String sql, List<Object[]> args) {
        this.sql = sql;
        this.args = args;
    }

    public static InsertOrUpdateNeedMany getInsertOrUpdateNeedMany(List<? extends BasePo> os) throws Exception {
        if (null == os || os.size() == 0) {
            throw new Exception("There is no value to process.");
        }
        List<Object[]> args = Lists.newLinkedList();
        List<Field> inc = Lists.newLinkedList();
        Class<? extends BasePo> clazz = os.get(0).getClass();
        Field[] fs = clazz.getDeclaredFields();
        InsertManyNeed.processInc(os, inc, fs);
        if (inc.size() == 0) {
            throw new Exception("Object has no valid value,please check.");
        }
        for (BasePo o : os) {
            List<Object> obs = Lists.newLinkedList();
            List<Object> obss = Lists.newLinkedList();
            for (Field f : inc) {
                f.setAccessible(true);
                Object v = f.get(o);
                if (v != null) {
                    Object value = CommonUtil.getFieldValue(f, v);
                    obs.add(value);
                    obss.add(value);
                }
            }
            obs.addAll(obss);
            args.add(obs.toArray());
        }
        return new InsertOrUpdateNeedMany(SqlBuilder.getInsertOrUpdateSql(clazz, inc, os.get(0).getTableName()), args);
    }
}
