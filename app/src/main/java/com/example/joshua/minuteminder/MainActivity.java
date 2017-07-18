package com.example.joshua.minuteminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.joshua.minuteminder.R.id.container;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private static ArrayList<Minder> minderArrayList = new ArrayList<Minder>(10);
    private static Minder minder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(container);
        mViewPager.setAdapter(mSectionsPagerAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void addMinder() {
        minderArrayList.add(minder);
        deployMinder(minder);
    }

    public void deployMinder(Minder m) {
        if (m.getIsActive()) {
            NotificationTimerTask myTask = new NotificationTimerTask();
            Timer myTimer = new Timer();

            //Scheduling parameters are task, time till start, and time upon which to repeat
            //30000 milliseconds is 30 seconds
            myTimer.schedule(myTask, m.getFrequency(), m.getFrequency());
        }
    }

    //Creates concrete class for abstract TimerTask
    class NotificationTimerTask extends TimerTask {
        public void run() {
            createNotification(getApplicationContext(), getCurrTime());

        }
    }

    private void createNotification(Context context, String message) {

        long when = System.currentTimeMillis();
        String appName = context.getResources().getString(R.string.app_name);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, CreateMinder.class), 0);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        Notification notification = builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.clock)
                .setTicker(appName)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(appName)
                .setContentText(message)
                .setSound(uri)
                .build();
        notificationManager.notify((int) when, notification);
    }

    //Returns current time in a formatted string
    private String getCurrTime() {
        Calendar calendar = Calendar.getInstance();
        int hour = calendar.get(Calendar.HOUR);
        int minute = calendar.get(Calendar.MINUTE);
        //Ensuring minute is always double digit
        String min = minute > 9 ? "" + minute : "0" + minute;
        //Ternary expression to set AM/PM
        String am_pm = (int) calendar.get(Calendar.AM_PM) == Calendar.AM ? " AM" : " PM";
        String returner = "The time is ";
        returner += hour + ":" + min + am_pm;
        return returner;
    }

    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        public PlaceholderFragment() {
        }
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            Button createMinder = (Button) rootView.findViewById(R.id.GoToCreateMinderButton);
            createMinder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(getActivity(), CreateMinder.class);
                    startActivity(intent);
                }
            });

            Button deleteAllMinders = (Button) rootView.findViewById(R.id.deleteAllMinders);
            deleteAllMinders.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    minderArrayList = new ArrayList<>(10);
                }
            });

//            ListView minderList = (ListView) rootView.findViewById(R.id.minderList);
//            ArrayAdapter<Minder> arrayAdapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_list_item_1, minderArrayList);
//
//            minderList.setAdapter(arrayAdapter);

            return rootView;
        }
    }

    public Minder getMinder() {
        return minder;
    }

    public static void setMinder(Minder m) {
        minder = m;
    }
}
