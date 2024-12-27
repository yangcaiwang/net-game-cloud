
package com.ycw.core.util;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

/**
 * <协议转换工具类>
 * <p>
 *
 * @author <yangcaiwang>
 * @version <1.0>
 */
public class ProtoJsonUtils {
	public static String toJson(Message message) {
		String json;
		try {
			json = JsonFormat.printer().includingDefaultValueFields().preservingProtoFieldNames().print(message);
			return json;
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage() + ":" + message, e);
		}
	}

	public static void toBuilder(Message.Builder builder, String json) {
		try {
			JsonFormat.parser().merge(json, builder);
		} catch (Exception e) {
			throw new RuntimeException(e.getMessage() + ":" + builder + "," + json, e);
		}
	}
}
