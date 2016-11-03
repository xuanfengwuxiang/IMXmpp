package com.heima.im.Utils;

import android.content.Context;
import android.database.Cursor;

import com.heima.im.activity.MyApp;
import com.heima.im.provider.ContactProvider;
import com.heima.im.provider.ContactProvider.CONTACT;

import opensource.jpinyin.PinyinFormat;
import opensource.jpinyin.PinyinHelper;

/**
 * Created by xuanfengwuxiang on 2016/10/8.
 */
public class NickUtils {
    public static String getNick(String nick){
        return PinyinHelper.convertToPinyinString(nick,"", PinyinFormat.WITHOUT_TONE).toUpperCase();
    }
    public static String filterAccount(String account){
        account = account.substring(0,account.indexOf("@"));
        account = account +"@"+ MyApp.SERVICE_NAME;
        return account;
    }
    public static String getNick(Context context, String account){
        String nick = "";
        Cursor cursor = context.getContentResolver().query(ContactProvider.CONTACT_URI,null, CONTACT.ACCOUNT+"=?",new String[]{account},null);
        if(cursor.moveToFirst()){
            nick = cursor.getString(cursor.getColumnIndex(CONTACT.NICK));


        }
        cursor.close();
        if (nick==null||"".equals(nick)){
            nick = account.substring(0,account.indexOf("@"));

        }
        return nick;
    }
}
