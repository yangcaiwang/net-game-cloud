package com.ycw.core.cluster.entity;


import java.io.Serializable;

/**
 * <数据源信息内部类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class DataSourceInfo implements Serializable {
    /**
     * url
     */
    private String url;

    /**
     * 账号
     */
    private String username;

    /**
     * 密码
     */
    private String password;

    public static DataSourceInfo valueOf() {
        return new DataSourceInfo();
    }

    public static DataSourceInfo valueOf(String url, String username, String password) {
        DataSourceInfo dataSourceInfo = new DataSourceInfo();
        dataSourceInfo.setUrl(url);
        dataSourceInfo.setUsername(username);
        dataSourceInfo.setPassword(password);
        return dataSourceInfo;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public String toString() {
        return "DataSourceInfo{" +
                "url='" + url + '\'' +
                ", username='" + username + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}