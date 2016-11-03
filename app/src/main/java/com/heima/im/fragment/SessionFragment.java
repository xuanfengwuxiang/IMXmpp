package com.heima.im.fragment;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.heima.im.R;
import com.heima.im.Utils.NickUtils;
import com.heima.im.activity.ChatActivity;
import com.heima.im.provider.ContactProvider;
import com.heima.im.provider.SmsProvider;

import  com.heima.im.provider.SmsProvider.SMS;


public class SessionFragment extends BaseFragment {

    private ListView sessionlistview;
    private CursorAdapter adapter = null;

    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        View view = View.inflate(getActivity(), R.layout.fragment_session,null);
        sessionlistview = (ListView) view.findViewById(R.id.sessionlistview);
        setAdapterOrNotify();
        getActivity().getContentResolver().registerContentObserver(SmsProvider.SMS_URI,true,mMyObserver);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().getContentResolver().unregisterContentObserver(mMyObserver);
    }

    private MyObserver mMyObserver = new MyObserver(new Handler());


    private class MyObserver extends ContentObserver {

        public MyObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {

            super.onChange(selfChange);
            System.out.println("低版本");
            setAdapterOrNotify();
        }

        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            System.out.println("高版本");
            setAdapterOrNotify();
        }
    }

    private void setAdapterOrNotify() {
        if (adapter==null){
            final Cursor cursor = getActivity().getContentResolver().query(SmsProvider.SMS_SEESION_URI,null,null,null,null);
            if (cursor.getCount()<1){
                return;

            }
            adapter = new CursorAdapter(getActivity(), cursor,true) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder holder = null;
                    if(convertView==null){
                        convertView = View.inflate(getActivity(),R.layout.item_session,null);
                        holder = new ViewHolder();
                        holder.account = (TextView) convertView.findViewById(R.id.account);
                        holder.nick = (TextView) convertView.findViewById(R.id.nick);

                        convertView.setTag(holder);

                    }else{
                        holder = (ViewHolder) convertView.getTag();
                    }
                    cursor.moveToPosition(position);
                    String session_id = cursor.getString(cursor.getColumnIndex(SMS.SESSION_ID));
                    String body = cursor.getString(cursor.getColumnIndex(SMS.BODY));
                    String nick = NickUtils.getNick(getActivity(),session_id);
                    holder.account.setText(body);
                    holder.nick.setText(nick);

                    return convertView;
                }

                @Override
                public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                    return null;
                }

                @Override
                public void bindView(View view, Context context, Cursor cursor) {

                }
            };
            sessionlistview.setAdapter(adapter);
            sessionlistview.setOnItemClickListener(new ListView.OnItemClickListener(){
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    adapter.getCursor().moveToPosition(i);
                    Cursor cursor = adapter.getCursor();
                    String session_id = cursor.getString(cursor.getColumnIndex(SMS.SESSION_ID));

                    String nick = NickUtils.getNick(getActivity(),session_id);
                    Intent intent = new Intent(getActivity(),ChatActivity.class);
                    intent.putExtra("account",session_id);
                    intent.putExtra("nick",nick);
                    startActivity(intent);

                }
            });

        }else{
            adapter.getCursor().requery();
        }

    }

    private class ViewHolder{
        ImageView head;
        TextView nick;
        TextView account;
    }
}
