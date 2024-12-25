package com.common.module.cluster.loadalance;

import java.util.ArrayList;
import java.util.List;

/**
 * <集群中服务器负载均衡实现类>
 * <p>
 * ps: 权重最小连接数
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class WeightConnLoadBalancer {
    private List<BaseServer> baseServers;

    public WeightConnLoadBalancer() {
        this.baseServers = new ArrayList<>();
    }

    public void addServer(BaseServer baseServer) {
        baseServers.add(baseServer);
    }

    public BaseServer chooseServer() {
        BaseServer selectedBaseServer = null;
        double lowestRatio = Double.MAX_VALUE;

        for (BaseServer baseServer : baseServers) {
            double ratio = baseServer.getConnectionWeightRatio();
            if (ratio < lowestRatio) {
                lowestRatio = ratio;
                selectedBaseServer = baseServer;
            }
        }

        if (selectedBaseServer != null) {
            selectedBaseServer.incrementConnections();
        }

        return selectedBaseServer;
    }

    public void releaseConnection(BaseServer baseServer) {
        baseServer.decrementConnections();
    }

    // 测试多线程环境下的负载均衡器
    public static void main(String[] args) throws InterruptedException {
        final WeightConnLoadBalancer loadBalancer = new WeightConnLoadBalancer();

        // 添加服务器到负载均衡器
        loadBalancer.addServer(BaseServer.valueOf("Server1", 5, 500));
        loadBalancer.addServer(BaseServer.valueOf("Server2", 3, 1));
        loadBalancer.addServer(BaseServer.valueOf("Server3", 2, 1));

        // 模拟多线程请求
        for (int i = 0; i < 1000; i++) {
            new Thread(() -> {
                BaseServer selectedBaseServer = loadBalancer.chooseServer();
                System.out.println(selectedBaseServer.getName() + ":" + selectedBaseServer.getCurrentConnections());
                // 模拟请求处理时间
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }).start();
        }
        Thread.sleep(5000);

        for (BaseServer baseServer : loadBalancer.baseServers) {
            System.out.println(baseServer.getName() + "::" + baseServer.getCurrentConnections());
        }
    }
}

