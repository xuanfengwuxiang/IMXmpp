package com.heima.im.Utils;


import android.icu.text.SimpleDateFormat;
import android.os.SystemClock;

import java.util.Date;


/**
 * Created by xuanfengwuxiang on 2016/10/6.
 */
public class MyTime {

    public static String getTime() {
        Date data = new Date(System.currentTimeMillis());

        return data.toLocaleString();
    }
    public static String getTime(long time) {
        Date data = new Date(time);
        return data.toLocaleString();
    }
}
