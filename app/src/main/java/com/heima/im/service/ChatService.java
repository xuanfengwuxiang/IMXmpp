package com.heima.im.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.heima.im.Utils.NickUtils;
import com.heima.im.Utils.ThreadUtils;
import com.heima.im.activity.MyApp;
import com.heima.im.dao.SmsDao;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.packet.Message;

public class ChatService extends Service {

    private ChatManager cm;
    private SmsDao dao;

    public ChatService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(ChatService.this, "聊天服务打开了！！！", Toast.LENGTH_SHORT).show();
        cm = MyApp.conn.getChatManager();
        cm.addChatListener(listener);
        dao = new SmsDao(getBaseContext());
    }

    private ChatManagerListener listener = new ChatManagerListener() {
        @Override
        public void chatCreated(Chat chat, boolean b) {
            if (!b){
                chat.addMessageListener(messageListener);
            }

        }
    };

    private MessageListener messageListener = new MessageListener() {
        @Override
        public void processMessage(Chat chat, final Message message) {
            if (!Message.Type.chat.equals(message.getType())){
                return;
            }
            String fromId = NickUtils.filterAccount(message.getFrom());
            if (!MyApp.account.equals(fromId)){
                dao.saveReceiveMessage(message);
            }
            ThreadUtils.runUIThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(ChatService.this, message.getBody(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (cm!=null){
            cm.removeChatListener(listener);

        }
    }

    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
