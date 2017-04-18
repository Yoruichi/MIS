package me.yoruichi.mis;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;
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
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

public abstract class BaseDao<T extends BasePo> {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());

    protected abstract Class<T> getEntityClass();

    protected Cache<String, T> selectOneCache;
    protected Cache<String, List<T>> selectManyCache;
    protected Cache<String, Object> customCache;

    protected Cache<String, T> getSelectOneCache() {
        if (selectOneCache == null)
            selectOneCache =
                    CacheBuilder.newBuilder().maximumSize(getCacheSize())
                            .expireAfterWrite(getCacheExpire(), getCacheExpireTimeUnit())
                            .build();
        return selectOneCache;
    }

    protected Cache<String, List<T>> getSelectManyCache() {
        if (selectManyCache == null)
            selectManyCache =
                    CacheBuilder.newBuilder().maximumWeight(getCacheSize()).weigher(
                            (Weigher<String, List<T>>) (key, value) -> value.size())
                            .expireAfterWrite(getCacheExpire(), getCacheExpireTimeUnit()).build();
        return selectManyCache;
    }

    protected Cache<String, Object> getCustomCache() {
        if (customCache == null)
            customCache =
                    CacheBuilder.newBuilder().maximumSize(getCacheSize())
                            .expireAfterWrite(getCacheExpire(), getCacheExpireTimeUnit())
                            .build();
        return customCache;
    }

    private int cacheSize = 2048;
    private int cacheExpire = 15;
    private TimeUnit cacheExpireTimeUnit = TimeUnit.MILLISECONDS;

    public JdbcTemplate getJdbcTemplate() {
        return jdbcTemplate;
    }

    public void setJdbcTemplate(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Autowired
    private JdbcTemplate jdbcTemplate;

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

    public void flushCache() {
        if (selectOneCache != null)
            selectOneCache.invalidateAll();
        if (selectManyCache != null)
            selectManyCache.invalidateAll();
    }

    public int updateOne(T o) throws Exception {
        try {
            UpdateNeed ind = UpdateNeed.getUpdateNeed(o);
            logger.info("running:{} with args{} based on po {}", ind.sql, ind.args, o);
            return getTemplate().update(ind.sql, ind.args);
        } catch (Exception e) {
            logger.error("Error when insert po class{}.Caused by:{}", o, e);
            throw e;
        }
    }

    public int[] updateMany(List<T> list) throws Exception {
        try {
            UpdateManyNeed ind = UpdateManyNeed.getUpdateManyNeed(list);
            logger.info("running:{} with args{} based on list {}", ind.sql, ind.args, list);
            return getTemplate().batchUpdate(ind.sql, ind.args);
        } catch (Exception e) {
            logger.error("Error when insert po list{}.Caused by:{}", list, e);
            throw e;
        }
    }

    public int insertOne(T o) throws Exception {
        try {
            InsertOneNeed ind = InsertOneNeed.getInsertOneNeed(o);
            logger.info("running:{} with args{} based on po {}", ind.sql, ind.args, o);
            return getTemplate().update(ind.sql, ind.args);
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
                PreparedStatement ps =
                        con.prepareStatement(ind.sql, Statement.RETURN_GENERATED_KEYS);
                for (int i = 0; i < ind.args.length; i++) {
                    ps.setObject((i + 1), ind.args[i]);
                }
                return ps;
            }, keyHolder);
            return keyHolder.getKey().intValue();
        } catch (Exception e) {
            logger.error("Error when insert po class{}.Caused by:{}", o, e);
            throw e;
        }
    }

    public int[] insertMany(List<T> list) throws Exception {
        try {
            InsertManyNeed ind = InsertManyNeed.getInsertManyNeed(list);
            logger.info("running:{} with args{} based on list {}", ind.sql, ind.args, list);
            return getTemplate().batchUpdate(ind.sql, ind.args);
        } catch (Exception e) {
            logger.error("Error when insert list of {}.Caused by:", list, e);
            throw e;
        }
    }

    public int insertOrUpdate(T o) throws Exception {
        try {
            InsertOrUpdateNeed ind = InsertOrUpdateNeed.getInsertOrUpdateNeed(o);
            logger.info("running:{} with args{} based on class{}", ind.sql, ind.args, o);
            return getTemplate().update(ind.sql, ind.args);
        } catch (Exception e) {
            logger.error("Error when insert or update po class{}.Caused by:{}", o, e);
            throw e;
        }
    }

    public int[] insertOrUpdateMany(List<T> list) throws Exception {
        try {
            InsertOrUpdateNeedMany ind = InsertOrUpdateNeedMany.getInsertOrUpdateNeedMany(list);
            logger.info("running:{} with args{} based on class{}", ind.sql, ind.args, list);
            return getTemplate().batchUpdate(ind.sql, ind.args);
        } catch (Exception e) {
            logger.error("Error when insert or update list of {}.Caused by:{}", list, e);
            throw e;
        }
    }

    public List<T> selectMany(T o) throws Exception {
        List<T> list = Lists.newArrayList();
        try {
            SelectNeed sed = SelectNeed.getSelectManyNeed(o);
            if (o.isUseCache()) {
                list = getSelectManyCache()
                        .get(sed.toString(), () -> {
                            logger.info("No found in cache and will run sql {} with args {}",
                                    sed.sql, sed.args);
                            return getTemplate().query(sed.sql, sed.args, rseList);
                        });
                logger.info("get result {} from cache with key {}", list, sed);
            } else {
                list = getTemplate().query(sed.sql, sed.args, rseList);
                logger.info("running:{} with args{} based on class{} got result{}", sed.sql,
                        sed.args, o, list);
            }
        } catch (Exception e) {
            logger.error("Error when select many of class{}.Caused by {}", o, e);
            throw e;
        }
        return list;
    }

    public T selectHeadOfMany(T o) throws Exception {
        List<T> list = this.selectMany(o);
        if (list.size() > 0) return list.get(0);
        return null;
    }

    public T selectOneOfMany(T o, int index) throws Exception {
        List<T> list = this.selectMany(o);
        if (list.size() > index) return list.get(index);
        return null;
    }

    public T select(T o) throws Exception {
        T t = null;
        try {
            SelectNeed sed = SelectNeed.getSelectOneNeed(o);
            if (o.isUseCache()) {
                t = getSelectOneCache().get(sed.toString(), () -> {
                    logger.info("No found in cache and will run sql {} with args {}", sed.sql,
                            sed.args);
                    List<T> l = getTemplate().query(sed.sql, sed.args, rseList);
                    if (l.size() > 0)
                        return l.get(0);
                    else
                        return null;
                });
                logger.info("get resule {} from cache with key {}", t, sed);
            } else {
                List<T> l = getTemplate().query(sed.sql, sed.args, rseList);
                if (l.size() > 0)
                    t = l.get(0);
                logger.info("running:{} with args{} based on class{} got result{}", sed.sql,
                        sed.args, o, t);
            }
        } catch (Exception e) {
            logger.error("Error when select one of class{}.Caused by {}", o, e);
            throw e;
        }
        return t;
    }

    public JdbcTemplate getTemplate() {
        return getJdbcTemplate();
    }

    public int getCacheSize() {
        return cacheSize;
    }

    public int getCacheExpire() {
        return cacheExpire;
    }

    public TimeUnit getCacheExpireTimeUnit() {
        return cacheExpireTimeUnit;
    }

    public <R extends Object> R doMethodWithCache(Function<T, R> function, T t, String key) {
        R r = null;
        if (t.isUseCache()) try {
            r = (R) getCustomCache().get(key, () -> {
                logger.info(
                        "No found value with key {} in cache and will run function {} with parameter {}",
                        key, function, t);
                return function.apply(t);
            });
            logger.info("get result {} from cache with key {}", r, key);
        } catch (ExecutionException e) {
            logger.error("Error!When run function {} with parameter {}", function, t);
            e.printStackTrace();
        }
        else {
            r = function.apply(t);
        }
        return r;
    }
}
