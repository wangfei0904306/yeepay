package com.example.yeepay.utils;

//import sun.plugin2.message.Message;

/**
 * 封装json结果集
 * 
 */
public class JsonResult {

	private Boolean success = false;// 返回是否成功
	private String msg = "";// 返回信息
	private Object obj = null;// 返回其他对象信息
	private Integer type = 0;// 返回消息类型,用于复杂返回信息,0代表普通返回信息,1代表特殊返回信息

	public JsonResult() {
	}

	public JsonResult(Boolean success, String msg) {
		this.success = success;
		this.msg = msg;
	}

	public JsonResult(Boolean success, String msg, Integer type) {
		this.success = success;
		this.msg = msg;
		this.type = type;
	}

	public JsonResult(Boolean success, String msg, Object obj, Integer type) {
		this.success = success;
		this.msg = msg;
		this.obj = obj;
		this.type = type;
	}

	public Boolean getSuccess() {
		return success;
	}

	public void setSuccess(Boolean success) {
		this.success = success;
	}

	public String getMsg() {
		return msg;
	}

	public void setMsg(String msg) {
		this.msg = msg;
	}

	public Object getObj() {
		return obj;
	}

	public void setObj(Object obj) {
		this.obj = obj;
	}

	public Integer getType() {
		return type;
	}

	public void setType(Integer type) {
		this.type = type;
	}


	// ==========  static methods

	public static JsonResult resultError(String message){
		return resultError(message,0);
	}

	public static JsonResult resultError(String message,int type){
		return new JsonResult(false,message,0);
	}

	public static JsonResult resultSuccess(Object obj){
		return resultSuccess("",obj);
	}

	public static JsonResult resultSuccess(String message,Object obj){
		return resultSuccess(message,obj,0);
	}

	public static JsonResult resultSuccess(String message,Object obj,int type){
		return new JsonResult(true, message,obj,type);
	}

}
