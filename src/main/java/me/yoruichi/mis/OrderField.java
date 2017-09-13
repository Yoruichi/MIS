package me.yoruichi.mis;

/**
 * Created by yoruichi on 17/9/13.
 */
public class OrderField {
    private String fieldNname;
    private boolean asc;

    public OrderField(){}

    public OrderField(String fieldNname, boolean asc) {
        this.fieldNname = fieldNname;
        this.asc = asc;
    }

    public String getFieldNname() {
        return fieldNname;
    }

    public OrderField setFieldNname(String fieldNname) {
        this.fieldNname = fieldNname;
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
