package com.gm.server.common.utils;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Properties;

public class ExecuteShellUtil {
    public static final Logger log = LoggerFactory.getLogger(ExecuteShellUtil.class);
    public static final int PORT = 22;
    public static final String USER = "root";

    private static final JSch jsch = new JSch();

    /**
     * 执行一条命令
     */
    public static String execCmd(String ip, String password, String... command) throws Exception {
        Session session = jsch.getSession(USER, ip, PORT);
        session.setPassword(password);
        Properties sshConfig = new Properties();
        sshConfig.put("StrictHostKeyChecking", "no");
        session.setConfig(sshConfig);
        session.connect(60 * 1000);
        log.info("Session connected!");
        // 打开执行shell指令的通道
        Channel channel = session.openChannel("exec");

        StringBuilder sb = new StringBuilder();
        try {
            for (String cmd : command) {
                String s = execCmdSingle(channel, cmd);
                log.info("exec result:{}", s);
                sb.append(s);
            }
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        } finally {
            // 释放资源
            if (channel.isConnected()) {
                channel.disconnect();
            }
            if (session.isConnected()) {
                session.disconnect();
            }
        }

        return sb.toString();
    }

    public static String execCmdSingle(Channel channel, String command) throws Exception {
        log.info("execCmd command - > {}", command);
        ChannelExec channelExec = (ChannelExec) channel;
        channelExec.setCommand(command);
        channel.setInputStream(null);
        channelExec.setErrStream(System.err);
        channel.connect();
        StringBuilder sb = new StringBuilder(16);
        try (InputStream in = channelExec.getInputStream();
             InputStreamReader isr = new InputStreamReader(in, StandardCharsets.UTF_8);
             BufferedReader reader = new BufferedReader(isr)) {
            String buffer;
            while ((buffer = reader.readLine()) != null) {
                sb.append("\n").append(buffer);
            }
            return sb.toString();
        }
    }

    public static String  callCmd(String locationCmd) throws IOException {
        BufferedReader bufferedReader = null;
        StringBuilder sb = new StringBuilder();
        sb.append("time:").append(new Date()).append("<br/>\n");
        try {
            Process child = null;
            InputStream in = null;
            try {
                child = Runtime.getRuntime().exec(locationCmd);
                in = child.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(in));
                String line;
                while((line=bufferedReader.readLine())!=null) {
                    if (!line.trim().isEmpty()) {
                        sb.append(line).append("<br/>\n");
                    }
                }
            } catch (IOException e) {
                log.error(e.getMessage(), e);
            } finally {
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            }

            try {
                if (child != null) {
                    child.waitFor();
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return sb.toString();
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return "callCmd execute finished";
    }
}
