package me.yoruichi.mis;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yoruichi
 * @date 16/12/28
 */
public class UpdateNeed {
    public String sql;
    public Object[] args;

    public UpdateNeed(String sql, Object[] args) {
        this.sql = sql;
        this.args = args;
    }

    static Object[] getConditionArgs(BasePo o) {
        List<Object> obs = SelectNeed.prepareConditionFieldListForPo(o);
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
        if (null == o) {
            throw new Exception("There is no value to process.");
        }
        if (o.getUpdateFieldMap().size() == 0) {
            throw new Exception("Object has no update value,please check.");
        }
        o.ready();
        return new UpdateNeed(SqlBuilder.getUpdateSql(o), getUpdateArgs(o));
    }
}
