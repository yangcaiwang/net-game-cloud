package com.ycw.core.cluster.property;

import com.google.common.collect.Maps;
import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.internal.event.EventBusesImpl;
import com.ycw.core.util.*;
import org.apache.commons.lang3.Validate;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * <系统属性配置类>
 * <p>
 * ps: 支持热加载的配置
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class PropertyConfig {

    private static final Logger log = LoggerFactory.getLogger(PropertyConfig.class);

    private static final String WINDOWS_RESOURCES_PATH = "src/main/resources";
    private static final String LINUX_RESOURCES_PATH = "resources";

    // 开服时间，秒 ，时间戳
    private static long serverOpenTime;
    private static TimeZone gameTimeZone = TimeZone.getDefault();
    private static ZoneId gameTimeZoneId = gameTimeZone.toZoneId();

    // 合服时间，秒 ，时间戳
    private static long serverMergeTime;

    public static void initOpenTime() {
        final String openTimeStr = getString("server.open.time", "");
        try {
            String zone = getString("server.zone", "");
            if (!StringUtils.isEmpty(zone)) {
                gameTimeZone = TimeZone.getTimeZone(zone);
                TimeZone.setDefault(gameTimeZone);
            }
            gameTimeZoneId = gameTimeZone.toZoneId();
            LocalDateTime parse = LocalDateTime.parse(openTimeStr, DateTimeFormatter.ofPattern(DateUnit.DATE_FORMAT_OPEN));
            serverOpenTime = parse.atZone(gameTimeZoneId).toEpochSecond();
            log.info("初始化开服时间：{} 开服天数：{} zoneId:{} zone:{}", openTimeStr, DateUnit.getOpenDays(), gameTimeZoneId, gameTimeZone);
        } catch (Exception e) {
            throw new RuntimeException("开服时间有误" + e.getMessage());
        }

        final String mergeTimeStr = getString("server.merge.time", "");
        try {
            if (!StringUtils.isEmpty(mergeTimeStr)) {
                LocalDateTime parse = LocalDateTime.parse(mergeTimeStr, DateTimeFormatter.ofPattern(DateUnit.DATE_FORMAT_OPEN));
                serverMergeTime = parse.atZone(gameTimeZoneId).toEpochSecond();
                log.error("初始化合服时间：{} 合服天数：{} ", mergeTimeStr, DateUnit.getMergeDays());
            }
        } catch (Exception e) {
            throw new RuntimeException("合服时间有误" + e.getMessage());
        }
    }

    public static long getOpenTime() {
        return serverOpenTime;
    }

    /**
     * 初始化log4j2,可动态修改配置生效
     *
     * @param configFilename 日志配置文件路径
     */
    public static void initLog4J2(String configFilename) {
        LoggerContext log4j2 = Configurator.initialize("log4j2", configFilename);
        File file = new File(configFilename);
        log4j2.setConfigLocation(file.toURI());
    }

    /**
     * 调试模式游戏相关的开放权限等不要使用这个标记
     */
    public static boolean isDebug() {

        return Boolean.parseBoolean(System.getProperty("debug", "true"));
    }

    /**
     * 检查sdk
     */
    public static boolean checkSdk() {
        return Boolean.parseBoolean(System.getProperty("sdk.check", "false"));
    }

    /**
     * 加载配置文件,可以同时加载多个或一个,如果配置文件被删除,会抛出异常
     *
     * @param paths 配置文件路径
     */
    static public void load(String... paths) {

        for (String path : paths) {
            if (Objects.isNull(read(path))) {
                continue;
            }
            FileListeners.getInstance().addListener(new File(path), f -> true, (file) -> {
                Map<String, String> modifies = read(file.getPath());
                if (Objects.nonNull(modifies)) {
                    afterPropertyChange(file.getName(), modifies);
                }
            });
        }
    }

    static protected Map<String, String> read(String path) {

        Map<String, String> modifies = Maps.newHashMap();
        Map<String, String> result;
        try {
            try {
                result = FileUtils.readFileToMap(path);
                if (CollectionUtils.isEmpty(result)) {
                    return null;
                }
            } catch (Exception e) {
                log.error(e.getMessage(), e);
                return null;
            }
            result.forEach((k, v) -> {
                if (!System.getProperties().containsKey(k) || !System.getProperty(k).equals(v)) {
                    modifies.put(k, v);
                    System.setProperty(k, v);// 把配置数据加入到系统参数中方便调用
                }
            });
            return modifies;
        } catch (Exception e) {
            log.error(e.getMessage() + ":" + path, e);
            return null;
        }
    }

    /**
     * 获取绝对路径
     * ps:兼容linux和windows前缀 + path
     *
     * @return 文件绝对路径
     */
    public static String getAbsolutePath(String path) {
        if (SystemUtils.isLinux()) {// 如果是Linux
            return "home/game/" + path;
        } else if (SystemUtils.isMac()) {
            return "home/game/" + path;
        } else {
//            return SystemUtils.getServerHome().replaceAll("\\\\", "/") + path;
            return SystemUtils.getServerHome() + path.replaceAll("/", "\\\\");
        }
    }

    /**
     * 获取绝对路径
     * ps:兼容linux和windows前缀 + path
     *
     * @return 文件绝对路径
     */
    public static String getAbsolutePath(String fileName, ServerType serverType) {
        StringBuilder path = new StringBuilder();
        path.append("/server/").append(serverType.getName()).append("/src/main/resources/").append(fileName);
        if (SystemUtils.isLinux()) {// 如果是Linux
            return "home/game/" + path;
        } else if (SystemUtils.isMac()) {
            return "home/game/" + path;
        } else {
            return SystemUtils.getServerHome() + path.toString().replaceAll("/", "\\\\");
        }
    }

    /**
     * 加载properties配置文件
     *
     * @param path 绝对路径
     */
    public static Properties loadProperties(String path) throws IOException {
        InputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(path)));
        Properties p = new Properties();
        p.load(in);
        return p;
    }

    /**
     * 加载多个properties配置文件
     *
     * @param paths 绝对路径
     */
    public static Properties[] loadProperties(String... paths) throws IOException {
        Properties[] properties = new Properties[paths.length];
        for (int i = 0; i < paths.length; i++) {
            String path = paths[i];
            InputStream in = new BufferedInputStream(Files.newInputStream(Paths.get(path)));
            Properties p = new Properties();
            p.load(in);
            properties[i] = p;
        }

        return properties;
    }

    /**
     * 加载yml配置文件
     *
     * @param filePath 绝对路径
     * @param clazz    类型
     */
    public static <T> T loadYml(String filePath, Class<T> clazz) {
        // 创建Yaml实例
        Yaml yaml = new Yaml(new Constructor(clazz));
        InputStream in = null;
        try {
            in = new BufferedInputStream(Files.newInputStream(Paths.get(filePath)));
            // 解析YAML文件
            return yaml.loadAs(in, clazz);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return null;
    }

//    public static void modifyClusterYml(String clusterYmlPath, String key, int value) {
//        try {
//            DumperOptions options = new DumperOptions();
//            options.setDefaultFlowStyle(DumperOptions.FlowStyle.BLOCK);
//            Yaml yaml = new Yaml(options);
//            Map<String, Object> ymlMap = loadYml(clusterYmlPath, key);
//            Map<String, Object> clusterMap = (Map<String, Object>) ymlMap.get(ClusterConstant.CLUSTER_PREFIX);
//            if (MapUtils.isEmpty(clusterMap)) {
//                return;
//            }
//            // 更新新的值
//            clusterMap.put(key, value);
//            //        获取的是 java运行时候的classes里的文件地址
////            clusterYmlPath.replace("/target/classes/", "/src/main/resources/").replaceAll("/", "\\\\");
//            yaml.dump(ymlMap, new FileWriter(clusterYmlPath));
//        } catch (IOException e) {
//            e.printStackTrace();
//            log.error("修改cluster.yml失败! key={},value={}", key, value);
//        }
//    }

    static public String getPathname(String key) {
        return getPathname(key, null);
    }

    /**
     * 获取(windows:src/main/resources/linux:resources)配置文件
     *
     * @param key          对应的value所在的绝对路径
     * @param defaultValue 只需要简单的名称就可以,内部会自动根据系统类型和项目根目录补全绝对路径
     * @return String
     */
    static public String getPathname(String key, String defaultValue) {
        Objects.requireNonNull(key);
        String value = null;
        if (defaultValue != null) {
            value = getString(key, defaultValue);
        } else {
            value = getString(key);
        }
        Objects.requireNonNull(value);
        String agentPathname = null;
        if (getBoolean("os.check.path", true)) {
            if (!value.contains("resources")) {
                agentPathname = SystemUtils.isWindows() ? WINDOWS_RESOURCES_PATH : SystemUtils.isLinux() ? LINUX_RESOURCES_PATH : null;
            }
        } else {
            agentPathname = SystemUtils.isWindows() ? WINDOWS_RESOURCES_PATH : SystemUtils.isLinux() ? LINUX_RESOURCES_PATH : null;
        }

        String fileName = SystemUtils.getServerHome() + "/" + value;
        if (agentPathname != null) {
            fileName = SystemUtils.getServerHome() + "/" + agentPathname + "/" + value;
        }

        return new File(fileName).getAbsolutePath();
    }

    static public String[] getPathnameArray(String key) {
        String[] array = getStringArray(key);
        String[] pathnames = new String[array.length];
        String agentPathname = null;
        if (getBoolean("os.check.path", true)) {
            String pathname = array[0];
            if (!pathname.contains("resources")) {
                agentPathname = SystemUtils.isWindows() ? WINDOWS_RESOURCES_PATH : SystemUtils.isLinux() ? LINUX_RESOURCES_PATH : null;
            }
        } else {
            agentPathname = SystemUtils.isWindows() ? WINDOWS_RESOURCES_PATH : SystemUtils.isLinux() ? LINUX_RESOURCES_PATH : null;
        }

        for (int i = 0; i < pathnames.length; i++) {
            if (agentPathname != null) {
                pathnames[i] = new File(SystemUtils.getServerHome() + "/" + agentPathname + "/" + array[i]).getAbsolutePath();
            } else {
                pathnames[i] = new File(SystemUtils.getServerHome() + "/" + array[i]).getAbsolutePath();
            }
        }
        return pathnames;
    }

    static public String[] getPathnameArray(String key, String[] defaultArray) {
        String[] array = getStringArray(key, defaultArray);
        String[] pathnames = new String[array.length];
        String agentPathname = SystemUtils.isWindows() ? WINDOWS_RESOURCES_PATH : SystemUtils.isLinux() ? LINUX_RESOURCES_PATH : null;
        for (int i = 0; i < pathnames.length; i++) {
            pathnames[i] = new File(SystemUtils.getServerHome() + "/" + agentPathname + "/" + array[i]).getAbsolutePath();
        }
        return pathnames;
    }

    static public String getString(String key) {
        Validate.isTrue(!StringUtils.isEmpty(key));
        String value = System.getProperty(key);
        Validate.isTrue(value != null, "not exists value for key [%s]", key);
        return value;
    }

    static public String getString(String key, String defaultValue) {

        return System.getProperty(key, defaultValue);
    }

    static public String[] getStringArray(String key) {

        String s = getString(key);
        if (s.startsWith("[") && s.endsWith("]")) return s.substring(1, s.length() - 1).split(",");
        return new String[]{s};
    }

    static public String[] getStringArray(String key, String[] defaultArray) {

        String s = getString(key, Arrays.toString(defaultArray));
        if (s.startsWith("[") && s.endsWith("]")) return s.substring(1, s.length() - 1).split(",");
        return new String[]{s};
    }

    static public Integer getInteger(String key) {

        return Integer.valueOf(getString(key));
    }

    static public Integer getInteger(String key, Integer defaultValue) {

        return Integer.valueOf(getString(key, String.valueOf(defaultValue.intValue())));
    }

    static public int getIntValue(String key) {

        return Integer.parseInt(getString(key));
    }

    static public int getIntValue(String key, int defaultValue) {

        return Integer.parseInt(getString(key, String.valueOf(defaultValue)));
    }

    static public String[] getIntArray(String key) {
        if (!checkKeyExist(key)) {
            return new String[]{};
        }
        String[] array = getStringArray(key);
        String[] _array = new String[array.length];
        for (int i = 0; i < array.length; i++) {
            String trim = array[i].trim();
            if (!StringUtils.isEmpty(trim)) {
                _array[i] = trim;
            }
        }
        return _array;
    }

    static public int[] getIntArray(String key, int[] defaultArray) {

        String[] defArray = new String[defaultArray.length];
        for (int i = 0; i < defArray.length; i++) {
            defArray[i] = String.valueOf(defaultArray[i]);
        }
        String[] array = getStringArray(key, defArray);
        int[] _array = new int[array.length];
        for (int i = 0; i < array.length; i++) {
            _array[i] = Integer.parseInt(array[i].trim());
        }
        return _array;
    }

    static public Long getLong(String key) {

        return Long.valueOf(getString(key));
    }

    static public Long getLong(String key, Long defaultValue) {

        return Long.valueOf(getString(key, String.valueOf(defaultValue.longValue())));
    }

    static public long getLongValue(String key) {

        return Long.parseLong(getString(key));
    }

    static public long getLongValue(String key, long defaultValue) {

        return Long.parseLong(getString(key, String.valueOf(defaultValue)));
    }

    static public Double getDouble(String key) {

        return Double.valueOf(getString(key));
    }

    static public Double getDouble(String key, Double defaultValue) {

        return Double.valueOf(getString(key, String.valueOf(defaultValue.doubleValue())));
    }

    static public double getDoubleValue(String key) {

        return Double.parseDouble(getString(key));
    }

    static public double getDoubleValue(String key, double defaultValue) {

        return Double.parseDouble(getString(key, String.valueOf(defaultValue)));
    }

    static public Boolean getBoolean(String key) {

        return Boolean.valueOf(getString(key));
    }

    static public Boolean getBoolean(String key, Boolean defaultValue) {

        return Boolean.valueOf(getString(key, String.valueOf(defaultValue.booleanValue())));
    }

    static public boolean getBooleanValue(String key) {

        return Boolean.parseBoolean(getString(key));
    }

    static public boolean getBooleanValue(String key, boolean defaultValue) {

        return Boolean.parseBoolean(getString(key, String.valueOf(defaultValue)));
    }

    static public Date getDate(String key) {

        try {
            return DateUnit.timeDate(getString(key));
        } catch (Exception e) {
            log.error("getDate for [" + key + "]" + getString(key), e);
            return null;
        }
    }

    static public Date getDate(String key, Date defaultValue) {

        try {
            return DateUnit.timeDate(getString(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    static public Class<?> getClass(String key) {

        try {
            return Class.forName(getString(key));
        } catch (Exception e) {
            log.error("getClass for[" + key + "]" + getString(key), e);
            return null;
        }
    }

    static public Class<?> getClass(String key, Class<?> defaultValue) {

        try {
            return Class.forName(getString(key));
        } catch (Exception e) {
            return defaultValue;
        }
    }

    static public void set(String key, String value) {

        Map<String, String> modifies = Maps.newHashMap();
        modifies.put(key, value);
        System.setProperty(key, value);
        afterPropertyChange(null, modifies);
    }

    static public void set(Map<String, String> map) {

        Map<String, String> modifies = Maps.newHashMap(map);
        map.forEach((key, value) -> System.setProperty(key, value));
        afterPropertyChange(null, modifies);
    }

    static private void afterPropertyChange(String file, Map<String, String> modifies) {
        if (modifies.containsKey("server.open.time") || modifies.containsKey("server.merge.time") || modifies.containsKey("server.zone")) {
            initOpenTime();
        }
        PropertyModifiedEvent event = new PropertyModifiedEvent(null, modifies);
        log.warn("file [{}] modifies [{}]", file, modifies);
        EventBusesImpl.getInstance().asyncPublish(event);
    }

    public static boolean has(String key) {
        if (StringUtils.isEmpty(key)) return false;
        String s = System.getProperty(key);
        return !StringUtils.isEmpty(s);
    }

    public static boolean checkIsWhiteHostOpen() {
        return "true".equals(getString("white.list", "false"));
    }

    public static boolean checkIsWebWhiteHostOpen() {
        return "true".equals(getString("web.white.list", "false"));
    }

    public static boolean checkKeyExist(String key) {
        return System.getProperties().containsKey(key);
    }

    public static boolean checkBooleanKey(String key, boolean defaultValue) {
        if (checkKeyExist(key)) {
            return getBoolean(key, defaultValue);
        }
        return defaultValue;
    }

    public static String getStringDef(String key, String def) {
        if (!checkKeyExist(key) || def == null) {
            return def;
        }
        return getString(key, def);
    }

    /**
     * 关闭注册
     **/
    public static boolean checkIsRegisterOpen() {
        return "true".equals(getStringDef("register.open", "true"));
    }

    public static boolean checkServerCountLimit() {
        return "true".equals(getStringDef("server.player.count.switch", "false"));
    }

    public static int getServerMaxPlayerCount() {
        return getIntValue("server.player.count", 3000);
    }

    /**
     * 关闭登陆
     **/
    public static boolean checkIsLoginOpen() {
        return "true".equals(getStringDef("login.open", "true"));
    }

    public static boolean checkRPCConnect() {
        return PropertyConfig.checkKeyExist("rpc.host");
    }

    public static TimeZone getGameTimeZone() {
        if (gameTimeZone != null) {
            return gameTimeZone;
        }
        return TimeZone.getDefault();
    }

    public static ZoneId getGameTimeZoneId() {
        return gameTimeZoneId;
    }

    public static void resetServerOpenTime(long openTime) {

        if (!getBoolean("allow.reset.open.time", true)) {
            return;
        }

        if (String.valueOf(openTime).length() == 10) {
            openTime = openTime * 1000L;
        }
        if (String.valueOf(openTime).length() != 13) {
            log.error("开服时间设置错误");
            return;
        }
        Date date = new Date(openTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUnit.DATE_FORMAT_OPEN);
        String format = dateFormat.format(date);
        resetPropertyKeyValue("server.open.time", format);
    }

    public static void resetServerMergeTime(long openTime) {

        if (String.valueOf(openTime).length() == 10) {
            openTime = openTime * 1000L;
        }
        if (String.valueOf(openTime).length() != 13) {
            log.error("开服时间设置错误");
            return;
        }
        Date date = new Date(openTime);
        SimpleDateFormat dateFormat = new SimpleDateFormat(DateUnit.DATE_FORMAT_OPEN);
        String format = dateFormat.format(date);
        resetPropertyKeyValue("server.merge.time", format);
    }

    public static void resetPropertyKeyValueByMap(Map<String, String> map) {
        String serverCnf = System.getProperty("server.cnf", "./src/main/resources/server.cnf");

        File file = new File(serverCnf);

        OutputStreamWriter outputStreamWriter = null;
        FileOutputStream fileOutputStream = null;
        try {
            List<String> strings = FileUtils.readFileToLines(file.getAbsolutePath());
            Map<String, String> notFoundKeyMap = new HashMap<>();
            map.forEach((k, v) -> {
                if (strings.stream().noneMatch(vv -> vv.contains(k))) {
                    notFoundKeyMap.put(k, v);
                }
            });
            fileOutputStream = new FileOutputStream(file);
            outputStreamWriter = new OutputStreamWriter(fileOutputStream, StringUtils.CHARSET_NAME);

            for (String string : strings) {
                String trim = string.trim();
                if (trim.startsWith("#")) {
                    outputStreamWriter.write(trim + "\n");
                    continue;
                }
                if ("".equals(trim)) {
                    outputStreamWriter.write("\n");
                    continue;
                }
                String[] split = string.split("=");
                String key = split[0];
                if (map.containsKey(key)) {
                    String value = map.get(key);
                    outputStreamWriter.write(key + "=" + value + "\n");
                    log.error("success modify key:{} = {}", key, value);
                } else {
                    outputStreamWriter.write(trim + "\n");
                }
            }
            if (!notFoundKeyMap.isEmpty()) {
                for (String s : notFoundKeyMap.keySet()) {
                    String v = notFoundKeyMap.get(s);
                    log.error("not found key:{} value:{}", s, v);
                    outputStreamWriter.write("\n");
                    outputStreamWriter.write(s + "=" + v + "\n");
                }
            }

            outputStreamWriter.flush();
            fileOutputStream.flush();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            if (outputStreamWriter != null) {
                try {
                    outputStreamWriter.close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        }
    }

    public static void resetPropertyKeyValue(String key, String value) {
        Map<String, String> map = new HashMap<>();
        map.put(key, value);
        resetPropertyKeyValueByMap(map);
    }

    public static long getServerMergeTime() {
        return serverMergeTime;
    }
}
