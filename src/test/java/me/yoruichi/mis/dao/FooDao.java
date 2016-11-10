package me.yoruichi.mis.dao;

import me.yoruichi.mis.BaseDao;
import me.yoruichi.mis.po.Foo;
import org.springframework.stereotype.Repository;

/**
 * Created by yoruichi on 16/10/26.
 */
@Repository
public class FooDao extends BaseDao<Foo> {
    @Override
    protected Class<Foo> getEntityClass() {
        return Foo.class;
    }
}
