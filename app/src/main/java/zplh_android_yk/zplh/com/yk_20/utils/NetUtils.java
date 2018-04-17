package zplh_android_yk.zplh.com.yk_20.utils;

import android.os.Environment;

import com.orhanobut.logger.Logger;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.builder.GetBuilder;
import com.zhy.http.okhttp.callback.Callback;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import zplh_android_yk.zplh.com.yk_20.constant.URLS;

/**
 * Created by yong hao zeng on 2018/4/16/016.
 */
public class NetUtils {
            //同步网络请求  获取File
        public static File getApk(String server) throws IOException {

            // 如果相等的话表示当前的sdcard挂载在手机上并且是可用的
            if (Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                URL url = new URL(server);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setConnectTimeout(25000);
                // 获取到文件的大小
                InputStream is = conn.getInputStream();
                File file = new File(Environment.getExternalStorageDirectory(), "wxykupdata.apk");
                FileOutputStream fos = new FileOutputStream(file);
                BufferedInputStream bis = new BufferedInputStream(is);
                byte[] buffer = new byte[1024];
                int len;
                int total = 0;
                while ((len = bis.read(buffer)) != -1) {
                    fos.write(buffer, 0, len);
                    total += len;
                }
                fos.close();
                bis.close();
                is.close();
                return file;
            } else {
                return null;
            }
        }

    /**
     * 异步get请求
     * @param url
     * @param params
     */
    public static void get_excute(String url, Map<String,String> params, Callback callback){
        Logger.t("网络请求").d(url,params);
        GetBuilder url1 = OkHttpUtils.get().url(URLS.updata_task_status());
        if (params==null)
            url1.build().execute(callback);
        else
            url1.params(params).build().execute(callback);
    }





}
