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
import com.heima.im.activity.ChatActivity;
import com.heima.im.provider.ContactProvider;
import com.heima.im.provider.ContactProvider.CONTACT;

import butterknife.ButterKnife;
import butterknife.InjectView;


public class ContactFragment extends BaseFragment {



    ListView contactlistview;
    private CursorAdapter adapter = null;
    private MyObserver mMyObserver = new MyObserver(new Handler());


    private class MyObserver extends ContentObserver{

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
    protected View createView(LayoutInflater inflater, ViewGroup container, Bundle
            savedInstanceState) {
        getActivity().getContentResolver().registerContentObserver(ContactProvider.CONTACT_URI,true,mMyObserver);
        View view = View.inflate(getActivity(), R.layout.fragment_contact, null);
        contactlistview = (ListView) view.findViewById(R.id.contactlistview);
        setAdapterOrNotify();
        return view;
    }

    private void setAdapterOrNotify() {
        if (adapter==null){
             final Cursor cursor = getActivity().getContentResolver().query(ContactProvider.CONTACT_URI,null,null,null, CONTACT.SORT+" ASC");
            if (cursor.getCount()<1){
                return;

            }
            adapter = new CursorAdapter(getActivity(), cursor,true) {
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    ViewHolder holder = null;
                    if(convertView==null){
                        convertView = View.inflate(getActivity(),R.layout.item_buddy,null);
                        holder = new ViewHolder();
                        holder.account = (TextView) convertView.findViewById(R.id.account);
                        holder.nick = (TextView) convertView.findViewById(R.id.nick);

                        convertView.setTag(holder);

                    }else{
                        holder = (ViewHolder) convertView.getTag();
                    }
                    cursor.moveToPosition(position);
                    holder.account.setText(cursor.getString(cursor.getColumnIndex(
                            CONTACT.ACCOUNT)));
                    holder.nick.setText(cursor.getString(cursor.getColumnIndex(
                            CONTACT.NICK)));

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
            contactlistview.setAdapter(adapter);
            contactlistview.setOnItemClickListener(new ListView.OnItemClickListener(){
                public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                    adapter.getCursor().moveToPosition(i);
                    Cursor cursor = adapter.getCursor();

                    String account = cursor.getString(cursor.getColumnIndex(CONTACT.ACCOUNT));
                    String nick = cursor.getString(cursor.getColumnIndex(CONTACT.NICK));
                    Intent intent = new Intent(getActivity(),ChatActivity.class);
                    intent.putExtra("account",account);
                    intent.putExtra("nick",nick);
                    startActivity(intent);

                }
            });

        }else{
            adapter.getCursor().requery();
        }

    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.reset(this);
        getActivity().getContentResolver().unregisterContentObserver(mMyObserver);
        System.out.println("contactFragment被销毁了。。。");
    }
    private class ViewHolder{
        ImageView head;
        TextView nick;
        TextView account;
    }

    @Override
    public void onStop() {
        super.onStop();
        System.out.println("contactFragment被停止了。。。");
    }

    @Override
    public void onPause() {
        super.onPause();
        System.out.println("contactFragment被暂停了。。。");
    }

    @Override
    public void onStart() {
        super.onStart();
        System.out.println("contactFragment被开始了。。。");
    }
}
