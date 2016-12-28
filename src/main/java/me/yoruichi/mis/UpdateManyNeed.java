package me.yoruichi.mis;

import com.google.common.collect.Lists;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by yoruichi on 16/12/28.
 */
public class UpdateManyNeed {
    public String sql;
    public List<Object[]> args;

    public UpdateManyNeed(String sql, List<Object[]> args) {
        this.sql = sql;
        this.args = args;
    }

    private static List<Object[]> getUpdateArgs(List<? extends BasePo> list) {
        List<Object[]> args = Lists.newLinkedList();
        list.forEach(o -> {
            List<Object> obs = Lists.newLinkedList();
            obs.addAll(o.getUpdateFieldMap().values());
            obs.addAll(o.getConditionFieldList().stream()
                    .filter(cf -> cf.getCondition() != BasePo.CONDITION.IN
                            && cf.getCondition() != BasePo.CONDITION.NOT_IN
                            && cf.getCondition() != BasePo.CONDITION.IS_NULL
                            && cf.getCondition() != BasePo.CONDITION.IS_NOT_NULL)
                    .map(ConditionField::getValue).collect(Collectors.toList()));
            o.getOrConditionList().stream().forEach(oo ->
                    obs.addAll(oo.getConditionFieldList().stream()
                            .filter(cf -> cf.getCondition() != BasePo.CONDITION.IN
                                    && cf.getCondition() != BasePo.CONDITION.NOT_IN
                                    && cf.getCondition() != BasePo.CONDITION.IS_NULL
                                    && cf.getCondition() != BasePo.CONDITION.IS_NOT_NULL)
                            .map(ConditionField::getValue).collect(Collectors.toList()))
            );
            args.add(obs.toArray());
        });
        return args;
    }

    public static UpdateManyNeed getUpdateManyNeed(List<? extends BasePo> list) throws Exception {
        BasePo o = list.get(0);
        if (o.getUpdateFieldMap().size() == 0) {
            throw new Exception("Object has no update value,please check.");
        }
        return new UpdateManyNeed(SqlBuilder
                .getUpdateSql(o.getClass(), o.getConditionFieldList(), o.getOrConditionFields(),
                        o.getUpdateFieldMap()), getUpdateArgs(list));
    }
}
