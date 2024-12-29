package com.ycw.core.cluster;

import com.ycw.core.cluster.constant.ClusterConstant;
import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.internal.cache.redission.RedissonClient;
import com.ycw.core.internal.loader.service.AbstractService;
import org.apache.commons.collections4.MapUtils;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.stream.Collectors;

/**
 * <集群操作分布式缓存实现类>
 * <p>
 * ps: 使用redission分布式读写锁
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ClusterServiceImpl extends AbstractService implements ClusterService {
    public static final Logger log = LoggerFactory.getLogger(ClusterServiceImpl.class);

    @Override
    public List<ServerEntity> getAllServerEntity() {
        List<ServerEntity> serverEntityList = new ArrayList<>();
        RMap<Integer, Map<String, ServerEntity>> groupMap = RedissonClient.getInstance().getRedisson().getMap(ClusterConstant.CLUSTER_GROUP);
        for (Map<String, ServerEntity> serverEntityMap : groupMap.values()) {
            serverEntityList.addAll(serverEntityMap.values());
        }

        serverEntityList.sort(Comparator.comparing(ServerEntity::getGroupId).thenComparing(ServerEntity::getServerType));
        return serverEntityList;
    }

    @Override
    public List<ServerEntity> getAllGrpcServerEntityByGroup(int... groupIds) {
        List<ServerEntity> serverEntityList = new ArrayList<>();

        RMap<Integer, Map<String, ServerEntity>> groupMap = RedissonClient.getInstance().getRedisson().getMap(ClusterConstant.CLUSTER_GROUP);
        for (int groupId : groupIds) {
            Map<String, ServerEntity> serverEntityMap = groupMap.get(groupId);
            if (MapUtils.isNotEmpty(serverEntityMap)) {
                serverEntityList.addAll(serverEntityMap.values()
                        .stream()
                        .filter(serverEntity -> !ServerType.isGateServer((serverEntity.getServerType())) && !ServerType.isLoginServer((serverEntity.getServerType())))
                        .collect(Collectors.toList()));
            }
        }

        return serverEntityList;
    }

    @Override
    public void saveServerEntity(ServerEntity serverEntity) {
        RMap<Integer, Map<String, ServerEntity>> groupMap = RedissonClient.getInstance().getRedisson().getMap(ClusterConstant.CLUSTER_GROUP);
        RReadWriteLock readWriteLock = groupMap.getReadWriteLock(1);
        RLock rLock = readWriteLock.writeLock();
        rLock.lock();
        try {
            Map<String, ServerEntity> serverEntityMap = groupMap.computeIfAbsent(serverEntity.getGroupId(), v -> new HashMap<>());

            serverEntityMap.put(serverEntity.getServerId(), serverEntity);

            groupMap.put(serverEntity.getGroupId(), serverEntityMap);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public ServerEntity getServerEntity(String serverId) {
        RMap<Integer, Map<String, ServerEntity>> groupMap = RedissonClient.getInstance().getRedisson().getMap(ClusterConstant.CLUSTER_GROUP);
        if (MapUtils.isNotEmpty(groupMap)) {
            for (Map<String, ServerEntity> serverEntityMap : groupMap.values()) {
                ServerEntity serverEntity = serverEntityMap.get(serverId);
                if (serverEntity == null) {
                    continue;
                }

                return serverEntity;
            }
        }

        return null;
    }

    @Override
    public List<ServerEntity> getServerEntity(int groupId, ServerType serverType) {
        List<ServerEntity> serverEntityList = new ArrayList<>();
        RMap<Integer, Map<String, ServerEntity>> groupMap = RedissonClient.getInstance().getRedisson().getMap(ClusterConstant.CLUSTER_GROUP);
        Map<String, ServerEntity> serverEntityMap = groupMap.get(groupId);
        if (MapUtils.isNotEmpty(serverEntityMap)) {
            for (ServerEntity serverEntity : serverEntityMap.values()) {
                if (serverEntity.getServerType() == serverType.value) {
                    serverEntityList.add(serverEntity);
                }
            }
        }

        return serverEntityList;
    }

    @Override
    public List<ServerEntity> getGateServerEntity(int... groupIds) {
        List<ServerEntity> serverEntityList = new ArrayList<>();
        RMap<Integer, Map<String, ServerEntity>> groupMap = RedissonClient.getInstance().getRedisson().getMap(ClusterConstant.CLUSTER_GROUP);
        if (MapUtils.isEmpty(groupMap)) {
            return null;
        }

        for (int groupId : groupIds) {
            Map<String, ServerEntity> serverEntityMap = groupMap.get(groupId);
            if (MapUtils.isEmpty(serverEntityMap)) {
                return null;
            }
            serverEntityList.addAll(serverEntityMap.values()
                    .stream()
                    .filter(serverEntity -> ServerType.isGateServer(serverEntity.getServerType()))
                    .collect(Collectors.toList()));
        }

        return serverEntityList;
    }

    @Override
    public void distributeServerToGroupId(String serverId, int oldGroupId, int newGroupId) {
        // TODO 动态给服务器动态分组 Grpc会丢失消息
    }
}
