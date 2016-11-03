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
import android.widget.Switch;

public class ContactProvider extends ContentProvider {
    private static final String authority = "com.heima.im.provider.ContactProvider";
    public static Uri CONTACT_URI = Uri.parse("content://"+authority+"/contacts");
    /*
    数据库字段常量定义
     */
    public static class CONTACT implements BaseColumns{
        public static final String ACCOUNT = "account";
        public static final String NICK = "nick";
        public static final String AVATAR = "avatar";
        public static final String SORT = "sort";// 拼音
    }
    private static final String TABLE = "contacts";
    private static final String DB = "contacts3.db";
    private String sql = "create table contacts (_id integer primary key autoincrement,account text,nick text,avatar text,sort text)";

    public class MyOpenHelper extends SQLiteOpenHelper{

        public MyOpenHelper(Context context) {
            super(context, DB, null, 1);
        }

        @Override
        public void onCreate(SQLiteDatabase sqLiteDatabase) {
            sqLiteDatabase.execSQL(sql);

        }

        @Override
        public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {

        }
    }
    private MyOpenHelper mMyOpenHelper = null;


    public ContactProvider() {
    }
    private static UriMatcher matcher = null;
    private static final int CONTACT_CODE = 0;
    static {
        matcher = new UriMatcher(UriMatcher.NO_MATCH);
        matcher.addURI(authority,TABLE,CONTACT_CODE);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int count = 0;
        switch (matcher.match(uri)){
            case CONTACT_CODE:
                System.out.println("执行delete---->");
                SQLiteDatabase db = mMyOpenHelper.getWritableDatabase();
                count = db.delete(TABLE,selection,selectionArgs);
                if (count>0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                break;

        }
        return count;
    }

    @Override
    public String getType(Uri uri) {
        // TODO: Implement this to handle requests for the MIME type of the data
        // at the given URI.

        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        switch (matcher.match(uri)){
            case CONTACT_CODE:
                System.out.println("执行insert---->");
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
        // TODO: Implement this to initialize your content provider on startup.
        mMyOpenHelper = new MyOpenHelper(getContext());

        return mMyOpenHelper==null?false:true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
        Cursor cursor = null;
        switch (matcher.match(uri)){
            case CONTACT_CODE:
                System.out.println("执行query---->");
                SQLiteDatabase db = mMyOpenHelper.getWritableDatabase();
                cursor = db.query(TABLE,projection,selection,selectionArgs,null,null,sortOrder);

                break;

        }
        return  cursor;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0;
        switch (matcher.match(uri)){
            case CONTACT_CODE:
                System.out.println("执行update---->");
                SQLiteDatabase db = mMyOpenHelper.getWritableDatabase();
                count = db.update(TABLE,values,selection,selectionArgs);
                if (count>0){
                    getContext().getContentResolver().notifyChange(uri,null);
                }
                break;

        }
        return count;
    }
}
