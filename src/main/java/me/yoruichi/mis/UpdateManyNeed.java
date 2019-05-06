package me.yoruichi.mis;

import com.google.common.collect.Lists;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author yoruichi
 * @date 16/12/28
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
            obs.addAll(Arrays.asList(UpdateNeed.getConditionArgs(o)));
            args.add(obs.toArray());
        });
        return args;
    }

    public static UpdateManyNeed getUpdateManyNeed(List<? extends BasePo> list) throws Exception {
        if (null == list || list.size() == 0) {
            throw new Exception("There is no value to process.");
        }
        BasePo o = list.get(0);
        if (o.getUpdateFieldMap().size() == 0) {
            throw new Exception("Object has no update value, please check.");
        }
        for (BasePo po : list) {
            po.ready();
        }
        return new UpdateManyNeed(SqlBuilder.getUpdateSql(o), getUpdateArgs(list));
    }
}
