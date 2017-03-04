package commandoengineer.appalyzer;

import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v7.app.ActionBar;
import android.content.Intent;

import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.ggogle.com.myapplication.R;
import commandoengineer.appalyzer.SlidingTabs.SlidingTabLayout;
import commandoengineer.appalyzer.fragments.Fragment1;
import commandoengineer.appalyzer.fragments.Fragment2;
import commandoengineer.appalyzer.fragments.MainFragment;


public class MainActivity extends ActionBarActivity {

    public static ViewPager viewPager;
    FragmentStatePagerAdapter mFragmentAdapter;
    SlidingTabLayout tabs;
    public static ActionBar supportBar;
    String tabNames[] = {"Limits", "Details", "Graph"};
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        toolbar=(Toolbar)this.findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);
        supportBar = getSupportActionBar();


        //TODO: Add BOOT time alarm set

        setViewPagerAndAdapter();
        tabs = (SlidingTabLayout) findViewById(R.id.tabs);
        tabs.setDistributeEvenly(true);
        tabs.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {
            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(R.color.tabsScrollColor);
            }
        });
        tabs.setSelectedIndicatorColors(getResources().getColor(R.color.white));
        tabs.setViewPager(viewPager);
        viewPager.setCurrentItem(1);
//
//        if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.LOLLIPOP)
//        {
//        Intent in=new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS);
//        startActivity(in);
//        }
/*
        Intent in = new Intent(Intent.ACTION_MAIN);
        in.setClassName("com.android.settings", "com.android.settings.UsageStats");
        startActivity(in);
bindService(in, new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
    Log.e("Connected","service");
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
},0);*/


        try {
            startService(new Intent(this, AppalyzerService.class));
        }             //STARTING SERVICE
        catch (Exception ex) {
            Log.e("Exception", "" + ex);
        }
    }

    public void setViewPagerAndAdapter() {
        mFragmentAdapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0:
                        return new Fragment2();
                    case 1:
                        return new MainFragment();
                    case 2:
                        return new Fragment1();
                    default:
                        throw new UnknownError();
                }
            }

            public CharSequence getPageTitle(int position) {
                return tabNames[position];
            }

            @Override
            public int getCount() {
                return 3;
            }
        };
        viewPager = (ViewPager) this.findViewById(R.id.pager);
        viewPager.setAdapter(mFragmentAdapter);

    }
public static void destroyGraphFragment()
{
  //  viewPager.getAdapter().destroyItem(viewPager,2,new Fragment1());
}
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_frag, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

}
