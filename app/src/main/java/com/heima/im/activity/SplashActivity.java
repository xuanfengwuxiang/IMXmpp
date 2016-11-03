package com.heima.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.SystemClock;
import android.os.Bundle;

import com.heima.im.R;
import com.heima.im.Utils.ThreadUtils;
import com.heima.im.activity.LoginActivity;

public class SplashActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        ThreadUtils.runInThread(new Runnable() {
            public void run() {
                SystemClock.sleep(2000);
                startActivity(new Intent(getBaseContext(),LoginActivity.class));
                finish();
            }
        });

    }
}
