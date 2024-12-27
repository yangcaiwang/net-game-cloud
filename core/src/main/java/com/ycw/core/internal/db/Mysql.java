
package com.ycw.core.internal.db;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.core.cluster.template.DbYmlTemplate;
import com.ycw.core.internal.db.annotation.DB;
import com.ycw.core.internal.db.annotation.Persistent;
import com.ycw.core.internal.db.annotation.Table;
import com.ycw.core.internal.db.constant.DataType;
import com.ycw.core.internal.db.constant.RollType;
import com.ycw.core.internal.db.constant.SQLType;
import com.ycw.core.internal.db.dao.DaoImpl;
import com.ycw.core.internal.db.entity.DBEntity;
import com.ycw.core.internal.db.entity.DBEntityUtils;
import com.ycw.core.internal.db.entity.DbField;
import com.ycw.core.internal.db.sql.DROPFIELD;
import com.ycw.core.internal.db.sql.FIXTABLE;
import com.ycw.core.internal.db.sql.MODIFYFIELD;
import com.ycw.core.util.AnnotationUtil;
import com.ycw.core.util.ArraysUtils;
import com.ycw.core.util.CollectionUtils;
import com.ycw.core.util.StringUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbcp2.BasicDataSourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.TimeUnit;

/**
 * <Mysql数据库工具类>
 * <p>
 * ps:
 * mysql连接池,解析dbname等操作</br>
 * 查询mysql最大可接受的连接数 show variables like 'max_connections';</br>
 * 查询mysql 接受的最大数据包大小 show VARIABLES like '%max_allowed_packet%'; </br>
 * 如果数据过多,可以修改my.cnf配置修改max_allowed_packet = 20M,具体多少根据业务调整</br>
 * 查询mysql是否开启慢查询 show variables like '%slow_query_log%'<br>
 * 如果要开启当前数据库慢查询 set global slow_query_log=1,</br>
 * 如果要永久开启所有数据库慢查询需要修改my.cnf配置</br>
 * slow_query_log =1</br>
 * slow_query_log_file=/usr/local/mysql/data/localhost-slow.log</br>
 * 慢查询时间,单位是秒(默认是10s)多长时间被视为慢任务? show variables like 'long_query_time'</br>
 * 可以通过set global long_query_time=1/0.1修改</br>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public final class Mysql {

    private static final Logger log = LoggerFactory.getLogger(Mysql.class);

    /**
     * 连接池{数据库别名:BasicDataSource}
     */
    private final Map<String, BasicDataSource> basicDataSourceMap = Maps.newHashMap();

    /**
     * 缓存的SQL语句
     * {table:{type:sql}},此处之所以设置一天有效期是为了兼顾滚表类型,日期过去后,老日期的表便不会再被访问,所以需要清除,以免占用内存
     */
    private final Cache<String, Cache<SQLType, String>> statementsCache = Caffeine.newBuilder().expireAfterAccess(1, TimeUnit.DAYS).build();

    /**
     * 数据库别名:数据库真实名称
     */
    private final Map<String, String> realNameMap = Maps.newHashMap();

    /**
     * class:数据库别名
     */
    private final Map<Class<? extends DBEntity>, String> aliasNameMap = Maps.newHashMap();

    /**
     * 表名:class
     * </p>
     * 检查数据表名称的相似度,避免出现player,player_role,这样的表名相似度极高的,滚表修复字段时乱串
     */
    private final Map<String, Class<? extends DBEntity>> table_name_Map = Maps.newHashMap();

    /**
     * 获取真实数据库名称
     *
     * @param cls
     * @return
     */
    public String realName(Class<? extends DBEntity> cls) {
        String name = aliasName(cls);
        return realName(name);
    }

    /**
     * 获取真实数据库名称
     *
     * @param aliasName 数据库别名
     * @return
     */
    public String realName(String aliasName) {
        return realNameMap.get(aliasName);
    }

    /**
     * 数据库别名-必须小写
     */
    public String aliasName(Class<? extends DBEntity> entityType) {
        return aliasNameMap.computeIfAbsent(entityType, v -> ((DB) AnnotationUtil.findAnnotation(entityType, DB.class)).aliasName().toLowerCase());
    }

    /**
     * 获取数据库别名
     */
    public String aliasName(String realName) {
        return realNameMap.keySet().stream().filter(s -> realNameMap.get(s).equals(realName)).findAny().get();
    }

    /**
     * 获取基本的sql语句
     */
    public String buildSQL(Class<? extends DBEntity> entityType, SQLType sqlType, String tableName) {
        return statementsCache.get(tableName, v -> Caffeine.newBuilder().expireAfterAccess(1, TimeUnit.DAYS).build()).get(sqlType, v -> sqlType.BUILDSQL(entityType, tableName));
    }

    public boolean containsAliasName(Class<? extends DBEntity> entityType) {
        return containsAliasName(aliasName(entityType));
    }

    public boolean containsAliasName(String aliasName) {
        return basicDataSourceMap.containsKey(aliasName);
    }

    /**
     * 打开一个mysql连接
     */
    public Connection open(String aliasName) {
        try {
            return basicDataSourceMap.get(aliasName).getConnection();
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + aliasName, e);
        }
    }

    /**
     * 打开一个mysql连接
     */
    public Connection open(Class<? extends DBEntity> entityType) {
        return open(aliasName(entityType));
    }

    /**
     * 关闭mysql连接
     */
    public <T extends Statement> boolean close(Connection conn, Collection<T> statements, Collection<ResultSet> rss) {
        try {
            if (!CollectionUtils.isEmpty(rss)) {
                for (ResultSet rs : rss) {
                    if (rs != null)
                        rs.close();
                }
            }
            if (!CollectionUtils.isEmpty(statements)) {
                for (T s : statements) {
                    if (s != null)
                        s.close();
                }
            }
            if (conn != null)
                conn.close();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 关闭mysql连接
     */
    public <T extends Statement> boolean close(Connection conn, T statement, ResultSet rs) {
        try {
            if (rs != null)
                rs.close();
            if (statement != null)
                statement.close();
            if (conn != null)
                conn.close();
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    /**
     * 修复表
     */
    public void fixTable(Class<? extends DBEntity> entityType) {

        // 如果在mysql.xml中没有配置该类数据库信息则不理会
        if (!containsAliasName(entityType)) {
            log.warn("[{}]没有配置数据库信息，不会初始化数据仓库", entityType);
            return;
        }
        String aliasName = aliasName(entityType);
        // TODO 表名重复检查
        String simple_table_name = DBEntityUtils.simpleTableName(entityType);
        Class<? extends DBEntity> old = table_name_Map.put(simple_table_name, entityType);
        if (old != null)
            throw new RuntimeException(String.format("实体[%s]数据表[%s]已存在,请重命名!", entityType, simple_table_name));
        // TODO 表名相似度检查
        if (DBEntityUtils.needRoll(entityType) || DBEntityUtils.tableNum(entityType) > 1) {
            Map<String, Class<?>> like_table_map = Maps.newHashMap();
            table_name_Map.forEach((table, clz) -> {
                if (table.contains(simple_table_name)) {
                    like_table_map.put(table, clz);
                }
            });
            if (like_table_map.size() > 1) {
                throw new RuntimeException(String.format("实体[%s]数据表[%s]与其他表名[%s]太相似,请重命名", entityType, simple_table_name, like_table_map));
            }
        }
        int tabNum = DBEntityUtils.tableNum(entityType);
        LinkedHashSet<Field> keys = DBEntityUtils.keySet(entityType);
        if (tabNum > 1) {
            if (keys.size() > 1)
                throw new RuntimeException(String.format("实体[%s]分表数量[%s]不允许设定联合主键", entityType, tabNum));
            Field pkf = CollectionUtils.peekFirst(keys);
            Persistent persistent = AnnotationUtil.findAnnotation(pkf, Persistent.class);
            DataType dataType = persistent.dataType();
            if (dataType != DataType.LONG)
                throw new RuntimeException(String.format("实体[%s]分表数量[%s]主键必须使用单一long", entityType, tabNum));
        }

        for (int i = 1; i <= tabNum; i++) {
            String tab = DBEntityUtils.simpleTableName(entityType);
            if (tabNum > 1) {
                tab = DBEntityUtils.merged(tab, i);
            } else if (DBEntityUtils.needRoll(entityType)) {
                tab = RollType.fixedTableName(tab, DBEntityUtils.rollType(entityType));
            }
            DaoImpl.getInstance().createTableIfNotExists(entityType, aliasName, tab);
        }
        // 匹配class字段和mysql字段, 删除多余字段并修复字段类型
        adapter(entityType);
        // 增加少了的字段
        List<Field> fieldList = new ArrayList<>(DBEntityUtils.fieldSet(entityType));
        for (int i = 0; i < fieldList.size(); i++) {
            Field field = fieldList.get(i);
            checkOrAddColumn(entityType, field, i == 0 ? null : fieldList.get(i - 1));
        }
    }

    /**
     * 检查并且添加缺少的字段
     */
    private void checkOrAddColumn(Class<? extends DBEntity> entityType, Field field, Field before) {

        String aliasName = Mysql.getInstance().aliasName(entityType);
        String column = DBEntityUtils.columnName(field);
        String simpleTableName = DBEntityUtils.simpleTableName(entityType);
        Set<String> tables = Sets.newHashSet();
        if (DBEntityUtils.needRoll(entityType)) {
            Set<String> tableNames = DaoImpl.getInstance().showTables(aliasName, simpleTableName);
            for (String tableName : tableNames) {
                if (tableName.startsWith(RollType.PREFIX)) {
                    tables.add(tableName);
                }
            }
        } else if (DBEntityUtils.tableNum(entityType) > 1) {
            try {
                Set<String> tabs = DaoImpl.getInstance().showTables(aliasName, simpleTableName);
                for (String tab : tabs) {
                    String[] ss = DBEntityUtils.parsed(tab);
                    if (ss.length > 1) {
                        Integer.parseInt(ss[ss.length - 1]);
                        tables.add(tab);
                    }
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        } else {
            tables.add(simpleTableName);
        }
        for (String table : tables) {
            if (!DaoImpl.getInstance().existsColumn(realName(aliasName), table, column)) {
                String addColumnSQL = FIXTABLE.BUILDADDFIELDSQL(table, new LinkedHashMap<Field, Field>() {

                    {
                        put(field, before);
                    }
                });
                try {
                    DaoImpl.getInstance().executeUpdate(aliasName, addColumnSQL);
                } catch (Exception e) {
                    throw new RuntimeException(String.format("数据表[%s]添加字段[%s]失败,sql=[%s]", table, column, addColumnSQL));
                }
                String addFieldAttrSQL = FIXTABLE.BUILDADDFIELDATTRSQL(table, field);
                if (addFieldAttrSQL != null) {
                    try {
                        DaoImpl.getInstance().executeUpdate(aliasName, addFieldAttrSQL);
                    } catch (Exception e) {
                        throw new RuntimeException(String.format("数据表[%s]添加字段属性[%s]失败sql=[%s]", table, column, addFieldAttrSQL));
                    }
                }
            }
        }
    }

    /**
     * 匹配class字段和mysql字段, 删除多余字段并修复字段类型</br>
     */
    private boolean adapter(Class<? extends DBEntity> entityType) {

        String aliasName = Mysql.getInstance().aliasName(entityType);
        Map<String, LinkedHashSet<DbField>> table_db_field_map = DaoImpl.getInstance().showTablesDbFiledList(entityType);
        if (CollectionUtils.isEmpty(table_db_field_map))
            throw new RuntimeException(String.format("检测表[%s]失败", entityType));
        LinkedHashSet<Field> fieldSet = DBEntityUtils.fieldSet(entityType);
        if (CollectionUtils.isEmpty(fieldSet))
            throw new RuntimeException(String.format("检测表[%s]失败", entityType));
        Map<String, Field> javaFields = Maps.newHashMap();
        for (Field field : fieldSet) {
            javaFields.putIfAbsent(DBEntityUtils.columnName(field), field);
        }
        Map<String, List<String>> dropMap = Maps.newHashMap();
        Map<String, List<Field>> modifyMap = Maps.newHashMap();
        for (Entry<String, LinkedHashSet<DbField>> entry : table_db_field_map.entrySet()) {
            String table = entry.getKey();
            LinkedHashSet<DbField> templetes = entry.getValue();
            for (DbField fieldTemp : templetes) {
                String column_name = fieldTemp.field;
                if (!javaFields.containsKey(column_name)) {
                    if (!StringUtils.isEmpty(fieldTemp.key)) {
                        if (!PropertyConfig.isDebug() && fieldTemp.key.equals(DbField.PRI)) {
                            throw new RuntimeException(String.format("数据表[%s]字段[%s]是主键,不可以删除", table, column_name));
                        } else if (!PropertyConfig.isDebug() && fieldTemp.key.equals(DbField.UNI)) {
                            throw new RuntimeException(String.format("数据表[%s]字段[%s]是唯一索引,不可以删除", table, column_name));
                        } else {
                            List<Map<String, Object>> list = DaoImpl.getInstance().getIndexesMetaData(open(entityType), null, null, table.toUpperCase(), false, false);
                            Map<String, Set<String>> indexColumns = Maps.newHashMap();
                            list.forEach(map -> {
                                String idx = (String) map.get("INDEX_NAME");
                                String column = (String) map.get("COLUMN_NAME");
                                indexColumns.computeIfAbsent(idx, v -> Sets.newHashSet()).add(column);
                            });
                            for (String idx : indexColumns.keySet()) {
                                Set<String> columns = indexColumns.get(idx);
                                if (!PropertyConfig.isDebug() && columns.size() > 1 && columns.contains(column_name)) {
                                    throw new RuntimeException(String.format("数据表[%s]字段[%s]是联合索引,不可以删除", table, column_name));
                                }
                            }
                        }
                    }
                    dropMap.computeIfAbsent(table, v -> Lists.newArrayList()).add(column_name);
                } else {
                    Field column_field = javaFields.get(column_name);
                    if (!fieldTemp.isSameTo(column_field, entityType)) {
                        if (DBEntityUtils.isPk(column_field))
                            throw new RuntimeException(String.format("实体[%s]字段[%s]是主键,不可以修改", entityType, column_field));
                        if (DBEntityUtils.isUniqueIndex(column_field))
                            throw new RuntimeException(String.format("实体[%s]字段[%s]是唯一索引,不可以修改", entityType, column_field));
                        if (DBEntityUtils.indexNum(entityType, column_field) > 1)
                            throw new RuntimeException(String.format("实体[%s]字段[%s]是联合索引,不可以修改", entityType, column_field));
                        modifyMap.computeIfAbsent(table, v -> Lists.newArrayList()).add(column_field);
                    }
                }
            }
        }
        if (!CollectionUtils.isEmpty(dropMap)) {
            boolean delField = false;
            Table tableAnno = AnnotationUtil.findAnnotation(entityType, Table.class);
            if (tableAnno != null) {
                delField = tableAnno.delField();
            }
            for (Entry<String, List<String>> entry : dropMap.entrySet()) {
                if (CollectionUtils.isEmpty(entry.getValue()))
                    continue;
                if (!PropertyConfig.isDebug()) {
                    if (!delField) {
                        log.error("{}:不允许删除字段:{}", entityType, StringUtils.toString(entry.getValue()));
                        continue;
                    }
                }
                String table = entry.getKey();
                String[] columns = ArraysUtils.toArray(entry.getValue(), String.class);
                try {
                    DaoImpl.getInstance().execute(aliasName, DROPFIELD.BUILDDROPFIELDSQL(table, columns));
                    log.error("{}:删除字段:{}", entityType, StringUtils.toString(entry.getValue()));
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        if (!CollectionUtils.isEmpty(modifyMap)) {
            boolean modField = false;
            Table tableAnno = AnnotationUtil.findAnnotation(entityType, Table.class);
            if (tableAnno != null) {
                modField = tableAnno.modField();
            }
            for (Entry<String, List<Field>> entry : modifyMap.entrySet()) {
                if (CollectionUtils.isEmpty(entry.getValue()))
                    continue;
                if (!PropertyConfig.isDebug()) {
                    if (!modField) {
                        log.error(entityType + ":不允许修改字段:" + StringUtils.toString(entry.getValue()));
                        continue;
                    }
                }
                String table = entry.getKey();
                Field[] columns = ArraysUtils.toArray(entry.getValue(), Field.class);
                try {
                    DaoImpl.getInstance().execute(aliasName, MODIFYFIELD.BUILDSQL(table, columns));
                    log.error(entityType + ":修改字段:" + StringUtils.toString(entry.getValue()));
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage(), e);
                }
            }
        }
        return true;
    }

    /**
     * 创建连接池
     *
     * @param properties 连接池配置文件,可以是多个,每个文件指向一个数据库的连接池配置
     */
    public void createPools(DbYmlTemplate... dbYmlTemplates) {
        for (DbYmlTemplate dbYmlTemplate : dbYmlTemplates) {
            try {
                createPool(dbYmlTemplate);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage(), e);
            }
        }
    }

    private void createPool(DbYmlTemplate dbYmlTemplate) throws Exception {
        String aliasName = dbYmlTemplate.getName();
        String[] ss = dbYmlTemplate.getUrl().split("\\?")[0].split("\\/");
        String realName = ss[ss.length - 1];
        if (StringUtils.stringContainIt(aliasName, StringUtils.Type.TYPE_CHINESE)) {
            throw new RuntimeException(String.format("数据库别名[%s]不允许出现中文", aliasName));
        }
        if (aliasName.length() > DB.MySQL_NAME_MAX_LEN) {
            throw new RuntimeException(String.format("数据库别名[%s]过长", aliasName));
        }
        if (StringUtils.stringContainIt(realName, StringUtils.Type.TYPE_CHINESE)) {
            throw new RuntimeException(String.format("数据库真实名[%s]不允许出现中文", realName));
        }
        if (realName.length() > DB.MySQL_NAME_MAX_LEN) {
            throw new RuntimeException(String.format("数据库真实名[%s]过长", realName));
        }

        if (dbYmlTemplate.isAuto()) {// 自动创库
            if (!basicDataSourceMap.containsKey(DB.HELP_DB)) {

                InputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(DbYmlTemplate.class.getPackage().getName())));
                Properties p = new Properties();
                p.load(in);

                Properties helpProperties = new Properties();
                p.forEach((k, v) -> helpProperties.put(k, v));


                helpProperties.put("url", dbYmlTemplate.getUrl().replaceAll(realName, DB.HELP_DB));
                basicDataSourceMap.putIfAbsent(DB.HELP_DB, BasicDataSourceFactory.createDataSource(helpProperties));
            }
            Connection conn = basicDataSourceMap.get(DB.HELP_DB).getConnection();
            DaoImpl.getInstance().createDatabase(conn, realName, DB.CHARSET_UTF8MB4, DB.COLLATE_UTF8MB4);
        }

//        realNameMap.putIfAbsent(aliasName, realName);
//        basicDataSourceMap.putIfAbsent(aliasName, BasicDataSourceFactory.createDataSource(p));
        log.info("建立数据库【{}】连接池成功", aliasName);
    }

    public void putDataSourceToMap(String aliasName, BasicDataSource source) {
        String url = source.getUrl();
        String[] ss = url.split("\\?")[0].split("\\/");
        String realName = ss[ss.length - 1];
        realNameMap.putIfAbsent(aliasName, realName);
        basicDataSourceMap.putIfAbsent(aliasName, source);
    }

    public void removeDataSource(String aliasName) {
        realNameMap.remove(aliasName);
        BasicDataSource remove = basicDataSourceMap.remove(aliasName);
        if (remove != null) {
            try {
                remove.close();
            } catch (SQLException e) {
                log.error(e.getMessage(), e);
            }
        }
    }

    private static Mysql instance;

    public static Mysql getInstance() {
        if (instance == null) {
            synchronized (Mysql.class) {
                if (instance == null) {
                    instance = new Mysql();
                }
            }
        }
        return instance;
    }
}
