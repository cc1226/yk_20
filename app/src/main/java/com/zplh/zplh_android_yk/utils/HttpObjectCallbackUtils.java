package com.zplh.zplh_android_yk.utils;

import java.lang.reflect.ParameterizedType;

/**
 * @项目名: 	BaMaiPatient
 * @类名:	HttpObjectCallback
 * @公司:	88my.com
 * @创建者:	温开创
 * @创建时间:	2015-9-23	上午11:59:43 
 * @描述:	TODO
 * 
 * @svn版本:	$Rev$
 * @更新人:	$Author$
 * @更新时间:	$Date$
 * @更新描述:	TODO
 */
public abstract class HttpObjectCallbackUtils<T> {

	//反射获取类
	private Class<T> clazz;

	@SuppressWarnings("unchecked")
	public HttpObjectCallbackUtils() {
		ParameterizedType type = (ParameterizedType) getClass()
				.getGenericSuperclass();
		clazz = (Class<T>) type.getActualTypeArguments()[0];
	}

	public Class<T> getClazz() {
		return clazz;
	}
	//成功是获取bean
	public abstract void onSuccess(T bean);
	//失败时返回错误码和错误信息
	public abstract void onFailure(int errorCode, String errorString);

}
