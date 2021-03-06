package me.yoruichi.mis.dao;

import me.yoruichi.mis.BaseDao;
import me.yoruichi.mis.po.Foo;
import me.yoruichi.mis.po.Gender;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Created by yoruichi on 16/10/26.
 */
@Repository
public class FooDao extends BaseDao<Foo> {
    @Autowired
//    @Qualifier("secondaryJdbcTemplate")
    private JdbcTemplate jdbcTemplate;

    //    @Override
    //    protected Class<Foo> getEntityClass() {
    //        return Foo.class;
    //    }

    @Override
    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

//    @Override
//    protected void initFieldProcessingMap() {
//        fieldProcessingMap.put(Gender.class, (code) -> Gender.codeOf((int) code));
//    }

    @Override
    public int getCacheSize() {
        return 3;
    }

    public Integer selectFooCustom(Foo foo) {
        return this.getTemplate().query("select count(1) from foo", new Object[] {},
                resultSet -> {
                    resultSet.next();
                    return resultSet.getInt(1);
                });
    }

}
