package me.yoruichi.mis;

import com.google.common.collect.ImmutableList;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Yoruichi
 */
public class SqlBuilder {

    public static final List<Character> upper =
            ImmutableList.of('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                    'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');
    public static final List<Character> lower =
            ImmutableList.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                    'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');
    public static final String SPLITTER_IN_TABLE_NAME = "_";

    public static String getUpdateSql(BasePo o) {
        return getUpdateSql(o.getClass(), o.getConditionFieldList(), o.getOrConditionList(), o.getAndConditionList(), o.getUpdateFieldMap(), o.getTableName());
    }

    public static String getUpdateSql(Class<? extends BasePo> c, List<ConditionField> conditionFields, List<? extends BasePo> orConditionList,
            List<? extends BasePo> andConditionList, Map<Field, Object> updateFieldMap, String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("update `").append(tableName).append("` set ");
        updateFieldMap.keySet().stream()
                .forEach(f -> sb.append("`").append(getDbName(f.getName())).append("` = ?")
                        .append(" ,"));
        sb.replace(sb.length() - 1, sb.length(), " ");
        getConditionSqlOuter(c, conditionFields, orConditionList, andConditionList, sb);
        return sb.toString();
    }

    public static String getInsertSql(Class<? extends BasePo> c, List<Field> includeFields, String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into `").append(tableName).append("` (");
        Field[] fields = c.getDeclaredFields();
        Arrays.stream(fields).filter(includeFields::contains)
                .forEach(f -> sb.append("`").append(getDbName(f.getName())).append("`,"));
        sb.replace(sb.length() - 1, sb.length(), ") values (");
        Arrays.stream(fields).filter(includeFields::contains)
                .forEach(f -> sb.append("?").append(","));
        sb.replace(sb.length() - 1, sb.length(), ")");
        return sb.toString();
    }

    public static String getDbName(String zName) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < zName.length(); i++) {
            char l = zName.charAt(i);
            if (upper.contains(l)) {
                if (i == 0) {
                    sb.append(lower.get(upper.indexOf(l)));
                } else {
                    sb.append(SPLITTER_IN_TABLE_NAME).append(lower.get(upper.indexOf(l)));
                }
            } else {
                sb.append(l);
            }
        }
        return sb.toString();
    }

    public static String getInsertOrUpdateSql(Class<? extends BasePo> c, List<Field> includeFields, String tableName) {
        StringBuilder sb = new StringBuilder();
        sb.append("insert into `").append(tableName).append("` (");
        Field[] fields = c.getDeclaredFields();
        Arrays.stream(fields).filter(includeFields::contains)
                .forEach(f -> sb.append("`").append(getDbName(f.getName())).append("`,"));
        sb.replace(sb.length() - 1, sb.length(), ") values (");
        Arrays.stream(fields).filter(includeFields::contains)
                .forEach(f -> sb.append("?").append(","));
        sb.replace(sb.length() - 1, sb.length(), ") on duplicate key update");
        Arrays.stream(fields).filter(includeFields::contains)
                .forEach(f -> sb.append(" `").append(getDbName(f.getName())).append("` = ? ,"));
        sb.replace(sb.length() - 1, sb.length(), "");
        return sb.toString();
    }

    public static String getSelectOneSql(Class<? extends BasePo> c, String tableName,
            List<ConditionField> conditionFields, List<? extends BasePo> orConditionList,
            List<? extends BasePo> andConditionList,
            List<Field> includeFields, boolean isForUpdate) {
        return getSelectSql(c, tableName, conditionFields, orConditionList, andConditionList, includeFields, null, false, 0, 0, isForUpdate);
    }

    public static <T extends BasePo> String getConditionSql(T o) {
        StringBuilder sb = new StringBuilder();
        Class<? extends BasePo> c = o.getClass();
        List<ConditionField> conditionFields = o.getConditionFieldList();
        List<? extends BasePo> orConditionList = o.getOrConditionList();
        List<? extends BasePo> andConditionList = o.getAndConditionList();
        boolean hasCondition = conditionFields != null && conditionFields.size() > 0;
        boolean hasOrCondition = orConditionList != null && orConditionList.size() > 0;
        boolean hasAndCondition = andConditionList != null && andConditionList.size() > 0;
        if (hasCondition) {
            if (hasOrCondition || hasAndCondition) {
                sb.append("(").append(getConditionSql(c, conditionFields)).append(")");
            } else {
                sb.append(getConditionSql(c, conditionFields));
            }
        }

        if (hasOrCondition) {
            orConditionList.stream().filter(oc -> oc.getConditionFieldList().size() > 0
                    || oc.getOrConditionList().size() > 0 || oc.getAndConditionList().size() > 0)
                    .forEach(oc -> sb.append(" or (").append(getConditionSql(oc)).append(")"));
        }
        if (hasAndCondition) {
            andConditionList.stream().filter(oc -> oc.getConditionFieldList().size() > 0
                    || oc.getOrConditionList().size() > 0 || oc.getAndConditionList().size() > 0)
                    .forEach(ac -> sb.append(" and (").append(getConditionSql(ac)).append(")"));
        }

        return sb.toString();
    }

    public static String getConditionSql(Class<? extends BasePo> c,
            List<ConditionField> conditionFields) {
        StringBuilder sb = new StringBuilder();
        if (conditionFields != null && conditionFields.size() > 0) {
            for (ConditionField cf : conditionFields) {
                sb.append(" `").append(getDbName(cf.getFieldName())).append("` ");
                switch (cf.getCondition()) {
                case IN:
                case NOT_IN:
                    sb.append(cf.getCondition().getSign()).append(" (");
                    Arrays.stream(cf.getValues()).forEach(a -> {
                        sb.append("?, ");
                    });
                    sb.replace(sb.length() - 2, sb.length() - 1, ")");
                    break;
                case IS_NULL:
                case IS_NOT_NULL:
                    sb.append(cf.getCondition().getSign());
                    break;
                default:
                    sb.append(cf.getCondition().getSign()).append(" ?");
                    break;
                }
                sb.append(" and");
            }
            sb.replace(sb.length() - 3, sb.length(), "");
        }
        return sb.toString();
    }

    public static String getSelectSql(Class<? extends BasePo> c, String tableName, List<ConditionField> conditionFields, List<? extends BasePo> orConditionList,
            List<? extends BasePo> andConditionList, List<Field> includeFields, Collection<OrderField> orderFields, boolean asc, int limit, int index,
            boolean isForUpdate) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        for (Field field : includeFields) {
            sb.append("`").append(getDbName(field.getName())).append("`,");
        }
        sb.replace(sb.length() - 1, sb.length(), " from `").append(tableName)
                .append("`");
        getConditionSqlOuter(c, conditionFields, orConditionList, andConditionList, sb);

        if (orderFields != null && orderFields.size() > 0) {
            sb.append(" order by ");
            for (OrderField orderField : orderFields) {
                sb.append("`").append(getDbName(orderField.getFieldName())).append("` ").append(orderField.isAsc() ? " asc" : " desc").append(",");
            }
            sb.replace(sb.length() - 1, sb.length(), " ");
        }
        if (limit > 0) {
            sb.append(" limit ").append(limit);
            if (index > 0) {
                sb.append(" offset ").append(index);
            }
        }
        if (isForUpdate) {
            sb.append(" for update");
        }
        return sb.toString();
    }

    public static String getSelectCountSql(Class<? extends BasePo> c, String tableName, List<ConditionField> conditionFields,
            List<? extends BasePo> orConditionList, List<? extends BasePo> andConditionList) {
        StringBuilder sb = new StringBuilder();
        sb.append("select count(1)").append(" from `").append(tableName).append("`");
        getConditionSqlOuter(c, conditionFields, orConditionList, andConditionList, sb);

        return sb.toString();
    }

    private static void getConditionSqlOuter(Class<? extends BasePo> c, List<ConditionField> conditionFields, List<? extends BasePo> orConditionList,
            List<? extends BasePo> andConditionList, StringBuilder sb) {
        if (conditionFields != null && conditionFields.size() > 0) {
            sb.append(" where");
            boolean hasOrCondition = orConditionList != null && orConditionList.size() > 0;
            boolean hasAndCondition = andConditionList != null && andConditionList.size() > 0;
            if (hasOrCondition || hasAndCondition) {
                sb.append("(").append(getConditionSql(c, conditionFields)).append(")");
            } else {
                sb.append(getConditionSql(c, conditionFields));
            }
            if (hasOrCondition) {
                orConditionList.stream().filter(oc -> oc.getConditionFieldList().size() > 0
                        || oc.getOrConditionList().size() > 0
                        || oc.getAndConditionList().size() > 0)
                        .forEach(oc -> sb.append(" or (").append(getConditionSql(oc)).append(")"));
            }
            if (hasAndCondition) {
                andConditionList.stream().filter(oc -> oc.getConditionFieldList().size() > 0
                        || oc.getOrConditionList().size() > 0
                        || oc.getAndConditionList().size() > 0)
                        .forEach(ac -> sb.append(" and (").append(getConditionSql(ac)).append(")"));
            }
        }
    }
}
