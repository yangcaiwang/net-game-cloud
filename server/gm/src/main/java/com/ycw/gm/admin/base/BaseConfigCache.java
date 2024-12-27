package com.ycw.gm.admin.base;

import com.ycw.core.internal.base.annotation.BaseConfig;
import com.ycw.core.internal.base.annotation.JsonToBaseConfig;
import com.ycw.gm.admin.base.config.*;

import java.util.Map;

@BaseConfig
public class BaseConfigCache {
    @JsonToBaseConfig(fileName = "Item.json")
    private static Map<Integer, ItemConfig> itemConfigMap;

    @JsonToBaseConfig(fileName = "LanguageAll.json")
    private static Map<Integer, LanguageAllConfig> languageAllConfigMap;
    private static Map<String, String> languageMap;

    @JsonToBaseConfig(fileName = "GameArg.json")
    private static Map<Integer, GameArgConfig> gameArgConfigMap;

    @JsonToBaseConfig(fileName = "ItemSource.json")
    private static Map<Integer, ItemSourceConfig> itemSourceConfigMap;

    @JsonToBaseConfig(fileName = "ActivityOpen.json")
    private static Map<Integer, ActivityOpenConfig> activityOpenConfigMap;

    public static Map<Integer, ItemConfig> getItemConfigMap() {
        return itemConfigMap;
    }

    public static void setItemConfigMap(Map<Integer, ItemConfig> itemConfigMap) {
        BaseConfigCache.itemConfigMap = itemConfigMap;
    }

    public static Map<Integer, LanguageAllConfig> getLanguageAllConfigMap() {
        return languageAllConfigMap;
    }

    public static void setLanguageAllConfigMap(Map<Integer, LanguageAllConfig> languageAllConfigMap) {
        BaseConfigCache.languageAllConfigMap = languageAllConfigMap;
    }

    public static Map<String, String> getLanguageMap() {
        return languageMap;
    }

    public static void setLanguageMap(Map<String, String> languageMap) {
        BaseConfigCache.languageMap = languageMap;
    }

    public static Map<Integer, GameArgConfig> getGameArgConfigMap() {
        return gameArgConfigMap;
    }

    public static void setGameArgConfigMap(Map<Integer, GameArgConfig> gameArgConfigMap) {
        BaseConfigCache.gameArgConfigMap = gameArgConfigMap;
    }

    public static Map<Integer, ItemSourceConfig> getItemSourceConfigMap() {
        return itemSourceConfigMap;
    }

    public static void setItemSourceConfigMap(Map<Integer, ItemSourceConfig> itemSourceConfigMap) {
        BaseConfigCache.itemSourceConfigMap = itemSourceConfigMap;
    }

    public static Map<Integer, ActivityOpenConfig> getActivityOpenConfigMap() {
        return activityOpenConfigMap;
    }

    public static void setActivityOpenConfigMap(Map<Integer, ActivityOpenConfig> activityOpenConfigMap) {
        BaseConfigCache.activityOpenConfigMap = activityOpenConfigMap;
    }
}
