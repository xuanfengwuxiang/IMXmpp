package com.heima.im.dao;

import android.app.Notification;
import android.content.ContentValues;
import android.content.Context;

import com.heima.im.Utils.NickUtils;
import com.heima.im.provider.SmsProvider;

import org.jivesoftware.smack.packet.Message;

import  com.heima.im.provider.SmsProvider.SMS;

/**
 * Created by xuanfengwuxiang on 2016/10/9.
 */
public class SmsDao {
    private Context context;

    public SmsDao(Context context) {
        this.context = context;
    }
    public void saveSendMessage(Message msg){
        ContentValues values = new ContentValues();
        String account = msg.getFrom();
        values.put(SMS.FROM_ID,account);
        String nick = NickUtils.getNick(context,account);
        String sessionName = NickUtils.getNick(context,msg.getTo());
        values.put(SMS.FROM_NICK,nick);
        values.put(SMS.SESSION_NAME,sessionName);
        values.put(SMS.FROM_AVATAR,0);
        values.put(SMS.BODY,msg.getBody());
        values.put(SMS.TIME,System.currentTimeMillis());
        values.put(SMS.TYPE,Message.Type.chat.toString());
        values.put(SMS.STATUS,"");
        values.put(SMS.UNREAD,0);
        values.put(SMS.SESSION_ID,msg.getTo());
        this.context.getContentResolver().insert(SmsProvider.SMS_URI,values);

   }
    public void saveReceiveMessage(Message msg){
        String from = NickUtils.filterAccount(msg.getFrom());
        ContentValues values = new ContentValues();
        values.put(SMS.FROM_ID,from);
        String nick = NickUtils.getNick(context,from);
        values.put(SMS.FROM_NICK,nick);
        values.put(SMS.SESSION_NAME,nick);
        values.put(SMS.FROM_AVATAR,0);
        values.put(SMS.BODY,msg.getBody());
        values.put(SMS.TIME,System.currentTimeMillis());
        values.put(SMS.TYPE,Message.Type.chat.toString());
        values.put(SMS.STATUS,"");
        values.put(SMS.UNREAD,0);
        values.put(SMS.SESSION_ID,from);
        this.context.getContentResolver().insert(SmsProvider.SMS_URI,values);

    }
}











