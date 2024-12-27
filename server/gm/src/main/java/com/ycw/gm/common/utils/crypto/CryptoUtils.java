
package com.ycw.gm.common.utils.crypto;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.LoggerFactory;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.net.URL;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.CRC32;
import java.util.zip.CheckedInputStream;

/**
 * 加密工具类，包含MD5,BASE64,SHA,CRC32
 */
public class CryptoUtils {

	static private final org.slf4j.Logger log = LoggerFactory.getLogger(CryptoUtils.class);


	/**
	 * 混淆/反混淆
	 * 
	 * @param key
	 *            指定一个密钥
	 * @param data
	 *            源数据的二进制表达形式
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
	 * @param key
	 *            指定一个密钥
	 * @param data
	 *            源数据
	 * @param charset
	 *            编码
	 * @return 混淆或者反混淆后的数据
	 */
	public static String garble(String key, String data, Charset charset) {
		return new String(garble(key.getBytes(charset), data.getBytes(charset)));
	}

	/**
	 * 混淆/反混淆,使用utf-8编码
	 * 
	 * @param key
	 *            指定一个密钥
	 * @param data
	 *            源数据
	 * @return 混淆或者反混淆后的数据
	 */
	public static String garble(String key, String data) {
		return garble(key, data, StandardCharsets.UTF_8);
	}

	/**
	 * MD5加密
	 * 
	 * @param bytes
	 *            an array of byte.
	 * @return a {@link String} object.
	 */
	public static String encodeMD5(final byte[] bytes) {

		return DigestUtils.md5Hex(bytes);
	}

	/**
	 * MD5加密，默认UTF-8
	 * 
	 * @param str
	 *            a {@link String} object.
	 * @return a {@link String} object.
	 */
	public static String encodeMD5(final String str) {

		return encodeMD5(str, StandardCharsets.UTF_8);
	}

	/**
	 * MD5加密
	 * 
	 * @param str
	 *            a {@link String} object.
	 * @param charset
	 *            a {@link String} object.
	 * @return a {@link String} object.
	 */
	public static String encodeMD5(final String str, final Charset charset) {
		byte[] bytes = str.getBytes(charset);
		return encodeMD5(bytes);
	}

	/**
	 * SHA加密
	 * 
	 * @param bytes
	 *            an array of byte.
	 * @return a {@link String} object.
	 */
	public static String encodeSHA(final byte[] bytes) {

		return DigestUtils.sha512Hex(bytes);
	}

	/**
	 * SHA加密
	 * 
	 * @param str
	 *            a {@link String} object.
	 * @param charset
	 *            a {@link String} object.
	 * @return a {@link String} object.
	 */
	public static String encodeSHA(final String str, final Charset charset) {
		byte[] bytes = str.getBytes(charset);
		return encodeSHA(bytes);
	}

	/**
	 * SHA加密,默认utf-8
	 * 
	 * @param str
	 *            a {@link String} object.
	 * @return a {@link String} object.
	 */
	public static String encodeSHA(final String str) {

		return encodeSHA(str, StandardCharsets.UTF_8);
	}

	/**
	 * BASE64加密
	 * 
	 * @param bytes
	 *            an array of byte.
	 * @return a {@link String} object.
	 */
	public static String encodeBASE64(final byte[] bytes) {

		return Base64.encodeBase64String(bytes);
	}

	/**
	 * BASE64加密
	 * 
	 * @param str
	 *            a {@link String} object.
	 * @param charset
	 *            a {@link String} object.
	 * @return a {@link String} object.
	 */
	public static String encodeBASE64(final String str, Charset charset) {
		byte[] bytes = str.getBytes(charset);
		return encodeBASE64(bytes);
	}

	/**
	 * BASE64加密,默认UTF-8
	 * 
	 * @param str
	 *            a {@link String} object.
	 * @return a {@link String} object.
	 */
	public static String encodeBASE64(final String str) {
		return encodeBASE64(str, StandardCharsets.UTF_8);
	}

	/**
	 * BASE64解密,默认UTF-8
	 * 
	 * @param str
	 *            a {@link String} object.
	 * @return a {@link String} object.
	 */
	public static String decodeBASE64(String str) {
		return decodeBASE64(str, StandardCharsets.UTF_8);
	}

	/**
	 * BASE64解密
	 * 
	 * @param str
	 *            a {@link String} object.
	 * @param charset
	 *            字符编码
	 * @return a {@link String} object.
	 */
	public static String decodeBASE64(String str, Charset charset) {
		byte[] bytes = str.getBytes(charset);
		return new String(Base64.decodeBase64(bytes));
	}

	/**
	 * CRC32字节校验
	 * 
	 * @param bytes
	 *            an array of byte.
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
	 * @param str
	 *            a {@link String} object.
	 * @param charset
	 *            a {@link String} object.
	 * @return a {@link String} object.
	 */
	public static String crc32(final String str, Charset charset) {
		byte[] bytes = str.getBytes(charset);
		return crc32(bytes);
	}

	/**
	 * CRC32字符串校验,默认UTF-8编码读取
	 * 
	 * @param str
	 *            a {@link String} object.
	 * @return a {@link String} object.
	 */
	public static String crc32(final String str) {

		return crc32(str, StandardCharsets.UTF_8);
	}

	/**
	 * CRC32流校验
	 * 
	 * @param input
	 *            a {@link InputStream} object.
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
	 * @param file
	 *            a {@link File} object.
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
	 * @param url
	 *            a {@link URL} object.
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

	/**
	 * 将加密后的字节数组转换成字符串
	 *
	 * @param b 字节数组
	 * @return 字符串
	 */
	public  static String byteArrayToHexString(byte[] b) {
		StringBuilder hs = new StringBuilder();
		String stmp;
		for (int n = 0; b!=null && n < b.length; n++) {
			stmp = Integer.toHexString(b[n] & 0XFF);
			if (stmp.length() == 1)
				hs.append('0');
			hs.append(stmp);
		}
		return hs.toString().toLowerCase();
	}

	public static void main(String[] args) throws Exception {
//		String s = "amount=600&appId=230607501702&cpOrderId=f4e051145_tSldsD622453325734184&roleId=510870011000000017&uid=145_tSldsD&zoneId=7001sxnp3prsg7qwtzj1kufe4qxdmwoejwcm";
//		String s = "appId=230607501702&cpOrderId=4f5e14145_tSldsD691397516447773&orderId=202306271046163457163377362_XA&platformId=188";
//		System.err.println(encodeMD5(s ));
		//0a44307c133637716a4140d9c52ff856
//		String s = "2V16YOP6FRS11H5O" + "101101" + "1416811075" + "3JOVFPIVV7UF203QMLR6T6ED8888LIAI0MZY776UYYTGERPKW9RL8AH57WUTY28K";
//		String sign = CryptoUtils.encodeMD5(s).toLowerCase();
//		System.err.println(sign);
//		String s = "GET&%2Fv3%2Fuser%2Fget_info&appid%3D123456%26format%3Djson%26openid%3D11111111111111111%26openkey%3D2222222222222222%26pf%3Dqzone%26userip%3D112.90.139.30";


//		Map<String, Object> map = new HashMap<>();
//		map.put("openkey", "2D44B271B3187D06A66988FD6B05D717");
//		map.put("openid", "C5E4F88185FADBD85D7863C0BB05E925");
//		map.put("pf", "qqgame");
//		map.put("appid", 102031271);
//		StringBuilder sb = new StringBuilder();
//		List<String> keyList = new ArrayList<>(map.keySet());
//		Collections.sort(keyList);
//		for (String s : keyList) {
//			sb.append(s).append("=").append(map.get(s)).append("&");
//		}
//		String substring = sb.substring(0, sb.length() - 1);

//		String ss = "%7B%26quot%3BplatformId%26quot%3B%3A5107%2C%26quot%3BroleId%26quot%3B%3A%26quot%3B510760011000000007%26quot%3B%2C%26quot%3Bid%26quot%3B%3A101%2C%26quot%3BgiftUid%26quot%3B%3A0%2C%26quot%3Bplatform%26quot%3B%3A2%2C%26quot%3BserverId%26quot%3B%3A6001%7D";
//		String decode = URLDecoder.decode(URLDecoder.decode(ss, "utf-8"), "utf-8");
//		System.err.println(decode);
//
//		String s11 = "{\"platformId\":5107,\"roleId\":\"510760011000000007\",\"id\":101,\"giftUid\":0,\"platform\":2,\"serverId\":6001}";
//		s11 = s11.replaceAll("\"", "&quot;");
//		String encode = URLEncoder.encode(s11);
//		System.err.println(encode);
//		String stringBuilder = "orderid=216901953525858" + "&" + "cporderid=510760011000000007_1690195352" + "&" +
//				"username=aiqu_t7776071448" + "&" + "gameid=283"  + "&" +
//				"roleid=510760011000000007" + "&" + "serverid=6001" + "&" +
//				"amount=6" + "&" + "paytime=1690195352" + "&" +
//				"extrasparams=" + ss + "&key=35aa3cc51696e46b11da6b8e3090313b";
//
//		System.err.println(stringBuilder);
//		String sss = "orderid=216901953525858&cporderid=510760011000000007_1690195352&username=aiqu_t7776071448&gameid=283&roleid=510760011000000007&serverid=6001&amount=6&paytime=1690195352&extrasparams=%7B%26quot%3BplatformId%26quot%3B%3A5107%2C%26quot%3BroleId%26quot%3B%3A%26quot%3B510760011000000007%26quot%3B%2C%26quot%3Bid%26quot%3B%3A101%2C%26quot%3BgiftUid%26quot%3B%3A0%2C%26quot%3Bplatform%26quot%3B%3A2%2C%26quot%3BserverId%26quot%3B%3A6001%7D&key=35aa3cc51696e46b11da6b8e3090313b";
//		String md5 = CryptoUtils.encodeMD5(stringBuilder);
//		System.err.println(md5);

//		JSONObject jsonObject = new JSONObject();
//		jsonObject.put("openid", "2EA7886E91FFF12A0886B2ACD46FA5DD");
//		jsonObject.put("openkey", "957199D916F58493A65C1943C1F2CAFA");
//		jsonObject.put("appid", "102034548");
//		jsonObject.put("pf", "qqgame");
//		jsonObject.put("pfkey", "51301B3B1D8CECD4A90FB864ED3E440B");
//		jsonObject.put("zoneid", 1);
//		jsonObject.put("format", "json");
//
////		jsonObject.put("amt",amt);
//		jsonObject.put("ts", 1690447221);
//		jsonObject.put("payitem", "101*60*1");
//		jsonObject.put("goodsmeta", "6元元宝礼包*6元元宝礼包");
//		jsonObject.put("goodsurl", "https%3A%2F%2Fppupdate.51gamer.cn%2FgameVersionFile%2Ffqcl%2FQQ%2Ficon%5Fpay%5Fcomm.png");
//		jsonObject.put("appmode", 1);
//		StringBuilder sb = new StringBuilder();
//		List<String> keyList = new ArrayList<>(jsonObject.keySet());
//		Collections.sort(keyList);
//		for (String s : keyList) {
//			sb.append(s).append("=").append(jsonObject.get(s)).append("&");
//		}
//		String substring = sb.substring(0, sb.length() - 1);
//
//		String encode = java.net.URLEncoder.encode(substring, "utf-8");
//		String path = "/v3/pay/buy_goods";
//		String urlEncode = java.net.URLEncoder.encode(path, "utf-8");
//
//		String srcArg = "POST&" + urlEncode + "&"+encode;
//		System.err.println("arg=" + srcArg);
//		String ss = "GET&%2FqqGameRecharge&amt%3D600%26appid%3D1450079926%26billno%3D%252DAPPDJSX180725%252D20230731%252DGHl0YFCRbCp1%26openid%3D2EA7886E91FFF12A0886B2ACD46FA5DD%26payamt_coins%3D0%26paychannel%3Dqbqd%26paychannelsubid%3D1%26payitem%3D101*60*1%26providetype%3D0%26pubacct_payamt_coins%3D%26tbazinga%3D1%26token%3DBF4FB60ACB7897EEBC1752BA012FEC9213144%26ts%3D1690791467%26version%3Dv3%26zoneid%3D1";
//		String sign = CryptoUtils.hmacsha1(ss, "4yjFagbHm25uX7c3&");
//		System.err.println(sign);

//		Map<String, String> map = new HashMap<>();
//		map.put("product", "1*1");
//		map.put("cno", "5202407241830364808");
//		map.put("roleid", "9999100110010");
//		map.put("price", "6");
//		map.put("rebate", "0");
//		map.put("sign", "86c1c702231b980227343050d7ce8d91");
//		map.put("userid", "1003126782791");
//		map.put("serverid", "1001");
//		map.put("timestamp", "1721817044");
//		map.put("status", "1");
//
//		String sign = map.remove("sign");
//
//		StringBuilder stringBuilder = new StringBuilder();
//
//		map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(stringStringEntry -> {
//			stringBuilder.append(stringStringEntry.getKey()).append("=").append(stringStringEntry.getValue()).append("&");
//		});
//		stringBuilder.append("key=").append("05f4bf6554e8c4bbbd00d7c843d66b91");
//		String string = stringBuilder.toString();
//
//		String md5 = CryptoUtils.encodeMD5(string);
//		System.out.println("md5:" + md5);
//		System.out.println("sign:" + sign);

		Map<String, String> map = new HashMap<>();
		map.put("giftid", "1");
		map.put("userid", "123");
		map.put("serverid", "s1");
		map.put("roleid", "321");
		map.put("time", "1692201600");

		StringBuilder stringBuilder = new StringBuilder();
		map.entrySet().stream().sorted(Map.Entry.comparingByKey()).forEach(stringStringEntry -> {
			stringBuilder.append(stringStringEntry.getKey()).append("=").append(stringStringEntry.getValue()).append("&");
		});
		stringBuilder.append("abccba");
		String string = stringBuilder.toString();

		String md5 = CryptoUtils.encodeMD5(string);
		System.out.println("shay ===> md5:" + md5);
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
