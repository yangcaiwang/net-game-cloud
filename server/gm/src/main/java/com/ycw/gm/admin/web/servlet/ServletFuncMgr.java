package com.ycw.gm.admin.web.servlet;

import com.ycw.gm.admin.web.servlet.inside.InsideServlet;
import com.ycw.gm.admin.web.servlet.inside.anno.FuncName;
import com.ycw.gm.admin.web.servlet.inside.anno.SdkType;
import com.ycw.gm.common.utils.spring.SpringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class ServletFuncMgr {

    protected final Logger logger = LoggerFactory.getLogger(this.getClass());
    private static ConcurrentHashMap<String, InsideServlet> servletMap = new ConcurrentHashMap<>();

    private void initMap() {
        Map<String, Object> beansByAnnoType = SpringUtils.getBeansByAnnoType(FuncName.class);
        beansByAnnoType.values().forEach(v -> {
            FuncName annotation = v.getClass().getAnnotation(FuncName.class);
            if (annotation != null) {
                try {
                    putServletMap(annotation.name(), (InsideServlet) v);
                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    private void initSdkMap() {
        Map<String, Object> beansByAnnoType = SpringUtils.getBeansByAnnoType(SdkType.class);
        beansByAnnoType.values().forEach(v -> {
            SdkType annotation = v.getClass().getAnnotation(SdkType.class);
            if (annotation != null) {
                try {

                } catch (Exception e) {
                    logger.error(e.getMessage(), e);
                }
            }
        });
    }

    public void putServletMap(String funcName, InsideServlet servlet) {
        servletMap.put(funcName, servlet);
    }

    public InsideServlet getServlet(String funcName) {
        if (servletMap.isEmpty()) {
            initMap();
        }
        return servletMap.get(funcName);
    }
}
