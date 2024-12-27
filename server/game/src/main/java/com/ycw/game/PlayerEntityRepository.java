package com.ycw.game;

import com.google.common.collect.Lists;
import com.ycw.core.internal.db.repository.CachedRepository;
import com.ycw.core.util.StringUtils;

import java.io.Serializable;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PlayerEntityRepository extends CachedRepository<PlayerEntity> {

	private Map<String, Long> nameMap = new ConcurrentHashMap<>();
	private Map<Long, String> roleIdNameMap = new ConcurrentHashMap<>();
	private Map<String, Map<Integer, Long>> accountRoleIdMap = new ConcurrentHashMap<>();
	public PlayerEntityRepository() {
		super();
		// 提前数据加载出来
		query("select `id`, `original_server_id`, `name`, `account` from player_role where `del_flag` = 0", rs -> {
			long id = rs.getLong("id");
			String account = rs.getString("account");
			String name = rs.getString("name");
			int originalServerId = rs.getInt("original_server_id");
			if (originalServerId == 0) {
				originalServerId = Integer.parseInt(String.valueOf(id).substring(4, 8));
			}
			putAccount(account, originalServerId, id);
			putName(name, id);
		});
	}

	public void exchangeRoleAccount(PlayerEntity oldPlayer, PlayerEntity newPlayer) {
		String oldAccount = oldPlayer.getAccount();
		long oldRoleId = oldPlayer.getId();
		Map<Integer, Long> longs = accountRoleIdMap.get(oldAccount);
		if (longs != null && !longs.isEmpty()) {
			longs.entrySet().removeIf(v -> v.getValue() == oldRoleId);
			int originalSid = newPlayer.getServerId() > 0 ? newPlayer.getServerId() : Integer.parseInt(String.valueOf(newPlayer.getId()).substring(4, 8));
			putAccount(oldAccount, originalSid, newPlayer.getId());
			log.info("exchange old account {} {} to new account:{} {}", oldAccount, oldRoleId, newPlayer.getAccount(), newPlayer.getId());
			oldPlayer.setAccount(newPlayer.getAccount());
			oldPlayer.update();
			Map<Integer, Long> longs1 = accountRoleIdMap.get(newPlayer.getAccount());
			if (longs1 != null && !longs1.isEmpty()) {
				longs1.entrySet().removeIf(v -> v.getValue() == newPlayer.getId());

				originalSid = oldPlayer.getServerId() > 0 ? oldPlayer.getServerId() : Integer.parseInt(String.valueOf(oldRoleId).substring(4, 8));
				putAccount(oldAccount, originalSid, newPlayer.getId());
				newPlayer.setAccount(oldAccount);
				newPlayer.update();
			}
		}
	}

	/**
	 * 根据网络句柄获取玩家
	 * 
	 * @param session
	 * @return
	 */
//	public PlayerEntity getPlayerBySession(IoSession session) {
//		Objects.requireNonNull(session);
//		Long playerId = IoSessions.getPlayerIdentity(session);
//		return playerId == null ? null : get(playerId);
//	}

	/**
	 * 根据主键获取玩家
	 * 
	 * @param playerId
	 * @return
	 */
	public PlayerEntity get(long playerId) {
		return super.get(playerId);
	}

	/**
	 * 使用时要先排除机器人，避免生成不必要的数据
	 * @return
	 */
	public Map<String, Long> getNameMap() {
		return nameMap;
	}

	/**
	 * 获取全服玩家id（不包括机器人）
	 * @return
	 */
	public List<Long> getNotRobotRoleList() {
		return new ArrayList<>(roleIdNameMap.keySet());
	}

//	public Set<Long> getRobots() {
//		return robotMap.keySet();
//	}
//
//	public Map<Long, Integer> getRobotMap() {
//		return robotMap;
//	}

	private void putName(String name, long roleId) {
		nameMap.put(name, roleId);
		roleIdNameMap.put(roleId, name);
	}

	public int getPlayerCount() {
		return roleIdNameMap.size();
	}

	private void putAccount(String account, int originalServerId, long roleId) {
		Map<Integer, Long> longs = accountRoleIdMap.computeIfAbsent(account, v -> new HashMap<>());
		longs.put(originalServerId, roleId);
	}

	public void changeAccount(String account, String newAccount) {
		Map<Integer, Long> remove = accountRoleIdMap.remove(account);
		accountRoleIdMap.put(newAccount, remove);
	}

	public Map<String, Map<Integer, Long>>  getAccount() {
		return accountRoleIdMap;
	}

	public boolean checkAccountExist(String account) {
		return accountRoleIdMap.containsKey(account);
	}

	public boolean checkAccountExist(String account, int serverId) {
		if (!accountRoleIdMap.containsKey(account)) {
			return false;
		}
		return accountRoleIdMap.get(account).containsKey(serverId);
	}

//	private void putRobotAccount(String account, long roleId) {
//		robotAccountRoleIdMap.put(account, roleId);
//	}
//
//	public boolean checkRobotAccountExist(String account) {
//		return robotAccountRoleIdMap.containsKey(account);
//	}

	public long getRoleIdByAccount(String account, int serverId) {
		Map<Integer, Long> longs = accountRoleIdMap.get(account);
		if (longs == null || longs.isEmpty()) {
			return 0L;
		}
//		if (longs.size() == 1) {
//			return longs.get(0);
//		}
//		Integer.parseInt(String.valueOf(v).substring(4, 8))
		return longs.getOrDefault(serverId, 0L);
	}

	// 删除角色
	public void deleteRole(long roleId) {
		PlayerEntity playerEntity = get(roleId);
		if (playerEntity != null) {
			String account = playerEntity.getAccount();
			Map<Integer, Long> longs = accountRoleIdMap.get(account);
			longs.entrySet().removeIf(v -> v.getValue() == roleId);
			if (longs.isEmpty()) {
				accountRoleIdMap.remove(account);
			}
			nameMap.remove(playerEntity.getName());
			roleIdNameMap.remove(roleId);
			playerEntity.setDelFlag(true);
			playerEntity.setAccount(account + "_del");
			playerEntity.update();
		}
	}

//	public long getRobotRoleIdByAccount(String account) {
//		return robotAccountRoleIdMap.getOrDefault(account, 0L);
//	}

//	public void loadAllName() {
//		query(String.format("SELECT `id`, `name` FROM `%s`", simpleTableName), rs -> {
//			long roleId = rs.getLong(1);
//			String name = rs.getString(2);
//			putName(name, roleId);
//		});
//	}

	/**
	 * 创建玩家实例
	 * 
	 * @param name
	 * @return
	 */
	public PlayerEntity makeRolePlayer(String name, String account, String channelId) {
		return makePlayer(name, account, channelId);
	}

	/**
	 * 创建player实例
	 * 
	 * @param name
	 * @return
	 */
	public PlayerEntity makePlayer(String name, String account, String channelId) {
		return makeSure(entity -> {
			entity.setName(name);
			entity.setAccount(account);
			entity.setChannelId(channelId);
			return true;
		});
	}

	public long getRoleIdByName(String name) {
		return nameMap.getOrDefault(name, 0L);
	}

	public boolean nameIsUsed(String name) {
		return nameMap.containsKey(name);
	}

	public boolean changeName(String newName, String oldName, long roleId) {
		Long value = nameMap.putIfAbsent(newName, roleId);
		if (value != null)
			return false;
		nameMap.remove(oldName, roleId);
		roleIdNameMap.put(roleId, newName);
		return true;
	}

	public List<PlayerEntity> getAllPlayer() {
		return new ArrayList<>(getAll(getNotRobotRoleList()).values());
	}

	@Override
	protected void afterCreated(PlayerEntity obj) {
		putName(obj.getName(), obj.getId());
//		if (obj.isRobot()) {
//			robotMap.put(obj.getId(), obj.getRobotResId());
//			putRobotAccount(obj.getAccount(), obj.getId());
//		} else {
//		}
		putAccount(obj.getAccount(), obj.getServerId(), obj.getId());
	}

	public String getRoleNameById(long roleId) {
		return roleIdNameMap.getOrDefault(roleId, "");
	}

	public void setRoleIdNameMap(Map<Long, String> roleIdNameMap) {
		this.roleIdNameMap = roleIdNameMap;
	}

	public Collection<Long> getRoles() {
		return roleIdNameMap.keySet();
	}
	
	public List<Long> queryPlayerListByLoginTime(long time) {
		String sql = String.format("SELECT `id` FROM `%s` WHERE UNIX_TIMESTAMP(`login_time`) > %s", simpleTableName, time / 1000);
		List<Long> list = Lists.newArrayList();
		query(sql, rs -> {
			long roleId = rs.getLong(1);
			list.add(roleId);
		});
		return list;
	}

	public List<Long> queryPlayerByAccount(String account) {
		String sql = String.format("SELECT `id` FROM `%s` WHERE `account` = %s", simpleTableName, account);
		List<Long> list = Lists.newArrayList();
		query(sql, rs -> {
			long roleId = rs.getLong(1);
			list.add(roleId);
		});
		return list;
	}


    @Override
    public Boolean delete(Serializable... pks) {
        log.error("不允许删除player对象, pks:" + StringUtils.toString(pks), new Throwable());
        return false;
    }

    @Override
    public Boolean delete(PlayerEntity... objs) {
		log.error("不允许删除player对象, objs:" + StringUtils.toString(Arrays.stream(objs).mapToLong(PlayerEntity::getId).toArray()), new Throwable());
        return false;
    }

    @Override
    public Boolean delete(Collection<PlayerEntity> c) {
        log.error("不允许删除player对象, entities:" + StringUtils.toString(c.stream().mapToLong(PlayerEntity::getId).toArray()), new Throwable());
        return false;
    }

    @Override
    public Boolean delete(String condition) {
        log.error("不允许删除player对象, condition:" + condition, new Throwable());
        return false;
    }

//	public Map<String, Long> getRobotAccountRoleIdMap() {
//		return robotAccountRoleIdMap;
//	}
}
