package com.heima.im.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.heima.im.R;
import com.heima.im.Utils.ThreadUtils;
import com.heima.im.bean.QQBuddyList;
import com.heima.im.bean.QQMessage;
import com.heima.im.bean.QQMessageType;
import com.heima.im.core.QQConnection;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;
import cn.itcast.server.bean.QQBuddy;

public class ContactActivity extends Activity {

    @InjectView(R.id.listview)
    ListView listview;
    private List<QQBuddy> list = new ArrayList<>();
    private ArrayAdapter<QQBuddy> adapter = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact);
        ButterKnife.inject(this);
        MyApp.me.addonQQMessageReceiveListener(listener);
        Intent intent = getIntent();
        QQBuddyList temp = (QQBuddyList) intent.getSerializableExtra("list");
        System.out.println(temp.buddyList.size());
        list.clear();
        list.addAll(temp.buddyList);
        setAdapterOrNotify();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        MyApp.me.removeonQQMessageReceiveListener(listener);
    }

    QQConnection.onQQMessageReceiveListener listener = new QQConnection
            .onQQMessageReceiveListener() {

        @Override
        public void onReceive(QQMessage msg) {
            if (QQMessageType.MSG_TYPE_BUDDY_LIST.equals(msg.type)) {
                QQBuddyList temp = new QQBuddyList();
                temp = (QQBuddyList) temp.fromXml(msg.content);
                list.clear();
                list.addAll(temp.buddyList);
                ThreadUtils.runUIThread(new Runnable() {
                    @Override
                    public void run() {
                        setAdapterOrNotify();
                    }
                });

            }
        }
    };

    private void setAdapterOrNotify() {
        if (list.size() < 1) {
            return;

        }
        if (adapter == null) {
            adapter = new ArrayAdapter<QQBuddy>(this, 0, list) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder holder = null;
                    if (convertView == null) {
                        convertView = View.inflate(getBaseContext(), R.layout.item_buddy, null);
                        holder = new ViewHolder();
                        holder.head = (ImageView) convertView.findViewById(R.id.head);
                        holder.nick = (TextView) convertView.findViewById(R.id.nick);
                        holder.account = (TextView) convertView.findViewById(R.id.account);
                        convertView.setTag(holder);

                    } else {
                        holder = (ViewHolder) convertView.getTag();
                    }
                    QQBuddy buddy = list.get(position);
                    holder.nick.setText(buddy.nick + "");
                    holder.account.setText(buddy.account + "@qq.com");
                    if (MyApp.userName.equals(buddy.account + "")) {
                        holder.nick.setText("[自己]");
                        holder.nick.setTextColor(Color.parseColor("#C80000"));

                    } else {
                        holder.nick.setTextColor(Color.parseColor("#000000"));

                    }
                    return convertView;
                }
            };
            listview.setAdapter(adapter);
            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    QQBuddy buddy = list.get(i);
                    if (MyApp.userName.equals(buddy.account+"")){
                        Toast.makeText(getBaseContext(),"不能跟自己聊天",Toast.LENGTH_SHORT).show();
                    }else{
                        Intent intent = new Intent(getBaseContext(),ChatActivity.class);
                        intent.putExtra("account",buddy.account+"");
                        intent.putExtra("nick",buddy.nick+"");

                        startActivity(intent);
                    }


                }
            });
       }else{
            adapter.notifyDataSetChanged();
       }
    }

    static class ViewHolder {
        ImageView head;
        TextView nick;
        TextView account;
    }
}
