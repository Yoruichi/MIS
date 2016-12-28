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

    public Foo setName(String name) {
        this.name = name;
        return this;
    }

    public Integer getAge() {
        return age;
    }

    public Foo setAge(Integer age) {
        this.age = age;
        return this;
    }

    public Integer getId() {
        return id;
    }

    public Foo setId(Integer id) {
        this.id = id;
        return this;
    }

    public Boolean getGender() {
        return gender;
    }

    public Foo setGender(Boolean gender) {
        this.gender = gender;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Foo setEmail(String email) {
        this.email = email;
        return this;
    }
}
