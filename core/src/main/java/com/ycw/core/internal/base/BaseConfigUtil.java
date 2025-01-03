package com.ycw.core.internal.base;

import com.alibaba.fastjson.JSONObject;
import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.ycw.core.cluster.property.FileListeners;
import com.ycw.core.internal.base.annotation.BaseConfig;
import com.ycw.core.internal.base.annotation.JsonToBaseConfig;
import com.ycw.core.internal.base.config.AbstractConfig;
import com.ycw.core.internal.base.event.TemplateFileChangedEvent;
import com.ycw.core.internal.event.EventBusesImpl;
import com.ycw.core.internal.loader.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * <基础配置数据工具类>
 * <p>
 * ps: 解析json文件转化配置类 放入缓存 {@link AbstractConfigCache}
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class BaseConfigUtil {

    private static String load_path;
    private static boolean isLoaded = false;
    protected final static Logger logger = LoggerFactory.getLogger(BaseConfigUtil.class);
    private static Map<String, Field> resConfigFields = new HashMap<>();

    private static BaseConfigUtil baseConfigUtil = new BaseConfigUtil();

    public static BaseConfigUtil getInstance() {
        return baseConfigUtil;
    }

    private final String FILE_SUFFIX = ".json";

    public void load(String rootPath, String scanPath) {
        load_path = rootPath;
        if (isLoaded) {
            return;
        }
        initFields(scanPath);
        try {
            for (String fileName : resConfigFields.keySet()) {
                reload(fileName);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
        startUpFileListener(rootPath);
        isLoaded = true;
    }

    private void initFields(String scanPath) {
        Set<Class<?>> set = new HashSet<>();
        Scanner.getInstance().scan(set, scanPath, v -> v.isAnnotationPresent(BaseConfig.class));

        for (Class<?> aClass : set) {
            Field[] declaredFields = aClass.getDeclaredFields();
            for (Field field : declaredFields) {
                JsonToBaseConfig annotation = field.getAnnotation(JsonToBaseConfig.class);
                if (annotation != null && !annotation.fileName().isEmpty()) {
                    String fileName = annotation.fileName();
                    if (!fileName.endsWith(FILE_SUFFIX)) {
                        fileName += FILE_SUFFIX;
                    }
                    Field put = resConfigFields.put(fileName, field);
                    if (put != null) {
                        throw new RuntimeException("重复定义配置文件字段：" + put + " ---- " + field);
                    }
                }
            }
        }
    }

    private void reload(String fileName) {
        if (!resConfigFields.containsKey(fileName)) {
            return;
        }
        try {
            Field field = resConfigFields.get(fileName);
            JsonToBaseConfig annotation = field.getAnnotation(JsonToBaseConfig.class);
            Class<?> clz = annotation.clz();
            if (clz == AbstractConfig.class) {
                ParameterizedType genericType = (ParameterizedType) field.getGenericType();
                clz = (Class<?>) genericType.getActualTypeArguments()[1];
            }
            logger.info("load game conf {}/{}", load_path, fileName);
            File file = new File(load_path + "/" + fileName);
            if (!file.exists()) {
                logger.error("file:{} not exist!", fileName);
                return;
            }
            load(clz, fileName, file);
        } catch (Exception e) {
            logger.error(e.getMessage() + ":" + fileName, e);
        }
    }

    private void load(Class<?> clz, String fileName, File file) {
        JsonFactory f = new MappingJsonFactory();
        JsonParser parser = null;
        try {
            Field field = resConfigFields.get(fileName);
            JsonToBaseConfig annotation = field.getAnnotation(JsonToBaseConfig.class);
            String key = annotation.key();
            String getKey = "get" + key.substring(0, 1).toUpperCase() + key.substring(1);
            Map<Object, Object> map = new HashMap<>();

            parser = f.createParser(file);
            JsonToken current = parser.nextToken();
            if (current != JsonToken.START_ARRAY) {
                logger.error("parse file:{} error", file.getName());
                return;
            }
            while (parser.nextToken() != JsonToken.END_ARRAY) {
                JsonNode treeNode = parser.readValueAsTree();
                Object object = JSONObject.parseObject(treeNode.toString(), clz);
                Method method = object.getClass().getMethod(getKey);
                Object invoke = method.invoke(object);
                if (invoke == null) {
                    logger.error("file :{} not exist key method:{}", fileName, getKey);
                    return;
                }
                map.put(invoke, object);
            }

            String fieldName = field.getName();
            String setMethodName = "set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1);
            Method method = field.getDeclaringClass().getMethod(setMethodName, field.getType());
            method.invoke(null, map);
        } catch (Exception e) {
            logger.error(e.getMessage() + ":" + fileName + "," + clz, e);
            throw new RuntimeException(e);
        } finally {
            if (parser != null) {
                try {
                    parser.close();
                } catch (IOException e) {
                    logger.error(e.getMessage(), e);
                }
            }
        }
    }

    private void startUpFileListener(String path) {
        FileListeners.getInstance().addListener(new File(path), f -> f.getName().endsWith(FILE_SUFFIX), (file) -> {
            reload(file.getName());
            EventBusesImpl.getInstance().asyncPublish(TemplateFileChangedEvent.valueOf(file));
        });
    }
}
