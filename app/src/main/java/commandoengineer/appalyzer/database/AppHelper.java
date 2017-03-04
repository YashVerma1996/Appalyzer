package commandoengineer.appalyzer.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class AppHelper extends SQLiteOpenHelper {
    static public final int databaseVersion = 2;
    static public final String databaseName = "appalyzer.db";

    public AppHelper(Context context) {
        super(context, databaseName, null, databaseVersion);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        String createAppTable = "CREATE TABLE " + AppContract.AppEntry.TABLE_NAME + " ("
                + AppContract.AppEntry._ID + " AUTO INCREMENT PRIMARY KEY, "
                + AppContract.AppEntry.APP_NAME + " TEXT NOT NULL, "
                + AppContract.AppEntry.PACKAGE_NAME + " TEXT NOT NULL, "
                + AppContract.AppEntry.DURATION + " REAL NOT NULL, "
                + AppContract.AppEntry.DATE + " DATE NOT NULL, "
                + AppContract.AppEntry.LIMIT_TIME + " REAL NOT NULL "
                + " );";

        sqLiteDatabase.execSQL(createAppTable);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        //if upgrading from 1 to 2
        //sqLiteDatabase.execSQL("ALTER TABLE " + AppEntry.TABLE_NAME + " ADD " + AppEntry.DATE + " DATE NOT NULL DEFAULT 19072015;");

        //if upgrading from 2 to 3
        sqLiteDatabase.execSQL("ALTER TABLE " + AppContract.AppEntry.TABLE_NAME + " ADD " + AppContract.AppEntry.LIMIT_TIME + " REAL NOT NULL DEFAULT 0;");
        //   sqLiteDatabase.execSQL("DROP TABLE IF EXISTS "+AppEntry.TABLE_NAME);
    }
}
