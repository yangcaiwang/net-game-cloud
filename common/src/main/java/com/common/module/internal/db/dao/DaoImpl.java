
package com.common.module.internal.db.dao;

import com.common.module.internal.db.Mysql;
import com.common.module.internal.db.annotation.DB;
import com.common.module.internal.db.constant.RollType;
import com.common.module.internal.db.constant.SQLType;
import com.common.module.internal.db.entity.DBEntity;
import com.common.module.internal.db.entity.DBEntityUtils;
import com.common.module.internal.db.entity.DbField;
import com.common.module.internal.db.sql.CREATEDATABASE;
import com.common.module.internal.db.sql.DELETEBYKEY;
import com.common.module.internal.db.sql.SELECT;
import com.common.module.util.ArraysUtils;
import com.common.module.util.StringUtils;
import com.common.module.cluster.property.PropertyConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.mysql.cj.jdbc.ServerPreparedStatement;
import org.apache.commons.dbcp2.DelegatingPreparedStatement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.Date;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DAO操作
 */
final public class DaoImpl implements IDao {

    private final Logger sqlLog = LoggerFactory.getLogger("db");

    private static final IDao INSTANCE = new DaoImpl();

    public static IDao getInstance() {
        return INSTANCE;
    }

    private DaoImpl() {
    }

    @Override
    public Boolean createDatabase(String realName, String charset, String collate) throws SQLException {
        return createDatabase(open(DB.HELP_DB), realName, charset, collate);
    }

    @Override
    public Boolean createDatabase(String realName) throws SQLException {
        return createDatabase(realName, DB.CHARSET_UTF8MB4, DB.COLLATE_UTF8MB4);
    }

    @Override
    public Boolean createDatabase(Connection conn, String realName, String charset, String collate) throws SQLException {
        Statement st = null;
        String sql = null;
        try {
            sql = CREATEDATABASE.BUILDSQL(realName, charset, collate);
            st = conn.createStatement();
            int result = st.executeUpdate(sql);
            return result >= 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + st, e);
        } finally {
            close(conn, st, null);
        }
    }

    @Override
    public Set<String> showDatabases() {
        Connection conn = null;
        Statement st = null;
        String sql = null;
        ResultSet rs = null;
        Set<String> set = Sets.newHashSet();
        try {
            conn = open(DB.HELP_DB);
            sql = "SHOW DATABASES";
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                set.add(rs.getString(1));
            }
            return set;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + st, e);
        } finally {
            close(conn, st, rs);
        }
    }

    @Override
    public Set<String> showDatabases(String like) {
        Connection conn = null;
        Statement st = null;
        String sql = null;
        ResultSet rs = null;
        Set<String> set = Sets.newHashSet();
        try {
            conn = open(DB.HELP_DB);
            sql = "SHOW DATABASES LIKE '%" + like + "%'";
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                set.add(rs.getString(1));
            }
            return set;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + st, e);
        } finally {
            close(conn, st, rs);
        }
    }

    @Override
    public Set<String> showDatabases(Connection conn) {
        Statement st = null;
        String sql = null;
        ResultSet rs = null;
        Set<String> set = Sets.newHashSet();
        try {
            sql = "SHOW DATABASES";
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                set.add(rs.getString(1));
            }
            return set;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + st, e);
        } finally {
            close(conn, st, rs);
        }
    }

    @Override
    public Set<String> showDatabases(Connection conn, String like) {
        Statement st = null;
        String sql = null;
        ResultSet rs = null;
        Set<String> set = Sets.newHashSet();
        try {
            sql = "SHOW DATABASES LIKE '%" + like + "%'";
            st = conn.createStatement();
            rs = st.executeQuery(sql);
            while (rs.next()) {
                set.add(rs.getString(1));
            }
            return set;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + st, e);
        } finally {
            close(conn, st, rs);
        }
    }

    @Override
    public Boolean existsDatabase(String realName) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = null;
        try {
            sql = String.format("SELECT DISTINCT information_schema.SCHEMATA.SCHEMA_NAME FROM information_schema.SCHEMATA WHERE SCHEMA_NAME='%s' LIMIT 1", realName);
            conn = open(Mysql.getInstance().aliasName(realName));
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs != null && rs.next()) {
                String d = rs.getString(1);
                return d.equals(realName);
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public Boolean existsTable(String realName, String tableName) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = null;
        try {
            sql = String.format("SELECT DISTINCT TABLE_SCHEMA, TABLE_NAME FROM information_schema.TABLES WHERE TABLE_SCHEMA='%s'  AND TABLE_NAME = '%s'  LIMIT 1", realName, tableName);
            conn = open(Mysql.getInstance().aliasName(realName));
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs != null && rs.next()) {
                String d = rs.getString(1);
                String t = rs.getString(2);
                return d.equals(realName) && t.equals(tableName);
            }
            return false;
        } catch (Exception e) {

            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public Boolean existsColumn(String realName, String tableName, String column) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = null;
        try {
            sql = String.format("SELECT DISTINCT TABLE_SCHEMA, TABLE_NAME,COLUMN_NAME FROM information_schema.COLUMNS WHERE TABLE_SCHEMA='%s'  AND TABLE_NAME = '%s' AND COLUMN_NAME = '%s' LIMIT 1", realName, tableName, column);
            conn = open(Mysql.getInstance().aliasName(realName));
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (rs != null && rs.next()) {
                String d = rs.getString(1);
                String t = rs.getString(2);
                String c = rs.getString(3);
                return d.equals(realName) && t.equals(tableName) && c.equals(column);
            }
            return false;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public Set<String> showTables(String aliasName, String like) {
        Set<String> tables = Sets.newHashSet();
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = null;
        try {
            sql = "SHOW TABLES LIKE '%" + like + "%'";
            conn = open(aliasName);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String table = rs.getString(1);
                tables.add(table);
            }
            return tables;
        } catch (Exception e) {

            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public Set<String> showTables(String aliasName) {
        Set<String> tables = Sets.newHashSet();
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = null;
        try {
            sql = "SHOW TABLES";
            conn = open(aliasName);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String table = rs.getString(1);
                tables.add(table);
            }
            return tables;
        } catch (Exception e) {

            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public <T extends DBEntity> Map<String, LinkedHashSet<DbField>> showTablesDbFiledList(Class<T> entityType) {
        Map<String, LinkedHashSet<DbField>> descTemps = Maps.newLinkedHashMap();
        List<String> sqlList = Lists.newArrayList();
        List<PreparedStatement> pstList = Lists.newArrayList();
        List<ResultSet> rsList = Lists.newArrayList();
        String aliasName = aliasName(entityType);
        String simpleTableName = simpleTableName(entityType);
        Connection conn = null;
        try {
            conn = open(aliasName);
            Set<String> tables = Sets.newHashSet();
            if (DBEntityUtils.needRoll(entityType)) {
                Set<String> tableNames = showTables(aliasName, simpleTableName);
                for (String tableName : tableNames) {
                    if (tableName.startsWith(RollType.PREFIX)) {
                        tables.add(tableName);
                    }
                }
            } else if (tableNum(entityType) > 1) {
                try {
                    Set<String> tabs = showTables(aliasName, simpleTableName);
                    for (String tab : tabs) {
                        if (tab.startsWith(simpleTableName)) {
                            String[] ss = DBEntityUtils.parsed(tab);
                            if (ss.length > 1) {
                                Integer.parseInt(ss[ss.length - 1]);
                                tables.add(tab);
                            }
                        }
                    }
                } catch (Exception e) {
                }
            } else {
                tables.add(simpleTableName);
            }
            for (String table : tables) {
                descTemps.putIfAbsent(table, Sets.newLinkedHashSet());
                String sql = "DESCRIBE `" + table + "`";
                sqlList.add(sql);
                PreparedStatement pst = conn.prepareStatement(sql);
                pstList.add(pst);
                ResultSet rs = pst.executeQuery();
                rsList.add(rs);
                while (rs.next()) {
                    String field = rs.getString(1);
                    String type = rs.getString(2);
                    String nil = rs.getString(3);
                    String key = rs.getString(4);
                    String def = rs.getString(5);
                    String extra = rs.getString(6);
                    descTemps.get(table).add(new DbField(field, type, nil, key, def, extra));
                }
            }
            return descTemps;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            close(conn, pstList, rsList);
        }
    }

    @Override
    public DbField getColumn(String aliasName, String tableName, String column) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = null;
        DbField dbField = null;
        try {
            sql = "DESCRIBE `" + tableName + "` `" + column + "`";
            conn = open(aliasName);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs.next()) {
                String field = rs.getString(1);
                String type = rs.getString(2);
                String nil = rs.getString(3);
                String key = rs.getString(4);
                String def = rs.getString(5);
                String extra = rs.getString(6);
                dbField = new DbField(field, type, nil, key, def, extra);
                return dbField;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public Boolean copyTable(String aliasName, String newTableName, String oldTableName) {
        try {
            Integer result = executeUpdate(aliasName, String.format("create table `%s` like `%s`", newTableName, oldTableName));
            return result >= 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public <T extends DBEntity> Boolean createTableIfNotExists(Class<T> entityType, String aliasName, String tableName) {
        try {
            Integer result = executeUpdate(aliasName, buildSQL(entityType, SQLType.CREATETABLE, tableName));
            return result >= 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Boolean cleanTable(String aliasName, String tableName) {
        try {
            Integer result = executeUpdate(aliasName, "DELETE FROM `" + tableName + "`");
            return result >= 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Boolean truncateTable(String aliasName, String tableName) {
        try {
            Integer result = executeUpdate(aliasName, "TRUNCATE TABLE `" + tableName + "`");
            return result >= 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public Boolean dropTable(String aliasName, String tableName) {
        try {
            Integer result = executeUpdate(aliasName, "DROP TABLE `" + tableName + "`");
            return result >= 0;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    @Override
    public <T extends DBEntity> Long returnGeneratedKeys(T obj) {
        Class<T> entityType = (Class<T>) obj.getEntityType();

        if (!DBEntityUtils.autoPk(entityType))
            throw new RuntimeException(entityType.getName());

        String aliasName = aliasName(entityType);
        String tab = simpleTableName(entityType);

        String sql = null;
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            sql = buildSQL(entityType, SQLType.INSERT, tab);
            conn = open(aliasName);
            if (useInnodb())
                conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            obj.setFirstCreateTime(new Date());
            com.common.module.internal.db.dao.Wrapper.getInstance().wrap(pst, obj);
            pst.executeUpdate();
            if (useInnodb())
                conn.commit();
            rs = pst.getGeneratedKeys();
            if (rs != null && rs.next()) {
                Long lastId = rs.getLong(1);
                return lastId;
            }
            return null;
        } catch (Exception e) {
            if (useInnodb()) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    throw new RuntimeException(e1.getMessage(), e1);
                }
            }
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public <T extends DBEntity> Boolean insert(Collection<T> c, Class<T> entityType) {

        String aliasName = aliasName(entityType);

        if (DBEntityUtils.needRoll(entityType)) {
            String tab = RollType.fixedTableName(DBEntityUtils.simpleTableName(entityType), DBEntityUtils.rollType(entityType));
            return insert(c, entityType, aliasName, tab);
        }

        if (DBEntityUtils.tableNum(entityType) == 1) {
            return insert(c, entityType, aliasName, DBEntityUtils.simpleTableName(entityType));
        }

        List<Boolean> l = Lists.newArrayList();
        Map<String, List<T>> tableNumMap = c.stream().collect(Collectors.groupingBy(e -> DBEntityUtils.getTableName(e), Collectors.toList()));
        tableNumMap.keySet().forEach(tableName -> {
            if (insert(tableNumMap.get(tableName), entityType, aliasName, tableName))
                l.add(true);
        });
        return l.size() == tableNumMap.keySet().size();
    }

    @Override
    public <T extends DBEntity> Boolean insert(Collection<T> c, Class<T> entityType, String aliasName, String tableName) {
        createTableIfNotExists(entityType, aliasName, tableName);

        String sql = null;
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            sql = buildSQL(entityType, SQLType.INSERT, tableName);
            conn = open(aliasName);
            if (useInnodb())
                conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
            Date now = new Date();
            boolean isLogDb = dbLog(aliasName);
            for (T obj : c) {
                obj.setFirstCreateTime(now);
                com.common.module.internal.db.dao.Wrapper.getInstance().wrap(pst, obj);
                pst.addBatch();
                if (!isLogDb) {
                    getPreparedStatementSQL(pst);
                }
            }
            int[] r = pst.executeBatch();
            if (useInnodb())
                conn.commit();
            if (r != null && r.length == c.size()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage() + sql, e);
            if (useInnodb()) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    throw new RuntimeException(e1.getMessage(), e1);
                } finally {
                }
            }
            return insertOrUpdate(c, entityType, aliasName, tableName);
        } finally {
            close(conn, pst, null);
        }
    }

    @Override
    public <T extends DBEntity> Boolean insertOrUpdate(Collection<T> c, Class<T> entityType) {

        if (DBEntityUtils.isLogger(entityType))
            throw new RuntimeException(entityType.getName());
        if (DBEntityUtils.needRoll(entityType))
            throw new RuntimeException(entityType.getName());

        String aliasName = aliasName(entityType);

        if (DBEntityUtils.tableNum(entityType) == 1) {
            return insertOrUpdate(c, entityType, aliasName, DBEntityUtils.simpleTableName(entityType));
        }

        List<Boolean> l = Lists.newArrayList();
        Map<String, List<T>> tableNumMap = c.stream().collect(Collectors.groupingBy(e -> DBEntityUtils.getTableName(e), Collectors.toList()));
        tableNumMap.keySet().forEach(tableName -> {
            if (insertOrUpdate(tableNumMap.get(tableName), entityType, aliasName, tableName))
                l.add(true);
        });
        return l.size() == tableNumMap.keySet().size();
    }

    @Override
    public <T extends DBEntity> Boolean insertOrUpdate(Collection<T> c, Class<T> entityType, String aliasName, String tableName) {
        createTableIfNotExists(entityType, aliasName, tableName);

        String sql = null;
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            sql = buildSQL(entityType, SQLType.INSERTONDUPLICATEKEYUPDATE, tableName);
            conn = open(aliasName);
            if (useInnodb())
                conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
            Date now = new Date();
            LinkedHashSet<Field> fields = DBEntityUtils.fieldSet(entityType);
            boolean isLogDb = dbLog(aliasName);
            for (T obj : c) {
                obj.setFirstCreateTime(now);
                int i = 1;
                for (Field field : fields) {
                    com.common.module.internal.db.dao.Wrapper.getInstance().wrap(pst, obj, field, i++);
                }
                for (Field field : fields) {
                    com.common.module.internal.db.dao.Wrapper.getInstance().wrap(pst, obj, field, i++);
                }
                pst.addBatch();
                if (!isLogDb) {
                    getPreparedStatementSQL(pst);
                }
            }
            int[] r = pst.executeBatch();
            if (useInnodb())
                conn.commit();
            if (r != null && r.length == c.size()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            if (useInnodb()) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    throw new RuntimeException(e1.getMessage(), e1);
                }
            }
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, null);
        }
    }

    @Override
    public <T extends DBEntity> Boolean insertIfNotExists(Collection<T> c, Class<T> entityType) {

        if (DBEntityUtils.isLogger(entityType))
            throw new RuntimeException(entityType.getName());
        if (DBEntityUtils.needRoll(entityType))
            throw new RuntimeException(entityType.getName());

        String aliasName = aliasName(entityType);

        if (DBEntityUtils.tableNum(entityType) == 1) {
            return insertIfNotExists(c, entityType, aliasName, DBEntityUtils.simpleTableName(entityType));
        }

        List<Boolean> l = Lists.newArrayList();
        Map<String, List<T>> tableNumMap = c.stream().collect(Collectors.groupingBy(e -> DBEntityUtils.getTableName(e), Collectors.toList()));
        tableNumMap.keySet().forEach(tableName -> {
            if (insertIfNotExists(tableNumMap.get(tableName), entityType, aliasName, tableName))
                l.add(true);
        });
        return l.size() == tableNumMap.keySet().size();
    }

    @Override
    public <T extends DBEntity> Boolean insertIfNotExists(Collection<T> c, Class<T> entityType, String aliasName, String tableName) {
        createTableIfNotExists(entityType, aliasName, tableName);

        String sql = null;
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            sql = buildSQL(entityType, SQLType.INSERTIGNORE, tableName);
            conn = open(aliasName);
            if (useInnodb())
                conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
            Date now = new Date();
            boolean isLogDb = dbLog(aliasName);
            for (T obj : c) {
                obj.setFirstCreateTime(now);
                com.common.module.internal.db.dao.Wrapper.getInstance().wrap(pst, obj);
                pst.addBatch();
                if (!isLogDb) {
                    getPreparedStatementSQL(pst);
                }
            }
            int[] r = pst.executeBatch();
            if (useInnodb())
                conn.commit();
            if (r != null && r.length == c.size()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            if (useInnodb()) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    throw new RuntimeException(e1.getMessage(), e1);
                }
            }
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, null);
        }
    }

    @Override
    public <T extends DBEntity> Boolean replace(Collection<T> c, Class<T> entityType) {
        String aliasName = aliasName(entityType);

        if (DBEntityUtils.tableNum(entityType) == 1) {
            return replace(c, entityType, aliasName, DBEntityUtils.simpleTableName(entityType));
        }

        List<Boolean> l = Lists.newArrayList();
        Map<String, List<T>> tableNumMap = c.stream().collect(Collectors.groupingBy(e -> DBEntityUtils.getTableName(e), Collectors.toList()));
        tableNumMap.keySet().forEach(tableName -> {
            if (replace(tableNumMap.get(tableName), entityType, aliasName, tableName))
                l.add(true);
        });
        return l.size() == tableNumMap.keySet().size();
    }

    @Override
    public <T extends DBEntity> Boolean replace(Collection<T> c, Class<T> entityType, String aliasName, String tableName) {
        createTableIfNotExists(entityType, aliasName, tableName);

        String sql = null;
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            sql = buildSQL(entityType, SQLType.REPLACE, tableName);
            conn = open(aliasName);
            if (useInnodb())
                conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
            Date now = new Date();
            boolean isLogDb = dbLog(aliasName);
            for (T obj : c) {
                obj.setFirstCreateTime(now);
                com.common.module.internal.db.dao.Wrapper.getInstance().wrap(pst, obj);
                pst.addBatch();
                if (!isLogDb) {
                    getPreparedStatementSQL(pst);
                }
            }
            int[] r = pst.executeBatch();
            if (useInnodb())
                conn.commit();
            if (r != null && r.length == c.size()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            if (useInnodb()) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    throw new RuntimeException(e1.getMessage(), e1);
                }
            }
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, null);
        }
    }

    @Override
    public <T extends DBEntity> Boolean delete(Class<T> entityType, Serializable... pks) {
        if (!DBEntityUtils.deleteable(entityType)) {
            throw new RuntimeException(entityType.getName() + " 不允許刪除條目 ：" + StringUtils.toString(pks));
        }
        if (DBEntityUtils.isLogger(entityType))
            throw new RuntimeException(entityType.getName());
        if (DBEntityUtils.needRoll(entityType))
            throw new RuntimeException(entityType.getName());
        String aliasName = aliasName(entityType);
        String tab = simpleTableName(entityType);
        int tabNum = tableNum(entityType);
        if (tabNum > 1) {
            tab = DBEntityUtils.getTableName(entityType, pks);
        }
        String sql = null;
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            sql = buildSQL(entityType, SQLType.DELETEBYKEY, tab);
            conn = open(aliasName);
            if (useInnodb())
                conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
            boolean isLogDb = dbLog(aliasName);
            if (!isLogDb) {
                getPreparedStatementSQL(pst);
            }
            com.common.module.internal.db.dao.Wrapper.getInstance().wrap(pst, pks);
            int result = pst.executeUpdate();
            if (useInnodb())
                conn.commit();
            return result == 1;
        } catch (Exception e) {
            if (useInnodb()) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    throw new RuntimeException(e1.getMessage(), e1);
                }
            }
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, null);
            Logger log = LoggerFactory.getLogger(DaoImpl.class);
            log.info("entityType:{}, pks:{}", entityType, StringUtils.toString(pks));
        }
    }

    @Override
    public <T extends DBEntity> Boolean deleteByKeys(Class<T> entityType, String... pks) {
        if (!DBEntityUtils.deleteable(entityType)) {
            throw new RuntimeException(entityType.getName() + " 不允許刪除條目 ：" + StringUtils.toString(pks));
        }
        if (DBEntityUtils.isLogger(entityType))
            throw new RuntimeException(entityType.getName());
        if (DBEntityUtils.needRoll(entityType))
            throw new RuntimeException(entityType.getName());
        String aliasName = aliasName(entityType);
        String tab = simpleTableName(entityType);
        int tabNum = tableNum(entityType);
        if (tabNum > 1) {
            tab = DBEntityUtils.getTableName(entityType, pks);
        }
        String sql = null;
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            sql = DELETEBYKEY.BUILDSQLIN(entityType, tab, pks);
//            sql = buildSQL(entityType, SQLType.DELETEBYKEY, tab);
            conn = open(aliasName);
            if (useInnodb())
                conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
            com.common.module.internal.db.dao.Wrapper.getInstance().wrap(pst, pks);
            boolean isLogDb = dbLog(aliasName);
            if (!isLogDb) {
                getPreparedStatementSQL(pst);
            }
            int result = pst.executeUpdate();
            if (useInnodb())
                conn.commit();
            return result == pks.length;
        } catch (Exception e) {
            if (useInnodb()) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    throw new RuntimeException(e1.getMessage(), e1);
                }
            }
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, null);
            Logger log = LoggerFactory.getLogger(DaoImpl.class);
            log.info("entityType:{}, pks:{}", entityType, StringUtils.toString(pks));
        }
    }

    @Override
    public <T extends DBEntity> Boolean delete(Class<T> entityType, String condition) {
        if (!DBEntityUtils.deleteable(entityType)) {
            throw new RuntimeException(entityType.getName() + " 不允許刪除條目 ：" + condition);
        }
        if (DBEntityUtils.isLogger(entityType))
            throw new RuntimeException(entityType.getName());
        if (DBEntityUtils.needRoll(entityType))
            throw new RuntimeException(entityType.getName());
        String aliasName = aliasName(entityType);
        Set<String> sqls = Sets.newHashSet();
        int tabNum = tableNum(entityType);
        for (int i = 1; i <= tabNum; i++) {
            String tab = simpleTableName(entityType);
            if (tabNum > 1) {
                tab = DBEntityUtils.merged(tab, i);
            }
            sqls.add("DELETE FROM `" + tab + "` WHERE " + condition);
        }
        try {
            execute(aliasName, sqls);
            return true;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage(), e);
        } finally {
            Logger log = LoggerFactory.getLogger(DaoImpl.class);
            log.info("entityType:{}, condition:{}", entityType, condition);
        }
    }

    @Override
    public <T extends DBEntity> Boolean delete(Collection<T> c, Class<T> entityType) {
        if (!DBEntityUtils.deleteable(entityType)) {
            throw new RuntimeException(entityType.getName() + " 不允許刪除條目 ：" + StringUtils.toString(c));
        }
        if (DBEntityUtils.isLogger(entityType))
            throw new RuntimeException(entityType.getName());
        if (DBEntityUtils.needRoll(entityType))
            throw new RuntimeException(entityType.getName());
        String aliasName = aliasName(entityType);
        if (DBEntityUtils.tableNum(entityType) == 1) {
            return delete(c, entityType, aliasName, DBEntityUtils.simpleTableName(entityType));
        }
        List<Boolean> l = Lists.newArrayList();
        Map<String, List<T>> tableNumMap = c.stream().collect(Collectors.groupingBy(e -> DBEntityUtils.getTableName(e), Collectors.toList()));
        tableNumMap.keySet().forEach(tableName -> {
            if (delete(tableNumMap.get(tableName), entityType, aliasName, tableName))
                l.add(true);
        });
        return l.size() == tableNumMap.keySet().size();
    }

    @Override
    public <T extends DBEntity> boolean delete(Collection<T> c, Class<T> entityType, String aliasName, String tableName) {
        if (!DBEntityUtils.deleteable(entityType)) {
            throw new RuntimeException(entityType.getName() + " 不允許刪除條目 ：" + StringUtils.toString(c));
        }
        String sql = null;
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            sql = buildSQL(entityType, SQLType.DELETEBYKEY, tableName);
            conn = open(aliasName);
            if (useInnodb())
                conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
            boolean isLogDb = dbLog(aliasName);
            for (T obj : c) {
                Serializable[] keys = obj.getPks();
                com.common.module.internal.db.dao.Wrapper.getInstance().wrap(pst, keys);
                pst.addBatch();
                if (!isLogDb) {
                    getPreparedStatementSQL(pst);
                }
            }
            int[] r = pst.executeBatch();
            if (useInnodb())
                conn.commit();
            if (r != null && r.length == c.size()) {
                return true;
            }
            return false;
        } catch (Exception e) {
            if (useInnodb()) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    throw new RuntimeException(e1.getMessage(), e1);
                }
            }
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, null);
            Logger log = LoggerFactory.getLogger(DaoImpl.class);
            log.info("entityType:{}, collection:{}", entityType, StringUtils.toString(c));
        }
    }

    @Override
    public <T extends DBEntity> Boolean update(Collection<T> c, Class<T> entityType) {
        if (DBEntityUtils.isLogger(entityType))
            throw new RuntimeException(entityType.getName());
        if (DBEntityUtils.needRoll(entityType))
            throw new RuntimeException(entityType.getName());
        String aliasName = aliasName(entityType);
        if (DBEntityUtils.tableNum(entityType) == 1) {
            return update(c, entityType, aliasName, DBEntityUtils.simpleTableName(entityType));
        }
        List<Boolean> l = Lists.newArrayList();
        Map<String, List<T>> tableNumMap = c.stream().collect(Collectors.groupingBy(e -> DBEntityUtils.getTableName(e), Collectors.toList()));
        tableNumMap.keySet().forEach(tableName -> {
            if (update(tableNumMap.get(tableName), entityType, aliasName, tableName))
                l.add(true);
        });
        return l.size() == tableNumMap.keySet().size();
    }

    @Override
    public <T extends DBEntity> Boolean update(Collection<T> c, Class<T> entityType, String aliasName, String tableName) {
        String sql = null;
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            sql = buildSQL(entityType, SQLType.UPDATEBYKEY, tableName);
            conn = open(aliasName);
            if (useInnodb())
                conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
            boolean isLogDb = dbLog(aliasName);
            for (T obj : c) {
                LinkedHashSet<Field> allFields = DBEntityUtils.fieldSet(entityType);
                LinkedHashSet<Field> keyFields = Sets.newLinkedHashSet();
                LinkedHashSet<Field> valueFields = Sets.newLinkedHashSet();
                allFields.forEach(field -> {
                    if (DBEntityUtils.isPk(field))
                        keyFields.add(field);
                    else if (!DBEntityUtils.isReadOnly(field))
                        valueFields.add(field);
                });
                int j = com.common.module.internal.db.dao.Wrapper.getInstance().wrap(pst, obj, valueFields, 1);
                com.common.module.internal.db.dao.Wrapper.getInstance().wrap(pst, obj, keyFields, j);
                pst.addBatch();
                if (!isLogDb) {
                    getPreparedStatementSQL(pst);
                }
            }
            int[] r = pst.executeBatch();
            if (useInnodb())
                conn.commit();
            if (r != null && r.length == c.size()) {
                return true;
            }
            return insertOrUpdate(c, entityType, aliasName, tableName);
        } catch (Exception e) {
            LoggerFactory.getLogger(getClass()).error(e.getMessage() + sql, e);
            if (useInnodb()) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    throw new RuntimeException(e1.getMessage(), e1);
                } finally {
                }
            }
            return insertOrUpdate(c, entityType, aliasName, tableName);
        } finally {
            close(conn, pst, null);
        }
    }

    @Override
    public <T extends DBEntity> List<Map<String, Object>> query(Class<T> entityType, String sql) {
        return query(aliasName(entityType), sql);
    }

    @Override
    public List<Map<String, Object>> query(String aliasName, String sql) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<Map<String, Object>> result = Lists.newArrayList();
        try {
            conn = open(aliasName);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            while (rs != null && rs.next()) {
                Map<String, Object> map = Maps.newLinkedHashMap();
                for (int i = 0; ++i <= meta.getColumnCount(); ) {
                    String name = meta.getColumnLabel(i);
                    Object value = rs.getObject(name);
                    map.put(name, value);
                }
                result.add(map);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public <T extends DBEntity> List<T> queryAll(Class<T> entityType) {
        if (DBEntityUtils.isLogger(entityType))
            throw new RuntimeException(entityType.getName());
        if (DBEntityUtils.needRoll(entityType))
            throw new RuntimeException(entityType.getName());
        List<T> list = Lists.newArrayList();
        String aliasName = aliasName(entityType);
        int tableNum = tableNum(entityType);
        for (int i = 1; i <= tableNum; i++) {
            String tab = simpleTableName(entityType);
            if (tableNum > 1) {
                tab = DBEntityUtils.merged(tab, i);
            }
            List<T> ts = queryAll(entityType, aliasName, tab);
            list.addAll(ts);
        }
        return list;
    }

    @Override
    public <T extends DBEntity> List<T> queryAll(Class<T> entityType, String aliasName, String tableName) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = null;
        List<T> result = Lists.newArrayList();
        try {
            sql = buildSQL(entityType, SQLType.SELECTALL, tableName);
            conn = open(aliasName);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs != null && rs.next()) {
                T obj = newEntityInstance(entityType);
                com.common.module.internal.db.dao.Wrapper.getInstance().wrap(rs, obj);
                result.add(obj);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public <T extends DBEntity> List<T> queryLimit(Class<T> entityType, String aliasName, String tableName, long beginLimit, long endLimit) {
        String sql = String.format("SELECT *FROM `%s` LIMIT %d,%d", tableName, beginLimit, endLimit);
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        List<T> result = Lists.newArrayList();
        try {
            conn = open(aliasName);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs != null && rs.next()) {
                T obj = newEntityInstance(entityType);
                com.common.module.internal.db.dao.Wrapper.getInstance().wrap(rs, obj);
                result.add(obj);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public <T extends DBEntity> T query(Class<T> entityType, Serializable... pks) {
        if (DBEntityUtils.isLogger(entityType))
            throw new RuntimeException(entityType.getName());
        if (DBEntityUtils.needRoll(entityType))
            throw new RuntimeException(entityType.getName());
        String aliasName = aliasName(entityType);
        String tab = simpleTableName(entityType);
        int tabNum = tableNum(entityType);
        if (tabNum > 1) {
            tab = DBEntityUtils.getTableName(entityType, pks);
        }
        return query(entityType, aliasName, tab, pks);
    }

    @Override
    public <T extends DBEntity> T query(Class<T> entityType, String aliasName, String tableName, Serializable... pks) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = null;
        try {
            sql = buildSQL(entityType, SQLType.SELECTBYKEY, tableName);
            conn = open(aliasName);
            pst = conn.prepareStatement(sql);
            com.common.module.internal.db.dao.Wrapper.getInstance().wrap(pst, pks);
            rs = pst.executeQuery();
            if (rs != null && rs.next()) {
                T obj = newEntityInstance(entityType);
                com.common.module.internal.db.dao.Wrapper.getInstance().wrap(rs, obj);
                return obj;
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public <T extends DBEntity> Map<String, T> queryAll(Class<T> entityType, Serializable[]... pksArray) {
        if (DBEntityUtils.isLogger(entityType))
            throw new RuntimeException(entityType.getName());
        if (DBEntityUtils.needRoll(entityType))
            throw new RuntimeException(entityType.getName());
        if (tableNum(entityType) > 1)
            throw new RuntimeException(entityType.getName());
        String aliasName = aliasName(entityType);
        String tab = simpleTableName(entityType);
        return queryAll(entityType, aliasName, tab, pksArray);
    }

    @Override
    public <T extends DBEntity> Map<String, T> queryAll(Class<T> entityType, String aliasName, String tableName, Serializable[]... pksArray) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = null;
        Map<String, T> result = Maps.newHashMap();
        try {
            sql = String.format(buildSQL(entityType, SQLType.SELECTOR, tableName), SELECT.BUILDKEYSOR(entityType, pksArray));
            conn = open(aliasName);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs != null && rs.next()) {
                T obj = newEntityInstance(entityType);
                com.common.module.internal.db.dao.Wrapper.getInstance().wrap(rs, obj);
                result.put(obj.getKey(), obj);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public <T extends DBEntity> Map<String, T> queryAll(Class<T> entityType, List<Serializable[]> pksList) {
        return queryAll(entityType, ArraysUtils.toArray(pksList, Serializable[].class));
    }

    @Override
    public <T extends DBEntity> Map<String, T> queryAll(Class<T> entityType, String aliasName, String tableName, List<Serializable[]> pksList) {
        return queryAll(entityType, aliasName, tableName, ArraysUtils.toArray(pksList, Serializable[].class));
    }

    @Override
    public <T extends DBEntity> Map<String, T> queryAll(Class<T> entityType, Collection<Long> keys) {
        if (DBEntityUtils.isLogger(entityType))
            throw new RuntimeException(entityType.getName());
        if (DBEntityUtils.needRoll(entityType))
            throw new RuntimeException(entityType.getName());
        Map<String, T> map = Maps.newHashMap();
        String aliasName = aliasName(entityType);
        int tableNum = tableNum(entityType);
        if (tableNum == 1)
            return queryAll(entityType, aliasName, simpleTableName(entityType), keys);
        Map<String, Set<Long>> tabKeyMap = keys.stream().collect(Collectors.groupingBy(id -> DBEntityUtils.getTableName(entityType, id), Collectors.toSet()));
        tabKeyMap.forEach((tab, keySet) -> {
            Map<String, T> ts = queryAll(entityType, aliasName, tab, keySet);
            map.putAll(ts);
        });
        return map;
    }

    @Override
    public <T extends DBEntity> Map<String, T> queryAll(Class<T> entityType, String aliasName, String tableName, Collection<Long> keys) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = null;
        Map<String, T> result = Maps.newHashMap();
        try {
            sql = String.format(buildSQL(entityType, SQLType.SELECTIN, tableName), SELECT.BUILDKEYSIN(entityType, keys));
            conn = open(aliasName);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs != null && rs.next()) {
                T obj = newEntityInstance(entityType);
                com.common.module.internal.db.dao.Wrapper.getInstance().wrap(rs, obj);
                result.put(obj.getKey(), obj);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public <T extends DBEntity> List<T> queryWithCondition(Class<T> entityType, String condition) {
        if (DBEntityUtils.isLogger(entityType))
            throw new RuntimeException(entityType.getName());
        if (DBEntityUtils.needRoll(entityType))
            throw new RuntimeException(entityType.getName());
        List<T> list = Lists.newArrayList();
        String aliasName = aliasName(entityType);
        int tableNum = tableNum(entityType);
        for (int i = 1; i <= tableNum; i++) {
            String tab = simpleTableName(entityType);
            if (tableNum > 1) {
                tab = DBEntityUtils.merged(tab, i);
            }
            List<T> ts = queryWithCondition(entityType, aliasName, tab, condition);
            list.addAll(ts);
        }
        return list;
    }

    @Override
    public <T extends DBEntity> List<T> queryWithCondition(Class<T> entityType, String aliasName, String tableName, String condition) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        String sql = null;
        List<T> result = Lists.newArrayList();
        try {
            sql = String.format(buildSQL(entityType, SQLType.SELECTBYCONDITION, tableName), condition);
            conn = open(aliasName);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            while (rs != null && rs.next()) {
                T obj = newEntityInstance(entityType);
                Wrapper.getInstance().wrap(rs, obj);
                result.add(obj);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public <T extends DBEntity> void query(Class<T> entityType, String sql, Querier querier) {
        query(aliasName(entityType), sql, querier);
    }

    @Override
    public void query(String aliasName, String sql, Querier querier) {
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;
        try {
            conn = open(aliasName);
            pst = conn.prepareStatement(sql);
            rs = pst.executeQuery();
            if (querier != null) {
                while (rs != null && rs.next()) {
                    querier.accept(rs);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, rs);
        }
    }

    @Override
    public int[] execute(String aliasName, Collection<String> sqls) throws SQLException {
        Connection conn = null;
        Statement st = null;
        try {
            conn = open(aliasName);
            if (useInnodb())
                conn.setAutoCommit(false);
            st = conn.createStatement();
            boolean isLogDb = dbLog(aliasName);
            for (String sql : sqls) {
                st.addBatch(sql);
                if (sql.toLowerCase().startsWith("update") || sql.toLowerCase().startsWith("insert") || sql.toLowerCase().startsWith("delete")) {
                    if (!isLogDb) {
                        getPreparedStatementSQL(st);
                    }
                }
            }
            int[] r = st.executeBatch();
            if (useInnodb())
                conn.commit();
            return r;
        } catch (Exception e) {
            if (useInnodb()) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    throw new RuntimeException(e1.getMessage(), e1);
                }
            }
            throw new RuntimeException(e.getMessage() + ":" + st, e);
        } finally {
            close(conn, st, null);
        }
    }

//    @Override
//    public int[] execute(String aliasName, String... sqls) throws SQLException {
//        return execute(aliasName, ArraysUtils.toList(sqls));
//    }

    @Override
    public Integer executeUpdate(String aliasName, String sql) throws SQLException {
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = open(aliasName);
            if (useInnodb())
                conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
            int result = pst.executeUpdate();
            if (useInnodb())
                conn.commit();
            return result;
        } catch (Exception e) {
            if (useInnodb()) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    throw new RuntimeException(e1.getMessage(), e1);
                }
            }
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, null);
        }
    }

    @Override
    public Boolean execute(String aliasName, String sql) throws SQLException {
        Connection conn = null;
        PreparedStatement pst = null;
        try {
            conn = open(aliasName);
            if (useInnodb())
                conn.setAutoCommit(false);
            pst = conn.prepareStatement(sql);
            boolean result = pst.execute();
            if (useInnodb())
                conn.commit();
            return result;
        } catch (Exception e) {
            if (useInnodb()) {
                try {
                    conn.rollback();
                } catch (SQLException e1) {
                    throw new RuntimeException(e1.getMessage(), e1);
                }
            }
            throw new RuntimeException(e.getMessage() + ":" + pst, e);
        } finally {
            close(conn, pst, null);
        }
    }

    private String getPreparedStatementSQL(Statement preparedStatement) {
        if (!recordSql()) {
            return "";
        }
        String sql = "";
        try {
            Class<?> clazz = preparedStatement.getClass();
            if (clazz == DelegatingPreparedStatement.class) {
                Statement delegate = ((DelegatingPreparedStatement) preparedStatement).getDelegate();
                if (delegate instanceof DelegatingPreparedStatement) {
                    delegate = ((DelegatingPreparedStatement) delegate).getDelegate();
                }
                if (delegate instanceof ServerPreparedStatement) {
                    String sql1 = ((ServerPreparedStatement) delegate).asSql();
                    sql1 = sql1.replaceAll("\t", "");
                    sql1 = sql1.replaceAll("\n", "");
                    sqlLog.info("{}", sql1);
                    return sql1;
                } else {
                    sql = "Unknown SQL";
                }
            } else {
                sql = "Unknown SQL";
            }
        } catch (Exception e) {
            sql = "Unknown SQL";
        }
        return sql;
    }

    public boolean recordSql() {
        return PropertyConfig.getBoolean("record.sql.switch", false);
    }

    private boolean dbLog(String aliasName) {
        return DB.DB_LOG_ALIAS.equals(aliasName);
    }

//    public ResultSet executeQuery(Connection connection, String sql) {
//        Statement statement = null;
//        try {
//            statement = connection.createStatement();
//            return statement.executeQuery(sql);
//        } catch (SQLException e) {
//        } finally {
//        }
//        return null;
//    }
}
