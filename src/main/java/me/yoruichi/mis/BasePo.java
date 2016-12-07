package me.yoruichi.mis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

public class BasePo implements Serializable {

    @JsonIgnoreProperties(value = {
            "conditionFieldList",
            "groupByField",
            "orderByField",
            "asc",
            "limit",
            "index",
            "forUpdate",
    })

    @ApiModelProperty(hidden = true)
    private List<ConditionField> conditionFieldList = Lists.newLinkedList();
    @ApiModelProperty(hidden = true)
    private List<String> groupByField = Lists.newLinkedList();
    @ApiModelProperty(hidden = true)
    private List<String> orderByField = Lists.newLinkedList();
    @ApiModelProperty(hidden = true)
    private boolean asc;
    @ApiModelProperty(hidden = true)
    private int limit;
    @ApiModelProperty(hidden = true)
    private int index;
    @ApiModelProperty(hidden = true)
    private boolean forUpdate;

    public BasePo groupBy(String... fields) {
        getGroupByField().addAll(Arrays.asList(fields));
        return this;
    }

    public BasePo orderBy(String... fields) {
        getOrderByField().addAll(Arrays.asList(fields));
        return this;
    }

    public BasePo gt(String field, Object o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.GT)
                        .setValue(o));
        return this;
    }

    public BasePo gt(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.GT)
                        .setValue(f.get(this)));
        return this;
    }

    public BasePo gte(String field, Object o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.GTE)
                        .setValue(o));
        return this;
    }

    public BasePo gte(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.GTE)
                        .setValue(f.get(this)));
        return this;
    }

    public BasePo lt(String field, Object o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.LT)
                        .setValue(o));
        return this;
    }

    public BasePo lt(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.LT)
                        .setValue(f.get(this)));
        return this;
    }

    public BasePo lte(String field, Object o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.LTE)
                        .setValue(o));
        return this;
    }

    public BasePo lte(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.LTE)
                        .setValue(f.get(this)));
        return this;
    }

    public BasePo eq(String field, Object o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.EQ)
                        .setValue(o));
        return this;
    }

    public BasePo eq(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.EQ)
                        .setValue(f.get(this)));
        return this;
    }

    public BasePo ne(String field, Object o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.NE)
                        .setValue(o));
        return this;
    }

    public BasePo ne(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.NE)
                        .setValue(f.get(this)));
        return this;
    }

    public BasePo isNull(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.IS_NULL)
                        .setValue(null));
        return this;
    }

    public BasePo isNotNull(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldList()
                .add(new ConditionField().setFieldName(field).setCondition(CONDITION.IS_NOT_NULL)
                        .setValue(null));
        return this;
    }

    public BasePo in(String field, Object[] o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        if (f.getType().equals(String.class)) {
            String[] so = new String[o.length];
            for (int i = 0; i < o.length; i++) {
                so[i] = "'" + o[i].toString() + "'";
            }
            getConditionFieldList()
                    .add(new ConditionField().setFieldName(field).setCondition(CONDITION.IN)
                            .setValues(so));
        } else {
            getConditionFieldList()
                    .add(new ConditionField().setFieldName(field).setCondition(CONDITION.IN)
                            .setValues(o));
        }
        return this;
    }

    public BasePo notIn(String field, Object[] o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        if (f.getType().equals(String.class)) {
            String[] so = new String[o.length];
            for (int i = 0; i < o.length; i++) {
                so[i] = "'" + o[i].toString() + "'";
            }
            getConditionFieldList()
                    .add(new ConditionField().setFieldName(field).setCondition(CONDITION.IN)
                            .setValues(so));
        } else {
            getConditionFieldList()
                    .add(new ConditionField().setFieldName(field).setCondition(CONDITION.NOT_IN)
                            .setValues(o));
        }
        return this;
    }

    public List<ConditionField> getConditionFieldList() {
        return conditionFieldList;
    }

    public BasePo setConditionFieldList(List<ConditionField> conditionFieldList) {
        this.conditionFieldList = conditionFieldList;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    public BasePo setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    public int getIndex() {
        return index;
    }

    public BasePo setIndex(int index) {
        this.index = index;
        return this;
    }

    public boolean isAsc() {
        return asc;
    }

    public BasePo setAsc(boolean asc) {
        this.asc = asc;
        return this;
    }

    public List<String> getOrderByField() {
        return orderByField;
    }

    public BasePo setOrderByField(List<String> orderByField) {
        this.orderByField = orderByField;
        return this;
    }

    public List<String> getGroupByField() {
        return groupByField;
    }

    public BasePo setGroupByField(List<String> groupByField) {
        this.groupByField = groupByField;
        return this;
    }

    public boolean isForUpdate() {
        return forUpdate;
    }

    public BasePo setForUpdate(boolean forUpdate) {
        this.forUpdate = forUpdate;
        return this;
    }

    public enum CONDITION {
        GT(">"), EQ("="), LT("<"), GTE(">="), LTE("<="), IN("in"), IS_NULL("is null"), IS_NOT_NULL(
                "is not null"), NE("<>"), NOT_IN("not in");
        private String sign;

        CONDITION(String sign) {
            this.setSign(sign);
        }

        public String getSign() {
            return sign;
        }

        public void setSign(String sign) {
            this.sign = sign;
        }
    }

    public void ready() throws Exception {
        Class<? extends BasePo> clazz = this.getClass();
        Field[] fs = clazz.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            fs[i].setAccessible(true);
            Object v = fs[i].get(this);
            if (v != null) {
                this.eq(fs[i].getName());
            }
        }
    }
}
