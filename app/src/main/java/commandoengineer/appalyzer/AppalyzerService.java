package commandoengineer.appalyzer;

import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Build;
import android.os.IBinder;
import android.os.PowerManager;
import android.util.Log;

import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import commandoengineer.appalyzer.database.AppContract;

/**
 * Created by Yash on 23/06/15.
 */
public class AppalyzerService extends Service {
    static String packageName;
    static String previousPackageName = null;
    static String appName;
    static String[] PROJECTION = {
            AppContract.AppEntry._ID,
            AppContract.AppEntry.PACKAGE_NAME,
            AppContract.AppEntry.APP_NAME,
            AppContract.AppEntry.DURATION,
            AppContract.AppEntry.DATE
    };
    static int col_id = 0;
    static int col_packageName = 1;
    static int col_appName = 2;
    static int col_duration = 3;
    static int col_date = 4;


    static ActivityManager.RunningTaskInfo foreground;
    static PackageInfo packageInfo = null;
    static long time;
    static List<UsageStats> appList;

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        AlarmManager alarmManager = (AlarmManager) this.getSystemService(ALARM_SERVICE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 3, new Intent(this, AppalyzerService.AlarmReceived.class), PendingIntent.FLAG_UPDATE_CURRENT);
        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME, 10, 10000, pendingIntent);

        return START_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    static public class AlarmReceived extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (isScreenOn(context)) {


                /*final ActivityManager am = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
                for (ActivityManager.RunningAppProcessInfo d : list) {
                    if (d.processName.equals("system")||d.processName.equals("com.micromax.ActivityClasses"))
                        continue;
                    if (d.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                        packageName = d.processName;
                        Toast.makeText(context, "New Logic"+packageName, Toast.LENGTH_SHORT).show();
                    }
                }*/


                //TODO: CHECK IF THE BELOW CODE WORKS ON LOLLIPOP

                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
                    UsageStatsManager usm = (UsageStatsManager) context.getSystemService(Context.USAGE_STATS_SERVICE);
                    time = System.currentTimeMillis();
                    appList = usm.queryUsageStats(UsageStatsManager.INTERVAL_DAILY, time - 1000 * 1000, time);

                    if (appList != null && appList.size() > 0) {
                        SortedMap<Long, UsageStats> mySortedMap = new TreeMap<Long, UsageStats>();
                        for (UsageStats usageStats : appList) {
                            mySortedMap.put(usageStats.getLastTimeUsed(), usageStats);
                        }
                        if (mySortedMap != null && !mySortedMap.isEmpty()) {
                            packageName = mySortedMap.get(mySortedMap.lastKey()).getPackageName();
                        }
                    }
                } else {    //TODO: THIS WORKS PERFECTLY ON PRE-LOLLIPOP
                    final ActivityManager activityManager = (ActivityManager) context.getSystemService(ACTIVITY_SERVICE);
                    foreground = activityManager.getRunningTasks(1).get(0);
                    packageName = foreground.topActivity.getPackageName();
                }
                Log.e("service", packageName);

                PackageManager pm = context.getPackageManager();

                try {
                    packageInfo = pm.getPackageInfo(packageName, 0);
                } catch (PackageManager.NameNotFoundException ex) {
                    Log.e("Exception", "Package Name Not Found" + ex);
                }

                appName = packageInfo.applicationInfo.loadLabel(pm).toString();

                if (previousPackageName != null) {
                    if (packageName.equals(previousPackageName)) {

                        if (isInDatabase(context, packageName)) {
                            Log.e("Database", "present in database, hence updating current ");
                            add10sec(context, packageName);
                        } else {
                            Log.e("Database", "Not Present in database ,hence creating new");
                            createNew10sec(context, packageName);
                        }
                    } else {
                        previousPackageName = packageName;
                    }
                } else {
                    previousPackageName = packageName;
                }
            }
        }
    }

    static public boolean isScreenOn(Context context) {
        PowerManager powerManager = (PowerManager) context.getSystemService(POWER_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
            return powerManager.isInteractive();
        } else {
            return powerManager.isScreenOn();
        }
    }

    static boolean isInDatabase(Context context, String packageName) {
        Log.e("isInDatabase", "Package is " + packageName + " Date is " + Utility.getCurrentDate());
        Cursor c = context.getContentResolver().query(AppContract.AppEntry.contentUri,
                new String[]{AppContract.AppEntry.PACKAGE_NAME},
                AppContract.AppEntry.PACKAGE_NAME + "=? AND " + AppContract.AppEntry.DATE + "=?",
                new String[]{packageName, Utility.getCurrentDate()}, null);
        if (c.moveToNext()) {
            c.close();
            return true;
        }
        Log.e("IsInDatabase", "Is not present in database");
        c.close();
        return false;
    }
//Made for lollipop
  /*  static void add10sec(Context context, String packageName) {
        Cursor c = context.getContentResolver().query(AppEntry.contentUri,
                PROJECTION,
                AppEntry.PACKAGE_NAME + "=?"*//* AND " + AppEntry.DATE + "=?"*//*,
                new String[]{packageName*//*, Utility.getCurrentDate()*//*},
                null
        );
        c.moveToFirst();

        ContentValues values = new ContentValues();
        values.put(AppEntry.PACKAGE_NAME, packageName);
        values.put(AppEntry.APP_NAME, c.getString(col_appName));
        values.put(AppEntry.DURATION, (c.getLong(col_duration) + 10));
        values.put(AppEntry.DATE, c.getString(col_date));


        Log.e("Updated", "Now Duration is=" + (c.getLong(col_duration) + 10));
        c.close();
        context.getContentResolver().update(AppEntry.contentUri,
                values,
                AppEntry.PACKAGE_NAME + "=?"*//* AND " + AppEntry.DATE + "=?"*//*,
                new String[]{packageName*//*, Utility.getCurrentDate()*//*});
    }
*/

    static void add10sec(Context context, String packageName) {
        String currentDate = Utility.getCurrentDate();
        Log.e("add10sec", "Package is " + packageName + " Date is " + currentDate);

        Cursor c = context.getContentResolver().query(AppContract.AppEntry.contentUri,
                PROJECTION,
                AppContract.AppEntry.PACKAGE_NAME + "=? AND " + AppContract.AppEntry.DATE + "=?",
                new String[]{packageName, currentDate},
                null
        );
        if (!c.moveToFirst())
            Log.e("Add10Sec", "No record found in database");
        c.moveToFirst();

        ContentValues values = new ContentValues();
        values.put(AppContract.AppEntry.PACKAGE_NAME, packageName);
        values.put(AppContract.AppEntry.APP_NAME, c.getString(col_appName));
        values.put(AppContract.AppEntry.DURATION, (c.getLong(col_duration) + 10));
        values.put(AppContract.AppEntry.DATE, currentDate);


        Log.e("Updated", "Now Duration is=" + (c.getLong(col_duration) + 10));
        c.close();
        int i = context.getContentResolver().update(AppContract.AppEntry.contentUri,
                values,
                AppContract.AppEntry.PACKAGE_NAME + "=? AND " + AppContract.AppEntry.DATE + "=?",
                new String[]{packageName, currentDate});
        Log.e("uodating", "RowsUpd=" + i);
    }

    static void createNew10sec(Context context, String packageName) {
        ContentValues values = new ContentValues();
        values.put(AppContract.AppEntry.PACKAGE_NAME, packageName);
        values.put(AppContract.AppEntry.APP_NAME, appName);
        values.put(AppContract.AppEntry.DURATION, 10);
        values.put(AppContract.AppEntry.DATE, Utility.getCurrentDate());
        values.put(AppContract.AppEntry.LIMIT_TIME,0);

        context.getContentResolver().insert(AppContract.AppEntry.contentUri, values);
        Log.e("Creating New", "New Created.");
    }

}
