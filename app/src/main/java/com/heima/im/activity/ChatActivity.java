package com.heima.im.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.heima.im.R;
import com.heima.im.Utils.MyTime;
import com.heima.im.Utils.ThreadUtils;
import com.heima.im.dao.SmsDao;
import com.heima.im.provider.SmsProvider;
import com.heima.im.provider.SmsProvider.SMS;


import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Message;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

public class ChatActivity extends Activity {

    @InjectView(R.id.title)
    TextView title;
    @InjectView(R.id.chatlistview)
    ListView chatlistview;
    @InjectView(R.id.input)
    EditText input;
    @InjectView(R.id.send)
    Button send;
    private String toAccount = "";
    private String toNick = "";
    private SmsDao dao;
    Chat currChat = null;
    private CursorAdapter adapter = null;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        getContentResolver().registerContentObserver(SmsProvider.SMS_URI,true,mMyObserver);
        dao = new SmsDao(this);

        ButterKnife.inject(this);
        Intent intent = getIntent();
        toAccount = intent.getStringExtra("account");
        toNick = intent.getStringExtra("nick");
        title.setText("与"+toNick+"聊天中...");
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                if (currChat==null){
                    ChatManager cm = MyApp.conn.getChatManager();
                    currChat = cm.createChat(toAccount,listener);

                }
            }
        });
        setAdapterOrNotify();
    }

    private MyObserver mMyObserver = new MyObserver(new Handler());

    private class MyObserver extends ContentObserver {

        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {

            super.onChange(selfChange);

            setAdapterOrNotify();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            setAdapterOrNotify();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (currChat!=null){
            currChat.removeMessageListener(listener);
            
        }
        getContentResolver().unregisterContentObserver(mMyObserver);

    }


    public void send(View view){
        final String body = input.getText().toString().trim();
        if ("".equals(body)){
            Toast.makeText(getApplicationContext(),"消息不能为空！",Toast.LENGTH_SHORT).show();
            return;

        }
        input.setText("");
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                try {
                    Message msg = new Message();
                    msg.setType(Message.Type.chat);
                    msg.setBody(body);
                    msg.setFrom(MyApp.account);
                    msg.setTo(toAccount);
                    dao.saveSendMessage(msg);
                    currChat.sendMessage(msg);
                } catch (XMPPException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    private void setAdapterOrNotify() {
        if (adapter==null){
            final Cursor cursor = getContentResolver().query(SmsProvider.SMS_URI,null,null,null, SMS.TIME+" ASC");
            if (cursor.getCount()<1){
                return;
            }
            adapter = new CursorAdapter(getBaseContext(),cursor) {

                @Override
                public int getViewTypeCount() {
                    return 2;
                }

                @Override
                public int getItemViewType(int position) {
                    cursor.moveToPosition(position);
                    String fromId  = cursor.getString(cursor.getColumnIndex(SMS.FROM_ID));
                    if (MyApp.account.equals(fromId)){
                        return 0;
                        
                    }
                    return 1;
                }

                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    int type = getItemViewType(position);
                    if (type==0){
                        ViewHolder holder1 = null;
                        if (convertView==null){
                            convertView = View.inflate(getBaseContext(),R.layout.item_chat_send,null);
                            holder1 = new ViewHolder();
                            holder1.content = (TextView) convertView.findViewById(R.id.content);
                            holder1.time = (TextView) convertView.findViewById(R.id.time);
                            convertView.setTag(holder1);

                        }else{
                            holder1 = (ViewHolder) convertView.getTag();
                        }
                        cursor.moveToPosition(position);
                        long time = cursor.getLong(cursor.getColumnIndex(SMS.TIME));
                        String timeStr = MyTime.getTime(time);
                        String body = cursor.getString(cursor.getColumnIndex(SMS.BODY));
                        System.out.println("聊天界面的数据");
                        holder1.content.setText(body);
                        holder1.time.setText(timeStr);
                    }else if (type==1){
                        ViewHolder holder2 = null;
                        if (convertView==null){
                            convertView = View.inflate(getBaseContext(),R.layout.item_chat_receive,null);
                            holder2 = new ViewHolder();
                            holder2.content = (TextView) convertView.findViewById(R.id.content);
                            holder2.time = (TextView) convertView.findViewById(R.id.time);
                            convertView.setTag(holder2);

                        }else{
                            holder2 = (ViewHolder) convertView.getTag();
                        }
                        cursor.moveToPosition(position);
                        long time = cursor.getLong(cursor.getColumnIndex(SMS.TIME));
                        String timeStr = MyTime.getTime(time);
                        String body = cursor.getString(cursor.getColumnIndex(SMS.BODY));
                        holder2.content.setText(body);
                        holder2.time.setText(timeStr);
                        
                    }
                    return convertView;
                }

                public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                    return null;
                }

                @Override
                public void bindView(View view, Context context, Cursor cursor) {

                }
            };
            chatlistview.setAdapter(adapter);
        }else{
            adapter.getCursor().requery();
        }
        if (adapter.getCursor().getCount()>1){
            chatlistview.setSelection(adapter.getCursor().getCount()-1);
        }
        


    }
    static class ViewHolder{
        TextView time;
        TextView content;
        ImageView head;
    }

    private MessageListener listener = new MessageListener() {
        @Override
        public void processMessage(Chat chat, final Message message) {
            if (Message.Type.chat.equals(message.getType())){
                System.out.println(message.toXML());
                dao.saveReceiveMessage(message);
                ThreadUtils.runUIThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ChatActivity.this,message.getBody(), Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }
    };
}
