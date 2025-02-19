package com.quartz.demo.util;

public class BaseReturnCode {
	/** 非法操作 **/
	public static final int ILLEGAL = -1;

	/** 参数错误 **/
	public static final int PARAMS_ERROR = 10000;
	
	/** 业务处理失败 **/
	public static final int PROCESS_ERROR = 20000;
	/** 服务商登录：不是服务商 **/
	public static final int FWS_LOGIN_NOTFWS = 20001;
	/** 服务商登录：未激活 **/
	public static final int FWS_LOGIN_WJH = 20002;
	
	/** SessionId错误 **/
	public static final int SESSIONID_ERROR = 30001;
	/** SessionId为空 **/
	public static final int SESSIONID_EMPTY = 30002;
	/** SessionId过期 **/
	public static final int SESSIONID_EXPIRED = 30003;
	/** SessionId解析失败 **/
	public static final int SESSIONID_PARSE_FAIL = 30004;
	
	/** 当期用户没有权限进行此操作 **/
	public static final int PERMISSION_NO = 30050;
	/** 权限解析失败 **/
	public static final int PERMISSION_PARSE_FAIL = 30051;

}
