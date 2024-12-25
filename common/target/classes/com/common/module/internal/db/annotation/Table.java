
package com.common.module.internal.db.annotation;

import com.common.module.internal.db.constant.RollType;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Inherited
@Documented
public @interface Table {

    /**
     * 表名，如果不写，默认使用当前t_class.getSimpleName()，必须小写
     */
    String name() default "";

    /**
     * 是否继承父类的字段
     */
    boolean mappedSuperclass() default true;

    /**
     * 排除一些指定的字段不要,这里的名称是指在mysql-table-column的名称
     */
    String[] filterColumns() default {};

    /**
     * 是否允许删除条目，默认为true，如果某些数据比如player实体不允许删除，修改注解为false
     */
    boolean deleteable() default true;

    /**
     * 分表数量,与rollType()滚表策略不可以同时使用,如果表数量大于1,那么主键不能使用默认生成器,可以重写数据仓库的makePk()</br>
     * 方法或者由统一的生成器创建</br>
     * int tableIndex = id%num+1</br>
     * tableIndex 从1开始,所以取模得到的如果是0那么数据会落在table_1</br>
     */
    int num() default 1;

    /**
     * 重用,这里指的是主键重用,并且必须是数字型唯一主键,联合主键/（滚表/分表）策略不允许重用
     */
    boolean reuse() default false;

    /**
     * 滚表类型,与num()分表策略不可以同时使用,通常日志表会指定滚动类型，默认不滚表，根据指定的方式滚表 @see {@link RollType}
     */
    RollType rollType() default RollType.NONE;

    /**
     * 1~99</br>
     * 不使用数据库自带自增规则,自定义ID生成器创建id起始值的偏移量,</br>
     * 避免出现,不同类型数据id相同导致某些模块出问题</br>
     * 比如,战斗成员(玩家id)和助战成员(助战id)所属的类型不同,但是id相同在战斗中通过唯一id标识无法取到想要的对象</br>
     */
    int offset() default 0;

    /**
     * 简单描述
     */
    String comment();

    /**
     * 预留可删除字段的接口，避免无法删除的字段，导致合服困难
     */
    boolean delField() default false;

    /**
     * 预留可修改字段的接口，避免无法修改的字段，导致合服困难
     */
    boolean modField() default false;

    /**
     * 缓存设置
     */
    Cached cached() default @Cached();
}
