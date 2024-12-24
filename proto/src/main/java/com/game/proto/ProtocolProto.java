// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: protocol.proto

package com.game.proto;

public final class ProtocolProto {
  private ProtocolProto() {}
  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistryLite registry) {
  }

  public static void registerAllExtensions(
      com.google.protobuf.ExtensionRegistry registry) {
    registerAllExtensions(
        (com.google.protobuf.ExtensionRegistryLite) registry);
  }
  /**
   * <pre>
   *协议定义
   *六位协议号 第一位1表示请求 2表示响应 3表示服务器通知 次二位表示模块id 后三位表示模块内协议号
   * </pre>
   *
   * Protobuf enum {@code protocol.ProtocolCmd}
   */
  public enum ProtocolCmd
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <pre>
     * 不需要，只是为了标记第一个
     * </pre>
     *
     * <code>NONE = 0;</code>
     */
    NONE(0),
    /**
     * <pre>
     *================================Gm=======================================
     * </pre>
     *
     * <code>ADD_LEVEL_REQ = 110001;</code>
     */
    ADD_LEVEL_REQ(110001),
    /**
     * <pre>
     * 升级
     * </pre>
     *
     * <code>ADD_LEVEL_RESP = 210001;</code>
     */
    ADD_LEVEL_RESP(210001),
    /**
     * <pre>
     *=========================================================================
     * </pre>
     *
     * <code>HEART_BEAT_REQ = 100000;</code>
     */
    HEART_BEAT_REQ(100000),
    /**
     * <pre>
     * 心跳
     * </pre>
     *
     * <code>HEART_BEAT_RESP = 200000;</code>
     */
    HEART_BEAT_RESP(200000),
    /**
     * <pre>
     * 进入游戏
     * </pre>
     *
     * <code>ENTER_GAME_REQ = 100001;</code>
     */
    ENTER_GAME_REQ(100001),
    /**
     * <pre>
     * 进入游戏
     * </pre>
     *
     * <code>ENTER_GAME_RESP = 200001;</code>
     */
    ENTER_GAME_RESP(200001),
    /**
     * <pre>
     * 玩家属性更新
     * </pre>
     *
     * <code>PLAYER_UPDATE_NOTIFY = 300003;</code>
     */
    PLAYER_UPDATE_NOTIFY(300003),
    /**
     * <pre>
     * 玩家隔天刷新
     * </pre>
     *
     * <code>PLAYER_NEXT_DAY_NOTIFY = 300004;</code>
     */
    PLAYER_NEXT_DAY_NOTIFY(300004),
    /**
     * <pre>
     * 断线重连
     * </pre>
     *
     * <code>PLAYER_RECONNECT_REQ = 100015;</code>
     */
    PLAYER_RECONNECT_REQ(100015),
    /**
     * <pre>
     * 断线重连
     * </pre>
     *
     * <code>PLAYER_RECONNECT_RESP = 200015;</code>
     */
    PLAYER_RECONNECT_RESP(200015),
    UNRECOGNIZED(-1),
    ;

    /**
     * <pre>
     * 不需要，只是为了标记第一个
     * </pre>
     *
     * <code>NONE = 0;</code>
     */
    public static final int NONE_VALUE = 0;
    /**
     * <pre>
     *================================Gm=======================================
     * </pre>
     *
     * <code>ADD_LEVEL_REQ = 110001;</code>
     */
    public static final int ADD_LEVEL_REQ_VALUE = 110001;
    /**
     * <pre>
     * 升级
     * </pre>
     *
     * <code>ADD_LEVEL_RESP = 210001;</code>
     */
    public static final int ADD_LEVEL_RESP_VALUE = 210001;
    /**
     * <pre>
     *=========================================================================
     * </pre>
     *
     * <code>HEART_BEAT_REQ = 100000;</code>
     */
    public static final int HEART_BEAT_REQ_VALUE = 100000;
    /**
     * <pre>
     * 心跳
     * </pre>
     *
     * <code>HEART_BEAT_RESP = 200000;</code>
     */
    public static final int HEART_BEAT_RESP_VALUE = 200000;
    /**
     * <pre>
     * 进入游戏
     * </pre>
     *
     * <code>ENTER_GAME_REQ = 100001;</code>
     */
    public static final int ENTER_GAME_REQ_VALUE = 100001;
    /**
     * <pre>
     * 进入游戏
     * </pre>
     *
     * <code>ENTER_GAME_RESP = 200001;</code>
     */
    public static final int ENTER_GAME_RESP_VALUE = 200001;
    /**
     * <pre>
     * 玩家属性更新
     * </pre>
     *
     * <code>PLAYER_UPDATE_NOTIFY = 300003;</code>
     */
    public static final int PLAYER_UPDATE_NOTIFY_VALUE = 300003;
    /**
     * <pre>
     * 玩家隔天刷新
     * </pre>
     *
     * <code>PLAYER_NEXT_DAY_NOTIFY = 300004;</code>
     */
    public static final int PLAYER_NEXT_DAY_NOTIFY_VALUE = 300004;
    /**
     * <pre>
     * 断线重连
     * </pre>
     *
     * <code>PLAYER_RECONNECT_REQ = 100015;</code>
     */
    public static final int PLAYER_RECONNECT_REQ_VALUE = 100015;
    /**
     * <pre>
     * 断线重连
     * </pre>
     *
     * <code>PLAYER_RECONNECT_RESP = 200015;</code>
     */
    public static final int PLAYER_RECONNECT_RESP_VALUE = 200015;


    public final int getNumber() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalArgumentException(
            "Can't get the number of an unknown enum value.");
      }
      return value;
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     * @deprecated Use {@link #forNumber(int)} instead.
     */
    @java.lang.Deprecated
    public static ProtocolCmd valueOf(int value) {
      return forNumber(value);
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     */
    public static ProtocolCmd forNumber(int value) {
      switch (value) {
        case 0: return NONE;
        case 110001: return ADD_LEVEL_REQ;
        case 210001: return ADD_LEVEL_RESP;
        case 100000: return HEART_BEAT_REQ;
        case 200000: return HEART_BEAT_RESP;
        case 100001: return ENTER_GAME_REQ;
        case 200001: return ENTER_GAME_RESP;
        case 300003: return PLAYER_UPDATE_NOTIFY;
        case 300004: return PLAYER_NEXT_DAY_NOTIFY;
        case 100015: return PLAYER_RECONNECT_REQ;
        case 200015: return PLAYER_RECONNECT_RESP;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<ProtocolCmd>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        ProtocolCmd> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<ProtocolCmd>() {
            public ProtocolCmd findValueByNumber(int number) {
              return ProtocolCmd.forNumber(number);
            }
          };

    public final com.google.protobuf.Descriptors.EnumValueDescriptor
        getValueDescriptor() {
      if (this == UNRECOGNIZED) {
        throw new java.lang.IllegalStateException(
            "Can't get the descriptor of an unrecognized enum value.");
      }
      return getDescriptor().getValues().get(ordinal());
    }
    public final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptorForType() {
      return getDescriptor();
    }
    public static final com.google.protobuf.Descriptors.EnumDescriptor
        getDescriptor() {
      return com.game.proto.ProtocolProto.getDescriptor().getEnumTypes().get(0);
    }

    private static final ProtocolCmd[] VALUES = values();

    public static ProtocolCmd valueOf(
        com.google.protobuf.Descriptors.EnumValueDescriptor desc) {
      if (desc.getType() != getDescriptor()) {
        throw new java.lang.IllegalArgumentException(
          "EnumValueDescriptor is not for this type.");
      }
      if (desc.getIndex() == -1) {
        return UNRECOGNIZED;
      }
      return VALUES[desc.getIndex()];
    }

    private final int value;

    private ProtocolCmd(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:protocol.ProtocolCmd)
  }


  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\016protocol.proto\022\010protocol*\217\002\n\013ProtocolC" +
      "md\022\010\n\004NONE\020\000\022\023\n\rADD_LEVEL_REQ\020\261\333\006\022\024\n\016ADD" +
      "_LEVEL_RESP\020\321\350\014\022\024\n\016HEART_BEAT_REQ\020\240\215\006\022\025\n" +
      "\017HEART_BEAT_RESP\020\300\232\014\022\024\n\016ENTER_GAME_REQ\020\241" +
      "\215\006\022\025\n\017ENTER_GAME_RESP\020\301\232\014\022\032\n\024PLAYER_UPDA" +
      "TE_NOTIFY\020\343\247\022\022\034\n\026PLAYER_NEXT_DAY_NOTIFY\020" +
      "\344\247\022\022\032\n\024PLAYER_RECONNECT_REQ\020\257\215\006\022\033\n\025PLAYE" +
      "R_RECONNECT_RESP\020\317\232\014B\037\n\016com.game.protoB\r" +
      "ProtocolProtob\006proto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
  }

  // @@protoc_insertion_point(outer_class_scope)
}