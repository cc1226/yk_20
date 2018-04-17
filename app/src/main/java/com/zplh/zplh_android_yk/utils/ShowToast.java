package com.zplh.zplh_android_yk.utils;

import android.app.Activity;
import android.widget.Toast;

public class ShowToast {

    private static Toast mToast = null;

    public static void show(final String mess, final Activity context) {
        context.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (mToast == null) {
                    mToast = Toast.makeText(context, mess, Toast.LENGTH_SHORT);
                } else {
                    mToast.setText(mess);
                }
                mToast.show();
            }
        });

    }
}