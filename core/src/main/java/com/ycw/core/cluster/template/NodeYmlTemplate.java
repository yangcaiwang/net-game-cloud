package com.ycw.core.cluster.template;

/**
 * <集群节点解析yml模版类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class NodeYmlTemplate extends AbstractYmlTemplate {
    /**
     * 服务器id
     */
    private String serverId;

    /**
     * 组id
     */
    private int groupId;

    /**
     * 权重
     */
    private int weight;

    public String getServerId() {
        return serverId;
    }

    public void setServerId(String serverId) {
        this.serverId = serverId;
    }

    public int getGroupId() {
        return groupId;
    }

    public void setGroupId(int groupId) {
        this.groupId = groupId;
    }

    public int getWeight() {
        return weight;
    }

    public void setWeight(int weight) {
        this.weight = weight;
    }
}
