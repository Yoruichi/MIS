package me.yoruichi.mis.po;

import me.yoruichi.mis.BasePo;

/**
 * Created by yoruichi on 16/10/26.
 */
public class Foo extends BasePo {
    private String name;
    private Integer age;
    private Integer id;
    private Boolean gender;
    private String email;

    @Override
    public String toString() {
        return "Foo{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", id=" + id +
                ", gender=" + gender +
                ", email=" + email +
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

    public String getEmail() {
        return email;
    }

    public Foo setEmail(String email) {
        this.email = email;
        return this;
    }
}
