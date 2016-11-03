package com.heima.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.heima.im.R;
import com.heima.im.Utils.ThreadUtils;

import com.heima.im.service.ChatService;
import com.heima.im.service.ImService;
import com.heima.im.service.PushService;


import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by xuanfengwuxiang on 2016/10/6.
 */
public class LoginActivity extends Activity {
    @InjectView(R.id.account)
    EditText account;
    @InjectView(R.id.pwd)
    EditText pwd;
    @InjectView(R.id.login)
    Button login;

    private String userName;
    private String password;
    private XMPPConnection conn;
    private boolean flag = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        account.setText("雷军");
        pwd.setText("123");
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                ConnectionConfiguration configuration = new ConnectionConfiguration(MyApp.HOST,MyApp.PORT);
                configuration.setDebuggerEnabled(true);
                configuration.setSecurityMode(ConnectionConfiguration.SecurityMode.disabled);
                conn = new XMPPConnection(configuration);
                try {
                    conn.connect();
                } catch (XMPPException e) {
                    e.printStackTrace();
                }


            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void login(View view){
        userName = account.getText().toString().trim();
        password = pwd.getText().toString().trim();
        ThreadUtils.runInThread(new Runnable() {

            public void run() {
                try {

                    conn.login(userName,password);
                    flag = true;

                } catch (XMPPException e) {
                    e.printStackTrace();
                    System.out.println("发生异常");
                    flag = false;
                }
                ThreadUtils.runUIThread(new Runnable() {
                    @Override
                    public void run() {
                        if (flag){
                            MyApp.conn = conn;
                            MyApp.userName = userName;
                            MyApp.account = userName+"@"+MyApp.SERVICE_NAME;
                            Toast.makeText(getApplicationContext(),"登陆成功",Toast.LENGTH_SHORT).show();
                            startService(new Intent(getBaseContext(),ImService.class));
                            startService(new Intent(getBaseContext(), PushService.class));
                            startService(new Intent(getBaseContext(), ChatService.class));
                            startActivity(new Intent(getBaseContext(),MainActivity.class));

                            finish();
                        }else {

                            Toast.makeText(getApplicationContext(),"登陆失败",Toast.LENGTH_SHORT).show();

                        }
                    }
                });
            }
        });


    }


}
