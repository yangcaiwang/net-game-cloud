
package com.common.module.internal.db.entity;

import com.alibaba.fastjson.parser.ParserConfig;
import com.alibaba.fastjson.util.TypeUtils;
import com.common.module.util.AnnotationUtil;
import com.common.module.internal.db.annotation.*;
import com.common.module.internal.db.constant.DataType;
import com.common.module.internal.db.constant.RollType;
import com.common.module.util.ArraysUtils;
import com.common.module.util.ClassUtils;
import com.common.module.util.CollectionUtils;
import com.common.module.util.StringUtils;
import com.common.module.cluster.property.PropertyConfig;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.common.util.concurrent.AtomicDouble;
import org.apache.commons.lang3.Validate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class DBEntityUtils {

    private static final Logger log = LoggerFactory.getLogger(DBEntityUtils.class);

    private static final Map<Class<? extends DBEntity>, LinkedHashSet<Field>> fieldMap = Maps.newHashMap();// 所有字段

    private static final Map<Class<? extends DBEntity>, LinkedHashSet<Field>> keyMap = Maps.newHashMap();// 主键字段

    private static final Map<Class<? extends DBEntity>, LinkedHashSet<Field>> indexMap = Maps.newHashMap();// 索引字段

    private static final Map<Class<? extends DBEntity>, LinkedHashSet<Field>> valueMap = Maps.newHashMap();// 非主键字段

    private static final Map<Class<? extends DBEntity>, Map<Field, Method>> getterMap = Maps.newHashMap();// getter方法

    private static final Map<Class<? extends DBEntity>, Map<Field, Method>> setterMap = Maps.newHashMap();// setter方法

    /**
     * 分隔符(分表下标后缀,滚表前缀日期后缀,联合主键等),因为下划线比较常用,所以搞个比较特殊的
     */
    public static final String SPLITTER = "___";

    /**
     * 将objects( 分表下标后缀,滚表前缀日期后缀,联合主键等) 拼接成一条字符串
     *
     * @param serializables
     * @return
     */
    public static String merged(Serializable... serializables) {
        if (serializables == null || serializables.length < 1)
            throw new NullPointerException();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < serializables.length; i++) {
            Serializable serializable = serializables[i];
            sb.append(TypeUtils.castToString(serializable));
            if (i < serializables.length - 1) {
                sb.append(SPLITTER);
            }
        }
        return sb.toString();
    }

    /**
     * 把拼接的字符串还原成String[]
     *
     * @param s
     * @return
     */
    public static String[] parsed(String s) {
        if (StringUtils.isEmpty(s))
            throw new NullPointerException();
        return s.split(SPLITTER);
    }

    /**
     * 检测db实体对象申明的字段和注解的类型是否匹配,只对基本类型做检查
     *
     * @param field
     * @param type
     * @return
     */
    private static boolean match(Field field, DataType type) {

        String genericType = field.getGenericType().getTypeName();
        if (type == DataType.STRING) {
            if (!genericType.equals(String.class.getName())) {
                return false;
            }
            return true;
        } else if (type == DataType.BYTE) {
            if (!genericType.equals(Byte.class.getName()) && !genericType.equals(byte.class.getName())) {
                return false;
            }
            return true;
        } else if (type == DataType.SHORT) {
            if (!genericType.equals(Short.class.getName()) && !genericType.equals(short.class.getName())) {
                return false;
            }
            return true;
        } else if (type == DataType.INT) {
            if (!genericType.equals(Integer.class.getName()) && !genericType.equals(int.class.getName()) && !genericType.equals(AtomicInteger.class.getTypeName())) {
                return false;
            }
            return true;
        } else if (type == DataType.LONG) {
            if (!genericType.equals(Long.class.getName()) && !genericType.equals(long.class.getName()) && !genericType.equals(AtomicLong.class.getTypeName())) {
                return false;
            }
            return true;
        } else if (type == DataType.FLOAT) {
            if (!genericType.equals(Float.class.getName()) && !genericType.equals(float.class.getName()) && !genericType.equals(AtomicDouble.class.getTypeName())) {
                return false;
            }
            return true;
        } else if (type == DataType.DOUBLE) {
            if (!genericType.equals(Double.class.getName()) && !genericType.equals(double.class.getName()) && !genericType.equals(AtomicDouble.class.getTypeName())) {
                return false;
            }
            return true;
        } else if (type == DataType.DECIMAL) {
            if (!genericType.equals(BigDecimal.class.getName())) {
                return false;
            }
            return true;
        } else if (type == DataType.BOOL) {
            if (!genericType.equals(Boolean.class.getName()) && !genericType.equals(boolean.class.getName()) && !genericType.equals(AtomicBoolean.class.getTypeName())) {
                return false;
            }
            return true;
        } else if (type == DataType.DATE) {
            if (!genericType.equals(Date.class.getName())) {
                return false;
            }
            return true;
        } else if (type == DataType.BLOB) {
            if (!genericType.equals("byte[]")) {
                return false;
            }
            return true;
        } else {
            return true;
        }
    }

    private static void toTransient(Field field) throws Exception {
        // Field modifiers = field.getClass().getDeclaredField("modifiers");
        // modifiers.setAccessible(true);
        // if (!Modifier.isTransient(field.getModifiers()))
        // modifiers.setInt(field, Modifier.TRANSIENT);
    }

    /**
     * 获取字段setter方法
     */
    public static Method findSetter(Class<? extends DBEntity> cls, Field field) {

        return setterMap.computeIfAbsent(cls, v -> Maps.newHashMap()).computeIfAbsent(field, v -> _findSetter(cls, field));
    }

    private static Method _findSetter(Class<? extends DBEntity> cls, Field field) {
        Method setter = null;
        String methodName = null;
        Class<?> paramClass = null;
        try {
            Custom customize = AnnotationUtil.findAnnotation(field, Custom.class);
            if (customize != null) {
                methodName = customize.setter();
                Validate.isTrue(!StringUtils.isEmpty(methodName), "持久化字段[%s]自定义序列化(setter)方法[%s]为空", field, customize);
                paramClass = String.class;
            } else {
                methodName = ClassUtils.findSetterName(field);
                paramClass = field.getType();
            }
            setter = cls.getDeclaredMethod(methodName, paramClass);
            setter.setAccessible(true);
            return setter;
        } catch (NoSuchMethodException e) {
            if (cls == DBEntity.class) {
                throw new RuntimeException(e.getMessage() + ":" + cls + "," + field.toGenericString(), e);
            }
            cls = ((Class<? extends DBEntity>) cls.getSuperclass());
            return _findSetter(cls, field);
        }
    }

    /**
     * 获取字段getter方法
     */
    public static Method findGetter(Class<? extends DBEntity> cls, Field field) {

        return getterMap.computeIfAbsent(cls, v -> Maps.newHashMap()).computeIfAbsent(field, v -> _findGetter(cls, field));
    }

    private static Method _findGetter(Class<? extends DBEntity> cls, Field field) {
        Method geter = null;
        String methodName = null;
        try {
            Custom customize = AnnotationUtil.findAnnotation(field, Custom.class);
            if (customize != null) {
                methodName = customize.getter();
                Validate.isTrue(!StringUtils.isEmpty(methodName), "持久化字段[%s]自定义反序列化(gettter)方法[%s]为空", field, customize);
            } else {
                methodName = ClassUtils.findGetterName(field);
            }
            geter = cls.getDeclaredMethod(methodName);
            geter.setAccessible(true);
            return geter;
        } catch (NoSuchMethodException e) {
            if (cls == DBEntity.class) {
                throw new RuntimeException(e.getMessage() + ":" + cls + "," + field.toGenericString(), e);
            }
            cls = ((Class<? extends DBEntity>) cls.getSuperclass());
            return _findGetter(cls, field);
        }
    }

    /**
     * 获取对象所有字段
     */
    public static LinkedHashSet<Field> fieldSet(Class<? extends DBEntity> cls) {

        return fieldMap.computeIfAbsent(cls, v -> {
            LinkedHashSet<Field> fieldSet = Sets.newLinkedHashSet();
            if (Modifier.isFinal(cls.getModifiers())) {
                throw new RuntimeException(String.format("实体[%s]不允许final修饰", cls));
            }
            Constructor<?>[] constructors = (Constructor<?>[]) cls.getDeclaredConstructors();
            if (constructors.length != 1) {
                throw new RuntimeException(String.format("实体[%s]只允许一个public无参构造器", cls));
            }
            if (!Modifier.isPublic(constructors[0].getModifiers())) {
                throw new RuntimeException(String.format("实体[%s]只允许一个public无参构造器", cls));
            }
            Class<?>[] parameterTypes = constructors[0].getParameterTypes();
            if (parameterTypes != null && parameterTypes.length > 0) {
                throw new RuntimeException(String.format("实体[%s]只允许一个public无参构造器", cls));
            }
            List<Field> fields = Lists.newArrayList();
            Table table = AnnotationUtil.findAnnotation(cls, Table.class);// 仅仅是想用DBEntity的方法去取数据而不需要生成数据表就不检查父类字段
            ClassUtils.fieldList(table == null ? true : table.mappedSuperclass(), cls, fields);

            String[] filterColumns = table == null ? null : table.filterColumns();

            Set<String> columnNamedSet = Sets.newHashSet();
            for (Field field : fields) {
                if (Modifier.isStatic(field.getModifiers())) {
                    try {
                        toTransient(field);// 静态字段转成transient不做持久化
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage() + ":" + field.toGenericString(), e);
                    }
                    continue;// 排除静态字段
                }
                Persistent persistent = AnnotationUtil.findAnnotation(field, Persistent.class);
                if (persistent == null) {
                    try {
                        toTransient(field);// 未表名持久化的字段转成transient不做持久化
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage() + ":" + field.toGenericString(), e);
                    }
                    continue;
                }
                if (filterColumns != null && ArraysUtils.contains(filterColumns, columnName(field))) {
                    try {
                        toTransient(field);// 指定排除的字段转成transient不做持久化
                    } catch (Exception e) {
                        throw new RuntimeException(e.getMessage() + ":" + field.toGenericString(), e);
                    }
                    continue;
                }
                DataType dataType = persistent.dataType();
                if (!customPersistent(cls, field)) {
                    if (!dataType.isNumber()) {
                        if (!Modifier.isTransient(field.getModifiers())) {
                            if (!ClassUtils.isAssignableFrom(Serializable.class, field.getType())) {
                                throw new RuntimeException(String.format("字段[%s]无法序列化", field));
                            }
                        }
                    }
                }
                if (!match(field, dataType)) {
                    throw new RuntimeException(String.format("持久化字段[%s].[%s]的类型是[%s]不能设置为db类型[%s]", field, persistent, field.getGenericType(), dataType.dbType));
                }
                if (Modifier.isTransient(field.getModifiers())) {
                    throw new RuntimeException(String.format("持久化字段[%s].[%s]isTransient,但是又加上了持久化注解,你想干什么呢?", field, persistent));
                }

                isAtomicField(field, dataType);// 字段原子性检查

                Custom custom = AnnotationUtil.findAnnotation(field, Custom.class);
                if (custom != null) {
                    Method getter = ClassUtils.getMethod(false, cls, custom.getter());
                    if (getter == null)
                        throw new RuntimeException(String.format("字段[%s]自定义了序列化方法[%s],但是却没有找到该方法", field, custom.getter()));
                    if (getter.getReturnType() != String.class)
                        throw new RuntimeException(String.format("字段[%s]自定义的序列化方法[%s]返回类型是[%s],必须是String!", field, custom.getter(), getter.getReturnType()));
                    Method setter = ClassUtils.getMethod(false, cls, custom.setter(), String.class);
                    if (setter == null)
                        throw new RuntimeException(String.format("字段[%s]自定义了反序列化方法[%s],但是却没有找到该方法", field, custom.setter()));
                }
                int setLen = persistent.len();
                if (setLen != 0 && setLen != dataType.len) {
                    if (dataType != DataType.STRING && dataType != DataType.ENUM && dataType != DataType.FLOAT && dataType != DataType.DOUBLE && dataType != DataType.DECIMAL) {
                        throw new RuntimeException(String.format("字段[%s]不可以调整长度", field));
                    }
                }
                int setScale = persistent.scale();
                if (setScale != 0 && setScale != dataType.scale) {
                    if (dataType != DataType.STRING && dataType != DataType.ENUM && dataType != DataType.FLOAT && dataType != DataType.DOUBLE && dataType != DataType.DECIMAL) {
                        throw new RuntimeException(String.format("字段[%s]不可以调整小数位", field));
                    }
                }

                boolean canBeNull = persistent.canBeNull();
                if (!canBeNull) {
                    if (dataType == DataType.OBJECT //
                            || dataType == DataType.OBJECT_TYPE //
                            || dataType == DataType.MEDIUM_OBJ_TYPE
                            || dataType == DataType.TEXT //
                            || dataType == DataType.MEDIUM_TEXT//
                            || dataType == DataType.LONG_TEXT//
                            || dataType == DataType.BLOB //
                            || dataType == DataType.MEDIUM_BLOB//
                            || dataType == DataType.LONG_BLOB) {
                        throw new RuntimeException(String.format("字段[%s]必须允许为空", field));
                    }
                }
                Class<?> type = field.getType();
                // 排除非基本类型,并且是抽象类型有没有自定义序列化规则的,之所以要排除基本类型,是因为int,long等也是abstract而非interface
                String genericType = field.getGenericType().getTypeName();
                boolean isBasicType = ClassUtils.isAssignableFrom(Number.class, type) //
                        || type.isPrimitive()//
                        || type == String.class //
                        || type == char.class//
                        || type == Character.class //
                        || type.isEnum()//
                        || type == Date.class//
                        || type == BigDecimal.class//
                        || type == boolean.class //
                        || type == Boolean.class //
                        || type == AtomicBoolean.class//
                        || type == byte.class //
                        || type == Byte.class //
                        || type == short.class //
                        || type == Short.class //
                        || type == int.class//
                        || type == Integer.class//
                        || type == AtomicInteger.class//
                        || type == long.class//
                        || type == Long.class//
                        || type == AtomicLong.class//
                        || type == float.class //
                        || type == Float.class //
                        || type == double.class //
                        || type == Double.class //
                        || genericType.equals("byte[]")//
                        ;
                if (!isBasicType && !customPersistent(cls, field) && (Modifier.isInterface(type.getModifiers()) || Modifier.isAbstract(type.getModifiers()))) {
                    throw new RuntimeException(String.format("字段[%s]是抽象类型[%s],请明确化或自定义@Custom序列化", field, type));
                }
                // 排除非线程安全集合
                if (ClassUtils.isAssignableFrom(Collection.class, type) || ClassUtils.isAssignableFrom(Map.class, type)) {
                    String simpleName = type.getSimpleName();
                    if (!simpleName.contains("Concurrent") && !simpleName.contains("CopyOnWrite") && !simpleName.contains("Blocking") && !simpleName.contains("Vector")) {
                        throw new RuntimeException(String.format("字段[%s]是非线程安全集合[%s],异步持久化取值时如果有其他线程对该集合执行remove操作可能会造成并发修改异常,虽然有容错次数重试机制,但已经不允许非安全集合对象作为持久化字段", field, type));
                    }
                }
                // 排除非二进制数组
                if (type.isArray() //
                        && dataType != DataType.BLOB //
                        && dataType != DataType.MEDIUM_BLOB//
                        && dataType != DataType.LONG_BLOB) {
                    throw new RuntimeException(String.format("字段[%s]是数组类型,保存类型是[%s],请使用并发安全集合", field, dataType));
                }

                if (!columnNamedSet.add(columnName(field))) {
                    throw new RuntimeException(String.format("持久化字段[%s].[%s]的名称column[%s]重复", field, persistent, columnName(field)));
                }

                fieldSet.add(field);
            }
            return fieldSet;
        });
    }

    private static void isAtomicField(Field field, DataType dataType) {
        // TODO 对基本类型字段做并发安全检查
        // Class<?> type = field.getType();
        // if (!isPk(field) && !isUniqueIndex(field)) {
        // if (type == byte.class || type == Byte.class || type == short.class
        // || type == Short.class || type == int.class || type == Integer.class)
        // {
        // throw new RuntimeException("字段:" + field.toGenericString() + "
        // 务必使用AtomicInteger");
        // }
        // if (type == long.class || type == Long.class) {
        // throw new RuntimeException("字段:" + field.toGenericString() + "
        // 务必使用AtomicLong");
        // }
        // if (type == boolean.class || type == Boolean.class) {
        // throw new RuntimeException("字段:" + field.toGenericString() + "
        // 务必使用AtomicBoolean");
        // }
        // if (type == float.class || type == Float.class || type ==
        // double.class || type == Double.class) {
        // throw new RuntimeException("字段:" + field.toGenericString() + "
        // 务必使用AtomicDouble");
        // }
        // }
    }

    /**
     * 获取对象所有主键字段
     */
    public static LinkedHashSet<Field> keySet(Class<? extends DBEntity> cls) {

        return keyMap.computeIfAbsent(cls, v -> Sets.newLinkedHashSet(fieldSet(cls).stream().filter(field -> AnnotationUtil.isAnnotation(field, PK.class)).collect(Collectors.toList())));
    }

    /**
     * 获取对象所有索引字段
     */
    public static LinkedHashSet<Field> indexSet(Class<? extends DBEntity> cls) {

        return indexMap.computeIfAbsent(cls, v -> Sets.newLinkedHashSet(fieldSet(cls).stream().filter(field -> AnnotationUtil.isAnnotation(field, Index.class)).collect(Collectors.toList())));
    }

    /**
     * 所有的索引
     *
     * @param cls
     * @return
     */
    public static Map<String, List<Field>> indexsNamedMap(Class<? extends DBEntity> cls) {

        LinkedHashSet<Field> indexSet = indexSet(cls);
        Map<String, List<Field>> indexMap = indexSet.stream().collect(Collectors.groupingBy(field -> indexName(field), Collectors.toList()));
        for (String indexName : indexMap.keySet()) {
            List<Field> indexFields = indexMap.get(indexName);
            if (indexFields.size() > 1) {
                Set<Index> indexs = Sets.newHashSet();
                indexFields.forEach(field -> indexs.add(AnnotationUtil.findAnnotation(field, Index.class)));
                if (indexs.size() > 1) {
                    throw new RuntimeException(String.format("实体[%s]的联合索引[%s]不一致", cls, indexName));
                }
            }
        }
        return indexMap;
    }

    /**
     * 唯一索引
     *
     * @param cls
     * @return
     */
//    public static Map<String, List<Field>> uniqueIndexsNamedMap(Class<? extends DBEntity> cls) {
//
//	LinkedHashSet<Field> indexSet = Sets.newLinkedHashSet(indexSet(cls));
//	indexSet.removeIf(field -> {
//	    Index index = AnnotationFinder.findAnnotation(field, Index.class);
//	    return !index.way().equals(Index.INDEX_TYPE_UNIQUE);
//	});
//
//	Map<String, List<Field>> indexMap = indexSet.stream().collect(Collectors.groupingBy(field -> indexName(field), Collectors.toList()));
//	for (String indexName : indexMap.keySet()) {
//	    List<Field> indexFields = indexMap.get(indexName);
//	    if (indexFields.size() > 1) {
//		Set<Index> indexs = Sets.newHashSet();
//		indexFields.forEach(field -> indexs.add(AnnotationFinder.findAnnotation(field, Index.class)));
//		if (indexs.size() > 1) {
//		    throw new RuntimeException(String.format("实体[%s]的联合索引[%s]不一致", cls, indexName));
//		}
//	    }
//	}
//	return indexMap;
//    }

    /**
     * 获取对象所有非主键字段
     */
    public static LinkedHashSet<Field> valueSet(Class<? extends DBEntity> cls) {

        return valueMap.computeIfAbsent(cls, v -> Sets.newLinkedHashSet(fieldSet(cls).stream().filter(field -> !AnnotationUtil.isAnnotation(field, PK.class)).collect(Collectors.toList())));
    }

    public static <E extends DBEntity> Object getValue(E obj, Field field) {
        try {
            return ((Persistent) AnnotationUtil.findAnnotation(field, Persistent.class)).dataType().getValue(obj, field);
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + field.toGenericString() + "," + obj, e);
        }
    }

    /**
     * 获取对象所有数据
     */
    public static <E extends DBEntity> LinkedHashMap<Field, Object> objMap(E obj) {

        LinkedHashMap<Field, Object> result = Maps.newLinkedHashMap();
        LinkedHashSet<Field> allFields = fieldSet((Class<? extends DBEntity>) obj.getEntityType());
        for (Field field : allFields) {
            try {
                result.put(field, getValue(obj, field));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage() + ":" + obj, e);
            }
        }
        return result;
    }

    /**
     * 获取对象所有主键数据
     */
    public static <E extends DBEntity> LinkedHashMap<Field, Object> keyMap(E obj) {

        LinkedHashMap<Field, Object> result = Maps.newLinkedHashMap();
        LinkedHashSet<Field> keyOfFields = keySet((Class<? extends DBEntity>) obj.getEntityType());
        for (Field field : keyOfFields) {
            try {
                result.put(field, getValue(obj, field));
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage() + ":+obj", e);
            }
        }
        return result;
    }

    /**
     * 获取对象所有索引数据
     */
    public static <E extends DBEntity> LinkedHashMap<Field, Object> indexMap(E obj) {

        LinkedHashMap<Field, Object> result = Maps.newLinkedHashMap();
        LinkedHashSet<Field> indexOfFields = indexSet((Class<? extends DBEntity>) obj.getEntityType());
        for (Field field : indexOfFields) {
            result.put(field, getValue(obj, field));
        }
        return result;
    }

    /** 获取对象所有非主键字段的数据 */
//    public static <E extends DBEntity> LinkedHashMap<Field, Object> valueMap(E obj) {
//
//	LinkedHashMap<Field, Object> result = Maps.newLinkedHashMap();
//	LinkedHashSet<Field> valueOfFields = valueSet((Class<? extends DBEntity>) obj.getEntityType());
//	for (Field field : valueOfFields) {
//	    try {
//		result.put(field, getValue(obj, field));
//	    } catch (Exception e) {
//		throw new RuntimeException(e.getMessage() + ":" + obj, e);
//	    }
//	}
//	return result;
//    }

    // ---------------------------------------------------------------------------------------------------------------

    /**
     * 简洁表名-必须小写
     */
    public static String simpleTableName(Class<? extends DBEntity> entityType) {

        return _simpleTableName(entityType);
    }

    /**
     * 是否允许删除条目，默认为true，如果某些数据比如player实体不允许删除，修改注解为false
     *
     * @param entityType
     * @return
     */
    public static boolean deleteable(Class<? extends DBEntity> entityType) {
        Table table = AnnotationUtil.findAnnotation(entityType, Table.class);
        return table.deleteable();
    }

    public static boolean isLogger(Class<? extends DBEntity> entityType) {
        return ClassUtils.isAssignableFrom(LoggerEntity.class, entityType);
    }

    private static String _simpleTableName(Class<? extends DBEntity> entityType) {
        Table table = AnnotationUtil.findAnnotation(entityType, Table.class);
        if (table == null)
            throw new RuntimeException(String.format("class[%s]没有配置数据表名,请加上注解[%s]", entityType, Table.class));
        String tabName = table.name();
        if (tabName == null || tabName.isEmpty()) {
            tabName = "t_" + entityType.getSimpleName();
        }
        if (tabName.contains(RollType.PREFIX)) {
            throw new RuntimeException(String.format("class[%s]配置数据表名[%s]格式有误,不允许出现字符[%s]", entityType, tabName, RollType.PREFIX));
        }
        if (StringUtils.stringContainIt(tabName, StringUtils.Type.TYPE_CHINESE)) {
            throw new RuntimeException(String.format("class[%s]配置数据表名[%s]格式有误,不允许出现中文", entityType, tabName));
        }
        if (tabName.length() > DB.MySQL_NAME_MAX_LEN) {
            throw new RuntimeException(String.format("class[%s]配置数据表名[%s]过长", entityType, tabName));
        }
        return tabName.toLowerCase();
    }

    /**
     * 分表数量
     */
    public static int tableNum(Class<? extends DBEntity> entityType) {

        return ((Table) AnnotationUtil.findAnnotation(entityType, Table.class)).num();
    }

    /**
     * 分表后所有的表
     */
    public static Set<String> tables(Class<? extends DBEntity> entityType) {
        Set<String> tabs = Sets.newHashSet();
        String simpleTab = simpleTableName(entityType);
        int tabNum = tableNum(entityType);
        for (int i = 1; i <= tabNum; i++) {
            tabs.add(merged(simpleTab, i));
        }
        return tabs;
    }

    /**
     * 根据实体在内存的key计算出在哪个表
     *
     * @param obj 实体对象
     * @return
     */
    public static <E extends DBEntity> String getTableName(E obj) {

        return getTableName(obj.getEntityType(), obj.getPks());
    }

    /**
     * 根据实体在内存的key计算出在哪个表
     *
     * @param entityType
     * @param pks        主键转成的string作为内存的key
     * @return
     */
    public static <E extends DBEntity> String getTableName(Class<E> entityType, Serializable... pks) {
        Validate.isTrue(pks != null && pks.length > 0);
        int tabNum = tableNum(entityType);
        if (tabNum > 1) {
            int index = 0;
            if (pks.length == 1) {// 单一数字主键
                try {
                    index = ((int) (TypeUtils.castToLong(pks[0]) % tabNum)) + 1;
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage() + ":" + entityType + "," + StringUtils.toString(pks), e);
                }
            } else {
                throw new RuntimeException("联合主键不允许分表,无法使用联合主键的值永恒地计算出真实表名,不同环境下可能不一样,造成数据落地位置不同:" + entityType);
            }
            return merged(simpleTableName(entityType), index);
        }
        if (needRoll(entityType))
            return RollType.fixedTableName(simpleTableName(entityType), rollType(entityType));
        return simpleTableName(entityType);
    }

    /**
     * 数据表的滚动类型
     */
    public static RollType rollType(Class<? extends DBEntity> entityType) {

        return ((Table) AnnotationUtil.findAnnotation(entityType, Table.class)).rollType();
    }

    /**
     * 是否需要滚表
     */
    public static boolean needRoll(Class<? extends DBEntity> entityType) {

        return rollType(entityType) != RollType.NONE;
    }

    /**
     * 缓存配置
     *
     * @param entityType
     * @return
     */
    public static Cached cache(Class<? extends DBEntity> entityType) {

        Table table = AnnotationUtil.findAnnotation(entityType, Table.class);
        return table.cached();
    }

    /**
     * 只读字段,插入后便不再更新
     *
     * @param field
     * @return
     */
    public static boolean isReadOnly(Field field) {

        return isPk(field) || isUniqueIndex(field);
    }

    /**
     * 是否是主键字段
     *
     * @param field
     * @return
     */
    public static boolean isPk(Field field) {

        return AnnotationUtil.isAnnotation(field, PK.class);
    }

    /**
     * 主键是否自增
     *
     * @param clz
     * @return
     */
    public static boolean autoPk(Class<? extends DBEntity> clz) {
        LinkedHashSet<Field> pkSet = keySet(clz);
        if (pkSet.size() == 1) {
            Field pkField = CollectionUtils.peekFirst(pkSet);
            String pkColumn = DBEntityUtils.columnName(pkField);
            Persistent pkPersistent = ((Persistent) AnnotationUtil.findAnnotation(pkField, Persistent.class));
            DataType pkDataType = pkPersistent.dataType();
            if (pkDataType == DataType.INT || pkDataType == DataType.LONG) {
                return ((PK) AnnotationUtil.findAnnotation(ClassUtils.getField(true, clz, pkColumn), PK.class)).auto();
            }
        }
        return false;
    }

    /**
     * 主键个数
     *
     * @param cls
     * @return
     */
    public static int pkNum(Class<? extends DBEntity> cls) {

        return keySet(cls).size();
    }

    /**
     * 索引个数
     *
     * @param cls
     * @param index
     * @return
     */
    public static int indexNum(Class<? extends DBEntity> cls, Field index) {

        return (int) indexSet(cls).stream().filter(field -> field.equals(index)).count();
    }

    /**
     * 是否唯一索引字段
     *
     * @param field
     * @return
     */
    public static boolean isUniqueIndex(Field field) {

        Index index = AnnotationUtil.findAnnotation(field, Index.class);
        return index != null && index.type().equals(Index.INDEX_TYPE_UNIQUE);
    }

    /**
     * 是否索引字段
     *
     * @param field
     * @return
     */
    public static boolean isIndex(Field field) {

        return AnnotationUtil.isAnnotation(field, Index.class);
    }

    /**
     * 获取字段的名称
     *
     * @param field
     * @return
     */
    public static String columnName(Field field) {

        return _columnName(field);
    }

    private static String _columnName(Field field) {

        Persistent persistent = AnnotationUtil.findAnnotation(field, Persistent.class);
        if (persistent == null)
            throw new RuntimeException("字段没有标记持久化:" + field);
        String name = persistent.name();
        if (name == null || name.isEmpty()) {
            name = field.getName();
        }
        if (StringUtils.stringContainIt(name, StringUtils.Type.TYPE_CHINESE)) {
            throw new RuntimeException(String.format("字段[%s]列名[%s]不允许出现中文", field, name));
        }
        if (name.length() > DB.MySQL_NAME_MAX_LEN) {
            throw new RuntimeException(String.format("字段[%s]列名[%s]过长", field, name));
        }
        return name.toLowerCase();
    }

    /**
     * 获取数据表初始id偏移量</br>
     *
     * @param entityType 数据表所在的class类型</br>
     *                   不使用数据库自带自增规则,自定义ID生成器创建id起始值的偏移量,</br>
     *                   避免出现,不同类型数据id相同导致某些模块出问题</br>
     *                   比如,战斗成员(玩家id)和助战成员(助战id)所属的类型不同,但是id相同在战斗中通过唯一id标识无法取到想要的对象
     *                   </br>
     * @return</br>
     */
    static int tableOffset(Class<? extends DBEntity> entityType) {

        return ((Table) AnnotationUtil.findAnnotation(entityType, Table.class)).offset();
    }

    static boolean reuse(Class<? extends DBEntity> entityType) {

        return ((Table) AnnotationUtil.findAnnotation(entityType, Table.class)).reuse();
    }

    /**
     * 获取索引的名称
     *
     * @param field
     * @return
     */
    public static String indexName(Field field) {

        return _indexName(field);
    }

    private static String _indexName(Field field) {

        Index index = AnnotationUtil.findAnnotation(field, Index.class);
        if (index == null)
            throw new RuntimeException("字段不存在索引:" + field);
        String name = index.name();
        if (name == null || name.isEmpty()) {
            name = "idx_" + columnName(field);
        }

        if (StringUtils.stringContainIt(name, StringUtils.Type.TYPE_CHINESE)) {
            throw new RuntimeException(String.format("索引[%s]名称[%s]不允许出现中文", field, name));
        }
        if (name.length() > DB.MySQL_NAME_MAX_LEN) {
            throw new RuntimeException(String.format("索引[%s]名称[%s]过长", field, name));
        }

        return name.toLowerCase();
    }

    /**
     * 字段是否需要自定义持久化
     *
     * @param field
     * @return
     */
    public static boolean customPersistent(Class<? extends DBEntity> objCls, Field field) {

        return AnnotationUtil.isAnnotation(field, Custom.class) && findSetter(objCls, field) != null && findGetter(objCls, field) != null;
    }

    /**
     * 构造一个实体
     *
     * @param predicate  创建实体时执行的初始化过程
     * @param entityType 实体类型
     * @param pks      主键,可以是联合主键,但是主键顺序不能错
     * @return
     */
    public static <E extends DBEntity> E newEntityInstance(Predicate<E> predicate, Class<E> entityType, Serializable... pks) {
        E entity = null;
        try {
            entity = ((E) entityType.newInstance());
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + entityType + "," + StringUtils.toString(pks), e);
        }
        if (pks != null && pks.length > 0) {
            checkPks(entityType, pks);
            LinkedHashSet<Field> keysField = keySet(entityType);
            int i = 0;
            for (Field field : keysField) {
                Object value = pks[i++];
                try {
                    field.set(entity, value);
                } catch (Exception e) {
                    throw new RuntimeException(e.getMessage() + ":" + field.toGenericString() + "," + value, e);
                }
            }
        }
        if (predicate != null) {
            try {
                if (!predicate.test(entity)) {
                    throw new RuntimeException(entityType + "," + StringUtils.toString(pks));
                }
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage() + ":" + entityType + "," + StringUtils.toString(pks), e);
            }
        }
        return entity;
    }

    public static <E extends DBEntity> E newEntityInstance(Class<E> entityType, Serializable... pks) {
        return newEntityInstance(null, entityType, pks);
    }

    static <E extends DBEntity> Serializable[] parsed2Pks(E obj) {
        LinkedHashMap<Field, Object> keyMap = keyMap(obj);
        Serializable[] pk = new Serializable[keyMap.size()];
        Iterator<Object> iterator = keyMap.values().iterator();
        int i = 0;
        while (iterator.hasNext()) {
            pk[i++] = (Serializable) iterator.next();
        }
        return pk;
    }

    /**
     * 通过通用的分隔符连接主键在内存的值(String)
     *
     * @param pks
     * @return
     */
    public static String mergedKey(Serializable... pks) {
        return merged(pks);
    }

    /**
     * 通过通用的分隔符还原主键
     *
     * @param entityType
     * @param mergedKey
     * @return
     */
    public static Serializable[] parsed2Pks(Class<? extends DBEntity> entityType, String mergedKey) {
        try {
            LinkedHashSet<Field> keySet = keySet(entityType);
            String[] ss = parsed(mergedKey);
            if (ss.length != keySet.size()) {
                throw new RuntimeException(String.format("{}主键个数与解析key{}得到的数组{}不一致", entityType, mergedKey, StringUtils.toString(ss)));
            }
            Serializable[] pks = new Serializable[ss.length];
            int i = 0;
            for (Field field : keySet) {
                Class<?> type = field.getType();
                pks[i] = (Serializable) TypeUtils.cast(ss[i], type, ParserConfig.getGlobalInstance());
                i++;
            }
            return pks;
        } catch (Exception e) {
            throw new RuntimeException(e.getMessage() + ":" + entityType + "," + mergedKey, e);
        }
    }

    /**
     * 检查主键顺序和类型是否正确
     *
     * @param pks
     */
    static void checkPks(Class<? extends DBEntity> entityType, Serializable... pks) {
        if (PropertyConfig.isDebug()) {
            Objects.requireNonNull(entityType);
            Objects.requireNonNull(pks);
            if (pks.length < 1)
                throw new RuntimeException("请务必传入主键:" + entityType);
            List<Field> keyList = Lists.newArrayList(DBEntityUtils.keySet(entityType));
            if (keyList.size() != pks.length)
                throw new RuntimeException(String.format("主键格式错误,@PK和Serializable... pks主键个数不一致:cls=[%s],@PK=[%s]!=Serializable... pks=[%s]", entityType, keyList.size(), pks.length));
            for (int i = 0; i < pks.length; i++) {
                Serializable pk = pks[i];
                Field field = keyList.get(i);
                Class<?> paramCls = pk.getClass();
                Class<?> selfCls = field.getType();
                if (!isSameClass(paramCls, selfCls))
                    throw new RuntimeException(String.format("[%s]第[%s]个主键数据类型错误:[%s]!=[%s]", entityType, (i + 1), paramCls, selfCls));
            }
            if (keyList.size() == 1) {
                Field field = CollectionUtils.peekFirst(keyList);
                PK pkAnno = ((PK) AnnotationUtil.findAnnotation(field, PK.class));
                if (!pkAnno.auto() && pkAnno.needCheckLength()) {
                    Persistent persistent = ((Persistent) AnnotationUtil.findAnnotation(field, Persistent.class));
                    DataType dataType = persistent.dataType();
                    JdbcRepository<? extends DBEntity> jdbcRepository = (JdbcRepository<? extends DBEntity>) Repositories.getRepository(entityType);
                    if (jdbcRepository.tableNum == 1 && !jdbcRepository.isLogger && dataType == DataType.LONG) {
                        try {
                            Long pk = TypeUtils.castToLong(pks[0]);
                            if (String.valueOf(pk).length() != 13)
                                throw new RuntimeException("非分表/日志单一long型主键必须是13位 :" + entityType + "," + field + "," + pks[0]);
                        } catch (Exception e) {
                            throw new RuntimeException(e.getMessage() + ":" + entityType + "," + StringUtils.toString(pks), e);
                        }
                    }
                }
            }
        }
    }

    /**
     * 检查数据类型是否一样
     *
     * @param cls1
     * @param cls2
     * @return
     */
    private static boolean isSameClass(Class<?> cls1, Class<?> cls2) {

        if (cls1 == cls2) {
            return true;
        }
        if ((cls1 == Byte.class && cls2 == byte.class) || (cls2 == Byte.class && cls1 == byte.class)) {
            return true;
        }
        if ((cls1 == Short.class && cls2 == short.class) || (cls2 == Short.class && cls1 == short.class)) {
            return true;
        }
        if ((cls1 == Integer.class && cls2 == int.class) || (cls2 == Integer.class && cls1 == int.class)) {
            return true;
        }
        if ((cls1 == Long.class && cls2 == long.class) || (cls2 == Long.class && cls1 == long.class)) {
            return true;
        }
        if ((cls1 == Double.class && cls2 == double.class) || (cls2 == Double.class && cls1 == double.class)) {
            return true;
        }
        if ((cls1 == Float.class && cls2 == float.class) || (cls2 == Float.class && cls1 == float.class)) {
            return true;
        }
        if ((cls1 == Boolean.class && cls2 == boolean.class) || (cls2 == Boolean.class && cls1 == boolean.class)) {
            return true;
        }
        if ((cls1 == Character.class && cls2 == char.class) || (cls2 == Character.class && cls1 == char.class)) {
            return true;
        }
        return false;
    }

}
