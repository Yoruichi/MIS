package com.redteamobile.mis;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yoruichi on 16/12/28.
 */
public class UpdateNeed {
    public String sql;
    public Object[] args;

    public UpdateNeed(String sql, Object[] args) {
        this.sql = sql;
        this.args = args;
    }

    private static Object[] getConditionArgs(BasePo o) {
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
//        obs.addAll(o.getConditionFieldList().stream()
//                .filter(cf -> cf.getCondition() != BasePo.CONDITION.IN
//                        && cf.getCondition() != BasePo.CONDITION.NOT_IN
//                        && cf.getCondition() != BasePo.CONDITION.IS_NULL
//                        && cf.getCondition() != BasePo.CONDITION.IS_NOT_NULL)
//                .map(ConditionField::getValue).collect(Collectors.toList()));
        o.getOrConditionList().stream().forEach(oo ->
                obs.addAll(Arrays.asList(getConditionArgs(oo)))
        );
        o.getAndConditionList().stream().forEach(oo ->
                obs.addAll(Arrays.asList(getConditionArgs(oo)))
        );
        return obs.toArray();
    }

    private static Object[] getUpdateArgs(BasePo o) {
        List<Object> obs = Lists.newLinkedList();
        obs.addAll(o.getUpdateFieldMap().values());
        obs.addAll(Arrays.asList(getConditionArgs(o)));
        return obs.toArray();
    }

    public static UpdateNeed getUpdateNeed(BasePo o) throws Exception {
        if (o.getUpdateFieldMap().size() == 0) {
            throw new Exception("Object has no update value,please check.");
        }
        o.ready();
        return new UpdateNeed(SqlBuilder
                .getUpdateSql(o.getClass(), o.getConditionFieldList(), o.getOrConditionList(),o.getAndConditionList(),
                        o.getUpdateFieldMap()), getUpdateArgs(o));
    }
}
