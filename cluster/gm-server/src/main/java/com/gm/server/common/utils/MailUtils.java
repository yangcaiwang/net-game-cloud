package com.gm.server.common.utils;

import com.common.module.cluster.property.PropertyConfig;
import com.sun.mail.util.MailSSLSocketFactory;
import org.apache.logging.log4j.util.Strings;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class MailUtils {

    //发件人信息
    private static String From = "ppgamer_server@163.com";
    //发件人邮箱
    private static String recipient = "ppgamer_server@163.com";
    //邮箱密码
    private static String password = "ZCMRKICCXTYQPVWP";
    //邮件发送的服务器
    private static String host = "smtp.163.com";

    public static List<String> receiveMails() {
        List<String> mails = new ArrayList<>();
        mails.add("1013728298@qq.com"); // sc
        return mails;
    }

    public static void sendMail(String title, String content) throws Exception {

        if (!PropertyConfig.getBooleanValue("mail.send.switch", false)) {
            return;
        }

        try {
            Properties properties = new Properties();

            properties.setProperty("mail.host", host);

            properties.setProperty("mail.transport.protocol","smtp");

            properties.setProperty("mail.smtp.auth","true");

            //QQ存在一个特性设置SSL加密
            MailSSLSocketFactory sf = null;
            try {
                sf = new MailSSLSocketFactory();
            } catch (GeneralSecurityException e) {
                e.printStackTrace();
            }
            sf.setTrustAllHosts(true);
            properties.put("mail.smtp.ssl.enable", "true");
            properties.put("mail.smtp.ssl.socketFactory", sf);

            //创建一个session对象
            Session session = Session.getDefaultInstance(properties, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(recipient,password);
                }
            });

            //开启debug模式
            session.setDebug(false);

            //获取连接对象
            Transport transport = null;
            try {
                transport = session.getTransport();
            } catch (NoSuchProviderException e) {
                e.printStackTrace();
            }

            //连接服务器
            transport.connect(host,From,password);


            //创建一个邮件发送对象
            MimeMessage mimeMessage = new MimeMessage(session);
            //邮件发送人
            mimeMessage.setFrom(new InternetAddress(recipient));

            List<String> strings = receiveMails();
            Address[] addresses = new Address[strings.size()];
            for (int i = 0; i < strings.size(); i++) {
                String str = strings.get(i);
                if (!Strings.isEmpty(str)) {
                    addresses[i] = new InternetAddress(str);
                }
            }

            //邮件接收人
            mimeMessage.setRecipients(Message.RecipientType.TO, addresses);

            //邮件标题
            mimeMessage.setSubject(title);

            //邮件内容
            mimeMessage.setContent(content,"text/html;charset=UTF-8");

            //发送邮件
            transport.sendMessage(mimeMessage,mimeMessage.getAllRecipients());

            transport.close();

        } catch (Exception e){
            throw e;
        }
    }
}
