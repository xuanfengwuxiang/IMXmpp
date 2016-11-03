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
import com.heima.im.bean.QQBuddyList;
import com.heima.im.bean.QQMessage;
import com.heima.im.bean.QQMessageType;
import com.heima.im.core.QQConnection;
import com.heima.im.service.ImService;

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
    public QQConnection conn;
    private String userName;
    private String password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.inject(this);
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                try {
                    conn = new QQConnection();
                    conn.addonQQMessageReceiveListener(listener);
                    conn.connect();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        conn.removeonQQMessageReceiveListener(listener);
    }

    public void login(View view){
        userName = account.getText().toString().trim();
        password = pwd.getText().toString().trim();
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                QQMessage msg = new QQMessage();
                msg.type = QQMessageType.MSG_TYPE_LOGIN;
                msg.content = userName+"#"+password;
                String xml = msg.toXml();
                if (conn!=null){
                    try {
                        conn.sendMessage(msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            }
        });


    }

    private QQConnection.onQQMessageReceiveListener listener = new QQConnection.onQQMessageReceiveListener(){
        public void onReceive(QQMessage msg) {
            System.out.println(msg);
            if (QQMessageType.MSG_TYPE_BUDDY_LIST.equals(msg.type)){
                final QQBuddyList list = new QQBuddyList();
                final QQBuddyList list2 = (QQBuddyList) list.fromXml(msg.content);
                ThreadUtils.runUIThread(new Runnable() {
                    @Override
                    public void run() {
                        MyApp.me = conn;
                        MyApp.userName = userName;
                        MyApp.account =userName+"@qq.com";
                        Toast.makeText(getApplicationContext(),"登陆成功",Toast.LENGTH_SHORT).show();
                        startService(new Intent(getBaseContext(), ImService.class));
                        Intent intent = new Intent(getBaseContext(),ContactActivity.class);
                        intent.putExtra("list",list2);
                        startActivity(intent);
                        finish();

                    }
                });

            }else{
                ThreadUtils.runUIThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getBaseContext(), "登录失败", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
}
