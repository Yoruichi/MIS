package me.yoruichi.mis;

/**
 * Created by yoruichi on 17/9/13.
 */
public class OrderField {
    private String fieldName;
    private boolean asc;

    public OrderField(){}

    public OrderField(String fieldNname, boolean asc) {
        this.fieldName = fieldNname;
        this.asc = asc;
    }

    public String getFieldName() {
        return fieldName;
    }

    public OrderField setFieldName(String fieldName) {
        this.fieldName = fieldName;
        return this;
    }

    public boolean isAsc() {
        return asc;
    }

    public OrderField setAsc(boolean asc) {
        this.asc = asc;
        return this;
    }
}
