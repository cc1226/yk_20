package com.zplh.zplh_android_yk.utils;

import com.google.gson.Gson;

/**
 * Created by Administrator on 2017/7/19.
 */

public class GsonUtil {
    //将Json数据解析成相应的映射对象
    public static <T> T parseJsonWithGson(String jsonData, Class<T> type) {
        Gson gson = new Gson();
        T result = gson.fromJson(jsonData, type);
        return result;
    }
}
