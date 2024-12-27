
package com.ycw.core.internal.db.dao;

import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.util.function.Consumer;

/**
 * <查询器接口>
 * <p>
 * ps:
 * 自定义查询数据库接口,之所以自定义接口而不直接使用Consumer
 * <ResultSet>是为了在处理函数上抛出异常,方便调用处rs.getXXX(1)不用再去逐个捕获异常
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface Query extends Consumer<ResultSet> {

    @Override
    default void accept(ResultSet rs) {
        try {
            acquire(rs);
        } catch (Exception e) {
            LoggerFactory.getLogger(Query.class).error(e.getMessage(), e);
        }
    }

    /**
     * 获取结果集里面的内容
     */
    void acquire(ResultSet rs) throws Exception;
}
