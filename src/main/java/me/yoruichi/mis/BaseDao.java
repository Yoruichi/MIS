package me.yoruichi.mis;

import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.Statement;
import java.util.List;

public abstract class BaseDao<T extends BasePo> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected abstract Class<T> getEntityClass();

    @Autowired
    private JdbcTemplate template;

    protected final ResultSetExtractor<List<T>> rseList = rs -> {
        List<T> l = Lists.newLinkedList();
        Class<T> clazz = getEntityClass();
        try {
            while (rs.next()) {
                T i = clazz.newInstance();
                Field[] fields = clazz.getDeclaredFields();
                for (int j = 0; j < fields.length; j++) {
                    int columnIndex =
                            rs.findColumn(SqlBuilder.getDbName(fields[j].getName()));
                    if (columnIndex > -1) {
                        fields[j].setAccessible(true);
                        fields[j].set(i, rs.getObject(columnIndex));
                    }
                }
                l.add(i);
            }
        } catch (Exception e) {
            logger.error("Error when processing class match {} .Caused by {}", clazz, e);
            e.printStackTrace();
        }
        return l;
    };


    public void insertOne(T o) throws Exception {
        try {
            InsertOneNeed ind = InsertOneNeed.getInsertOneNeed(o);
            logger.info("running:{} with args{} based on po {}", ind.sql, ind.args, o);
            getTemplate().update(ind.sql, ind.args);
        } catch (Exception e) {
            logger.error("Error when insert po class{}.Caused by:{}", o, e);
            throw e;
        }
    }

    public int insertOneGetId(T o) throws Exception {
        try {
            InsertOneNeed ind = InsertOneNeed.getInsertOneNeed(o);
            logger.info("running:{} with args{} based on po {}", ind.sql, ind.args, o);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getTemplate().update(con -> {
                PreparedStatement ps = con.prepareStatement(ind.sql, Statement.RETURN_GENERATED_KEYS);
                for (int i = 0; i < ind.args.length ; i++) {
                    ps.setObject((i+1), ind.args[i]);
                }
                return ps;
            }, keyHolder);
            return keyHolder.getKey().intValue();
        } catch (Exception e) {
            logger.error("Error when insert po class{}.Caused by:{}", o, e);
            throw e;
        }
    }

    public void insertMany(List<T> list) throws Exception {
        try {
            InsertManyNeed ind = InsertManyNeed.getInsertManyNeed(list);
            logger.info("running:{} with args{} based on list {}", ind.sql, ind.args, list);
            getTemplate().batchUpdate(ind.sql, ind.args);
        } catch (Exception e) {
            logger.error("Error when insert list of {}.Caused by:", list, e);
            throw e;
        }
    }

    public void insertOrUpdate(T o) throws Exception {
        try {
            InsertOrUpdateNeed ind = InsertOrUpdateNeed.getInsertOrUpdateNeed(o);
            logger.info("running:{} with args{} based on class{}", ind.sql, ind.args, o);
            getTemplate().update(ind.sql, ind.args);
        } catch (Exception e) {
            logger.error("Error when insert or update po class{}.Caused by:{}", o, e);
            throw e;
        }
    }

    public void insertOrUpdateMany(List<T> list) throws Exception {
        try {
            InsertOrUpdateNeedMany ind = InsertOrUpdateNeedMany.getInsertOrUpdateNeedMany(list);
            logger.info("running:{} with args{} based on class{}", ind.sql, ind.args, list);
            getTemplate().batchUpdate(ind.sql, ind.args);
        } catch (Exception e) {
            logger.error("Error when insert or update list of {}.Caused by:{}", list, e);
            throw e;
        }
    }

    public List<T> selectMany(T o) throws Exception {
        List<T> list = Lists.newArrayList();
        try {
            SelectNeed sed = SelectNeed.getSelectManyNeed(o);
            list = getTemplate().query(sed.sql, sed.args, rseList);
            logger.info("running:{} with args{} based on class{} got result{}", sed.sql, sed.args,
                    o, list);
        } catch (Exception e) {
            logger.error("Error when select many of class{}.Caused by {}", o, e);
            throw e;
        }
        return list;
    }

    public T selectHeadOfMany(T o) throws Exception {
        List<T> list = Lists.newArrayList();
        try {
            SelectNeed sed = SelectNeed.getSelectManyNeed(o);
            list = getTemplate().query(sed.sql, sed.args, rseList);
            logger.info("running:{} with args{} based on class{} got result{}", sed.sql, sed.args,
                    o, list);
            if (list.size() > 0) return list.get(0);
        } catch (Exception e) {
            logger.error("Error when select many of class{}.Caused by {}", o, e);
            throw e;
        }
        return null;
    }

    public T selectOneOfMany(T o, int index) throws Exception {
        List<T> list = Lists.newArrayList();
        try {
            SelectNeed sed = SelectNeed.getSelectManyNeed(o);
            list = getTemplate().query(sed.sql, sed.args, rseList);
            logger.info("running:{} with args{} based on class{} got result{}", sed.sql, sed.args,
                    o, list);
            if (list.size() > index) return list.get(index);
        } catch (Exception e) {
            logger.error("Error when select many of class{}.Caused by {}", o, e);
            throw e;
        }
        return null;
    }

    public T select(T o) throws Exception {
        T t = null;
        try {
            SelectNeed sed = SelectNeed.getSelectOneNeed(o);
            List<T> l = getTemplate().query(sed.sql, sed.args, rseList);
            if (l.size() > 0)
                t = l.get(0);
            logger.info("running:{} with args{} based on class{} got result{}", sed.sql, sed.args,
                    o, t);
        } catch (Exception e) {
            logger.error("Error when select one of class{}.Caused by {}", o, e);
            throw e;
        }
        return t;
    }

    public JdbcTemplate getTemplate() {
        return template;
    }

}
