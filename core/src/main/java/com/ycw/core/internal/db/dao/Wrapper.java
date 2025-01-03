
package com.ycw.core.internal.db.dao;

import com.google.common.collect.Lists;
import com.ycw.core.internal.db.annotation.Persistent;
import com.ycw.core.internal.db.entity.DBEntity;
import com.ycw.core.internal.db.entity.DBEntityUtils;
import com.ycw.core.util.AnnotationUtil;
import com.ycw.core.util.ArraysUtils;
import com.ycw.core.util.StringUtils;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.function.Predicate;

/**
 * <包装器类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public final class Wrapper {

    private Wrapper() {

    }

    private static volatile Wrapper instance;

    public static Wrapper getInstance() {
        if (instance == null) {
            synchronized (Wrapper.class) {
                if (instance == null) {
                    instance = new Wrapper();
                }
            }
        }
        return instance;
    }

    public void wrap(PreparedStatement pst, Serializable... keys) throws Exception {
        for (int i = 1; i <= keys.length; i++) {
            try {
                pst.setObject(i, keys[i - 1]);
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage() + ":" + StringUtils.toString(keys) + "," + i + "," + pst.getUpdateCount() + "," + pst, e);
            }
        }
    }

    public void wrap(PreparedStatement pst, DBEntity obj) throws Exception {
        LinkedHashSet<Field> fields = DBEntityUtils.fieldSet(obj.getEntityType());
        int i = 1;
        for (Field field : fields)
            wrap(pst, obj, field, i++);
    }

    public int wrap(PreparedStatement pst, DBEntity obj, LinkedHashSet<Field> fields, int i) throws Exception {
        int j = i;
        for (Field field : fields)
            wrap(pst, obj, field, j++);
        return j;
    }

    public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
        try {
            ((Persistent) AnnotationUtil.findAnnotation(field, Persistent.class)).dataType().wrap(pst, obj, field, i);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + field + "," + i + "," + pst.getUpdateCount() + "," + pst + "," + obj, e);
        }
    }

    // --------------------------------------------------------------------------------------------------------------------------------------------------------------//
    public void wrap(ResultSet rs, DBEntity obj) throws Exception {
        wrap(rs, obj, null);
    }

    /**
     * 自定义从mysql数据结果集中解析数据
     */
    public void wrap(ResultSet rs, DBEntity obj, Predicate<String> columnFilter) throws Exception {
        LinkedHashSet<Field> fieldSet = DBEntityUtils.fieldSet(obj.getEntityType());
        for (Field field : fieldSet) {
            if (columnFilter == null) {
                wrap(obj, rs, field);
            } else {
                if ((!columnFilter.test(DBEntityUtils.columnName(field))))
                    wrap(obj, rs, field);
            }
        }
    }

    public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
        try {
            ((Persistent) AnnotationUtil.findAnnotation(field, Persistent.class)).dataType().wrap(obj, rs, field);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + field + "," + rs.getCursorName() + "," + rs + "," + obj, e);
        }
    }

    /**
     * 真实的sql
     */
    String buildSQL(String sql, LinkedHashMap<Field, Object>... maps) {
        String _sql = sql.replaceAll("\\?", "%s");
        LinkedList<Object> values = Lists.newLinkedList();
        for (LinkedHashMap<Field, Object> map : maps) {
            values.addAll(map.values());
        }
        Object[] params = ArraysUtils.toArray(values, Object.class);
        for (int i = 0; i < params.length; i++) {
            String v = "'" + params[i] + "'";
            params[i] = v;
        }
        return String.format(_sql, params);
    }
}
