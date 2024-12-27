
package com.ycw.core.internal.db.entity;

import com.ycw.core.internal.db.annotation.Condition;
import com.ycw.core.internal.db.annotation.Index;
import com.ycw.core.internal.db.annotation.Persistent;
import com.ycw.core.internal.db.constant.DataType;
import com.ycw.core.internal.db.constant.QueryCondition;
import com.ycw.core.util.CollectionUtils;
import com.ycw.core.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.Closeable;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * <db实体抽象类>
 * <p>
 * ps:
 * 持久化对象超类,如果要转二进制,必须确保所有的非transient修饰字段都是Serializable
 * 如果需要在多个进程间传递并且不同版本要相互兼容 , 请务必实现serialVersionUID
 * 要获取class类型,务必使用getEntityType(),一定不要使用getClass()
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
abstract public class DBEntity implements Serializable, Cloneable, Closeable, JVMable, MySQLable, Comparable<DBEntity> {

    transient protected final Logger log = LoggerFactory.getLogger(getClass());

    @Condition(condition = QueryCondition.range)
    @Persistent(name = "first_create_time", dataType = DataType.DATE, comment = "创建时间")
    @Index(name = "idx_first_create_time")
    private Date firstCreateTime;

    @Persistent(name = "extra", dataType = DataType.OBJECT, comment = "FastJson格式存储的扩展信息")
    private ConcurrentHashMap<String, String> extra;

    private final AtomicReference<MySQLable.Options> options = new AtomicReference<>(MySQLable.Options.NONE);// 增删改查等状态,默认无状态
    private final AtomicReference<Serializable[]> pks = new AtomicReference<>(null);// 主键数组
    private final AtomicReference<String> key = new AtomicReference<>(null);// 主键拼接成的字符串
    private final Class<? extends DBEntity> entityType;

    public DBEntity() {
        super();
        entityType = getClass();
    }

    private ConcurrentHashMap<String, String> extra() {
        if (extra == null) {
            synchronized (this) {
                if (extra == null) {
                    extra = new ConcurrentHashMap<>();
                }
            }
        }
        return extra;
    }

    /**
     * 持久化 class 实体类型,如果使用了代理就不是getClass(),切忌不要直接调用getClass()
     */
    public <E extends DBEntity> Class<E> getEntityType() {
        return (Class<E>) entityType;
    }

    @Override
    public Serializable[] getPks() {
        try {
            if (Objects.isNull(pks.get()))
                pks.compareAndSet(null, DBEntityUtils.parsed2Pks(this));
            return pks.get();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public String getKey() {
        try {
            if (Objects.isNull(key.get()))
                key.compareAndSet(null, DBEntityUtils.mergedKey(getPks()));
            return key.get();
        } catch (Exception e) {
            return null;
        }
    }

    public void delKey() {
        key.set(null);
    }

    /**
     * 获取全部扩展自定义信息
     */
    public Map<String, String> getUnmodifiableExtra() {
        return CollectionUtils.unmodifiableMap(extra());
    }

    AtomicReference<MySQLable.Options> atomicReferenceOptions() {
        return options;
    }

    MySQLable.Options options() {
        return atomicReferenceOptions().get();
    }

    /**
     * 重置扩展自定义信息
     */
    public void setExtra(Map<String, String> m) {
        Objects.requireNonNull(m);
        this.extra = new ConcurrentHashMap<>(m);
    }

    /**
     * 是否存在自定义信息-key
     */
    public boolean hasExtraKey(String key) {
        return extra().containsKey(key);
    }

    /**
     * 是否存在自定义信息-value
     */
    public boolean hasExtraValue(String key) {
        return extra().containsValue(key);
    }

    /**
     * 设置自定义扩展信息
     */
    public String addExtraProperty(String key, String value) {
        return extra().put(key, value);
    }

    /**
     * 如果不存在才设置自定义扩展信息
     */
    public String computeIfAbsentExtraProperty(String key, Function<String, String> mappingFunction) {
        return extra().computeIfAbsent(key, mappingFunction);
    }

    /**
     * 移除自定义信息
     */
    public void removeExtraProperty(Predicate<Entry<String, String>> condition) {
        extra().entrySet().removeIf(condition);
    }

    /**
     * 移除自定义信息
     */
    public String removeExtraProperty(String key) {
        return extra().remove(key);
    }

    /**
     * 移除自定义信息
     */
    public boolean removeExtraProperty(String key, String value) {
        return extra().remove(key, value);
    }

    /**
     * 获取自定义扩展信息
     */
    public String getExtraProperty(String key) {
        return getExtraProperty(key, null);
    }

    /**
     * 获取自定义扩展信息
     */
    public String getExtraProperty(String key, String defaultValue) {
        return extra().getOrDefault(key, defaultValue);
    }

    /**
     * 是否有效实体-未被删除即有效
     */
    boolean valid() {
        return options.get() != MySQLable.Options.DELETE;
    }

    /**
     * 标记对象需要插入数据库
     */
    void insert() {
        if (valid())
            setOptions(MySQLable.Options.INSERT);
    }

    void trySave() {
        if (valid() && options.get() != MySQLable.Options.INSERT) {
            setOptions(MySQLable.Options.UPDATE);
        }
    }

    /**
     * 手动标记实体对象需要更新到数据库,发生变更必须调用
     */
    public void update() {
        trySave();
    }

    /**
     * 标记对象将要被删除,再次获取会得到null,但是不会立刻删除
     */
    public void delete() {
        if (!DBEntityUtils.deleteable(entityType)) {
            throw new RuntimeException(entityType.getName() + " 不允許刪除條目 ：" + this);
        }
        if (valid())
            setOptions(MySQLable.Options.DELETE);
    }

    /**
     * 对象恢复自由
     */
    void freedom() {
        if (valid())
            setOptions(MySQLable.Options.NONE);
    }

    void setOptions(MySQLable.Options opt) {
        if (opt != null && opt != options.get()) {
            if (options.compareAndSet(options.get(), opt)) {
                if (options.get() != MySQLable.Options.NONE) {
                    IRepository<? extends DBEntity> repository = Repositories.getRepository(entityType);
                    if (repository instanceof DelayCachedRepository) {
                        DelayCachedRepository<DBEntity> delayCachedRepository = (DelayCachedRepository<DBEntity>) repository;
                        delayCachedRepository.commitSave(this);
                    }

//					EventBusesImpl.getInstance().syncPublish(new MySQLableOptionsChangedEvent<>(this));
                }
            }
        }
    }

    public void setOptionsNoNotify(MySQLable.Options opt) {
        if (opt != null && opt != options.get()) {
            options.compareAndSet(options.get(), opt);
        }
    }

    /**
     * 实体首次创建日期
     */
    public Date getFirstCreateTime() {
        return firstCreateTime;
    }

    /**
     * 设置实体首次创建日期
     */
    public void setFirstCreateTime(Date d) {
        if (this.firstCreateTime == null) {
            this.firstCreateTime = d;
        }
    }

    @Override
    public String toString() {
        return StringUtils.toString(this);
    }

    /**
     * 如果开启弱引用,那么需要在finalize里做立即保存操作
     */
//	@Override
//	protected void finalize() throws Throwable {
//		super.finalize();
//	}
    @Override
    public void close() throws IOException {

    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }

    @Override
    public int compareTo(DBEntity o) {
        return 0;
    }

    /**
     * 合服玩家id字段名
     */
    public abstract String mergeRoleKey();
}
