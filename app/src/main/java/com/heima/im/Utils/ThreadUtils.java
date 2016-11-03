package com.heima.im.Utils;

import android.os.Handler;

/**
 * Created by xuanfengwuxiang on 2016/10/6.
 */
public class ThreadUtils {
    public static void runInThread(Runnable r){
        new Thread(r).start();

    }
    public static Handler handler = new Handler();
    public static void runUIThread(Runnable r){
        handler.post(r);
    }

}
