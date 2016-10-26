package com.redtea.jdbc.warp.base;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class SqlBuilder {

    private static final Logger logger = LoggerFactory.getLogger(SqlBuilder.class);

    private static final List<Character> upper =
            ImmutableList.of('A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N',
                    'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z');
    private static final List<Character> lower =
            ImmutableList.of('a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n',
                    'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z');

    public static String getInsertSql(Class<? extends BasePo> c, List<Field> includeFields) {
        StringBuilder sb = new StringBuilder();
        String tableName = getDbName(c.getSimpleName());
        sb.append("insert into `").append(tableName).append("` (");
        Field[] fields = c.getDeclaredFields();
        Arrays.stream(fields).filter(includeFields::contains).forEach(f -> {
            sb.append("`").append(getDbName(f.getName())).append("`,");
        });
        sb.replace(sb.length() - 1, sb.length(), ") values (");
        Arrays.stream(fields).filter(includeFields::contains).forEach(f -> {
            sb.append("?").append(",");
        });
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
                    sb.append("_").append(lower.get(upper.indexOf(l)));
                }
            } else {
                sb.append(l);
            }
        }
        return sb.toString();
    }

    public static String getInsertOrUpdateSql(Class<? extends BasePo> c, List<Field> includeFields) {
        StringBuilder sb = new StringBuilder();
        String tableName = getDbName(c.getSimpleName());
        sb.append("insert into `").append(tableName).append("` (");
        Field[] fields = c.getDeclaredFields();
        Arrays.stream(fields).filter(includeFields::contains).forEach(f -> {
            sb.append("`").append(getDbName(f.getName())).append("`,");
        });
        sb.replace(sb.length() - 1, sb.length(), ") values (");
        Arrays.stream(fields).filter(includeFields::contains).forEach(f -> {
            sb.append("?").append(",");
        });
        sb.replace(sb.length() - 1, sb.length(), ") on duplicate key update");
        Arrays.stream(fields).filter(includeFields::contains).forEach(f -> {
            sb.append(" `").append(getDbName(f.getName())).append("` = ? ,");
        });
        sb.replace(sb.length() - 1, sb.length(), "");
        return sb.toString();
    }

    public static String getSelectOneSql(Class<? extends BasePo> c,
                                         List<ConditionField> conditionFields, List<Field> includeFields) {
        return getSelectSql(c, conditionFields, includeFields, null, false, 0, 0);
    }

    public static String getSelectSql(Class<? extends BasePo> c,
                                      List<ConditionField> conditionFields, List<Field> includeFields, List<Field> orderFields,
                                      boolean asc, int limit, int index) {
        StringBuilder sb = new StringBuilder();
        sb.append("select ");
        for (Field field : includeFields) {
            sb.append("`").append(getDbName(field.getName())).append("`,");
        }
        sb.replace(sb.length() - 1, sb.length(), " from `").append(getDbName(c.getSimpleName()))
                .append("`");
        if (conditionFields != null && conditionFields.size() > 0) {
            sb.append(" where");
            for (ConditionField cf : conditionFields) {
                sb.append(" `").append(getDbName(cf.getFieldName())).append("` ");
                switch (cf.getCondition()) {
                    case IN:
                    case NOT_IN:
                        String values = Lists.newArrayList(cf.getValues()).toString();
                        sb.append(cf.getCondition().getSign()).append(" (")
                                .append(values.substring(1, values.length() - 1)).append(")");
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

        if (orderFields != null && orderFields.size() > 0) {
            sb.append(" order by `");
            for (Field orderField : orderFields) {
                sb.append(getDbName(orderField.getName())).append("`,");
            }
            sb.replace(sb.length() - 1, sb.length(), asc ? "` asc" : " desc");
        }
        if (limit > 0) {
            sb.append(" limit ").append(limit);
            if (index > 0) {
                sb.append(" offset ").append(index);
            }
        }
        return sb.toString();
    }
}
