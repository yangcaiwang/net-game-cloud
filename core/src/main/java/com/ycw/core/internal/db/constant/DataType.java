
package com.ycw.core.internal.db.constant;

import com.alibaba.fastjson.JSON;
import com.google.common.util.concurrent.AtomicDouble;
import com.ycw.core.internal.db.annotation.Persistent;
import com.ycw.core.internal.db.entity.DBEntity;
import com.ycw.core.internal.db.entity.DBEntityUtils;
import com.ycw.core.util.AnnotationUtil;
import com.ycw.core.util.DateUnit;
import com.ycw.core.util.SerializationUtils;
import com.ycw.core.util.StringUtils;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * <数据类型枚举类>
 * <p>
 * ps:
 * 持久化数据类型,持久化对象的每个字段可通过注解此类型，指定数据的类型，默认长度，默认值，空值，非空值，是否基本数据类型等
 * text/blob 不可以设置默认值,不可以设置长度,所以一定要允许为null,不可以设置canBeNull=true
 * 如果是复合类型的set,list,map等,请申明成CopyOnWriteSet,CopyOnWriteArrayList,ConcurrentMap
 * 如果是Object引用类型,也要确保所有的符合字段都是线程安全的
 * 避免入库取值时发生异常 除非确定集合里的数据不会被删除,可以申明成非线程安全对象（已经禁止此种做法）
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public enum DataType {

    /**
     * byte,无需设置长度
     */
    BYTE("TINYINT", 4) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            byte v = rs.getByte(n);
            if (field.getGenericType().getTypeName().equals(Byte.class.getName())) {
                field.set(obj, Byte.valueOf(v));
            } else {
                field.set(obj, v);
            }
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            if (field.getGenericType().getTypeName().equals(Byte.class.getName())) {
                if (value == null) {
                    pst.setByte(i, (byte) 0);
                } else {
                    pst.setByte(i, ((Byte) value).byteValue());
                }
            } else {
                pst.setByte(i, (byte) value);
            }
        }
    }, //

    /**
     * short,无需设置长度
     */
    SHORT("SMALLINT", 6) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            short v = rs.getShort(n);
            if (field.getGenericType().getTypeName().equals(Short.class.getName())) {
                field.set(obj, Short.valueOf(v));
            } else {
                field.set(obj, v);
            }
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {

            Object value = getValue(obj, field);
            if (field.getGenericType().getTypeName().equals(Short.class.getName())) {
                if (value == null) {
                    pst.setShort(i, (short) 0);
                } else {
                    pst.setShort(i, ((Short) value).shortValue());
                }
            } else {
                pst.setShort(i, (short) value);
            }
        }
    }, //

    /**
     * int,Integer,AtomicInteger无需设置长度
     */
    INT("INT", 11) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            int v = rs.getInt(n);
            if (field.getGenericType().getTypeName().equals(Integer.class.getName())) {
                field.set(obj, Integer.valueOf(v));
            } else if (field.getGenericType().getTypeName().equals(AtomicInteger.class.getTypeName())) {
                field.set(obj, new AtomicInteger(v));
            } else {
                field.set(obj, v);
            }
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            if (field.getGenericType().getTypeName().equals(Integer.class.getName())) {
                if (value == null) {
                    pst.setInt(i, (int) 0);
                } else {
                    pst.setInt(i, ((Integer) value).intValue());
                }
            } else if (field.getGenericType().getTypeName().equals(AtomicInteger.class.getTypeName())) {
                if (value == null) {
                    pst.setInt(i, (int) 0);
                } else {
                    pst.setInt(i, ((AtomicInteger) value).intValue());
                }
            } else {
                pst.setInt(i, (int) value);
            }
        }
    }, //

    /**
     * long or AtomicLong无需设置长度
     */
    LONG("BIGINT", 20) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {

            String n = DBEntityUtils.columnName(field);
            long v = rs.getLong(n);
            if (field.getGenericType().getTypeName().equals(Long.class.getName())) {
                field.set(obj, Long.valueOf(v));
            } else if (field.getGenericType().getTypeName().equals(AtomicLong.class.getTypeName())) {
                field.set(obj, new AtomicLong(v));
            } else {
                field.set(obj, v);
            }
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            if (field.getGenericType().getTypeName().equals(Long.class.getName())) {
                if (value == null) {
                    pst.setLong(i, (long) 0L);
                } else {
                    pst.setLong(i, ((Long) value).longValue());
                }
            } else if (field.getGenericType().getTypeName().equals(AtomicLong.class.getTypeName())) {
                if (value == null) {
                    pst.setLong(i, (long) 0L);
                } else {
                    pst.setLong(i, ((AtomicLong) value).longValue());
                }
            } else {
                pst.setLong(i, (long) value);
            }
        }
    }, //

    /**
     * float(size,d),@Persistent(len=11,scale=2),可自行调整(size,d)
     */
    FLOAT("FLOAT", 11, 2) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            float v = rs.getFloat(n);
            if (field.getGenericType().getTypeName().equals(Float.class.getName())) {
                field.set(obj, Float.valueOf(v));
            } else if (field.getGenericType().getTypeName().equals(AtomicDouble.class.getTypeName())) {
                field.set(obj, new AtomicDouble(v));
            } else {
                field.set(obj, v);
            }
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            if (field.getGenericType().getTypeName().equals(Float.class.getName())) {
                if (value == null) {
                    pst.setFloat(i, (float) 0f);
                } else {
                    pst.setFloat(i, ((Float) value).floatValue());
                }
            } else if (field.getGenericType().getTypeName().equals(AtomicDouble.class.getTypeName())) {
                if (value == null) {
                    pst.setFloat(i, (float) 0f);
                } else {
                    pst.setFloat(i, ((AtomicDouble) value).floatValue());
                }
            } else {
                pst.setFloat(i, (float) value);
            }
        }
    }, //

    /**
     * float(size,d),@Persistent(len=20,scale=2),可自行调整(size,d)
     */
    DOUBLE("DOUBLE", 20, 2) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            double v = rs.getDouble(n);
            if (field.getGenericType().getTypeName().equals(Double.class.getName())) {
                field.set(obj, Double.valueOf(v));
            } else if (field.getGenericType().getTypeName().equals(AtomicDouble.class.getTypeName())) {
                field.set(obj, new AtomicDouble(v));
            } else {
                field.set(obj, v);
            }
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            if (field.getGenericType().getTypeName().equals(Double.class.getName())) {
                if (value == null) {
                    pst.setDouble(i, (double) 0d);
                } else {
                    pst.setDouble(i, ((Double) value).doubleValue());
                }
            } else if (field.getGenericType().getTypeName().equals(AtomicDouble.class.getTypeName())) {
                if (value == null) {
                    pst.setDouble(i, (double) 0d);
                } else {
                    pst.setDouble(i, ((AtomicDouble) value).doubleValue());
                }
            } else {
                pst.setDouble(i, (double) value);
            }
        }
    }, //

    /**
     * decmail(size,d),@Persistent(len=20,scale=2),可自行调整(size,d)
     */
    DECIMAL("DECIMAL", 20, 2) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            BigDecimal v = rs.getBigDecimal(n);
            field.set(obj, v);
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            pst.setBigDecimal(i, (BigDecimal) value);
        }
    }, //

    /**
     * boolean,Boolean,AtomicBoolean无需设置长度
     */
    BOOL("BIT", 1) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            boolean v = rs.getBoolean(n);
            if (field.getGenericType().getTypeName().equals(Boolean.class.getName())) {
                field.set(obj, Boolean.valueOf(v));
            } else if (field.getGenericType().getTypeName().equals(AtomicBoolean.class.getTypeName())) {
                field.set(obj, new AtomicBoolean(v));
            } else {
                field.set(obj, v);
            }
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            if (field.getGenericType().getTypeName().equals(Boolean.class.getName())) {
                if (value == null) {
                    pst.setBoolean(i, (boolean) false);
                } else {
                    pst.setBoolean(i, ((Boolean) value).booleanValue());
                }
            } else if (field.getGenericType().getTypeName().equals(AtomicBoolean.class.getTypeName())) {
                if (value == null) {
                    pst.setBoolean(i, (boolean) false);
                } else {
                    pst.setBoolean(i, ((AtomicBoolean) value).get());
                }
            } else {
                pst.setBoolean(i, (boolean) value);
            }
        }
    }, //

    /**
     * byte[],无需设置长度,最大长度65535字节
     */
    BLOB("BLOB", 0) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            byte[] v = rs.getBytes(n);
            if (v != null)
                field.set(obj, v);
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            byte[] v = (byte[]) getValue(obj, field);
            pst.setBytes(i, v);
        }
    }, //

    /**
     * String,默认长度255字节,可以自己修改长度
     */
    STRING("VARCHAR", 255) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            String v = rs.getString(n);
            if (v != null) {
                field.set(obj, v);
            }
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            pst.setString(i, (String) value);
        }
    }, //

    /**
     * 枚举,保存类型为string,也就是枚举的名称,切忌把枚举定义成持久化字段不能再修改,默认长度30字节,可以自己修改长度,如果不允许为空,
     * 应该申明一个ordinal=0,name=NULL的默认对象
     */
    ENUM("VARCHAR", 30) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            String v = rs.getString(n);
            if (v != null) {
                Class clz = field.getType();
                Enum e = Enum.valueOf(clz, v);
                field.set(obj, e);
            }
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            Class clz = field.getType();
            Enum v = value != null ? (Enum) value : null;
            pst.setString(i, v == null ? null : ((Enum) v).name());
        }
    },

    /**
     * TEXT,65535字节
     */
    TEXT("TEXT", 0) {
        @Override
        public Object getValue(DBEntity obj, Field field) throws Exception {
            if (field.getType() == String.class)
                return STRING.getValue(obj, field);
            return OBJECT.getValue(obj, field);
        }

        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            if (field.getType() == String.class) {
                STRING.wrap(obj, rs, field);
                return;
            }
            OBJECT.wrap(obj, rs, field);
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            if (field.getType() == String.class) {
                STRING.wrap(pst, obj, field, i);
                return;
            }
            OBJECT.wrap(pst, obj, field, i);
        }
    }, //

    /**
     * java.util.Date,无需设置长度
     */
    DATE("DATETIME", 0) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            Timestamp v = rs.getTimestamp(n);
            if (v != null) {
                field.set(obj, new Date(v.getTime()));
            }
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            Date v = value != null ? (Date) value : null;
            pst.setTimestamp(i, v == null ? null : new Timestamp(((Date) v).getTime()));
        }
    }, //

    /**
     * 保存格式为fastjson,可以是任何对象;;最大长度65535字节,无需设置长度,集合类型的List,Set,Map,JSONObject,
     * JSONArray, Object等各种对象都支持 如果数据特别大根据需求预估使用
     * TEXT(正常文本),MEDIUM_TEXT(中等文本),LONG_TEXT(超大文本)
     */
    OBJECT("TEXT", 0) {// text

        @Override
        public Object getValue(DBEntity obj, Field field) throws Exception {
            if (DBEntityUtils.customPersistent(obj.getEntityType(), field)) {
                Method getter = DBEntityUtils.findGetter((Class<? extends DBEntity>) obj.getEntityType(), field);
                return getter.invoke(obj);
            }
            Object v = super.getValue(obj, field);
            return v == null ? null : v instanceof JSON ? ((JSON) v).toJSONString() : StringUtils.toStringFeature(v);
        }

        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            String v = rs.getString(n);
            if (v != null) {
                if (DBEntityUtils.customPersistent(obj.getEntityType(), field)) {
                    Method seter = DBEntityUtils.findSetter(obj.getEntityType(), field);
                    seter.invoke(obj, v);
                    return;
                }
                field.set(obj, SerializationUtils.jsonToBean(v, field.getGenericType()));
            }
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            pst.setString(i, (String) value);
        }
    }, //

    /**
     * 保存格式为fastjson,可以是任何对象;;最大长度65535字节,无需设置长度,集合类型的List,Set,Map,JSONObject,
     * JSONArray, Object等各种对象都支持 如果数据特别大根据需求预估使用
     * TEXT(正常文本),MEDIUM_TEXT(中等文本),LONG_TEXT(超大文本)
     */
    OBJECT_TYPE("TEXT", 0) {// text

        @Override
        public Object getValue(DBEntity obj, Field field) throws Exception {
            if (DBEntityUtils.customPersistent(obj.getEntityType(), field)) {
                Method getter = DBEntityUtils.findGetter(obj.getEntityType(), field);
                return getter.invoke(obj);
            }
            Object v = super.getValue(obj, field);
            return v == null ? null : v instanceof JSON ? ((JSON) v).toJSONString() : StringUtils.toJsonString(v);
        }

        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            String v = rs.getString(n);
            if (v != null) {
                if (DBEntityUtils.customPersistent(obj.getEntityType(), field)) {
                    Method seter = DBEntityUtils.findSetter(obj.getEntityType(), field);
                    seter.invoke(obj, v);
                    return;
                }
                field.set(obj, SerializationUtils.jsonToBean(v, field.getGenericType()));
            }
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            pst.setString(i, (String) value);
        }
    }, //

    /**
     * 保存格式为fastjson,可以是任何对象;;最大长度16777215字节,无需设置长度,集合类型的List,Set,Map,JSONObject,
     * JSONArray, Object等各种对象都支持 如果数据特别大根据需求预估使用
     * TEXT(正常文本),MEDIUM_TEXT(中等文本),LONG_TEXT(超大文本)
     */
    MEDIUM_OBJ_TYPE("MEDIUMTEXT", 0) {// text

        @Override
        public Object getValue(DBEntity obj, Field field) throws Exception {
            if (DBEntityUtils.customPersistent(obj.getEntityType(), field)) {
                Method getter = DBEntityUtils.findGetter(obj.getEntityType(), field);
                return getter.invoke(obj);
            }
            Object v = super.getValue(obj, field);
            return v == null ? null : v instanceof JSON ? ((JSON) v).toJSONString() : StringUtils.toJsonString(v);
        }

        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            String n = DBEntityUtils.columnName(field);
            String v = rs.getString(n);
            if (v != null) {
                if (DBEntityUtils.customPersistent(obj.getEntityType(), field)) {
                    Method seter = DBEntityUtils.findSetter(obj.getEntityType(), field);
                    seter.invoke(obj, v);
                    return;
                }
                field.set(obj, SerializationUtils.jsonToBean(v, field.getGenericType()));
            }
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            pst.setString(i, (String) value);
        }
    }, //

    /**
     * 带类型的序列化，支持有父类子类的情况
     * 保存格式为fastjson,可以是任何对象;;最大长度16777215字节,无需设置长度,集合类型的List,Set,Map,JSONObject,
     * JSONArray, Object等各种对象都支持 如果数据特别大根据需求预估使用
     * TEXT(正常文本),MEDIUM_TEXT(中等文本),LONG_TEXT(超大文本)
     */
    BIG_OBJECT("MEDIUMTEXT", 0) {
        @Override
        public Object getValue(DBEntity obj, Field field) throws Exception {
            if (DBEntityUtils.customPersistent(obj.getEntityType(), field)) {
                Method getter = DBEntityUtils.findGetter((Class<? extends DBEntity>) obj.getEntityType(), field);
                return getter.invoke(obj);
            }
            Object v = super.getValue(obj, field);
            return v == null ? null : StringUtils.toJsonString(v);
        }

        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            OBJECT.wrap(obj, rs, field);
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            Object value = getValue(obj, field);
            pst.setString(i, (String) value);
        }
    }, //

    /**
     * byte[];中等长度二进制文本,最大长度16777215字节,无需设置长度
     */
    MEDIUM_BLOB("MEDIUMBLOB", 0) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            BLOB.wrap(obj, rs, field);
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            BLOB.wrap(pst, obj, field, i);
        }
    },

    /**
     * byte[];超大二进制文本,最大长度4294967295字节,无需设置长度
     */
    LONG_BLOB("LONGBLOB", 0) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            BLOB.wrap(obj, rs, field);
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            BLOB.wrap(pst, obj, field, i);
        }
    },

    /**
     * any;中等长度文本,最大长度16777215字节,无需设置长度
     */
    MEDIUM_TEXT("MEDIUMTEXT", 0) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            TEXT.wrap(obj, rs, field);
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            TEXT.wrap(pst, obj, field, i);
        }
    },

    /**
     * any;超大文本,最大长度4294967295字节,无需设置长度
     */
    LONG_TEXT("LONGTEXT", 0) {
        @Override
        public void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception {
            TEXT.wrap(obj, rs, field);
        }

        @Override
        public void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception {
            TEXT.wrap(pst, obj, field, i);
        }
    },
    ;

    /**
     * 在mysql存储的数据类型
     */
    public final String dbType;

    /**
     * 在mysql存储可使用的长度
     */
    public final int len;

    /**
     * 保留几位小数,只有float,double,decimal才需要,DECIMAL(M,D) ，如果M>D，为M+2否则为D+2
     */
    public final int scale;

    private DataType(String dbType, int len, int scale) {
        this.dbType = dbType.trim();
        this.len = len;
        this.scale = scale;
    }

    private DataType(String dbType, int len) {
        this(dbType, len, 0);
    }

    /**
     * 从mysql取数据出来
     */
    public abstract void wrap(DBEntity obj, ResultSet rs, Field field) throws Exception;

    /**
     * 填充sql语句持久化到mysql
     */
    public abstract void wrap(PreparedStatement pst, DBEntity obj, Field field, int i) throws Exception;

    /**
     * 获取字段在jvm内存的值
     */
    public Object getValue(DBEntity obj, Field field) throws Exception {
        return field.get(obj);
    }

    /**
     * 是否允许为null
     */
    public boolean canBeNull(Field field) {
        return ((Persistent) AnnotationUtil.findAnnotation(field, Persistent.class)).canBeNull() && !this.isNumber() && !DBEntityUtils.isPk(field) && !DBEntityUtils.isUniqueIndex(field);
    }

    /**
     * 如果不允许为空写入表中的默认值
     */
    public Object defualtValue() {
        switch (this) {
            case BOOL:
            case BYTE:
                return (byte) 0;
            case SHORT:
                return (short) 0;
            case INT:
                return 0;
            case LONG:
                return 0L;
            case FLOAT:
                return 0f;
            case DOUBLE:
                return 0d;
            case STRING:
                return "";
            case ENUM:
                return "NULL";
            case DECIMAL:
                return BigDecimal.valueOf(0d);
            case DATE:
                return new Timestamp(DateUnit.offsetTime);
            case OBJECT:
            case OBJECT_TYPE:
            case MEDIUM_OBJ_TYPE:
            case TEXT:
            case MEDIUM_TEXT:
            case LONG_TEXT:
            case BLOB:
            case MEDIUM_BLOB:
            case LONG_BLOB:
                return null;
            default:
                throw new RuntimeException(this + "");
        }
    }

    /**
     * 是否是数字
     */
    public boolean isNumber() {
        switch (this) {
            case BYTE:
            case SHORT:
            case INT:
            case LONG:
            case FLOAT:
            case DOUBLE:
            case DECIMAL:
            case BOOL:
                return true;
            default:
                return false;
        }
    }

}
