package com.ycw.gm.framework.datasource;

import com.alibaba.druid.pool.DruidDataSource;
import com.alibaba.druid.spring.boot.autoconfigure.DruidDataSourceBuilder;
import com.ycw.gm.framework.config.properties.DruidProperties;
import org.springframework.jdbc.datasource.lookup.AbstractRoutingDataSource;

import javax.sql.DataSource;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 动态数据源
 * 
 * @author gamer
 */
public class DynamicDataSource extends AbstractRoutingDataSource
{
    private Map<Object, Object> targetDataSources = new ConcurrentHashMap<>();
    private DruidProperties druidProperties;
    public DynamicDataSource(DataSource defaultTargetDataSource, Map<Object, Object> targetDataSources,  DruidProperties druidProperties)
    {
        this.targetDataSources.putAll(targetDataSources);
        super.setDefaultTargetDataSource(defaultTargetDataSource);
        this.druidProperties = druidProperties;
        super.setTargetDataSources(targetDataSources);
//        super.afterPropertiesSet();
    }

    public void initOtherDataSource(String key, String url, String username, String password) throws Exception {
        if (targetDataSources.containsKey(key)) {
            DynamicDataSourceContextHolder.setDataSourceType(key);
            return;
        }

        DruidProperties druidProperties = this.druidProperties;
        DruidDataSource dataSource = DruidDataSourceBuilder.create().build();
        druidProperties.dataSource(dataSource);
        dataSource.setBreakAfterAcquireFailure(true);
        dataSource.setUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.init();
        this.targetDataSources.put(key, dataSource);
        DynamicDataSourceContextHolder.setDataSourceType(key);
    }

    @Override
    protected Object determineCurrentLookupKey()
    {
        return DynamicDataSourceContextHolder.getDataSourceType();
    }

    @Override
    protected DataSource determineTargetDataSource() {
        Object lookupKey = determineCurrentLookupKey();
        if (lookupKey == null) {
            return getResolvedDefaultDataSource();
        }
        DataSource dataSource = (DataSource) this.targetDataSources.get(lookupKey);
        if (dataSource == null) {
            return getResolvedDefaultDataSource();
        }
        return dataSource;
    }

    public void removeDataSourceByKey(String key) {
        DruidDataSource remove = (DruidDataSource)this.targetDataSources.remove(key);
        if (remove != null) {
            remove.close();
        }
    }

    public void cleanDataSource() {
        DynamicDataSourceContextHolder.clearDataSourceType();
    }
}