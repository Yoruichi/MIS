package me.yoruichi.mis.po;

import me.yoruichi.mis.Alias;
import me.yoruichi.mis.BasePo;

import java.time.LocalDateTime;

/**
 * Created by yoruichi on 16/10/26.
 */
public class Foo extends BasePo {
    private String name;
    private Integer age;
    private Long id;
    @Alias
    private Gender gender;
    private String email;
    private LocalDateTime cTime;

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

    public Long getId() {
        return id;
    }

    public Foo setId(Long id) {
        this.id = id;
        return this;
    }

    public String getEmail() {
        return email;
    }

    public Foo setEmail(String email) {
        this.email = email;
        return this;
    }

    public LocalDateTime getcTime() {
        return cTime;
    }

    public Foo setcTime(LocalDateTime cTime) {
        this.cTime = cTime;
        return this;
    }

    public Foo setGender(Gender gender) {
        this.gender = gender;
        return this;
    }

    public Gender getGender() {
        return this.gender;
    }

}
