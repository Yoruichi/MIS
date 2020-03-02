package me.yoruichi.mis;

import com.google.common.base.Joiner;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.Weigher;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.ResultSetExtractor;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * @author apple
 */
public abstract class BaseDao<T extends BasePo> {

    protected static final Logger log = LoggerFactory.getLogger(BaseDao.class);

    protected Class<T> getEntityClass() {
        return (Class<T>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    protected Cache<String, Optional<T>> selectOneCache;
    protected Cache<String, Optional<Long>> selectCountCache;
    protected Cache<String, Optional<List<T>>> selectManyCache;
    protected Cache<String, Optional<Object>> customCache;

    protected Cache<String, Optional<T>> getSelectOneCache() {
        if (selectOneCache == null) {
            selectOneCache =
                    CacheBuilder.newBuilder().maximumSize(getCacheSize())
                            .expireAfterWrite(getCacheExpire(), getCacheExpireTimeUnit())
                            .build();
        }
        return selectOneCache;
    }

    protected Cache<String, Optional<Long>> getSelectCountCache() {
        if (selectCountCache == null) {
            selectCountCache =
                    CacheBuilder.newBuilder().maximumSize(getCacheSize())
                            .expireAfterWrite(getCacheExpire(), getCacheExpireTimeUnit())
                            .build();
        }
        return selectCountCache;
    }

    protected Cache<String, Optional<List<T>>> getSelectManyCache() {
        if (selectManyCache == null) {
            selectManyCache =
                    CacheBuilder.newBuilder().maximumWeight(getCacheSize()).weigher(
                            (Weigher<String, Optional<List<T>>>) (key, value) -> value.get().size())
                            .expireAfterWrite(getCacheExpire(), getCacheExpireTimeUnit()).build();
        }
        return selectManyCache;
    }

    protected Cache<String, Optional<Object>> getCustomCache() {
        if (customCache == null) {
            customCache =
                    CacheBuilder.newBuilder().maximumSize(getCacheSize())
                            .expireAfterWrite(getCacheExpire(), getCacheExpireTimeUnit())
                            .build();
        }
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

    protected Map<Class, Function> fieldProcessingMap = Maps.newHashMap();

    protected void initFieldProcessingMap() {
        //customFieldProcessingMap.put(fieldClazz, function);
    }

    protected final ResultSetExtractor<List<T>> rseList = rs -> {
        initFieldProcessingMap();
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
                        if (fieldProcessingMap.containsKey(fields[j].getType())) {
                            fields[j].set(i, fieldProcessingMap.get(fields[j].getType()).apply(rs.getObject(columnIndex)));
                        } else if (fields[j].getType().equals(LocalDateTime.class)) {
                            Timestamp rowData = rs.getTimestamp(columnIndex);
                            if (Objects.nonNull(rowData)) {
                                fields[j].set(i, rowData.toLocalDateTime());
                            }
                        } else if (fields[j].getType().equals(LocalDate.class)) {
                            Date rowData = rs.getDate(columnIndex);
                            if (Objects.nonNull(rowData)) {
                                fields[j].set(i, rowData.toLocalDate());
                            }
                        } else if (fields[j].getType().equals(LocalTime.class)) {
                            Time rowData = rs.getTime(columnIndex);
                            if (Objects.nonNull(rowData)) {
                                fields[j].set(i, rowData.toLocalTime());
                            }
                        } else {
                            Object value = rs.getObject(columnIndex);
                            if (Objects.nonNull(value)) {
                                fields[j].set(i, CommonUtil.setFieldValue(fields[j], value));
                            }
                        }
                    }
                }
                l.add(i);
            }
        } catch (Exception e) {
            log.error("Error when processing class match {} .Caused by {}", clazz, e);
            e.printStackTrace();
        }
        return l;
    };

    public void flushCache() {
        if (selectOneCache != null) {
            selectOneCache.invalidateAll();
        }
        if (selectManyCache != null) {
            selectManyCache.invalidateAll();
        }
    }

    public int updateOne(T o) throws Exception {
        try {
            UpdateNeed ind = UpdateNeed.getUpdateNeed(o);
            log.debug("running:{} with args{} based on po {}", ind.sql, Arrays.toString(ind.args), o);
            return getTemplate().update(ind.sql, ind.args);
        } catch (Exception e) {
            log.error("Error when insert po class{}.Caused by:{}", o, e);
            throw e;
        }
    }

    public int[] updateMany(List<T> list) throws Exception {
        try {
            UpdateManyNeed ind = UpdateManyNeed.getUpdateManyNeed(list);
            log.debug("running:{} with args{} based on list {}", ind.sql,
                    ind.args.stream().map(Arrays::toString).reduce((a, b) -> Joiner.on(":").join(a, b)).get(), list);
            return getTemplate().batchUpdate(ind.sql, ind.args);
        } catch (Exception e) {
            log.error("Error when update po list{}.Caused by:{}", list, e);
            throw e;
        }
    }

    public int insertOne(T o) throws Exception {
        try {
            InsertOneNeed ind = InsertOneNeed.getInsertOneNeed(o);
            log.debug("running:{} with args{} based on po {}", ind.sql, Arrays.toString(ind.args), o);
            return getTemplate().update(ind.sql, ind.args);
        } catch (Exception e) {
            log.error("Error when insert po class{}.Caused by:{}", o, e);
            throw e;
        }
    }

    public long insertOneGetLongId(T o) throws Exception {
        try {
            InsertOneNeed ind = InsertOneNeed.getInsertOneNeed(o);
            log.debug("running:{} with args{} based on po {}", ind.sql, Arrays.toString(ind.args), o);
            KeyHolder keyHolder = new GeneratedKeyHolder();
            getTemplate().update(con -> {
                PreparedStatement ps =
                        con.prepareStatement(ind.sql, Statement.RETURN_GENERATED_KEYS);
                for (int i = 0; i < ind.args.length; i++) {
                    ps.setObject((i + 1), ind.args[i]);
                }
                return ps;
            }, keyHolder);
            return keyHolder.getKey().longValue();
        } catch (Exception e) {
            log.error("Error when insert po class{}.Caused by:{}", o, e);
            throw e;
        }
    }

    public int insertOneGetId(T o) throws Exception {
        return (int) insertOneGetLongId(o);
    }

    public int[] insertMany(List<T> list) throws Exception {
        try {
            InsertManyNeed ind = InsertManyNeed.getInsertManyNeed(list);
            log.debug("running:{} with args{} based on list {}", ind.sql,
                    ind.args.stream().map(Arrays::toString).reduce((a, b) -> Joiner.on(":").join(a, b)).get(), list);
            return getTemplate().batchUpdate(ind.sql, ind.args);
        } catch (Exception e) {
            log.error("Error when insert list of {}.Caused by:", list, e);
            throw e;
        }
    }

    public int insertOrUpdate(T o) throws Exception {
        try {
            InsertOrUpdateNeed ind = InsertOrUpdateNeed.getInsertOrUpdateNeed(o);
            log.debug("running:{} with args{} based on class{}", ind.sql, Arrays.toString(ind.args), o);
            return getTemplate().update(ind.sql, ind.args);
        } catch (Exception e) {
            log.error("Error when insert or update po class{}.Caused by:{}", o, e);
            throw e;
        }
    }

    public int[] insertOrUpdateMany(List<T> list) throws Exception {
        try {
            InsertOrUpdateNeedMany ind = InsertOrUpdateNeedMany.getInsertOrUpdateNeedMany(list);
            log.debug("running:{} with args{} based on class{}", ind.sql,
                    ind.args.stream().map(Arrays::toString).reduce((a, b) -> Joiner.on(":").join(a, b)).get()
                    , list);
            return getTemplate().batchUpdate(ind.sql, ind.args);
        } catch (Exception e) {
            log.error("Error when insert or update list of {}.Caused by:{}", list, e);
            throw e;
        }
    }

    public List<T> selectMany(T o) throws Exception {
        List<T> list = Lists.newArrayList();
        try {
            SelectNeed sed = SelectNeed.getSelectManyNeed(o);
            if (o.isUseCache()) {
                Optional<List<T>> op = getSelectManyCache().get(sed.toString(), () -> {
                    log.debug("No found in cache and will run sql {} with args {}", sed.sql,
                            Arrays.toString(sed.args));
                    return Optional.ofNullable(getTemplate().query(sed.sql, sed.args, rseList));
                });
                if (op.isPresent()) {
                    list = op.get();
                    log.debug("Get result {} from cache with key {}", list, sed);
                } else {
                    log.warn("Warn! Get NULL result from cache with key {}", sed);
                }
            } else {
                list = getTemplate().query(sed.sql, sed.args, rseList);
                log.debug("running:{} with args{} based on class{} got result{}", sed.sql,
                        Arrays.toString(sed.args), o, list);
            }
        } catch (Exception e) {
            log.error("Error when select many of class {}.Caused by: {}", o, e);
            throw e;
        }
        return list;
    }

    public T selectHeadOfMany(T o) throws Exception {
        List<T> list = this.selectMany(o);
        if (list.size() > 0) {
            return list.get(0);
        }
        return null;
    }

    public T selectOneOfMany(T o, int index) throws Exception {
        List<T> list = this.selectMany(o);
        if (list.size() > index) {
            return list.get(index);
        }
        return null;
    }

    public Long selectCount(T o) throws Exception {
        Long size = 0L;
        try {
            SelectNeed sed = SelectNeed.getSelectCountNeed(o);
            if (o.isUseCache()) {
                try {
                    Optional<Long> op = getSelectCountCache().get(sed.toString(), () -> {
                        log.debug("No found in cache and will run sql {} with args {}", sed.sql,
                                Arrays.toString(sed.args));
                        Long s = getTemplate().queryForObject(sed.sql, sed.args, Long.class);
                        return Optional.ofNullable(s);
                    });
                    if (op.isPresent()) {
                        size = op.get();
                        log.debug("Get result {} from cache with key {}", size, sed);
                    } else {
                        log.warn("Warn! Get NULL result from cache with key {}", sed);
                    }
                } catch (ExecutionException e) {
                    log.warn("Warn! Get result from cache with key {}, Caused by:", sed, e);
                }
            } else {
                size = getTemplate().queryForObject(sed.sql, sed.args, Long.class);
                log.debug("running:{} with args{} based on class{} got result{}", sed.sql,
                        Arrays.toString(sed.args), o, size);
            }
        } catch (Exception e) {
            log.error("Error when select one of class{}.Caused by {}", o, e);
            throw e;
        }
        return size;
    }

    public T select(T o) throws Exception {
        T t = null;
        try {
            SelectNeed sed = SelectNeed.getSelectOneNeed(o);
            if (o.isUseCache()) {
                try {
                    Optional<T> op = getSelectOneCache().get(sed.toString(), () -> {
                        log.debug("No found in cache and will run sql {} with args {}", sed.sql,
                                Arrays.toString(sed.args));
                        List<T> l = getTemplate().query(sed.sql, sed.args, rseList);
                        if (l.size() > 0) {
                            return Optional.ofNullable(l.get(0));
                        } else {
                            return Optional.empty();
                        }
                    });
                    if (op.isPresent()) {
                        t = op.get();
                        log.debug("Get result {} from cache with key {}", t, sed);
                    } else {
                        log.info("Warn! Get NULL result from cache with key {}", sed);
                    }
                } catch (ExecutionException e) {
                    log.warn("Warn! Get result from cache with key {}, Caused by:", sed, e);
                }
            } else {
                List<T> l = getTemplate().query(sed.sql, sed.args, rseList);
                if (l.size() > 0) {
                    t = l.get(0);
                }
                log.debug("running:{} with args{} based on class{} got result{}", sed.sql,
                        Arrays.toString(sed.args), o, t);
            }
        } catch (Exception e) {
            log.error("Error when select one of class{}.Caused by {}", o, e);
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

    public <R extends Object> R doMethod(Function<T, R> function, T t, String key) {
        R r = null;
        if (t.isUseCache()) {
            try {
                Optional<Object> op = getCustomCache().get(key, () -> {
                    log.debug(
                            "No found value with key {} in cache and will run function {} with parameter {}",
                            key, function.getClass().getName(), t);
                    return Optional.ofNullable(function.apply(t));
                });
                if (op.isPresent()) {
                    r = (R) op.get();
                    log.debug("Get result {} from cache with key {}", r, key);
                } else {
                    log.warn("Warn! Get NULL result from cache with key {}", key);
                }
            } catch (ExecutionException e) {
                log.error("Error!When run function {} with parameter {}", function, t);
                e.printStackTrace();
            }
        } else {
            r = function.apply(t);
        }
        return r;
    }
}
