package com.ggogle.com.myapplication.fragments;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.ggogle.com.myapplication.R;
import com.ggogle.com.myapplication.database.AppContract;

import android.widget.CursorAdapter;
import android.widget.ListView;


public class Fragment2 extends Fragment {

    static String[] PROJECTION = {
            AppContract.AppEntry._ID,
            AppContract.AppEntry.PACKAGE_NAME,
            AppContract.AppEntry.APP_NAME,
            AppContract.AppEntry.DURATION,
            AppContract.AppEntry.LIMIT_TIME
    };

    static int col_packageName = 1;
    static int col_appName = 2;
    static int col_duration = 3;
    static int col_limitTime = 4;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View r = inflater.inflate(R.layout.frag_limit, container, false);
/*

        ListView limitListView = (ListView) r.findViewById(R.id.limits_listView);
        Cursor c = getActivity().getContentResolver().query(AppContract.AppEntry.contentUri,
                PROJECTION,
                AppContract.AppEntry.LIMIT_TIME + "!=?",
                new String[]{"0"},
                null);
        CursorAdapter mLimitAdapter=new CursorAdapter(getActivity(),c,0) {
            @Override
            public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
                return LayoutInflater.from(context).inflate(R.layout.limit_list_item,viewGroup,false);
            }

            @Override
            public void bindView(View view, Context context, Cursor cursor) {

            }
        };
        limitListView.setAdapter(mLimitAdapter);
*/

        return r;
    }

}
