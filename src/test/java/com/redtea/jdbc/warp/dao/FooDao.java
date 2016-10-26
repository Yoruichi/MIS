package com.redtea.jdbc.warp.dao;

import com.redtea.jdbc.warp.base.BaseDao;
import com.redtea.jdbc.warp.po.Foo;
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
