package com.ycw.core.cluster;

import com.ycw.core.cluster.entity.ServerEntity;
import com.ycw.core.cluster.enums.ServerType;

import java.util.List;

/**
 * <集群操作分布式缓存接口>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public interface ClusterService {

    List<ServerEntity> selectAllServerEntity();

    /**
     * 保存服务器数据
     *
     * @param serverEntity 服务器对象
     */
    void saveServerEntity(ServerEntity serverEntity);

    /**
     * 通过服务器id 获取服务器对象
     *
     * @param serverId 服务器id
     * @return {@link ServerEntity} 服务器对象
     */
    ServerEntity getServerEntity(String serverId);

    /**
     * 通过服务器类型 获取服务器对象
     *
     * @return {@link ServerEntity} 服务器对象
     */
    ServerEntity getServerEntity(ServerType serverType);

    /**
     * 通过组id获取网关对象
     *
     * @param groupId 组id
     * @return {@link ServerEntity} 服务器对象
     */
    ServerEntity getGateServerEntity(int groupId);

    /**
     * 通过网关对象开启网关客户端连接
     *
     * @param gateServerEntity 网关服务器对象
     */
    void startGateGrpcClient(ServerEntity gateServerEntity);

    /**
     * 分配服务器到指定组
     *
     * @param serverId   服务器id
     * @param oldGroupId 旧组
     * @param newGroupId 新组
     */
    void distributeServerToGroupId(String serverId, int oldGroupId, int newGroupId);
}
