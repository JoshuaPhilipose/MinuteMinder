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
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.joshua.minuteminder.R.id.container;

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

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


    public static class PlaceholderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";
        private static boolean minderToggle = false;
        private static int startupFrequency = 31;
        private static Timer timer = new Timer();
        private NumberPicker minderFrequency;
        private Spinner frequencyUnit;

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


            minderFrequency = (NumberPicker) rootView.findViewById(R.id.minderFrequency);
            String[] nums = new String[60];
            for(int i = 0; i < nums.length; i++) {
                nums[i] = Integer.toString(i + 1);
            }
            minderFrequency.setMinValue(1);
            minderFrequency.setMaxValue(60);
            minderFrequency.setWrapSelectorWheel(true);
            minderFrequency.setDisplayedValues(nums);
            minderFrequency.setValue(startupFrequency);

            frequencyUnit = (Spinner) rootView.findViewById(R.id.frequencyUnit);
            frequencyUnit.setSelection(1, true);

            Button updateMinder = (Button) rootView.findViewById(R.id.updateMinderButton);
            updateMinder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    updateMinder();
                }
            });

            ToggleButton onOff = (ToggleButton) rootView.findViewById(R.id.onOffToggle);
            onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        minderToggle = true;
                    } else {
                        minderToggle = false;
                    }
                }
            });
            return rootView;
        }

        public long calculateFrequency(NumberPicker minderFrequency, Spinner frequencyUnit) {
            long frequency = ((long) minderFrequency.getValue()) * 1000;
            if (frequencyUnit.getSelectedItem().equals("Seconds")) {
                frequency = frequency;
            } else if (frequencyUnit.getSelectedItem().equals("Minutes")) {
                frequency = frequency * 60;
            } else if (frequencyUnit.getSelectedItem().equals("Hours")) {
                frequency = frequency * 60 * 60;
            }
            return frequency;
        }

        private void updateMinder() {
            final long frequency = calculateFrequency(minderFrequency, frequencyUnit);
            startupFrequency = (int) frequency;
            NotificationTimerTask myTask = new NotificationTimerTask();
            timer.cancel();
            timer = new Timer();

            //Scheduling parameters are task, time till start, and time upon which to repeat
            //30000 milliseconds is 30 seconds
            timer.schedule(myTask, 1000, frequency);
        }

        //Creates concrete class for abstract TimerTask
        class NotificationTimerTask extends TimerTask {
            public void run() {
                if (minderToggle) {
                    createNotification(getCurrTime());
                }
            }
        }

        private void createNotification(String message) {

            long when = System.currentTimeMillis();
            String appName = getContext().getResources().getString(R.string.app_name);
//            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationManager notificationManager = (NotificationManager) getActivity().getSystemService(getActivity().NOTIFICATION_SERVICE);

            PendingIntent contentIntent = PendingIntent.getActivity(getContext(), 0, new Intent(getActivity(), MainActivity.class), 0);
            Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
            NotificationCompat.Builder builder = new NotificationCompat.Builder(
                    getContext());
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
    }
}
