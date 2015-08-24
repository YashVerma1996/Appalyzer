package com.ggogle.com.myapplication.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ggogle.com.myapplication.database.AppContract.AppEntry;

public class AppHelper extends SQLiteOpenHelper {
    static public final int databaseVersion = 2;
    static public final String databaseName = "appalyzer.db";

    public AppHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createAppTable = "CREATE TABLE " + AppEntry.TABLE_NAME + " ("
                + AppEntry._ID + " AUTO INCREMENT PRIMARY KEY, "
                + AppEntry.APP_NAME + " TEXT NOT NULL, "
                + AppEntry.PACKAGE_NAME + " TEXT NOT NULL, "
                + AppEntry.DURATION + " REAL NOT NULL, "
                + AppEntry.DATE + " DATE NOT NULL, "
                + AppEntry.LIMIT_TIME + " REAL NOT NULL "
                + " );";

        sqLiteDatabase.execSQL(createAppTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //if upgrading from 1 to 2
        //sqLiteDatabase.execSQL("ALTER TABLE " + AppEntry.TABLE_NAME + " ADD " + AppEntry.DATE + " DATE NOT NULL DEFAULT 19072015;");

        //if upgrading from 2 to 3
        sqLiteDatabase.execSQL("ALTER TABLE " + AppEntry.TABLE_NAME + " ADD " + AppEntry.LIMIT_TIME + " REAL NOT NULL DEFAULT 0;");
        //   sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+AppEntry.TABLE_NAME);
    }
}
