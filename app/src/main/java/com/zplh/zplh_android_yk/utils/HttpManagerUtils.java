package com.zplh.zplh_android_yk.utils;

import android.content.Context;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;

import org.xutils.HttpManager;
import org.xutils.common.Callback;
import org.xutils.ex.HttpException;
import org.xutils.http.RequestParams;
import org.xutils.x;

/**
 *
 */
public class HttpManagerUtils {

    private static HttpManagerUtils instance;
    //private LoadDataDialog loadDataDialog;
    //private int count = 0;

    private HttpManagerUtils() {
    }

    public static HttpManagerUtils getInstance() {
        if (instance == null) {
            synchronized (HttpManagerUtils.class) {
                if (instance == null) {
                    instance = new HttpManagerUtils();
                }
            }
        }
        return instance;
    }

    /**
     * 不需要loading页面的请求
     *
     * @param
     * @param parameters
     * @param callback
     */
    public void sendRequest(final RequestParams parameters, final HttpObjectCallbackUtils callback) {

        sendRequest(null, parameters, callback);
    }

    /**
     * 需要loading页面的网络请求
     *
     * @param context
     * @param
     * @param parameters
     * @param callback
     */
    @SuppressWarnings("rawtypes")
    public void sendRequest(final Context context, final RequestParams parameters, final HttpObjectCallbackUtils callback) {

        //RequestParams params = new RequestParams(url);
        //params.addQueryStringParameter("key", "value");
        //params.addHeader("Content-Type", "application/x-www-form-urlencoded");

        x.http().get(parameters, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //打印响应结果字符串
//                LogUtils.i("响应结果json数据-----------------------------" + result);
                Log.e("WG", "响应结果json----------- " + result);
                // 解析
                // 获得json root
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(result);
                JsonObject root = element.getAsJsonObject();
                // 获得 ret 节点
                JsonPrimitive StatusJson = root.getAsJsonPrimitive("ret");
                int ret = StatusJson.getAsInt();// 获得值
                if (ret == 200) {
                    // 解析Data节点
                    if (callback != null) {
                        callback.onSuccess(new Gson().fromJson(result, callback.getClazz()));
                    }

                    // 存储用户
                    // 页面跳转
                } else {
                    // 解析错误的
                    int errorCode = ret;
                    // 获得错误信息
                    JsonPrimitive errorStringJson = root.getAsJsonPrimitive("msg");
                    String errorString = errorStringJson.getAsString();
                   /* if ("非法请求".equals(errorString)) {
                        errorString = "请登录";
                    }*/

                    if (callback != null) {
                        callback.onFailure(errorCode, errorString);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
//                    LogUtils.i("网络连接异常" + responseCode + "----" + responseMsg);
                    Log.e("WG", "网络连接异常 " + responseCode + "----" + responseMsg);
                    /*if (parameters != null) {
                        for (Map.Entry<String, String> me : parameters.entrySet()) {
                            //打印请求参数
                            LogUtils.i("网络连接异常" + me.getKey() + "=" + me.getValue());
                        }
                    }*/
                    BMExceptionUtils bmException = new BMExceptionUtils(responseCode);
                    if (callback != null) {
                        callback.onFailure(responseCode, bmException.getMessage());
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure(0, "网络连接失败");
                    }
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });
    }

    /**
     * post
     *
     * @param
     * @param
     * @param parameters
     * @param callback
     */
    @SuppressWarnings("rawtypes")
    public void sendPostRequest(final RequestParams parameters, final HttpObjectCallbackUtils callback) {

        //RequestParams params = new RequestParams(url);
        //params.addQueryStringParameter("key", "value");
        //params.addHeader("Content-Type", "application/x-www-form-urlencoded");

        x.http().post(parameters, new Callback.CommonCallback<String>() {
            @Override
            public void onSuccess(String result) {
                //打印响应结果字符串
                Log.e("WG", "响应结果json数据-----------------------------" + result);
                // 解析
                // 获得json root
                JsonParser parser = new JsonParser();
                JsonElement element = parser.parse(result);
                JsonObject root = element.getAsJsonObject();
                // 获得 retCode 节点
                JsonPrimitive StatusJson = root.getAsJsonPrimitive("ret");
                int retCode = StatusJson.getAsInt();// 获得值
                if (retCode == 200) {
                    // 解析Data节点
                    if (callback != null) {
                        callback.onSuccess(new Gson().fromJson(result, callback.getClazz()));
                    }

                    // 存储用户
                    // 页面跳转
                } else {
                    // 解析错误的
                    int errorCode = retCode;
                    // 获得错误信息
                    JsonPrimitive errorStringJson = root.getAsJsonPrimitive("msg");
                    String errorString = errorStringJson.getAsString();
                    /*if ("非法请求".equals(errorString)) {
                        errorString = "请登录";
                    }*/

                    if (callback != null) {
                        callback.onFailure(errorCode, errorString);
                    }
                }
            }

            @Override
            public void onError(Throwable ex, boolean isOnCallback) {

                if (ex instanceof HttpException) { // 网络错误
                    HttpException httpEx = (HttpException) ex;
                    int responseCode = httpEx.getCode();
                    String responseMsg = httpEx.getMessage();
//                    LogUtils.i("网络连接异常" + responseCode + "----" + responseMsg);
                    Log.e("WG", "网络连接异常" + responseCode + "----" + responseMsg);
                    /*if (parameters != null) {
                        for (Map.Entry<String, String> me : parameters.entrySet()) {
                            //打印请求参数
                            LogUtils.i("网络连接异常" + me.getKey() + "=" + me.getValue());
                        }
                    }*/
                    BMExceptionUtils bmException = new BMExceptionUtils(responseCode);
                    if (callback != null) {
                        callback.onFailure(responseCode, bmException.getMessage());
                    }
                } else {
                    if (callback != null) {
                        callback.onFailure(0, "网络连接失败");
                    }
                }

            }

            @Override
            public void onCancelled(CancelledException cex) {
            }

            @Override
            public void onFinished() {

            }
        });
    }
}
