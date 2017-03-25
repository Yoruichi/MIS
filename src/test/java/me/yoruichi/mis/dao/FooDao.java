package me.yoruichi.mis.dao;

import me.yoruichi.mis.BaseDao;
import me.yoruichi.mis.po.Foo;
import org.springframework.stereotype.Repository;

/**
 * Created by yoruichi on 16/10/26.
 */
@Repository
public class FooDao extends BaseDao<Foo> {
    //    @Autowired
//    @Qualifier("secondaryJdbcTemplate")
//    private JdbcTemplate jdbcTemplate;

    @Override
    protected Class<Foo> getEntityClass() {
        return Foo.class;
    }

//    @Override
//    public JdbcTemplate getJdbcTemplate() {
//        return jdbcTemplate;
//    }

}
