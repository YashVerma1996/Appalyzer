package commandoengineer.appalyzer;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;

import commandoengineer.appalyzer.database.AppContract;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Yash on 06/07/15.
 */
public class Utility {
    public static String selectedDate = null;


    public static void setSelectedDate(String date) {
        selectedDate = date;
    }
public static String friendlyDate()
{Date date=null;

    SimpleDateFormat simpleDateFormat=new SimpleDateFormat("ddMMyyyy");
    try{date=simpleDateFormat.parse(selectedDate);}
    catch (Exception e){e.printStackTrace();}

    SimpleDateFormat friendlyFormat=new SimpleDateFormat("EEE MMM dd,yyyy");
 return friendlyFormat.format(date);

}
    public static String getSelectedDate() {
        if (selectedDate == null)
            selectedDate = getCurrentDate();
        return selectedDate;
    }

    public static long getTotalUsageTime(Context context) {
        long TUT = 0;
        Cursor c = context.getContentResolver().query(AppContract.AppEntry.contentUri, new String[]{AppContract.AppEntry.DURATION},
                AppContract.AppEntry.DATE + "=?",
                new String[]{getSelectedDate()}, null);

        while (c.moveToNext()) {
            TUT += c.getLong(0);
        }

        c.close();
        Log.e("TUT", "TUT=" + TUT);
        return TUT;
    }

    public static String getCurrentDate() {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");
        //Log.e("Date",simpleDateFormat.format(currentTime));
        return simpleDateFormat.format(currentTime);
    }

    public static int presentYear() {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy");
        //Log.e("Date",simpleDateFormat.format(currentTime));
        return Integer.parseInt(simpleDateFormat.format(currentTime));
    }

    public static int presentDay() {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd");
        //Log.e("Date",simpleDateFormat.format(currentTime));
        return Integer.parseInt(simpleDateFormat.format(currentTime));
    }

    public static int presentMonth() {
        long currentTime = System.currentTimeMillis();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM");
        //Log.e("Date",simpleDateFormat.format(currentTime));
        return Integer.parseInt(simpleDateFormat.format(currentTime));
    }




}
