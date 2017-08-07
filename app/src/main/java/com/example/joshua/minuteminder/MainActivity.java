package com.example.joshua.minuteminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
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

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.joshua.minuteminder.R.id.container;
import static com.example.joshua.minuteminder.R.id.minderFrequency;

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


    public static class MinderFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        private static final String minderPrefs = "MinderPreferences";
        private static final String calendarPrefs = "CalendarPreferences";
        private static final String historyPrefs = "HistoryPreferences";

        private static boolean minderToggle = false;
        private static boolean isStartPM = false;
        private static boolean isEndPM = false;

        private static Timer timer = new Timer();
        private int startHour = 8;
        private int endHour = 20;
        private int startMinute = 0;
        private int endMinute = 0;

        private NumberPicker minderFrequency;
        private Spinner frequencyUnit;
        private NumberPicker startHourPicker;
        private NumberPicker startMinutePicker;
        private NumberPicker endHourPicker;
        private NumberPicker endMinutePicker;

        public MinderFragment() {
        }

        public static MinderFragment newInstance(int sectionNumber) {
            MinderFragment fragment = new MinderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.minder_fragment, container, false);
            SharedPreferences sharedPref = getContext().getSharedPreferences(minderPrefs, Context.MODE_PRIVATE);
            int startupFrequency = sharedPref.getInt("startupFrequency", 30);


            //MINDER FREQUENCY
            minderFrequency = (NumberPicker) rootView.findViewById(R.id.minderFrequency);
            String[] minderFrequencyNums = new String[60];
            for(int i = 0; i < minderFrequencyNums.length; i++) {
                minderFrequencyNums[i] = Integer.toString(i + 1);
            }
            minderFrequency.setMinValue(1);
            minderFrequency.setMaxValue(60);
            minderFrequency.setWrapSelectorWheel(true);
            minderFrequency.setDisplayedValues(minderFrequencyNums);
            minderFrequency.setValue(startupFrequency);

            frequencyUnit = (Spinner) rootView.findViewById(R.id.frequencyUnit);
            frequencyUnit.setSelection(1, true);

            //ACTIVE HOURS NUMBER PICKERS
            String[] hours = new String[12];
            for(int i = 0; i < hours.length; i++) {
                hours[i] = Integer.toString(i + 1);
            }

            String[] minutes = new String[60];
            for(int i = 0; i < minutes.length; i++) {
                minutes[i] = Integer.toString(i);
            }

            startHourPicker = (NumberPicker) rootView.findViewById(R.id.startHour);
            startHourPicker.setMinValue(1);
            startHourPicker.setMaxValue(12);
            startHourPicker.setWrapSelectorWheel(true);
            startHourPicker.setDisplayedValues(hours);
            startHourPicker.setValue(1);

            startMinutePicker = (NumberPicker) rootView.findViewById(R.id.startMinute);
            startMinutePicker.setMinValue(1);
            startMinutePicker.setMaxValue(60);
            startMinutePicker.setWrapSelectorWheel(true);
            startMinutePicker.setDisplayedValues(minutes);
            startMinutePicker.setValue(1);
//            startMinutePicker.setFormatter(new NumberPicker.Formatter() {
//                @Override
//                public String format(int i) {
//                    return String.format("%02d", i);
//                }
//            });

            endHourPicker = (NumberPicker) rootView.findViewById(R.id.endHour);
            endHourPicker.setMinValue(1);
            endHourPicker.setMaxValue(12);
            endHourPicker.setWrapSelectorWheel(true);
            endHourPicker.setDisplayedValues(hours);
            endHourPicker.setValue(1);

            endMinutePicker = (NumberPicker) rootView.findViewById(R.id.endMinute);
            endMinutePicker.setMinValue(1);
            endMinutePicker.setMaxValue(60);
            endMinutePicker.setWrapSelectorWheel(true);
            endMinutePicker.setDisplayedValues(minutes);
            endMinutePicker.setValue(1);
//            endMinutePicker.setFormatter(new NumberPicker.Formatter() {
//                @Override
//                public String format(int i) {
//                    return String.format("%02d", i);
//                }
//            });

            setDividerColor(minderFrequency, Color.TRANSPARENT);
            setDividerColor(startHourPicker, Color.TRANSPARENT);
            setDividerColor(startMinutePicker, Color.TRANSPARENT);
            setDividerColor(endHourPicker, Color.TRANSPARENT);
            setDividerColor(endMinutePicker, Color.TRANSPARENT);

            //TOGGLES
            final ToggleButton onOff = (ToggleButton) rootView.findViewById(R.id.onOffToggle);
            onOff.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        minderToggle = true;
                    } else {
                        minderToggle = false;
                    }
                }
            });

            final ToggleButton startAMPM = (ToggleButton) rootView.findViewById(R.id.startAMPM);
            startAMPM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        isStartPM = true;
                    } else {
                        isStartPM = false;
                    }
                }
            });

            final ToggleButton endAMPM = (ToggleButton) rootView.findViewById(R.id.endAMPM);
            endAMPM.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    boolean temp = endAMPM.isChecked();
                    if (isChecked) {
                        isEndPM = true;
                    } else {
                        isEndPM = false;
                    }
                }
            });

            //BUTTONS
            Button updateMinder = (Button) rootView.findViewById(R.id.updateMinderButton);
            updateMinder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOff.setChecked(true);

                    startHour = startHourPicker.getValue();
                    endHour = endHourPicker.getValue();
                    startMinute = startMinutePicker.getValue() - 1;
                    endMinute = endMinutePicker.getValue() - 1;

                    //Convert to army time for easy calculation
                    startHour = isStartPM ? startHour + 12 : startHour;
                    endHour = isEndPM ? endHour + 12 : endHour;

                    updateMinder();
                    Snackbar.make(view, "Minder updated!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
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

            SharedPreferences sharedPref = getContext().getSharedPreferences(minderPrefs, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();

            editor.putInt("startupFrequency", minderFrequency.getValue());
            editor.apply();

            NotificationTimerTask myTask = new NotificationTimerTask();
            timer.cancel();
            timer = new Timer();

            //Scheduling parameters are task, time till start, and time upon which to repeat
            //30000 milliseconds is 30 seconds
            timer.schedule(myTask, 1000, frequency);
        }

        private boolean checkTimeBoundary() {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR);
            int minute = calendar.get(Calendar.MINUTE);
            String am_pm = (int) calendar.get(Calendar.AM_PM) == Calendar.AM ? "AM" : "PM";
            hour = am_pm == "AM" ? hour : hour + 12;

            if (startHour <= endHour) {
                if (startHour < hour && hour < endHour) {//Is it between the start/end hours?
                    return true;
                } else if (startHour == hour && hour == endHour) {//if all are the same hour, check minutes - if it isn't in between, must be false
                    if (startMinute <= hour && hour <= endMinute) {
                        return true;
                    }
                    return false;
                } else if (startHour == hour && hour < endHour) {
                    if (startMinute <= minute) {
                        return true;
                    }
                    return false;
                } else if (startHour < hour && hour == endHour) {
                    if (minute <= endMinute) {
                        return true;
                    }
                    return false;
                }
            } else if (startHour > endHour) {
                //TODO: Don't do this yet, let's just see if it works so far
            }

            return false;
        }

        //Creates concrete class for abstract TimerTask
        class NotificationTimerTask extends TimerTask {
            public void run() {
                boolean temp = checkTimeBoundary();
                if (minderToggle && temp) {
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

        private void setDividerColor(NumberPicker picker, int color) {
            java.lang.reflect.Field[] pickerFields = NumberPicker.class.getDeclaredFields();
            for (java.lang.reflect.Field pf : pickerFields) {
                if (pf.getName().equals("mSelectionDivider")) {
                    pf.setAccessible(true);
                    try {
                        ColorDrawable colorDrawable = new ColorDrawable(color);
                        pf.set(picker, colorDrawable);
                    } catch (IllegalArgumentException e) {
                        e.printStackTrace();
                    } catch (Resources.NotFoundException e) {
                        e.printStackTrace();
                    }
                    catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }
    }

    public static class AutoSilenceFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public AutoSilenceFragment() {
        }

        public static AutoSilenceFragment newInstance(int sectionNumber) {
            AutoSilenceFragment fragment = new AutoSilenceFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.autosilence_fragment, container, false);

            return rootView;
        }
    }

    public static class HistoryFragment extends Fragment {
        private static final String ARG_SECTION_NUMBER = "section_number";

        public HistoryFragment() {
        }

        public static HistoryFragment newInstance(int sectionNumber) {
            HistoryFragment fragment = new HistoryFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.history_fragment, container, false);

            return rootView;
        }
    }
}
