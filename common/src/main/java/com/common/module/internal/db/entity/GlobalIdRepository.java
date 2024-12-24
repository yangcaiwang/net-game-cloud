package com.common.module.internal.db.entity;

import com.common.module.internal.db.Mysql;
import com.common.module.internal.db.dao.DaoImpl;
import com.common.module.util.CollectionUtils;
import com.google.common.collect.Lists;

import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.atomic.AtomicLong;

@Deprecated
public class GlobalIdRepository extends DelayCachedRepository<GlobalIdEntity> {

	public GlobalIdRepository() {
		super();
		for (Entry<String, GlobalIdEntity> entry : asMap().entrySet()) {
			String key = entry.getKey();
			GlobalIdEntity value = entry.getValue();
			GlobalIdEntity globalIdEntity = value;
			if (!Mysql.getInstance().containsAliasName(globalIdEntity.getDbName())) {
				continue;// 如果没有配置这个库就跳过算了
			}
			Set<String> tables = DaoImpl.getInstance().showTables(globalIdEntity.getDbName(), globalIdEntity.getTabName());
			List<Long> longs = Lists.newArrayList();
			for (String tab : tables) {
				try {
					AtomicLong atomicLong = new AtomicLong();
					DaoImpl.getInstance().query(globalIdEntity.getDbName(), String.format("SELECT MAX(`%s`) FROM `%s`", "id", tab), rs -> atomicLong.set(rs.getLong(1)));
					longs.add(atomicLong.get());
				} catch (Exception e) {
					throw new RuntimeException(e.getMessage(), e);
				}
			}
			if (!CollectionUtils.isEmpty(longs)) {
				Collections.sort(longs);
				if (longs.get(longs.size() - 1) > globalIdEntity.getMaxId()) {
					globalIdEntity.setMaxId(longs.get(longs.size() - 1));
				}
			}
		}

	}
}
