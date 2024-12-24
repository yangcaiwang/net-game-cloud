
package com.common.module.util;

import com.google.protobuf.Message;
import com.google.protobuf.util.JsonFormat;

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
