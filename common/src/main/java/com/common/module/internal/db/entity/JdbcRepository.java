
package com.common.module.internal.db.entity;

import com.common.module.util.AnnotationUtil;
import com.common.module.internal.cache.redission.RedissonClient;
import com.common.module.internal.db.Mysql;
import com.common.module.internal.db.annotation.PK;
import com.common.module.internal.db.annotation.Persistent;
import com.common.module.internal.db.constant.DataType;
import com.common.module.internal.db.constant.RollType;
import com.common.module.internal.db.dao.DaoImpl;
import com.common.module.util.ClassUtils;
import com.common.module.util.CollectionUtils;
import com.common.module.util.StringUtils;
import com.common.module.internal.random.ProbabilityUtils;
import com.common.module.internal.loader.service.AbstractService;
import com.google.common.collect.Sets;
import org.redisson.api.RAtomicLong;

import java.lang.reflect.Field;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JDBC数据仓库
 *
 * @param <E>
 */
abstract public class JdbcRepository<E extends DBEntity> extends AbstractService implements IRepository<E> {

	/** 实体类型 */
	protected final Class<E> entityType;

	/** 数据库别名 */
	protected String aliasName;

	/** 简洁的数据表名称,如果设置了滚表或者分表,不包含滚表的前缀和后缀,不包含分表的后缀 */
	protected String simpleTableName;

	/** 是否日志 */
	protected boolean isLogger;

	/** 分表数量 */
	protected int tableNum;

	/** 滚表类型 */
	protected RollType rollType;

	/** 数据表生成新数据时的偏移量 */
	protected int tableOffset;

	/** 是否重用主键 */
	protected boolean reuse;

	protected LinkedHashSet<Field> pkSet;

	/** 当前数据库最大的id数值,仅仅对单个数字型主键有效,只有使用非自增主键才有用 */
	AtomicLong maxPk;

	static Set<Integer> tableOffsetSet = Sets.newConcurrentHashSet();

	public JdbcRepository() {
		super();
		entityType = (Class<E>) ClassUtils.getTypeArguments(getClass())[0];
		if (Mysql.getInstance().containsAliasName(entityType)) {
			aliasName = Mysql.getInstance().aliasName(entityType);
			isLogger = DBEntityUtils.isLogger(entityType);
			simpleTableName = DBEntityUtils.simpleTableName(entityType);
			tableNum = DBEntityUtils.tableNum(entityType);
			rollType = DBEntityUtils.rollType(entityType);
			tableOffset = DBEntityUtils.tableOffset(entityType);
			reuse = DBEntityUtils.reuse(entityType);

			if (tableOffset > 0 && !tableOffsetSet.add(tableOffset))
				throw new RuntimeException("数据偏移量@Table(offset=)重复:" + entityType);

			if (tableNum > 1 && DBEntityUtils.pkNum(entityType) > 1) {
				throw new RuntimeException("分表策略只能使用单一long型主键:" + entityType);
			}

			if (tableNum > 1 && rollType != RollType.NONE)
				throw new RuntimeException("滚表和分表策略不能同时存在:" + entityType);

			if (reuse && rollType != RollType.NONE)
				throw new RuntimeException("滚表策略主键不允许重用:" + entityType);

			if (reuse && tableNum > 1)
				throw new RuntimeException("分表策略主键不允许重用:" + entityType);

			Mysql.getInstance().fixTable(entityType);
			pkSet = DBEntityUtils.keySet(entityType);
			if (reuse && pkSet.size() > 1)
				throw new RuntimeException("联合主键不允许重用:" + entityType);

			if (pkSet.size() == 1) {// 单一主键
				Field pkField = CollectionUtils.peekFirst(pkSet);
				String pkColumn = DBEntityUtils.columnName(pkField);
				Persistent pkPersistent = ((Persistent) AnnotationUtil.findAnnotation(pkField, Persistent.class));
				DataType pkDataType = pkPersistent.dataType();
				if (reuse && pkDataType != DataType.LONG && pkDataType != DataType.INT)
					throw new RuntimeException("重用主键必须是单一数字(int or bigint)主键:" + entityType + "," + pkField);
				if (tableNum > 1 && pkDataType != DataType.LONG && pkDataType != DataType.INT)
					throw new RuntimeException("分表策略主键必须是单一数字(int or bigint)主键:" + entityType + "," + pkField);
				if (tableNum == 1) {// 没有分表并且非日志表并且主键是单一数字型,查出最大值最为自增id起始值
					if (!isLogger) {
						if (!((PK) AnnotationUtil.findAnnotation(pkField, PK.class)).auto()) {
							// 要想在内存中自增来创建数字主键必须是int or long 才够用
							if ((pkDataType == DataType.INT || pkDataType == DataType.LONG)) {
								try {
									maxPk = new AtomicLong(0);
									long maxNumberId = maxColumn(pkColumn);
									if (maxNumberId > 0)
										maxPk.set(maxNumberId);
									else
										maxPk.set(createPk());
								} catch (Exception e) {
									throw new RuntimeException(e.getMessage(), e);
								}
							}
						}
					}
				}
			}
			log.debug("Jdbc数据仓库创建成功:\nentityType[{}],\ndb[{}],\nsimpleTable[{}],\nislogger[{}],\ntableNum[{}],\nrollType[{}],\noffset[{}],\nreuse[{}],\npks[{}],\nmaxPk[{}],\n", entityType, aliasName, simpleTableName, isLogger, tableNum, rollType, tableOffset, reuse, pkSet, maxPk);
			Repositories.addRepository(entityType, this);
		}
	}

	/**
	 * 清空table的数据,并且截断数据表
	 */
	protected void clearAll() {
		if (DBEntityUtils.needRoll(entityType) || tableNum > 1)
			throw new RuntimeException("can not truncate table : " + entityType);
		DaoImpl.getInstance().truncateTable(aliasName, simpleTableName);
		resetPk();
	}

	protected void clearMulAll() {
		if (tableNum > 1) {
			Set<String> tables = tables();
			for (String tb : tables) {
				DaoImpl.getInstance().truncateTable(aliasName, tb);
			}
		}
		resetPk();
	}

	@Override
	public Class<E> entityType() {
		return entityType;
	}

	@Override
	public Set<String> tables() {
		Set<String> set = Sets.newHashSet();
		if (tableNum > 1) {
			for (int i = 1; i <= tableNum; i++) {
				set.add(DBEntityUtils.merged(simpleTableName, i));
			}
			return set;
		}
		return Sets.newHashSet(simpleTableName);
	}

	long createPk() {
		return IdentityCreator.create();
	}

	@Override
	public long makePk() {
		if (tableNum > 1) {
			return makeRedisGlobalPk();
		} else {
			return maxPk.incrementAndGet();
		}
	}

	/**
	 * 如果做了分表,这个方法用在redis创建全局唯一ID,分表不会出现不同表id重复
	 */
	protected long makeRedisGlobalPk() {
		RAtomicLong atomicLong = RedissonClient.redisson().getAtomicLong(StringUtils.merged("#Global#Cluster#", getServerKeyId(), simpleTableName));
		if (atomicLong.get() == 0) {
			atomicLong.compareAndSet(0L, ProbabilityUtils.random(1000, 9999));
		}
		long pk = atomicLong.incrementAndGet();
		log.debug("create global id by redis {}:{}", simpleTableName, pk);
		return pk;
	}

	@Override
	public void resetPk() {
		if (maxPk != null) {
			maxPk.compareAndSet(maxPk.get(), createPk());
		}
	}
}
