package com.heima.im.service;

import android.app.Service;
import android.content.ContentValues;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.heima.im.Utils.NickUtils;
import com.heima.im.Utils.ThreadUtils;
import com.heima.im.activity.MyApp;
import com.heima.im.provider.ContactProvider;

import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.RosterExchangeListener;

import java.util.Collection;


public class ImService extends Service {
    private Roster roster;

    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getBaseContext(),"服务后台开启了",Toast.LENGTH_SHORT).show();
        ThreadUtils.runInThread(new Runnable() {
            @Override
            public void run() {
                roster = MyApp.conn.getRoster();
                roster.addRosterListener(listener);
                Collection<RosterEntry> list = roster.getEntries();
                for (RosterEntry person : list){
                    saveOrUpdateRosterEntry(person);
                }
            }
        });

    }

    public IBinder onBind(Intent intent) {
        return null;
    }

    private RosterListener listener = new RosterListener() {
        @Override
        public void entriesAdded(Collection<String> collection) {
            System.out.println("有人添加好友了...");
            printLn(collection);
            for(String address : collection){
                RosterEntry person = roster.getEntry(address);
                saveOrUpdateRosterEntry(person);

            }
        }

        @Override
        public void entriesUpdated(Collection<String> collection) {
            System.out.println("有人重命名好友了...");
            for(String address : collection){
                RosterEntry person = roster.getEntry(address);
                saveOrUpdateRosterEntry(person);

            }
        }

        @Override
        public void entriesDeleted(Collection<String> collection) {
            System.out.println("有人删除好友了...");
            printLn(collection);
            for(String address : collection){
                getContentResolver().delete(ContactProvider.CONTACT_URI, ContactProvider.CONTACT.ACCOUNT+"=?",new String[]{address});
            }

        }

        private void printLn(Collection<String> collection) {
            for(String address : collection){
                System.out.println(address);
            }
        }

        @Override
        public void presenceChanged(Presence presence) {

        }
    };

    private void saveOrUpdateRosterEntry(RosterEntry person) {
        ContentValues values = new ContentValues();
        String account = person.getUser();
        values.put(ContactProvider.CONTACT.ACCOUNT,person.getUser());//account
        String nick = person.getName();
        if (nick==null||"".equals(nick)){
            nick = account.substring(0,account.indexOf("@"));

        }
        values.put(ContactProvider.CONTACT.NICK,nick);
        values.put(ContactProvider.CONTACT.AVATAR,0);
        values.put(ContactProvider.CONTACT.SORT, NickUtils.getNick(nick));
        int count = getContentResolver().update(ContactProvider.CONTACT_URI,values, ContactProvider.CONTACT.ACCOUNT+"=?",new String[]{account});
        if (count<1){
            getContentResolver().insert(ContactProvider.CONTACT_URI,values);

        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        roster.removeRosterListener(listener);
    }
}
