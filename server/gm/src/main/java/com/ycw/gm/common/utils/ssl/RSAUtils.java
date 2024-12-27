package com.ycw.gm.common.utils.ssl;

import com.ycw.core.cluster.enums.ServerType;
import com.ycw.core.cluster.property.PropertyConfig;
import com.ycw.gm.common.utils.file.FileListeners;
import com.ycw.gm.common.utils.spring.SpringUtils;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;
import sun.misc.BASE64Decoder;

import javax.crypto.Cipher;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

public class RSAUtils {

    private static final Logger log = LoggerFactory.getLogger(RSAUtils.class);

    private static final String PRIVATE_KEY = "MIICdgIBADANBgkqhkiG9w0BAQEFAASCAmAwggJcAgEAAoGBAJwnHpQqDAnm56nS\n" +
            "Xn7GK9IlUT3H1RSjB2Q8/sfbVsj+Tb+dzUxPc7lNY8r29z/yf20lOzvZUSJRDT0K\n" +
            "htu0JQdc+kT/kJfB8TbAqT9PEqgGn2oJyTk2Aywv70z/laaC8MTzDF5LDKKUyPp6\n" +
            "zFXBnOCouFmxpIYpJCf0LA3qatSNAgMBAAECgYBx9ebOMQWKMHc/q+UE1nHisxNg\n" +
            "aDetEPHzCMTUbNIga9+oCW1r1MxbHwzGcoitpbezmylom0goKEjmFImpTAZ8/jzf\n" +
            "Juy7jAp3XIYU7J2lZ5+IrXFthOgQxGUK3si0MMWFJhFvolB6jZMb1tA+ZW4lgbSe\n" +
            "xn5NrgapVQ4xGEpKLQJBAM3CNQE2Mp37TCy1G+REfktrgFmnypl02vJpe6oDHNOr\n" +
            "ck+hlMA/qiVS/KqFx2FqXWB18ihIXyB/CB+wqCqjGJcCQQDCSBifaw349DkJ8eoq\n" +
            "CGm/+Nuya71asmTvSEec0K3pmlHB5VveEp3Z37GeAWLNudfgtK+zzzesMPAWjfrt\n" +
            "vpx7AkB5zZmrGfZk5zp0zNGyE4ngA8d7S4T3yuFB14fJYQZTl46X7UMte4Kxjard\n" +
            "F0ysMlcMn0W26wDFL+4TNHSYqOwJAkEAmARxv8u0kygFuZJg96K9nEjNHz7OWzlb\n" +
            "YR/daQagDjmB34Xn7EwE14YBJPFQrkjMcjbvhHKPzVw9gGh3/682IQJARej3Mt6l\n" +
            "RYnsXsB6nTFjjsN7Iv5gBysKZS5KextNxaA66jdAgg/xG6lG26HKK4/LxT2FvL/P\n" +
            "TYcpgAqhI8PdRA==";

    private static final String PUBLIC_KEY = "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQCcJx6UKgwJ5uep0l5+xivSJVE9\n" +
            "x9UUowdkPP7H21bI/k2/nc1MT3O5TWPK9vc/8n9tJTs72VEiUQ09CobbtCUHXPpE\n" +
            "/5CXwfE2wKk/TxKoBp9qCck5NgMsL+9M/5WmgvDE8wxeSwyilMj6esxVwZzgqLhZ\n" +
            "saSGKSQn9CwN6mrUjQIDAQAB";

    private static final String ALGORITHM_RSA = "RSA";
    private static final int MAX_DECRYPT_BLOCK = 128;
    private static final int MAX_ENCRYPT_BLOCK = 117;

    private static final String PRIVATE_KEY_FILE = "rsa_private_key.pem";
    private static final String PUBLIC_KEY_FILE = "rsa_public_key.pem";

    private static final String OPENSSL_ALGO_SHA1 = "SHA1withRSA";

    private static final String PKCS_RSA = "RSA/ECB/PKCS1Padding";
    private static PrivateKey privateKey;
    private static PublicKey publicKey;
    private static BASE64Decoder base64decoder = new BASE64Decoder();

    public static void load() {
        String privatePath = PropertyConfig.getAbsolutePath(PropertyConfig.getAbsolutePath("ssl/rsa_private_key.pem", ServerType.GM_SERVER));
        String publicPath = PropertyConfig.getAbsolutePath(PropertyConfig.getAbsolutePath("ssl/rsa_public_key.pem", ServerType.GM_SERVER));
        prePrivateKey(privatePath);
        prePublicKey(publicPath);
        SpringUtils.getBean(FileListeners.class).addListener(new File(PropertyConfig.getAbsolutePath(PropertyConfig.getAbsolutePath("ssl/", ServerType.GM_SERVER))), f -> f.getName().endsWith(".pem"), f -> {
            if (f.getName().equals(PRIVATE_KEY_FILE)) {
                prePrivateKey(privatePath);
            }
            if (f.getName().equals(PUBLIC_KEY_FILE)) {
                prePublicKey(publicPath);
            }
        });
    }

    private static void prePrivateKey(String path) {
        try {
            String fileName = path + PRIVATE_KEY_FILE;
            File file = ResourceUtils.getFile(fileName);
            initPrivateKey(readKeyFile(file));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static String readKeyFile(File file) throws Exception {
        BufferedReader br = new BufferedReader(new FileReader(file));
        StringBuilder pkey = new StringBuilder();
        String s = br.readLine();
        while (s != null) {
            if (s.charAt(0) != '-') {
                pkey.append(s).append("\n");
            }
            s = br.readLine();
        }
        return pkey.toString();
    }

    private static void initPrivateKey(String key) {
        try {
            byte[] keyByte = base64decoder.decodeBuffer(key);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyByte);
            privateKey = kf.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                byte[] keyByte = base64decoder.decodeBuffer(PRIVATE_KEY);
                KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
                PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyByte);
                privateKey = kf.generatePrivate(keySpec);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }

    }

    private static void prePublicKey(String path) {
        try {
            File file = ResourceUtils.getFile(path + PUBLIC_KEY_FILE);
            initPublicKey(readKeyFile(file));
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                byte[] keyByte = base64decoder.decodeBuffer(PUBLIC_KEY);
                KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByte);
                publicKey = kf.generatePublic(keySpec);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    private static void initPublicKey(String key) {
        try {
            byte[] keyByte = base64decoder.decodeBuffer(key);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByte);
            publicKey = kf.generatePublic(keySpec);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
            try {
                byte[] keyByte = base64decoder.decodeBuffer(PUBLIC_KEY);
                KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
                X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByte);
                publicKey = kf.generatePublic(keySpec);
            } catch (Exception ex) {
                log.error(ex.getMessage(), ex);
            }
        }
    }

    /**
     * RSA私钥解密
     *
     * @param str 加密字符串
     * @return 铭文
     * @throws Exception 解密过程中的异常信息
     */
    public static String decrypt(String str) throws Exception {
        //64位解码加密后的字符串
        byte[] inputByte = Base64.decodeBase64(str.getBytes(StandardCharsets.UTF_8));
        if (privateKey == null) {
            initPrivateKey(PRIVATE_KEY);
        }
        PrivateKey priKey = privateKey;
        //RSA解密
        Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
        cipher.init(Cipher.DECRYPT_MODE, priKey);
        // 解密时超过128字节就报错。为此采用分段解密的办法来解密
        byte[] dataReturn = new byte[0];
        for (int i = 0; i < inputByte.length; i += MAX_DECRYPT_BLOCK) {
            byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(inputByte, i, i + MAX_DECRYPT_BLOCK));
            dataReturn = ArrayUtils.addAll(dataReturn, doFinal);
        }

        return new String(dataReturn);
    }

    public static String encrypt(String sourceData) {
        try {
            byte[] data = sourceData.getBytes();
            byte[] dataReturn = new byte[0];
            if (publicKey == null) {
                initPublicKey(PUBLIC_KEY);
            }
            PublicKey pk = publicKey;
            Cipher cipher = Cipher.getInstance(ALGORITHM_RSA);
            cipher.init(Cipher.ENCRYPT_MODE, pk);

            // 加密时超过117字节就报错。为此采用分段加密的办法来加密
            StringBuilder sb = new StringBuilder();
            for (int i = 0; i < data.length; i += MAX_ENCRYPT_BLOCK) {
                byte[] doFinal = cipher.doFinal(ArrayUtils.subarray(data, i, i + MAX_ENCRYPT_BLOCK));
                sb.append(new String(doFinal));
                dataReturn = ArrayUtils.addAll(dataReturn, doFinal);
            }

            return Base64.encodeBase64URLSafeString(dataReturn);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean encryptShaWithRSA(String content, String sign, String publicKey) {
        try {
            byte[] keyByte = base64decoder.decodeBuffer(publicKey);
            KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
            X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByte);
            PublicKey publicKey1 = kf.generatePublic(keySpec);

            Signature instance = Signature.getInstance(OPENSSL_ALGO_SHA1);
            instance.initVerify(publicKey1);
            instance.update(content.getBytes(StandardCharsets.UTF_8));
            return instance.verify(Base64.decodeBase64(sign));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static String decryptShaWithRAS(String str, String publicKey) throws Exception {
        //64位解码加密后的字符串

        byte[] inputByte = base64decoder.decodeBuffer(str);

        byte[] keyByte = base64decoder.decodeBuffer(publicKey);
        KeyFactory kf = KeyFactory.getInstance(ALGORITHM_RSA);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyByte);
        PublicKey pk = kf.generatePublic(keySpec);
        //RSA解密
        Cipher cipher = Cipher.getInstance(PKCS_RSA);
        cipher.init(Cipher.DECRYPT_MODE, pk);
        // 解密时超过128字节就报错。为此采用分段解密的办法来解密
        return new String(cipher.doFinal(inputByte));
    }
}
