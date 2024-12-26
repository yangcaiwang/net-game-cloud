package com.gm.server.admin.web.servlet;

import com.common.module.cluster.property.PropertyConfig;
import com.gm.server.common.utils.MailUtils;
import com.gm.server.framework.manager.AsyncManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.TimerTask;

public class MailNoticeMgr {

    static protected final Logger log = LoggerFactory.getLogger(MailNoticeMgr.class);
    public static void sendNoticeMail(String title, String content) {
        if (!PropertyConfig.getBooleanValue("mail.send.switch", false)) {
            return;
        }
        AsyncManager.me().execute(new TimerTask() {
            @Override
            public void run() {
                try {
                    MailUtils.sendMail(title, content);
                } catch (Exception e) {
                    log.error(e.getMessage(), e);
                }
            }
        });
    }
}
