
package com.ycw.core.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * <加密工具类>
 * <p>
 * ps: 包含MD5,BASE64,SHA,CRC32
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class CryptoUtils {
    private static final Logger log = LoggerFactory.getLogger(CryptoUtils.class);

    /**
     * 混淆/反混淆
     *
     * @param key  指定一个密钥
     * @param data 源数据的二进制表达形式
     * @return 混淆或者反混淆后的数据
     */
    public static byte[] garble(byte[] key, byte[] data) {
        int len = Math.min(key.length, data.length);
        for (int i = 0; i < len; i += 3) {
            data[i] ^= key[i];
        }
        return data;
    }

    /**
     * 混淆/反混淆
     *
     * @param key     指定一个密钥
     * @param data    源数据
     * @param charset 编码
     * @return 混淆或者反混淆后的数据
     */
    public static String garble(String key, String data, Charset charset) {
        return new String(garble(key.getBytes(charset), data.getBytes(charset)));
    }

    /**
     * 混淆/反混淆,使用utf-8编码
     *
     * @param key  指定一个密钥
     * @param data 源数据
     * @return 混淆或者反混淆后的数据
     */
    public static String garble(String key, String data) {
        return garble(key, data, StringUtils.CHARSET);
    }

    /**
     * MD5加密
     *
     * @param bytes an array of byte.
     * @return a {@link String} object.
     */
    public static String encodeMD5(final byte[] bytes) {

        return DigestUtils.md5Hex(bytes);
    }

    /**
     * MD5加密，默认UTF-8
     *
     * @param str a {@link String} object.
     * @return a {@link String} object.
     */
    public static String encodeMD5(final String str) {

        return encodeMD5(str, StringUtils.CHARSET);
    }

    /**
     * MD5加密
     *
     * @param str     a {@link String} object.
     * @param charset a {@link String} object.
     * @return a {@link String} object.
     */
    public static String encodeMD5(final String str, final Charset charset) {
        byte[] bytes = str.getBytes(charset);
        return encodeMD5(bytes);
    }

    /**
     * SHA加密
     *
     * @param bytes an array of byte.
     * @return a {@link String} object.
     */
    public static String encodeSHA(final byte[] bytes) {

        return DigestUtils.sha512Hex(bytes);
    }

    /**
     * SHA加密
     *
     * @param str     a {@link String} object.
     * @param charset a {@link String} object.
     * @return a {@link String} object.
     */
    public static String encodeSHA(final String str, final Charset charset) {
        byte[] bytes = str.getBytes(charset);
        return encodeSHA(bytes);
    }

    /**
     * SHA加密,默认utf-8
     *
     * @param str a {@link String} object.
     * @return a {@link String} object.
     */
    public static String encodeSHA(final String str) {

        return encodeSHA(str, StringUtils.CHARSET);
    }

    /**
     * BASE64加密
     *
     * @param bytes an array of byte.
     * @return a {@link String} object.
     */
    public static String encodeBASE64(final byte[] bytes) {

        return Base64.encodeBase64String(bytes);
    }

    /**
     * BASE64加密
     *
     * @param str     a {@link String} object.
     * @param charset a {@link String} object.
     * @return a {@link String} object.
     */
    public static String encodeBASE64(final String str, Charset charset) {
        byte[] bytes = str.getBytes(charset);
        return encodeBASE64(bytes);
    }

    /**
     * BASE64加密,默认UTF-8
     *
     * @param str a {@link String} object.
     * @return a {@link String} object.
     */
    public static String encodeBASE64(final String str) {
        return encodeBASE64(str, StringUtils.CHARSET);
    }

    /**
     * BASE64解密,默认UTF-8
     *
     * @param str a {@link String} object.
     * @return a {@link String} object.
     */
    public static String decodeBASE64(String str) {
        return decodeBASE64(str, StringUtils.CHARSET);
    }

    /**
     * BASE64解密
     *
     * @param str     a {@link String} object.
     * @param charset 字符编码
     * @return a {@link String} object.
     */
    public static String decodeBASE64(String str, Charset charset) {
        byte[] bytes = str.getBytes(charset);
        return new String(Base64.decodeBase64(bytes));
    }

    /**
     * CRC32字节校验
     *
     * @param bytes an array of byte.
     * @return a {@link String} object.
     */
    public static String crc32(byte[] bytes) {

        CRC32 crc32 = new CRC32();
        crc32.update(bytes);
        return Long.toHexString(crc32.getValue());
    }

    /**
     * CRC32字符串校验
     *
     * @param str     a {@link String} object.
     * @param charset a {@link String} object.
     * @return a {@link String} object.
     */
    public static String crc32(final String str, Charset charset) {
        byte[] bytes = str.getBytes(charset);
        return crc32(bytes);
    }

    /**
     * CRC32字符串校验,默认UTF-8编码读取
     *
     * @param str a {@link String} object.
     * @return a {@link String} object.
     */
    public static String crc32(final String str) {

        return crc32(str, StringUtils.CHARSET);
    }

    /**
     * CRC32流校验
     *
     * @param input a {@link InputStream} object.
     * @return a {@link String} object.
     */
    public static String crc32(InputStream input) {

        CRC32 crc32 = new CRC32();
        CheckedInputStream checkInputStream = null;
        int test = 0;
        try {
            checkInputStream = new CheckedInputStream(input, crc32);
            do {
                test = checkInputStream.read();
            } while (test != -1);
            return Long.toHexString(crc32.getValue());
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(checkInputStream);
        }
    }

    /**
     * CRC32文件唯一校验
     *
     * @param file a {@link File} object.
     * @return a {@link String} object.
     */
    public static String crc32(File file) {

        InputStream input = null;
        try {
            input = new BufferedInputStream(new FileInputStream(file));
            return crc32(input);
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    /**
     * CRC32文件唯一校验
     *
     * @param url a {@link URL} object.
     * @return a {@link String} object.
     */
    public static String crc32(URL url) {

        InputStream input = null;
        try {
            input = url.openStream();
            return crc32(input);
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            IOUtils.closeQuietly(input);
        }
    }

    public static String hmacsha256(String data, String secret) {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            sha256_HMAC.init(secret_key);
            byte[] bytes = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            hash = byteArrayToHexString(bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return hash;
    }

    /**
     * 将加密后的字节数组转换成字符串
     *
     * @param b 字节数组
     * @return 字符串
     */
    public static String byteArrayToHexString(byte[] b) {
        StringBuilder hs = new StringBuilder();
        String stmp;
        for (int n = 0; b != null && n < b.length; n++) {
            stmp = Integer.toHexString(b[n] & 0XFF);
            if (stmp.length() == 1)
                hs.append('0');
            hs.append(stmp);
        }
        return hs.toString().toLowerCase();
    }

    public static String hmacsha1(String data, String secret) {
        String hash = "";
        try {
            Mac sha256_HMAC = Mac.getInstance("HmacSHA1");
            SecretKeySpec secret_key = new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA1");
            sha256_HMAC.init(secret_key);

            byte[] bytes = sha256_HMAC.doFinal(data.getBytes(StandardCharsets.UTF_8));
            hash = Base64.encodeBase64String(bytes);
        } catch (Exception e) {
            log.error(e.getMessage(), e);
        }
        return hash;
    }

    public static void main(String[] args) throws Exception {
        String key = UUID.randomUUID().toString();
        String s = UUID.randomUUID().toString();
        System.err.println("key=" + key + ",s=" + s);
        byte[] keyBytes = key.getBytes(StringUtils.CHARSET);
        byte[] data = s.getBytes(StringUtils.CHARSET);
        System.err.println(StringUtils.toString(data));
        byte[] result = garble(keyBytes, data);
        System.err.println(StringUtils.toString(result));

        byte[] sourceData = garble(keyBytes, result);
        System.err.println(StringUtils.toString(sourceData));
        System.err.println(new String(sourceData, StringUtils.CHARSET));

        String garble = garble(key, s);
        System.err.println(garble);
        garble = garble(key, garble);
        System.err.println(garble);

        System.err.println(computeMD5Hash("r54hkj3lu4t0r0fj2z7m89ke7122step1721641764"));
    }

    public static String computeMD5Hash(String input) {
        try {
            // Create MD5 Hash
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(input.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuilder hexString = new StringBuilder();
            for (byte aMessageDigest : messageDigest) {
                String h = Integer.toHexString(0xFF & aMessageDigest);
                while (h.length() < 2) h = "0" + h;
                hexString.append(h);
            }
            return hexString.toString();
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
