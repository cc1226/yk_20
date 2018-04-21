package com.zplh.zplh_android_yk.utils;

import android.util.Log;


public class BMExceptionUtils extends Exception {

	private static final long serialVersionUID = 6099338502419098400L;

	private static final String TAG = BMExceptionUtils.class.getSimpleName();


	private int errorCode;
	private String message;

	public BMExceptionUtils(int errorCode) {
		switch (errorCode){
			case 0:
				message = "网络连接失败";
			break;
			case 400:
				message = "错误请求";
			break;
			case 404:
				message = "服务器找不到资源";
			break;
			case 500  :
				message = "内部服务器错误";
			break;
			case 503  :
				message = "无法获得服务";
			break;

		}
		this.setErrorCode(errorCode);
		this.setMessage(message);
	}

	public BMExceptionUtils(int errorCode, String message) {
		this.setErrorCode(errorCode);
		this.setMessage(message);
	}

	public int getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	@Override
	public String getMessage(){
		return message;
	}
	
	public void setMessage(String message){
		this.message = message == null ? "unknow" : message;
	}
	
	@Override
	public void printStackTrace() {
		Log.e(TAG, "error code: " + errorCode + " message: " + message);
		super.printStackTrace();
	}

}
