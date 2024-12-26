package com.common.module.cluster;

import com.common.module.cluster.constant.ClusterConstant;
import com.common.module.cluster.entity.ParseYml;
import com.common.module.cluster.entity.ServerEntity;
import com.common.module.cluster.enums.Begin;
import com.common.module.cluster.enums.ServerType;
import com.common.module.cluster.property.PropertyConfig;
import com.common.module.internal.cache.redission.RedissonClient;
import com.common.module.internal.loader.service.AbstractService;
import com.common.module.network.grpc.GrpcManager;
import com.common.module.util.SerializationUtils;
import org.apache.commons.collections4.MapUtils;
import org.redisson.api.RLock;
import org.redisson.api.RMap;
import org.redisson.api.RReadWriteLock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

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
    public ServerEntity getServerEntity(ServerType serverType) {
        RMap<Integer, Map<String, ServerEntity>> groupMap = RedissonClient.getInstance().getRedisson().getMap(ClusterConstant.CLUSTER_GROUP);
        Map<String, ServerEntity> serverEntityMap = groupMap.get(0);
        if (MapUtils.isNotEmpty(serverEntityMap)) {
            for (ServerEntity serverEntity : serverEntityMap.values()) {
                if (serverEntity.getServerType() == serverType) {
                    return serverEntity;
                }
            }
        }

        return null;
    }

    @Override
    public ServerEntity getGateServerEntity(int groupId) {
        RMap<Integer, Map<String, ServerEntity>> groupMap = RedissonClient.getInstance().getRedisson().getMap(ClusterConstant.CLUSTER_GROUP);
        if (MapUtils.isEmpty(groupMap)) {
            return null;
        }

        Map<String, ServerEntity> serverEntityMap = groupMap.get(groupId);
        if (MapUtils.isEmpty(serverEntityMap)) {
            return null;
        }

        return serverEntityMap.values()
                .stream()
                .filter(serverEntity -> serverEntity.getServerType() == ServerType.GATE_SERVER)
                .findFirst()
                .orElse(null);
    }

    @Override
    public void startGateGrpcClient(ServerEntity serverEntity) {
        RMap<Integer, Map<String, ServerEntity>> groupMap = RedissonClient.getInstance().getRedisson().getMap(ClusterConstant.CLUSTER_GROUP);
        if (MapUtils.isEmpty(groupMap)) {
            return;
        }

        RReadWriteLock readWriteLock = groupMap.getReadWriteLock(1);
        RLock rLock = readWriteLock.writeLock();
        try {
            Map<String, ServerEntity> serverEntityMap = groupMap.get(serverEntity.getGroupId());
            if (MapUtils.isEmpty(serverEntityMap)) {
                return;
            }

            serverEntityMap.put(serverEntity.getServerId(), serverEntity);
            groupMap.put(serverEntity.getGroupId(), serverEntityMap);

            String path = PropertyConfig.getPrefixPath() + PropertyConfig.getString("cluster.path", "");
            Map<String, Object> clusterMap = PropertyConfig.loadYml(path);
            if (MapUtils.isNotEmpty(clusterMap)) {
                Object grpc = clusterMap.get(Begin.GRPC_S.key);
                ParseYml parseYmlGrpc = SerializationUtils.jsonToBean(SerializationUtils.beanToJson(grpc), ParseYml.class);
                GrpcManager.getInstance().startGrpcClient(serverEntity, parseYmlGrpc.getHeartbeatTime(), parseYmlGrpc.getHeartbeatTimeout());
                log.info("======================= [{}] grpc client started ip::{} port:{} =======================", serverEntity.getServerType().getServerId(), serverEntity.getGrpcClientHost(), serverEntity.getGrpcClientPort());
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            rLock.unlock();
        }
    }

    @Override
    public void distributeServerToGroupId(String serverId, int oldGroupId, int newGroupId) {
        // TODO 动态给服务器动态分组 Grpc会丢失消息
    }
}
