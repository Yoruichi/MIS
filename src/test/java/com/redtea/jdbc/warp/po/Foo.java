package com.redtea.jdbc.warp.po;

import com.redtea.jdbc.warp.base.BasePo;

/**
 * Created by yoruichi on 16/10/26.
 */
public class Foo extends BasePo {
    private String name;
    private Integer age;
    private Integer id;
    private Boolean gender;

    @Override
    public String toString() {
        return "Foo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", id=" + id +
                ", gender=" + gender +
                '}';
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getAge() {
        return age;
    }

    public void setAge(Integer age) {
        this.age = age;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Boolean getGender() {
        return gender;
    }

    public void setGender(Boolean gender) {
        this.gender = gender;
    }
}
