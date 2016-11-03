package com.heima.im.provider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;
import android.provider.BaseColumns;

import java.security.PrivateKey;

public class SmsProvider extends ContentProvider {
    private static final String authority = "com.heima.im.provider.SmsProvider";
    public static class SMS implements BaseColumns{
        public static final String FROM_ID = "from_id";// 发送
        public static final String FROM_NICK = "from_nick";
        public static final String FROM_AVATAR = "from_avatar";
        public static final String BODY = "body";
        public static final String TYPE = "type";// chat
        public static final String TIME = "time";
        public static final String STATUS = "status";
        public static final String UNREAD = "unread";
        public static final String SESSION_ID = "session_id";
        public static final String SESSION_NAME = "session_name";

    }
    public static final String DB = "sms.db";
    public static final String TABLE = "sms";
    private SQLiteDatabase db = null;
    private MyopenHelper mMyOpenHelper;
    public class MyopenHelper extends SQLiteOpenHelper {

        public MyopenHelper(Context context) {
            super(context, DB, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL("create table sms(_id integer primary key autoincrement ,session_id text,session_name text,from_id text,from_nick text,from_avatar integer ,body text,type text ,unread integer,status text,time long)");
        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }


    public SmsProvider() {
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (matcher.match(uri)){
            case SMS:

                SQLiteDatabase db = mMyOpenHelper.getWritableDatabase();
                long id = db.insert(TABLE,"",values);
                if (id!=-1){
                    uri = ContentUris.withAppendedId(uri,id);
                    getContext().getContentResolver().notifyChange(uri,null);

                }
                break;
        }
        return uri;
    }

    @Override
    public boolean onCreate() {
        mMyOpenHelper = new MyopenHelper(getContext());
        return mMyOpenHelper==null?false:true;
    }
    public static final Uri SMS_URI = Uri.parse("content://"+authority+"/sms");
    public static final Uri SMS_SEESION_URI= Uri.parse("content://"+authority+"/session");
    public static final int SMS = 0;
    public static final int SMS_SESSION = 1;
    private static UriMatcher matcher = null;
    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(authority,"sms",SMS);
        matcher.addURI(authority,"session",SMS_SESSION);
    }



    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch (matcher.match(uri)){
            case SMS:

                db = mMyOpenHelper.getWritableDatabase();
                cursor = db.query(TABLE,null,selection,selectionArgs,null,null,sortOrder);

                break;
            case SMS_SESSION:
                db = mMyOpenHelper.getWritableDatabase();
                cursor = db.query(TABLE,null,selection,selectionArgs,"session_id",null,"time DESC");
                break;

        }
        return  cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
       return 0;
    }
}
