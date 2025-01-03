// Generated by the protocol buffer compiler.  DO NOT EDIT!
// source: error.proto

package com.game.proto;

public final class ErrorProto {
  private ErrorProto() {}
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
   * 错误码常量定义
   * </pre>
   *
   * Protobuf enum {@code error.ErrorCode}
   */
  public enum ErrorCode
      implements com.google.protobuf.ProtocolMessageEnum {
    /**
     * <pre>
     * 错误码常量定义
     * </pre>
     *
     * <code>NO_ERROR = 0;</code>
     */
    NO_ERROR(0),
    /**
     * <pre>
     * 登录验证失败
     * </pre>
     *
     * <code>LOGIN_VERIFY_FAIL = 10000;</code>
     */
    LOGIN_VERIFY_FAIL(10000),
    /**
     * <pre>
     * 客户端参数有误
     * </pre>
     *
     * <code>SENT_PARAM_ERROR = 10001;</code>
     */
    SENT_PARAM_ERROR(10001),
    /**
     * <pre>
     * 配置参数出错
     * </pre>
     *
     * <code>RES_PARAM_ERROR = 10002;</code>
     */
    RES_PARAM_ERROR(10002),
    UNRECOGNIZED(-1),
    ;

    /**
     * <pre>
     * 错误码常量定义
     * </pre>
     *
     * <code>NO_ERROR = 0;</code>
     */
    public static final int NO_ERROR_VALUE = 0;
    /**
     * <pre>
     * 登录验证失败
     * </pre>
     *
     * <code>LOGIN_VERIFY_FAIL = 10000;</code>
     */
    public static final int LOGIN_VERIFY_FAIL_VALUE = 10000;
    /**
     * <pre>
     * 客户端参数有误
     * </pre>
     *
     * <code>SENT_PARAM_ERROR = 10001;</code>
     */
    public static final int SENT_PARAM_ERROR_VALUE = 10001;
    /**
     * <pre>
     * 配置参数出错
     * </pre>
     *
     * <code>RES_PARAM_ERROR = 10002;</code>
     */
    public static final int RES_PARAM_ERROR_VALUE = 10002;


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
    public static ErrorCode valueOf(int value) {
      return forNumber(value);
    }

    /**
     * @param value The numeric wire value of the corresponding enum entry.
     * @return The enum associated with the given numeric wire value.
     */
    public static ErrorCode forNumber(int value) {
      switch (value) {
        case 0: return NO_ERROR;
        case 10000: return LOGIN_VERIFY_FAIL;
        case 10001: return SENT_PARAM_ERROR;
        case 10002: return RES_PARAM_ERROR;
        default: return null;
      }
    }

    public static com.google.protobuf.Internal.EnumLiteMap<ErrorCode>
        internalGetValueMap() {
      return internalValueMap;
    }
    private static final com.google.protobuf.Internal.EnumLiteMap<
        ErrorCode> internalValueMap =
          new com.google.protobuf.Internal.EnumLiteMap<ErrorCode>() {
            public ErrorCode findValueByNumber(int number) {
              return ErrorCode.forNumber(number);
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
      return com.game.proto.ErrorProto.getDescriptor().getEnumTypes().get(0);
    }

    private static final ErrorCode[] VALUES = values();

    public static ErrorCode valueOf(
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

    private ErrorCode(int value) {
      this.value = value;
    }

    // @@protoc_insertion_point(enum_scope:error.ErrorCode)
  }

  public interface ErrorRespOrBuilder extends
      // @@protoc_insertion_point(interface_extends:error.ErrorResp)
      com.google.protobuf.MessageOrBuilder {

    /**
     * <pre>
     * 错误码
     * </pre>
     *
     * <code>optional int32 error_code = 1;</code>
     * @return Whether the errorCode field is set.
     */
    boolean hasErrorCode();
    /**
     * <pre>
     * 错误码
     * </pre>
     *
     * <code>optional int32 error_code = 1;</code>
     * @return The errorCode.
     */
    int getErrorCode();

    /**
     * <pre>
     * 错误内容
     * </pre>
     *
     * <code>optional string error_msg = 2;</code>
     * @return Whether the errorMsg field is set.
     */
    boolean hasErrorMsg();
    /**
     * <pre>
     * 错误内容
     * </pre>
     *
     * <code>optional string error_msg = 2;</code>
     * @return The errorMsg.
     */
    java.lang.String getErrorMsg();
    /**
     * <pre>
     * 错误内容
     * </pre>
     *
     * <code>optional string error_msg = 2;</code>
     * @return The bytes for errorMsg.
     */
    com.google.protobuf.ByteString
        getErrorMsgBytes();
  }
  /**
   * <pre>
   * 错误码消息
   * </pre>
   *
   * Protobuf type {@code error.ErrorResp}
   */
  public static final class ErrorResp extends
      com.google.protobuf.GeneratedMessageV3 implements
      // @@protoc_insertion_point(message_implements:error.ErrorResp)
      ErrorRespOrBuilder {
  private static final long serialVersionUID = 0L;
    // Use ErrorResp.newBuilder() to construct.
    private ErrorResp(com.google.protobuf.GeneratedMessageV3.Builder<?> builder) {
      super(builder);
    }
    private ErrorResp() {
      errorMsg_ = "";
    }

    @java.lang.Override
    @SuppressWarnings({"unused"})
    protected java.lang.Object newInstance(
        UnusedPrivateParameter unused) {
      return new ErrorResp();
    }

    public static final com.google.protobuf.Descriptors.Descriptor
        getDescriptor() {
      return com.game.proto.ErrorProto.internal_static_error_ErrorResp_descriptor;
    }

    @java.lang.Override
    protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
        internalGetFieldAccessorTable() {
      return com.game.proto.ErrorProto.internal_static_error_ErrorResp_fieldAccessorTable
          .ensureFieldAccessorsInitialized(
              com.game.proto.ErrorProto.ErrorResp.class, com.game.proto.ErrorProto.ErrorResp.Builder.class);
    }

    private int bitField0_;
    public static final int ERROR_CODE_FIELD_NUMBER = 1;
    private int errorCode_ = 0;
    /**
     * <pre>
     * 错误码
     * </pre>
     *
     * <code>optional int32 error_code = 1;</code>
     * @return Whether the errorCode field is set.
     */
    @java.lang.Override
    public boolean hasErrorCode() {
      return ((bitField0_ & 0x00000001) != 0);
    }
    /**
     * <pre>
     * 错误码
     * </pre>
     *
     * <code>optional int32 error_code = 1;</code>
     * @return The errorCode.
     */
    @java.lang.Override
    public int getErrorCode() {
      return errorCode_;
    }

    public static final int ERROR_MSG_FIELD_NUMBER = 2;
    @SuppressWarnings("serial")
    private volatile java.lang.Object errorMsg_ = "";
    /**
     * <pre>
     * 错误内容
     * </pre>
     *
     * <code>optional string error_msg = 2;</code>
     * @return Whether the errorMsg field is set.
     */
    @java.lang.Override
    public boolean hasErrorMsg() {
      return ((bitField0_ & 0x00000002) != 0);
    }
    /**
     * <pre>
     * 错误内容
     * </pre>
     *
     * <code>optional string error_msg = 2;</code>
     * @return The errorMsg.
     */
    @java.lang.Override
    public java.lang.String getErrorMsg() {
      java.lang.Object ref = errorMsg_;
      if (ref instanceof java.lang.String) {
        return (java.lang.String) ref;
      } else {
        com.google.protobuf.ByteString bs = 
            (com.google.protobuf.ByteString) ref;
        java.lang.String s = bs.toStringUtf8();
        errorMsg_ = s;
        return s;
      }
    }
    /**
     * <pre>
     * 错误内容
     * </pre>
     *
     * <code>optional string error_msg = 2;</code>
     * @return The bytes for errorMsg.
     */
    @java.lang.Override
    public com.google.protobuf.ByteString
        getErrorMsgBytes() {
      java.lang.Object ref = errorMsg_;
      if (ref instanceof java.lang.String) {
        com.google.protobuf.ByteString b = 
            com.google.protobuf.ByteString.copyFromUtf8(
                (java.lang.String) ref);
        errorMsg_ = b;
        return b;
      } else {
        return (com.google.protobuf.ByteString) ref;
      }
    }

    private byte memoizedIsInitialized = -1;
    @java.lang.Override
    public final boolean isInitialized() {
      byte isInitialized = memoizedIsInitialized;
      if (isInitialized == 1) return true;
      if (isInitialized == 0) return false;

      memoizedIsInitialized = 1;
      return true;
    }

    @java.lang.Override
    public void writeTo(com.google.protobuf.CodedOutputStream output)
                        throws java.io.IOException {
      if (((bitField0_ & 0x00000001) != 0)) {
        output.writeInt32(1, errorCode_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        com.google.protobuf.GeneratedMessageV3.writeString(output, 2, errorMsg_);
      }
      getUnknownFields().writeTo(output);
    }

    @java.lang.Override
    public int getSerializedSize() {
      int size = memoizedSize;
      if (size != -1) return size;

      size = 0;
      if (((bitField0_ & 0x00000001) != 0)) {
        size += com.google.protobuf.CodedOutputStream
          .computeInt32Size(1, errorCode_);
      }
      if (((bitField0_ & 0x00000002) != 0)) {
        size += com.google.protobuf.GeneratedMessageV3.computeStringSize(2, errorMsg_);
      }
      size += getUnknownFields().getSerializedSize();
      memoizedSize = size;
      return size;
    }

    @java.lang.Override
    public boolean equals(final java.lang.Object obj) {
      if (obj == this) {
       return true;
      }
      if (!(obj instanceof com.game.proto.ErrorProto.ErrorResp)) {
        return super.equals(obj);
      }
      com.game.proto.ErrorProto.ErrorResp other = (com.game.proto.ErrorProto.ErrorResp) obj;

      if (hasErrorCode() != other.hasErrorCode()) return false;
      if (hasErrorCode()) {
        if (getErrorCode()
            != other.getErrorCode()) return false;
      }
      if (hasErrorMsg() != other.hasErrorMsg()) return false;
      if (hasErrorMsg()) {
        if (!getErrorMsg()
            .equals(other.getErrorMsg())) return false;
      }
      if (!getUnknownFields().equals(other.getUnknownFields())) return false;
      return true;
    }

    @java.lang.Override
    public int hashCode() {
      if (memoizedHashCode != 0) {
        return memoizedHashCode;
      }
      int hash = 41;
      hash = (19 * hash) + getDescriptor().hashCode();
      if (hasErrorCode()) {
        hash = (37 * hash) + ERROR_CODE_FIELD_NUMBER;
        hash = (53 * hash) + getErrorCode();
      }
      if (hasErrorMsg()) {
        hash = (37 * hash) + ERROR_MSG_FIELD_NUMBER;
        hash = (53 * hash) + getErrorMsg().hashCode();
      }
      hash = (29 * hash) + getUnknownFields().hashCode();
      memoizedHashCode = hash;
      return hash;
    }

    public static com.game.proto.ErrorProto.ErrorResp parseFrom(
        java.nio.ByteBuffer data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.game.proto.ErrorProto.ErrorResp parseFrom(
        java.nio.ByteBuffer data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.game.proto.ErrorProto.ErrorResp parseFrom(
        com.google.protobuf.ByteString data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.game.proto.ErrorProto.ErrorResp parseFrom(
        com.google.protobuf.ByteString data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.game.proto.ErrorProto.ErrorResp parseFrom(byte[] data)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data);
    }
    public static com.game.proto.ErrorProto.ErrorResp parseFrom(
        byte[] data,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws com.google.protobuf.InvalidProtocolBufferException {
      return PARSER.parseFrom(data, extensionRegistry);
    }
    public static com.game.proto.ErrorProto.ErrorResp parseFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.game.proto.ErrorProto.ErrorResp parseFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.game.proto.ErrorProto.ErrorResp parseDelimitedFrom(java.io.InputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input);
    }
    public static com.game.proto.ErrorProto.ErrorResp parseDelimitedFrom(
        java.io.InputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseDelimitedWithIOException(PARSER, input, extensionRegistry);
    }
    public static com.game.proto.ErrorProto.ErrorResp parseFrom(
        com.google.protobuf.CodedInputStream input)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input);
    }
    public static com.game.proto.ErrorProto.ErrorResp parseFrom(
        com.google.protobuf.CodedInputStream input,
        com.google.protobuf.ExtensionRegistryLite extensionRegistry)
        throws java.io.IOException {
      return com.google.protobuf.GeneratedMessageV3
          .parseWithIOException(PARSER, input, extensionRegistry);
    }

    @java.lang.Override
    public Builder newBuilderForType() { return newBuilder(); }
    public static Builder newBuilder() {
      return DEFAULT_INSTANCE.toBuilder();
    }
    public static Builder newBuilder(com.game.proto.ErrorProto.ErrorResp prototype) {
      return DEFAULT_INSTANCE.toBuilder().mergeFrom(prototype);
    }
    @java.lang.Override
    public Builder toBuilder() {
      return this == DEFAULT_INSTANCE
          ? new Builder() : new Builder().mergeFrom(this);
    }

    @java.lang.Override
    protected Builder newBuilderForType(
        com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
      Builder builder = new Builder(parent);
      return builder;
    }
    /**
     * <pre>
     * 错误码消息
     * </pre>
     *
     * Protobuf type {@code error.ErrorResp}
     */
    public static final class Builder extends
        com.google.protobuf.GeneratedMessageV3.Builder<Builder> implements
        // @@protoc_insertion_point(builder_implements:error.ErrorResp)
        com.game.proto.ErrorProto.ErrorRespOrBuilder {
      public static final com.google.protobuf.Descriptors.Descriptor
          getDescriptor() {
        return com.game.proto.ErrorProto.internal_static_error_ErrorResp_descriptor;
      }

      @java.lang.Override
      protected com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
          internalGetFieldAccessorTable() {
        return com.game.proto.ErrorProto.internal_static_error_ErrorResp_fieldAccessorTable
            .ensureFieldAccessorsInitialized(
                com.game.proto.ErrorProto.ErrorResp.class, com.game.proto.ErrorProto.ErrorResp.Builder.class);
      }

      // Construct using com.game.proto.ErrorProto.ErrorResp.newBuilder()
      private Builder() {

      }

      private Builder(
          com.google.protobuf.GeneratedMessageV3.BuilderParent parent) {
        super(parent);

      }
      @java.lang.Override
      public Builder clear() {
        super.clear();
        bitField0_ = 0;
        errorCode_ = 0;
        errorMsg_ = "";
        return this;
      }

      @java.lang.Override
      public com.google.protobuf.Descriptors.Descriptor
          getDescriptorForType() {
        return com.game.proto.ErrorProto.internal_static_error_ErrorResp_descriptor;
      }

      @java.lang.Override
      public com.game.proto.ErrorProto.ErrorResp getDefaultInstanceForType() {
        return com.game.proto.ErrorProto.ErrorResp.getDefaultInstance();
      }

      @java.lang.Override
      public com.game.proto.ErrorProto.ErrorResp build() {
        com.game.proto.ErrorProto.ErrorResp result = buildPartial();
        if (!result.isInitialized()) {
          throw newUninitializedMessageException(result);
        }
        return result;
      }

      @java.lang.Override
      public com.game.proto.ErrorProto.ErrorResp buildPartial() {
        com.game.proto.ErrorProto.ErrorResp result = new com.game.proto.ErrorProto.ErrorResp(this);
        if (bitField0_ != 0) { buildPartial0(result); }
        onBuilt();
        return result;
      }

      private void buildPartial0(com.game.proto.ErrorProto.ErrorResp result) {
        int from_bitField0_ = bitField0_;
        int to_bitField0_ = 0;
        if (((from_bitField0_ & 0x00000001) != 0)) {
          result.errorCode_ = errorCode_;
          to_bitField0_ |= 0x00000001;
        }
        if (((from_bitField0_ & 0x00000002) != 0)) {
          result.errorMsg_ = errorMsg_;
          to_bitField0_ |= 0x00000002;
        }
        result.bitField0_ |= to_bitField0_;
      }

      @java.lang.Override
      public Builder mergeFrom(com.google.protobuf.Message other) {
        if (other instanceof com.game.proto.ErrorProto.ErrorResp) {
          return mergeFrom((com.game.proto.ErrorProto.ErrorResp)other);
        } else {
          super.mergeFrom(other);
          return this;
        }
      }

      public Builder mergeFrom(com.game.proto.ErrorProto.ErrorResp other) {
        if (other == com.game.proto.ErrorProto.ErrorResp.getDefaultInstance()) return this;
        if (other.hasErrorCode()) {
          setErrorCode(other.getErrorCode());
        }
        if (other.hasErrorMsg()) {
          errorMsg_ = other.errorMsg_;
          bitField0_ |= 0x00000002;
          onChanged();
        }
        this.mergeUnknownFields(other.getUnknownFields());
        onChanged();
        return this;
      }

      @java.lang.Override
      public final boolean isInitialized() {
        return true;
      }

      @java.lang.Override
      public Builder mergeFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws java.io.IOException {
        if (extensionRegistry == null) {
          throw new java.lang.NullPointerException();
        }
        try {
          boolean done = false;
          while (!done) {
            int tag = input.readTag();
            switch (tag) {
              case 0:
                done = true;
                break;
              case 8: {
                errorCode_ = input.readInt32();
                bitField0_ |= 0x00000001;
                break;
              } // case 8
              case 18: {
                errorMsg_ = input.readStringRequireUtf8();
                bitField0_ |= 0x00000002;
                break;
              } // case 18
              default: {
                if (!super.parseUnknownField(input, extensionRegistry, tag)) {
                  done = true; // was an endgroup tag
                }
                break;
              } // default:
            } // switch (tag)
          } // while (!done)
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.unwrapIOException();
        } finally {
          onChanged();
        } // finally
        return this;
      }
      private int bitField0_;

      private int errorCode_ ;
      /**
       * <pre>
       * 错误码
       * </pre>
       *
       * <code>optional int32 error_code = 1;</code>
       * @return Whether the errorCode field is set.
       */
      @java.lang.Override
      public boolean hasErrorCode() {
        return ((bitField0_ & 0x00000001) != 0);
      }
      /**
       * <pre>
       * 错误码
       * </pre>
       *
       * <code>optional int32 error_code = 1;</code>
       * @return The errorCode.
       */
      @java.lang.Override
      public int getErrorCode() {
        return errorCode_;
      }
      /**
       * <pre>
       * 错误码
       * </pre>
       *
       * <code>optional int32 error_code = 1;</code>
       * @param value The errorCode to set.
       * @return This builder for chaining.
       */
      public Builder setErrorCode(int value) {

        errorCode_ = value;
        bitField0_ |= 0x00000001;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 错误码
       * </pre>
       *
       * <code>optional int32 error_code = 1;</code>
       * @return This builder for chaining.
       */
      public Builder clearErrorCode() {
        bitField0_ = (bitField0_ & ~0x00000001);
        errorCode_ = 0;
        onChanged();
        return this;
      }

      private java.lang.Object errorMsg_ = "";
      /**
       * <pre>
       * 错误内容
       * </pre>
       *
       * <code>optional string error_msg = 2;</code>
       * @return Whether the errorMsg field is set.
       */
      public boolean hasErrorMsg() {
        return ((bitField0_ & 0x00000002) != 0);
      }
      /**
       * <pre>
       * 错误内容
       * </pre>
       *
       * <code>optional string error_msg = 2;</code>
       * @return The errorMsg.
       */
      public java.lang.String getErrorMsg() {
        java.lang.Object ref = errorMsg_;
        if (!(ref instanceof java.lang.String)) {
          com.google.protobuf.ByteString bs =
              (com.google.protobuf.ByteString) ref;
          java.lang.String s = bs.toStringUtf8();
          errorMsg_ = s;
          return s;
        } else {
          return (java.lang.String) ref;
        }
      }
      /**
       * <pre>
       * 错误内容
       * </pre>
       *
       * <code>optional string error_msg = 2;</code>
       * @return The bytes for errorMsg.
       */
      public com.google.protobuf.ByteString
          getErrorMsgBytes() {
        java.lang.Object ref = errorMsg_;
        if (ref instanceof String) {
          com.google.protobuf.ByteString b = 
              com.google.protobuf.ByteString.copyFromUtf8(
                  (java.lang.String) ref);
          errorMsg_ = b;
          return b;
        } else {
          return (com.google.protobuf.ByteString) ref;
        }
      }
      /**
       * <pre>
       * 错误内容
       * </pre>
       *
       * <code>optional string error_msg = 2;</code>
       * @param value The errorMsg to set.
       * @return This builder for chaining.
       */
      public Builder setErrorMsg(
          java.lang.String value) {
        if (value == null) { throw new NullPointerException(); }
        errorMsg_ = value;
        bitField0_ |= 0x00000002;
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 错误内容
       * </pre>
       *
       * <code>optional string error_msg = 2;</code>
       * @return This builder for chaining.
       */
      public Builder clearErrorMsg() {
        errorMsg_ = getDefaultInstance().getErrorMsg();
        bitField0_ = (bitField0_ & ~0x00000002);
        onChanged();
        return this;
      }
      /**
       * <pre>
       * 错误内容
       * </pre>
       *
       * <code>optional string error_msg = 2;</code>
       * @param value The bytes for errorMsg to set.
       * @return This builder for chaining.
       */
      public Builder setErrorMsgBytes(
          com.google.protobuf.ByteString value) {
        if (value == null) { throw new NullPointerException(); }
        checkByteStringIsUtf8(value);
        errorMsg_ = value;
        bitField0_ |= 0x00000002;
        onChanged();
        return this;
      }
      @java.lang.Override
      public final Builder setUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.setUnknownFields(unknownFields);
      }

      @java.lang.Override
      public final Builder mergeUnknownFields(
          final com.google.protobuf.UnknownFieldSet unknownFields) {
        return super.mergeUnknownFields(unknownFields);
      }


      // @@protoc_insertion_point(builder_scope:error.ErrorResp)
    }

    // @@protoc_insertion_point(class_scope:error.ErrorResp)
    private static final com.game.proto.ErrorProto.ErrorResp DEFAULT_INSTANCE;
    static {
      DEFAULT_INSTANCE = new com.game.proto.ErrorProto.ErrorResp();
    }

    public static com.game.proto.ErrorProto.ErrorResp getDefaultInstance() {
      return DEFAULT_INSTANCE;
    }

    private static final com.google.protobuf.Parser<ErrorResp>
        PARSER = new com.google.protobuf.AbstractParser<ErrorResp>() {
      @java.lang.Override
      public ErrorResp parsePartialFrom(
          com.google.protobuf.CodedInputStream input,
          com.google.protobuf.ExtensionRegistryLite extensionRegistry)
          throws com.google.protobuf.InvalidProtocolBufferException {
        Builder builder = newBuilder();
        try {
          builder.mergeFrom(input, extensionRegistry);
        } catch (com.google.protobuf.InvalidProtocolBufferException e) {
          throw e.setUnfinishedMessage(builder.buildPartial());
        } catch (com.google.protobuf.UninitializedMessageException e) {
          throw e.asInvalidProtocolBufferException().setUnfinishedMessage(builder.buildPartial());
        } catch (java.io.IOException e) {
          throw new com.google.protobuf.InvalidProtocolBufferException(e)
              .setUnfinishedMessage(builder.buildPartial());
        }
        return builder.buildPartial();
      }
    };

    public static com.google.protobuf.Parser<ErrorResp> parser() {
      return PARSER;
    }

    @java.lang.Override
    public com.google.protobuf.Parser<ErrorResp> getParserForType() {
      return PARSER;
    }

    @java.lang.Override
    public com.game.proto.ErrorProto.ErrorResp getDefaultInstanceForType() {
      return DEFAULT_INSTANCE;
    }

  }

  private static final com.google.protobuf.Descriptors.Descriptor
    internal_static_error_ErrorResp_descriptor;
  private static final 
    com.google.protobuf.GeneratedMessageV3.FieldAccessorTable
      internal_static_error_ErrorResp_fieldAccessorTable;

  public static com.google.protobuf.Descriptors.FileDescriptor
      getDescriptor() {
    return descriptor;
  }
  private static  com.google.protobuf.Descriptors.FileDescriptor
      descriptor;
  static {
    java.lang.String[] descriptorData = {
      "\n\013error.proto\022\005error\"Y\n\tErrorResp\022\027\n\nerr" +
      "or_code\030\001 \001(\005H\000\210\001\001\022\026\n\terror_msg\030\002 \001(\tH\001\210" +
      "\001\001B\r\n\013_error_codeB\014\n\n_error_msg*^\n\tError" +
      "Code\022\014\n\010NO_ERROR\020\000\022\026\n\021LOGIN_VERIFY_FAIL\020" +
      "\220N\022\025\n\020SENT_PARAM_ERROR\020\221N\022\024\n\017RES_PARAM_E" +
      "RROR\020\222NB\034\n\016com.game.protoB\nErrorProtob\006p" +
      "roto3"
    };
    descriptor = com.google.protobuf.Descriptors.FileDescriptor
      .internalBuildGeneratedFileFrom(descriptorData,
        new com.google.protobuf.Descriptors.FileDescriptor[] {
        });
    internal_static_error_ErrorResp_descriptor =
      getDescriptor().getMessageTypes().get(0);
    internal_static_error_ErrorResp_fieldAccessorTable = new
      com.google.protobuf.GeneratedMessageV3.FieldAccessorTable(
        internal_static_error_ErrorResp_descriptor,
        new java.lang.String[] { "ErrorCode", "ErrorMsg", "ErrorCode", "ErrorMsg", });
  }

  // @@protoc_insertion_point(outer_class_scope)
}
