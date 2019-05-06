package me.yoruichi.mis;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import io.swagger.annotations.ApiModelProperty;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BasePo implements Serializable {

    @JsonIgnoreProperties(value = {
            "conditionFieldList",
            //            "groupByField",
            "orderByField",
            "asc",
            "limit",
            "index",
            "forUpdate",
            "orConditionList",
            "updateFieldMap",
            "useCache",
    })

    @ApiModelProperty(hidden = true)
    private Map<String, ConditionField> conditionFieldMap = Maps.newLinkedHashMap();
    //    @ApiModelProperty(hidden = true)
    //    private Set<String> groupByField = Sets.newLinkedHashSet();
    @ApiModelProperty(hidden = true)
    private Set<OrderField> orderByField = Sets.newLinkedHashSet();
    @ApiModelProperty(hidden = true)
    private boolean asc;
    @ApiModelProperty(hidden = true)
    private int limit;
    @ApiModelProperty(hidden = true)
    private int index;
    @ApiModelProperty(hidden = true)
    private boolean forUpdate;
    @ApiModelProperty(hidden = true)
    private Map<Field, Object> updateFieldMap = Maps.newLinkedHashMap();
    @ApiModelProperty(hidden = true)
    private List<BasePo> orConditionList = Lists.newLinkedList();
    @ApiModelProperty(hidden = true)
    private List<BasePo> andConditionList = Lists.newLinkedList();
    @ApiModelProperty(hidden = true)
    private boolean useCache = false;

    public Map<Field, Object> getUpdateFieldMap() {
        return updateFieldMap;
    }

    public <T extends BasePo> T update(String field, Object value)
            throws NoSuchFieldException, IllegalAccessException {
        Field f = this.getClass().getDeclaredField(field);
        if (f != null) {
            this.updateFieldMap.put(f, value);
        }
        return (T) this;
    }

    public List<? extends BasePo> getAndConditionList() {
        return andConditionList;
    }

    public List<? extends BasePo> getOrConditionList() {
        return orConditionList;
    }

    public List<List<ConditionField>> getOrConditionFields() throws Exception {
        List<List<ConditionField>> orConditionFields = Lists.newLinkedList();
        for (BasePo o : this.getOrConditionList()) {
            orConditionFields.add(o.ready().getConditionFieldList());
        }
        return orConditionFields;
    }

    public List<List<ConditionField>> getAndConditionFields() throws Exception {
        List<List<ConditionField>> andConditionFields = Lists.newLinkedList();
        for (BasePo o : this.getAndConditionList()) {
            andConditionFields.add(o.ready().getConditionFieldList());
        }
        return andConditionFields;
    }

    public <T extends BasePo> T or(T other) throws Exception {
        if (!other.getClass().getName().equals(this.getClass().getName())) {
            throw new Exception("Are you crazy?Please put same class in one SQL.");
        }
        this.orConditionList.add(other.ready());
        return (T) this;
    }

    public <T extends BasePo> T and(T other) throws Exception {
        if (!other.getClass().getName().equals(this.getClass().getName())) {
            throw new Exception("Are you crazy?Please put same class in one SQL.");
        }
        this.andConditionList.add(other.ready());
        return (T) this;
    }

    //    public <T extends BasePo> T groupBy(String... fields) {
    //        getGroupByField().addAll(Arrays.asList(fields));
    //        return (T) this;
    //    }

    public <T extends BasePo> T orderBy(String field, boolean asc) {
        getOrderByField().add(new OrderField(field, asc));
        return (T) this;
    }

    public <T extends BasePo> T orderBy(String... fields) {
        Set<OrderField> orderFieldList =
                Arrays.asList(fields).stream().map(f -> new OrderField(f, this.asc))
                        .collect(Collectors.toSet());
        getOrderByField().addAll(orderFieldList);
        return (T) this;
    }

    public <T extends BasePo> T like(String field, String o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.LIKE),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.LIKE)
                                .setValue(o));
        return (T) this;
    }

    public <T extends BasePo> T gt(String field, Object o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.GT),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.GT)
                                .setValue(o));
        return (T) this;
    }

    public <T extends BasePo> T gt(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.GT),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.GT)
                                .setValue(f.get(this)));
        return (T) this;
    }

    public <T extends BasePo> T gte(String field, Object o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.GTE),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.GTE)
                                .setValue(o));
        return (T) this;
    }

    public <T extends BasePo> T gte(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.GTE),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.GTE)
                                .setValue(f.get(this)));
        return (T) this;
    }

    public <T extends BasePo> T lt(String field, Object o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.LT),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.LT)
                                .setValue(o));
        return (T) this;
    }

    public <T extends BasePo> T lt(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.LT),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.LT)
                                .setValue(f.get(this)));
        return (T) this;
    }

    public <T extends BasePo> T lte(String field, Object o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.LTE),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.LTE)
                                .setValue(o));
        return (T) this;
    }

    public <T extends BasePo> T lte(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.LTE),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.LTE)
                                .setValue(f.get(this)));
        return (T) this;
    }

    public <T extends BasePo> T eq(String field, Object o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.EQ),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.EQ)
                                .setValue(o));
        return (T) this;
    }

    public <T extends BasePo> T eq(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.EQ),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.EQ)
                                .setValue(f.get(this)));
        return (T) this;
    }

    public <T extends BasePo> T ne(String field, Object o) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.NE),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.NE)
                                .setValue(o));
        return (T) this;
    }

    public <T extends BasePo> T ne(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.NE),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.NE)
                                .setValue(f.get(this)));
        return (T) this;
    }

    public <T extends BasePo> T isNull(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.IS_NULL),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.IS_NULL)
                                .setValue(null));
        return (T) this;
    }

    public <T extends BasePo> T isNotNull(String field) throws Exception {
        Field f = this.getClass().getDeclaredField(field);
        f.setAccessible(true);
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.IS_NOT_NULL),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.IS_NOT_NULL)
                                .setValue(null));
        return (T) this;
    }

    public <T extends BasePo> T in(String field, Object[] o) throws Exception {
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.IN),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.IN)
                                .setValues(o));
        return (T) this;
    }

    public <T extends BasePo> T notIn(String field, Object[] o) throws Exception {
        getConditionFieldMap()
                .put(this.getConditionFieldKey(field, CONDITION.NOT_IN),
                        new ConditionField().setFieldName(field).setCondition(CONDITION.NOT_IN)
                                .setValues(o));
        return (T) this;
    }

    public int getLimit() {
        return limit;
    }

    public <T extends BasePo> T setLimit(int limit) {
        this.limit = limit;
        return (T) this;
    }

    public int getIndex() {
        return index;
    }

    public <T extends BasePo> T setIndex(int index) {
        this.index = index;
        return (T) this;
    }

    public boolean isAsc() {
        return asc;
    }

    public <T extends BasePo> T setDesc() {
        this.asc = false;
        return (T) this;
    }

    public <T extends BasePo> T setAsc() {
        this.asc = true;
        return (T) this;
    }

    public <T extends BasePo> T setAsc(boolean asc) {
        this.asc = asc;
        return (T) this;
    }

    public boolean isForUpdate() {
        return forUpdate;
    }

    public <T extends BasePo> T setForUpdate(boolean forUpdate) {
        this.forUpdate = forUpdate;
        return (T) this;
    }

    public Map<String, ConditionField> getConditionFieldMap() {
        return conditionFieldMap;
    }

    public <T extends BasePo> T setConditionFieldMap(
            Map<String, ConditionField> conditionFieldMap) {
        this.conditionFieldMap = conditionFieldMap;
        return (T) this;
    }

    //    public Set<String> getGroupByField() {
    //        return groupByField;
    //    }
    //
    //    public <T extends BasePo> T setGroupByField(Set<String> groupByField) {
    //        this.groupByField = groupByField;
    //        return (T) this;
    //    }

    public Set<OrderField> getOrderByField() {
        return orderByField;
    }

    public <T extends BasePo> T setOrderByField(Set<OrderField> orderByField) {
        this.orderByField = orderByField;
        return (T) this;
    }

    public List<ConditionField> getConditionFieldList() {
        return Lists.newArrayList(this.getConditionFieldMap().values());
    }

    public boolean isUseCache() {
        return useCache;
    }

    public <T extends BasePo> T withCache() {
        return this.setUseCache(true);
    }

    public <T extends BasePo> T withoutCache() {
        return this.setUseCache(false);
    }

    public <T extends BasePo> T setUseCache(boolean useCache) {
        this.useCache = useCache;
        return (T) this;
    }

    public enum CONDITION {
        GT(">"),
        EQ("="),
        LT("<"),
        GTE(">="),
        LTE("<="),
        IN("in"),
        IS_NULL("is null"),
        IS_NOT_NULL(
                "is not null"),
        NE("<>"),
        NOT_IN("not in"),
        LIKE("like");
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

    public <T extends BasePo> T ready() throws Exception {
        Class<? extends BasePo> clazz = this.getClass();
        Field[] fs = clazz.getDeclaredFields();
        for (int i = 0; i < fs.length; i++) {
            fs[i].setAccessible(true);
            Object v = fs[i].get(this);
            if (v != null) {
                this.eq(fs[i].getName());
            }
        }
        return (T) this;
    }

    /**
     * Should be override if sharding table
     *
     * @return
     */
    public String getTableName() {
        return SqlBuilder.getDbName(getClass().getSimpleName());
    }

    private String getConditionFieldKey(String field, CONDITION condition) {
        return field + ":" + condition.getSign();
    }
}
