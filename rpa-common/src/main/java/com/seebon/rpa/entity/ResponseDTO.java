package com.seebon.rpa.entity;

import java.io.Serializable;

/**
 * <b>Description</b>:<br/>
 * 本处为手机提供服务接口,response enity
 * 基础返回值构造
 * <br/>
 * <b>CreatedTime</b>:2015年12月7日下午5:16:09 <br/>
 * <b>Environment</b>:Mac OSX 10.10.5 <br/>
 * @author king.peng(pengdeyi)<br/>
 */
public class ResponseDTO<T> implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1155304937107668153L;
	
	private String code;
	
	private String message;
	
	private T data;

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
