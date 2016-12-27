package me.yoruichi.mis;

import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.List;

/**
 * Created by yoruichi on 16/12/27.
 */
public class InsertOrUpdateNeedMany {
    public String sql;
    public List<Object[]> args;

    public InsertOrUpdateNeedMany(String sql, List<Object[]> args) {
        this.sql = sql;
        this.args = args;
    }

    public static InsertOrUpdateNeedMany getInsertOrUpdateNeedMany(List<? extends BasePo> os) throws Exception {
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
            args.add(obs.toArray());
        }
        return new InsertOrUpdateNeedMany(SqlBuilder.getInsertOrUpdateSql(clazz, inc), args);
    }
}
