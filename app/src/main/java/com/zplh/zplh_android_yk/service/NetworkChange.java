package com.zplh.zplh_android_yk.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import com.zplh.zplh_android_yk.R;
import com.zplh.zplh_android_yk.utils.SPUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2017/8/8.
 */

/**
 * 监听网络状态的广播
 */
public class NetworkChange extends BroadcastReceiver {
    private AudioManager audioManage;
    public final int wifi = 2, mobile = 1, none = 0;
    public int oldState = none;
    public List<OnNetWorkChange> onNetWorkChange = new ArrayList<>();
    private static NetworkChange networkChange;
    private MediaPlayer mPlayer;

    public static NetworkChange getInstance() {
        if (networkChange == null) {
            networkChange = new NetworkChange();
        }
        return networkChange;
    }

    //回调接口
    public interface OnNetWorkChange {
        //返回各个（wifi,移动网络，没有网络）状态的值，上一个网络状态的值，当前的网络状态的值
        void onChange(int wifi, int mobile, int none, int oldStatus, int newStatus);
    }

    /**
     * 增加网络变化监听回调对象
     * 如果设置多个回调，请务必不要设置相同名字的OnNetWorkChange对象，否则会无效
     *
     * @param onNetWorkChange 回调对象
     */
    public void setOnNetWorkChange(OnNetWorkChange onNetWorkChange) {
        if (this.onNetWorkChange.contains(onNetWorkChange)) {
            return;
        }
        this.onNetWorkChange.add(onNetWorkChange);
//        LogUtils.i("网络状态", "添加一个回调。已设置：" + this.onNetWorkChange.size());
        Log.e("WG", "网络状态： "+ this.onNetWorkChange.size() );
    }

    /**
     * 取消网络变化监听监听回调
     *
     * @param onNetWorkChange 回调对象
     */
    public void delOnNetWorkChange(OnNetWorkChange onNetWorkChange) {
        if (this.onNetWorkChange.contains(onNetWorkChange)) {
            this.onNetWorkChange.remove(onNetWorkChange);
//            LogUtils.i("网络状态", "删除一个回调。还有：" + this.onNetWorkChange.size());
            Log.e("WG", "网络状态 "+ this.onNetWorkChange.size() );
        }
    }

    /**
     * 触发网络状态监听回调
     *
     * @param nowStatus 当前网络状态
     */
    private void setChange(int nowStatus) {

        for (OnNetWorkChange change : onNetWorkChange) {
            change.onChange(wifi, mobile, none, oldState, nowStatus);
        }
        oldState = nowStatus;
    }

    @Override
    public void onReceive(final Context context, Intent intent) {
        audioManage= (AudioManager) context.getSystemService(Service.AUDIO_SERVICE);
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        final NetworkInfo mobNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        final NetworkInfo wifiNetInfo = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (onNetWorkChange == null) {
            //当没有设置回调的时候，什么都不做
            return;
        }
        if (!mobNetInfo.isConnected() && !wifiNetInfo.isConnected()) {
//            LogUtils.i("通知", "网络不可以用");
            Log.e("WG", "网络不可用" );
            SPUtils.putBoolean(context,"isNetWork",false);
            setChange(none);
        } else if (mobNetInfo.isConnected()) {
//            LogUtils.i("通知", "仅移动网络可用");
            Log.e("WG", "仅移动网络可用 " );
            SPUtils.putBoolean(context,"isNetWork",true);
//            stop();
//            audioManage.setStreamMute(AudioManager.STREAM_SYSTEM, true);
//            audioManage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            setChange(mobile);
        } else if (wifiNetInfo.isConnected()) {
//            LogUtils.i("通知", "Wifi网络可用");
            Log.e("WG", "wifi网络可用 " );
//            stop();
//            audioManage.setStreamMute(AudioManager.STREAM_SYSTEM, true);
//            audioManage.setRingerMode(AudioManager.RINGER_MODE_SILENT);
            setChange(wifi);
        }

}

    /**
     * 播放
     */
    public void paly(Context c) {
        /**
         * 开头就调用stop()方法，可避免用户多次单机Play按钮创建多个MediaPlayer实例的情况发生。
         */
        stop();

        /**
         * 音频文件放在res/raw目录下。目录raw负责存放那些不需要Android编译系统特别处理的各类文件。
         */
//        mPlayer = MediaPlayer.create(c, R.raw.jingbao);
         mPlayer.setLooping(true);
//        mPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                stop();
//            }
//        });

        mPlayer.start();
    }
    /**
     * 停止
     */
    public void stop() {
        if (mPlayer != null) {
            /**
             * MediaPlayer.release()方法可销毁MediaPlayer的实例。销毁是“停止”的一种具有攻击意味的说法，
             * 但我们有充足的理由使用销毁一词。
             * 除非调用MediaPlayer.release()方法，否则MediaPlayer将一直占用着音频解码硬件及其它系统资源
             * 。而这些资源是由所有应用共享的。
             * MediaPlayer有一个stop()方法。该方法可使MediaPlayer实例进入停止状态，等需要时再重新启动
             * 。不过，对于简单的音频播放应用，建议 使用release()方法销毁实例，并在需要时进行重见。基于以上原因，有一个简单可循的规则：
             * 只保留一个MediaPlayer实例，保留时长即音频文件 播放的时长。
             */
            mPlayer.release();
            mPlayer = null;
        }
    }



    /**
     * 暂停
     */
    public void pause() {
        if (mPlayer != null) {
            mPlayer.pause();
        }
    }
}
