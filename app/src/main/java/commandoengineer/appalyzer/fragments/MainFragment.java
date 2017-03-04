package commandoengineer.appalyzer.fragments;


import android.app.DatePickerDialog;
import android.support.v4.content.CursorLoader;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.app.ActionBar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.CursorAdapter;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.support.v7.widget.Toolbar;

import commandoengineer.appalyzer.MainActivity;
import com.ggogle.com.myapplication.R;
import commandoengineer.appalyzer.Utility;
import commandoengineer.appalyzer.database.AppContract;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;


public class MainFragment extends Fragment implements LoaderManager.LoaderCallbacks<Cursor> {

    CursorAdapter mCursorAdapter;
    static String[] PROJECTION = {
            AppContract.AppEntry._ID,
            AppContract.AppEntry.PACKAGE_NAME,
            AppContract.AppEntry.APP_NAME,
            AppContract.AppEntry.DURATION
    };

    static int col_packageName = 1;
    static int col_appName = 2;
    static int col_duration = 3;
    long duration_hour = 0;
    long duration_min = 0, duration_sec;
    float percentage;
    long TUT;
    int LOADER_ID = 0;


    String sortOrder = AppContract.AppEntry.DURATION + " DESC";
    PackageManager pm;

    TextView appNameView, durationHrView, durationMinView, SecLabel, HrLabel, label;
    ImageView iconView;
    ProgressBar progressBar;
    ActionBar actionBar;
    View rootView;
    Calendar calendar = Calendar.getInstance();
    final SimpleDateFormat simpleDateFormat = new SimpleDateFormat("ddMMyyyy");


    @Override
    public void onCreate(Bundle savedInstanceState) {
        getLoaderManager().initLoader(0, null, this);
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);
        actionBar = MainActivity.supportBar;
        setHasOptionsMenu(true);

        ListView listView = (ListView) rootView.findViewById(R.id.main_listView);
        TUT = Utility.getTotalUsageTime(getActivity());
        listView.addHeaderView(getActivity().getLayoutInflater().inflate(R.layout.total_header, null));
        setHeader(TUT, rootView);

        getLoaderManager().initLoader(0, null, this);

        Cursor c = getActivity().getContentResolver().query(AppContract.AppEntry.contentUri,
                PROJECTION,
                AppContract.AppEntry.DATE + "=?",
                new String[]{Utility.getSelectedDate()}, sortOrder);


        mCursorAdapter = new CursorAdapter(getActivity(), c, 0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                return LayoutInflater.from(context).inflate(R.layout.main_list_item, viewGroup, false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {
                while (getAppIcon(context, cursor.getString(col_packageName)) == null) {
                    cursor.moveToNext();
                }
//                Log.e("View","Binding View");
//                Log.e("DATA", "no.of records=" + cursor.getCount());

                initializeViews(view);
                setDataToViews(context, cursor);


            }
        };
        listView.setAdapter(mCursorAdapter);


        final LinearLayout ll = (LinearLayout) rootView.findViewById(R.id.ll);
        ImageView prev = (ImageView) ll.findViewById(R.id.prevDay);
        ImageView next = (ImageView) ll.findViewById(R.id.nextDay);
        label = (TextView) ll.findViewById(R.id.label);

        prev.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(calendar.DATE, -1);
                Utility.selectedDate = simpleDateFormat.format(calendar.getTime());
                Log.e("Selected Date", "" + Utility.getSelectedDate());
                getLoaderManager().restartLoader(0, null, MainFragment.this);
                TUT = Utility.getTotalUsageTime(getActivity());
                setHeader(TUT, rootView);
                label.setText(getDisplayString());
//                MainActivity.viewPager.setCurrentItem(0);
//                MainActivity.viewPager.setCurrentItem(1);

            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar.add(calendar.DATE, 1);
                Utility.selectedDate = simpleDateFormat.format(calendar.getTime());
                Log.e("Selected Date", "" + Utility.getSelectedDate());
                getLoaderManager().restartLoader(0, null, MainFragment.this);
                setHeader(Utility.getTotalUsageTime(getActivity()), rootView);
                label.setText(getDisplayString());/*
                MainActivity.viewPager.setCurrentItem(0);
                MainActivity.viewPager.setCurrentItem(1);*/
                MainActivity.destroyGraphFragment();

            }
        });
        final Toolbar toolbar=(Toolbar)getActivity().findViewById(R.id.tool_bar);
//        // listView.addHeaderView(getActivity().getLayoutInflater().inflate(R.layout.blank_header,null));
        listView.setOnScrollListener(new AbsListView.OnScrollListener() {
            private int mLastFirstVisibleItem;

            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {
            }

            @Override
            public void onScroll(AbsListView absListView, int firstVisibleItem, int visibleItemCount, int lastVisibleItem) {
                if (firstVisibleItem > mLastFirstVisibleItem) {
                    if (ll.getVisibility() == View.VISIBLE)
                        ll.setVisibility(View.GONE);
                    toolbar.animate().translationY(-actionBar.getHeight());
                   // toolbar.setVisibility(View.GONE);

                }
                if (mLastFirstVisibleItem > firstVisibleItem) {
                    if (ll.getVisibility() == View.GONE)
                        ll.setVisibility(View.VISIBLE);
//                    toolbar.animate().translationY(actionBar.getHeight());
                }

                mLastFirstVisibleItem = firstVisibleItem;
            }
        });

        return rootView;
    }

    public String getDisplayString() {
        Date dateSelected = null;

        try {
            dateSelected = simpleDateFormat.parse(Utility.selectedDate);
        } catch (Exception e) {
            e.printStackTrace();
        }

      //  Log.e("Date", "==SelectedDate is" + Utility.selectedDate);
        long selectedMillis = dateSelected.getTime();
    //    Log.e("Date", "Selected Millis=" + selectedMillis);
        long currentMillis = System.currentTimeMillis();
        //Log.e("Date","Current Millis="+currentMillis);
        long difference = currentMillis - selectedMillis;
  //      Log.e("Date", "Difference Millis=" + difference);
        if (difference < 0) {
            //Toast.makeText(getActivity(),"-ve",Toast.LENGTH_LONG).show();
            return Utility.friendlyDate();
        } else {

            difference /= 24 * 3600 * 1000;
            //Toast.makeText(getActivity(),"Difference="+difference,Toast.LENGTH_LONG).show();

            if (difference == 0)
                return "Today";
            else if (difference < 7)
                return getWeekDay();
            else
                return Utility.friendlyDate();
        }
    }

    public String getWeekDay() {
        String m;
        switch (calendar.get(Calendar.DAY_OF_WEEK)) {
            case 1:
                m = "Sunday";
                break;
            case 2:
                m = "Monday";
                break;
            case 3:
                m = "Tuesday";
                break;
            case 4:
                m = "Wednesday";
                break;
            case 5:
                m = "Thursday";
                break;
            case 6:
                m = "Friday";
                break;
            case 7:
                m = "Saturday";
                break;
            default:
                m = "OtherDay";
        }
        return m;
    }


    public Drawable getAppIcon(Context context, String packageName) {
        pm = context.getPackageManager();
        PackageInfo packageInfo = null;

        try {
            packageInfo = pm.getPackageInfo(packageName, 0);
        } catch (PackageManager.NameNotFoundException ex) {
            Log.e("Exception", "Package Name Not Found" + ex);
            int r = context.getContentResolver().delete(AppContract.AppEntry.contentUri, AppContract.AppEntry.PACKAGE_NAME + "=?", new String[]{packageName});
            Log.e("Deleted", "rows Del==" + r);
            return null;
        }

        return packageInfo.applicationInfo.loadIcon(pm);

    }

    public void initializeViews(View view) {
        appNameView = (TextView) view.findViewById(R.id.appNameView);
        durationHrView = (TextView) view.findViewById(R.id.durationHourView);
        durationMinView = (TextView) view.findViewById(R.id.durationMinView);
        SecLabel = (TextView) view.findViewById(R.id.secLabelView);
        HrLabel = (TextView) view.findViewById(R.id.hrLabelView);
        iconView = (ImageView) view.findViewById(R.id.appIconView);
        progressBar = (ProgressBar) view.findViewById(R.id.progressBar);
    }

    public void setDataToViews(Context context, Cursor cursor) {
        appNameView.setText(cursor.getString(col_appName));

        Log.e("View", cursor.getString(col_appName));
        duration_sec = cursor.getLong(col_duration);
        duration_min = duration_sec / 60;
        duration_hour = duration_min / 60;

        if (duration_hour == 0) {
            durationHrView.setVisibility(View.INVISIBLE);
            HrLabel.setVisibility(View.INVISIBLE);
        } else {
            duration_min = duration_min % 60;
            durationHrView.setVisibility(View.VISIBLE);
            HrLabel.setVisibility(View.VISIBLE);
        }

        if (duration_min == 0 && duration_hour == 0) {
            durationMinView.setText(Long.toString(duration_sec));
            SecLabel.setText("sec");
        } else {
            durationMinView.setText(Long.toString(duration_min));
            SecLabel.setText("min");
        }
        durationHrView.setText(Long.toString(duration_hour));
        iconView.setImageDrawable(getAppIcon(context, cursor.getString(col_packageName)));
        TUT=Utility.getTotalUsageTime(context);
        percentage = (float) (duration_sec * 10000 / TUT);
        percentage /= 100;
        progressBar.setProgress(Math.round(percentage));
    }

    public MainFragment() {
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement
        if (id == R.id.interval) {
            DatePickerDialog datePickerDialog = new DatePickerDialog(getActivity(), 3, new DatePickerDialog.OnDateSetListener() {

                @Override
                public void onDateSet(DatePicker datePicker, int year, int monthMinus1, int date) {
                    //Toast.makeText(getActivity(), "Setdate is " + date + " " + monthMinus1 + " " + year, Toast.LENGTH_SHORT).show();
                    long selectedDate = year + (monthMinus1 + 1) * 10000 + date * 1000000;
                    if (selectedDate / 10000000 == 0) {
                        String m = "0";
                        Utility.selectedDate = m.concat(Long.toString(selectedDate));
                    } else {
                        Utility.selectedDate = Long.toString(selectedDate);
                    }

                    getLoaderManager().restartLoader(0, null, MainFragment.this);
                    TUT = Utility.getTotalUsageTime(getActivity());
                    setHeader(TUT, rootView);
                    calendar.set(year, monthMinus1, date);
//                    Log.e("Date", "Calendar.getTime() =" + simpleDateFormat.format(calendar.getTime()));
//                    Log.e("Date", "DisplayString() =" + getDisplayString());
//
                    label.setText(getDisplayString());
//                    Log.e("Selected", "Year+ month+day=" + year + "" + (monthMinus1 + 1) + "" + date);

                }
            }, Utility.presentYear(), Utility.presentMonth() - 1, Utility.presentDay());

            datePickerDialog.show();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {

        Uri uri = AppContract.AppEntry.contentUri;
        return new CursorLoader(getActivity(), uri, PROJECTION,
                AppContract.AppEntry.DATE + "=?",
                new String[]{Utility.getSelectedDate()}, sortOrder);


    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //   Log.e("DATA","no.of records="+data.getCount());

        mCursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.changeCursor(null);
    }

    public void setHeader(long TUT, View r) {
        LinearLayout l = (LinearLayout) r.findViewById(R.id.lLayout);
        TextView TU = (TextView) r.findViewById(R.id.TotalUsage);

        if (TUT == 0) {
            l.setVisibility(View.GONE);
            TU.setText(getString(R.string.noData));
        } else {
            l.setVisibility(View.VISIBLE);
            TU.setText(getString(R.string.total_header));

            TextView tuHr, tuHrLabel, tuMin, tuMinLabel;
            tuHr = (TextView) r.findViewById(R.id.tuHr);
            tuHrLabel = (TextView) r.findViewById(R.id.tuHrLabel);
            tuMin = (TextView) r.findViewById(R.id.tuMin);
            tuMinLabel = (TextView) r.findViewById(R.id.tuMinLabel);
            long duration_min = 0, duration_hr;
            if (TUT > 60) {
                tuMinLabel.setText(getString(R.string.min));
                duration_min = TUT / 60;
                if (duration_min > 60) {
                    tuHr.setVisibility(View.VISIBLE);
                    tuHrLabel.setVisibility(View.VISIBLE);
                    duration_hr = duration_min / 60;
                    duration_min %= 60;
                    tuHr.setText(Long.toString(duration_hr));
                    tuMin.setText(Long.toString(duration_min));
                } else {
                    tuHr.setVisibility(View.GONE);
                    tuHrLabel.setVisibility(View.GONE);
                    tuMin.setText(Long.toString(duration_min));
                }

            } else {
                //TUT is less than 60
                tuHr.setVisibility(View.GONE);
                tuHrLabel.setVisibility(View.GONE);
                tuMinLabel.setText(getString(R.string.sec));
                tuMin.setText(Long.toString(TUT));
            }
        }

    }

}
