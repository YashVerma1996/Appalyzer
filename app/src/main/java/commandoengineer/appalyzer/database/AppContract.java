package commandoengineer.appalyzer.database;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.net.Uri;
import android.provider.BaseColumns;

public class AppContract {
    public static final String authority = "com.commando.appalyzer";
    public static final Uri BASE_URI=Uri.parse("content://"+authority);
    public static final String pathAppStats="app_stats";

    public static class AppEntry implements BaseColumns
    {
        public static Uri contentUri=BASE_URI.buildUpon().appendPath(pathAppStats).build();
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + '/' + authority + '/' + pathAppStats;
        public static String TABLE_NAME = "app_stats";
        public static String APP_NAME="app_name";
        public static String PACKAGE_NAME="package";
        public static String DURATION="duration";
        public static String DATE="date";
        public static String LIMIT_TIME="limit_time";

       public static Uri buildAppUri(long id)
        {
            return ContentUris.withAppendedId(contentUri,id);
        }
    }
}
