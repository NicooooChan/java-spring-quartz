package com.quartz.demo.util;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.annotation.JSONType;
import org.apache.commons.lang3.StringUtils;

/**
 * 接口返回的对象
 * @author 艾准
 */
@JSONType(orders={"success","code","msg","data"})//设置排序规则
public class BaseReturn {
	private static final int CODE_SUCCESS = 0;//返回CODE。0=成功

	private boolean success = true;		//是否成功
	private int code = CODE_SUCCESS;	//默认成功0
	private String msg;		//附带信息
	private Object data;	//附带数据

	public BaseReturn() {
		super();
	}
	/**
	 * 构造失败的
	 * code不能为0
	 */
	public BaseReturn(int code, String msg) {
		this.success = false;
		this.code = code;
		this.msg = msg;
	}
	/**
	 * 构造一个成功的结果，无数据
	 * @param msg
	 */
	public BaseReturn(String msg) {
		this.msg = msg;
	}
	/**
	 * 构造一个成功的结果，带数据
	 * @param msg
	 */
	public BaseReturn(String msg, Object data) {
		this.msg = msg;
		this.data = data;
	}
	/**
	 * 转JSONString
	 * 过时，请采用toJSONString(String dateFormat)
	 * @return
	 */
	@Deprecated
	public String toJSONString(){
		return JSON.toJSONString(this);
	}
	/**
	 * 转JSONString
	 * 可以传入日期格式，比如"yyyy-MM-dd HH:mm:ss"
	 * 传入NULL，将采用默认格式"yyyy-MM-dd HH:mm:ss"
	 * @return
	 */
	public String toJSONString(String dateFormat){
		if(StringUtils.isBlank(dateFormat)){
			dateFormat = "yyyy-MM-dd HH:mm:ss";
		}
		return JSON.toJSONStringWithDateFormat(this, dateFormat);
	}

	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public Object getData() {
		return data;
	}
	public void setData(Object data) {
		this.data = data;
	}
	public boolean isSuccess() {
		return success;
	}
	public void setSuccess(boolean success) {
		this.success = success;
	}
}
