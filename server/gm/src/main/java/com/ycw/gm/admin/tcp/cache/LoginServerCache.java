package com.ycw.gm.admin.tcp.cache;

import java.util.HashMap;
import java.util.Map;

public class LoginServerCache {
    // 公告缓存
    public static Map<String, String> gameContent = new HashMap<>();
    // 公告上传更新时间（s）
    public static Map<String, Long> gameContentUpdateTime = new HashMap<>();
}
