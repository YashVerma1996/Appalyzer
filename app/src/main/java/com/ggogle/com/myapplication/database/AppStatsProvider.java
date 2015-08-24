package com.ggogle.com.myapplication.database;

import android.content.ContentProvider;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.net.Uri;

import java.sql.SQLException;
import java.util.UnknownFormatConversionException;

public class AppStatsProvider extends ContentProvider {
    SQLiteOpenHelper mOpenHelper;
    final int App_Stats=101;
    private UriMatcher sUriMatcher=buildUriMatcher();

    public AppStatsProvider() {
    }

    public boolean onCreate(){
        mOpenHelper = new AppHelper(getContext());
        return true;
    }

    UriMatcher buildUriMatcher() {

        UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
        String authority = AppContract.authority;
        matcher.addURI(authority, AppContract.pathAppStats, App_Stats);

        return matcher;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        Cursor retCursor;

        if(sUriMatcher.match(uri)==App_Stats)
        retCursor= mOpenHelper.getReadableDatabase().query(AppContract.AppEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
        else
        throw new UnsupportedOperationException("Unknown Uri "+uri);
        retCursor.setNotificationUri(getContext().getContentResolver(),uri);
        return retCursor;
    }
    @Override
       public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db=mOpenHelper.getWritableDatabase();
        Uri returnUri;
        long _id=db.insert(AppContract.AppEntry.TABLE_NAME,null,values);

        if(_id>0){returnUri= AppContract.AppEntry.buildAppUri(_id);}
        else {throw new android.database.SQLException("Insertion Failed into "+uri);}
        db.close();
        return returnUri;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        int rowsDeleted=0;
        SQLiteDatabase db=mOpenHelper.getWritableDatabase();
        rowsDeleted=db.delete(AppContract.AppEntry.TABLE_NAME,selection,selectionArgs);

        if (rowsDeleted != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        db.close();
        return rowsDeleted;
    }

    @Override
    public String getType(Uri uri) {
       switch (sUriMatcher.match(uri))
       {
           case App_Stats: return AppContract.AppEntry.CONTENT_TYPE;
           default:throw new UnsupportedOperationException("Not yet implemented");
       }

    }

    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        SQLiteDatabase db= mOpenHelper.getWritableDatabase();
        int rowsUpdated=0;

        rowsUpdated=db.update(AppContract.AppEntry.TABLE_NAME,values,selection,selectionArgs);
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        db.close();
        return rowsUpdated;
    }
}
