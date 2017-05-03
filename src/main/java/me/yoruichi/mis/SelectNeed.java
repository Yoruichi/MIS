package me.yoruichi.mis;

import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yoruichi on 16/10/25.
 */
public class SelectNeed {
    public String sql;
    public Object[] args;

    public SelectNeed(String sql, Object[] args) {
        this.sql = sql;
        this.args = args;
    }

    @Override
    public String toString() {
        String key = sql;
        for (int i = 0; i < args.length; i++) {
            if (key.contains("?")) {
                key = key.replaceFirst("\\?", args[i].toString());
            }
        }
        return key;
    }

    public static SelectNeed getSelectOneNeed(BasePo o) throws Exception {
        Class<? extends BasePo> clazz = o.getClass();
        Field[] fs = clazz.getDeclaredFields();
        String[] includeFields = new String[fs.length];
        for (int i = 0; i < fs.length; i++) {
            includeFields[i] = fs[i].getName();
        }
        return getSelectOneNeed(o, includeFields, o.isForUpdate());
    }

    public static SelectNeed getSelectOneNeed(BasePo o, String[] includeFields, boolean isForUpdate)
            throws Exception {
        Class<? extends BasePo> clazz = o.getClass();
        List<Field> inc = Lists.newLinkedList();
        for (int i = 0; i < includeFields.length; i++)
            inc.add(clazz.getDeclaredField(includeFields[i]));
        if (inc.size() == 0) throw new Exception("Object has no valid value,please check.");
        o.ready();
        return new SelectNeed(
                SqlBuilder
                        .getSelectOneSql(clazz, o.getConditionFieldList(), o.getOrConditionList(),
                                o.getAndConditionList(), inc, isForUpdate),
                getSelectArgs(o));
    }

    private static Object[] getSelectArgs(BasePo o) {
        List<Object> obs = Lists.newLinkedList();
        obs.addAll(o.getConditionFieldList().stream()
                .filter(cf -> cf.getCondition() != BasePo.CONDITION.IN
                        && cf.getCondition() != BasePo.CONDITION.NOT_IN
                        && cf.getCondition() != BasePo.CONDITION.IS_NULL
                        && cf.getCondition() != BasePo.CONDITION.IS_NOT_NULL)
                .map(ConditionField::getValue).collect(Collectors.toList()));
        o.getOrConditionList().stream().forEach(oo ->
                obs.addAll(Arrays.asList(getSelectArgs(oo)))
        );
        o.getAndConditionList().stream().forEach(oo ->
                obs.addAll(Arrays.asList(getSelectArgs(oo)))
        );
        return obs.toArray();
    }

    public static SelectNeed getSelectManyNeed(BasePo o) throws Exception {
        Class<? extends BasePo> clazz = o.getClass();
        Field[] fs = clazz.getDeclaredFields();
        String[] includeFields = new String[fs.length];
        for (int i = 0; i < fs.length; i++) {
            includeFields[i] = fs[i].getName();
        }
        List<Field> orderByFieldList = Lists.newLinkedList();
        for (String orderField : o.getOrderByField()) {
            Field orderByField = clazz.getDeclaredField(orderField);
            orderByFieldList.add(orderByField);
        }
        return getSelectManyNeed(o, includeFields, orderByFieldList, o.isAsc(), o.getLimit(),
                o.getIndex(), o.isForUpdate());
    }

    public static SelectNeed getSelectManyNeed(BasePo o, String[] includeFields,
            List<Field> orderByFields,
            boolean asc, int limit, int index, boolean isForUpdate) throws Exception {
        Class<? extends BasePo> clazz = o.getClass();
        List<Field> inc = Lists.newLinkedList();
        for (int i = 0; i < includeFields.length; i++)
            inc.add(clazz.getDeclaredField(includeFields[i]));
        if (inc.size() == 0) throw new Exception("Object has no valid value,please check.");
        o.ready();
        return new SelectNeed(
                SqlBuilder.getSelectSql(clazz, o.getConditionFieldList(), o.getOrConditionList(),
                        o.getAndConditionList(), inc, orderByFields, asc, limit, index,
                        isForUpdate),
                getSelectArgs(o));
    }
}
