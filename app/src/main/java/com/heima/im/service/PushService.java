package com.heima.im.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.widget.Toast;

import com.heima.im.Utils.ThreadUtils;
import com.heima.im.activity.MyApp;

import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

public class PushService extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
        Toast.makeText(getBaseContext(), "推送服务打开...",Toast.LENGTH_SHORT).show();
        MyApp.conn.addPacketListener(new PacketListener() {
            @Override
            public void processPacket(final Packet packet) {
                System.out.println(packet.toXML());

                    ThreadUtils.runUIThread(new Runnable() {
                        @Override
                        public void run() {
                            Message msg = (Message) packet;
                            Toast.makeText(PushService.this, "我是推送"+msg.getBody(), Toast.LENGTH_SHORT).show();
                        }
                    });


            }
        },null);

    }

    public PushService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
