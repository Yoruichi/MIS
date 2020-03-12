package me.yoruichi.mis;

import com.google.common.collect.Lists;
import io.vavr.CheckedFunction0;
import io.vavr.control.Try;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yoruichi
 * @date 16/10/25
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
        if (null == o) {
            throw new Exception("There is no value to process.");
        }
        Class<? extends BasePo> clazz = o.getClass();
        Field[] fs = clazz.getDeclaredFields();
        String[] includeFields =
                Arrays.stream(fs).filter(field -> !field.isAnnotationPresent(Exclude.class)).map(field -> field.getName()).toArray(String[]::new);
        return getSelectOneNeed(o, includeFields, o.isForUpdate());
    }

    private static SelectNeed getSelectOneNeed(BasePo o, String[] includeFields, boolean isForUpdate) throws Exception {
        Class<? extends BasePo> clazz = o.getClass();
        List<Field> inc = Lists.newLinkedList();
        for (int i = 0; i < includeFields.length; i++) {
            inc.add(clazz.getDeclaredField(includeFields[i]));
        }
        if (inc.size() == 0) {
            throw new Exception("Object has no valid value,please check.");
        }
        o.ready();
        return new SelectNeed(SqlBuilder
                .getSelectOneSql(clazz, o.getTableName(), o.getConditionFieldList(), o.getOrConditionList(), o.getAndConditionList(), inc, isForUpdate),
                getSelectArgs(o));
    }

    public static SelectNeed getSelectCountNeed(BasePo o) throws Exception {
        if (null == o) {
            throw new Exception("There is no value to process.");
        }
        Class<? extends BasePo> clazz = o.getClass();
        o.ready();
        return new SelectNeed(SqlBuilder.getSelectCountSql(clazz, o.getTableName(), o.getConditionFieldList(), o.getOrConditionList(), o.getAndConditionList()),
                getSelectArgs(o));
    }

    private static Object[] getSelectArgs(BasePo o) {
        List<Object> obs = prepareConditionFieldListForPo(o);
        o.getOrConditionList().stream().forEach(oo ->
                obs.addAll(Arrays.asList(getSelectArgs(oo)))
        );
        o.getAndConditionList().stream().forEach(oo ->
                obs.addAll(Arrays.asList(getSelectArgs(oo)))
        );
        return obs.toArray();
    }

    static List<Object> prepareConditionFieldListForPo(BasePo o) {
        List<Object> obs = Lists.newLinkedList();
        o.getConditionFieldList().stream()
                .forEach(cf -> {
                            switch (cf.getCondition()) {
                            case IN:
                            case NOT_IN:
                                obs.addAll(Arrays.asList(cf.getValues()));
                                break;
                            case IS_NULL:
                            case IS_NOT_NULL:
                                break;
                            default:
                                obs.add(cf.getValue());
                                break;
                            }
                        }
                );
        return obs;
    }

    public static SelectNeed getSelectManyNeed(BasePo o) throws Exception {
        if (null == o) {
            throw new Exception("There is no value to process.");
        }
        Class<? extends BasePo> clazz = o.getClass();
        Field[] fs = clazz.getDeclaredFields();
        String[] includeFields =
                Arrays.stream(fs).filter(field -> !field.isAnnotationPresent(Exclude.class)).map(field -> field.getName()).toArray(String[]::new);
        return getSelectManyNeed(o, includeFields, o.getOrderByField(), o.isAsc(), o.getLimit(), o.getIndex(), o.isForUpdate());
    }

    private static SelectNeed getSelectManyNeed(BasePo o, String[] includeFields, Collection<OrderField> orderByFields, boolean asc, int limit, int index,
            boolean isForUpdate) throws Exception {
        Class<? extends BasePo> clazz = o.getClass();
        List<Field> inc = Arrays.stream(includeFields).map(s -> Try.of(() -> clazz.getDeclaredField(s)).get())
                .filter(field -> !field.isAnnotationPresent(Exclude.class)).collect(Collectors.toList());
        if (inc.size() == 0) {
            throw new Exception("Object has no valid value,please check.");
        }
        o.ready();
        return new SelectNeed(SqlBuilder
                .getSelectSql(clazz, o.getTableName(), o.getConditionFieldList(), o.getOrConditionList(), o.getAndConditionList(), inc, orderByFields, asc,
                        limit, index, isForUpdate), getSelectArgs(o));
    }
}
