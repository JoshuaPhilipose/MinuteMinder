package com.example.joshua.minuteminder;

import android.app.Dialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.googleapis.extensions.android.gms.auth.GooglePlayServicesAvailabilityIOException;
import com.google.api.client.googleapis.extensions.android.gms.auth.UserRecoverableAuthIOException;

import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.client.util.ExponentialBackOff;

import com.google.api.services.calendar.CalendarScopes;
import com.google.api.client.util.DateTime;

import com.google.api.services.calendar.model.*;

import android.Manifest;
import android.accounts.AccountManager;
import android.app.ProgressDialog;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.text.method.ScrollingMovementMethod;
import android.widget.LinearLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import pub.devrel.easypermissions.AfterPermissionGranted;
import pub.devrel.easypermissions.EasyPermissions;

import static com.example.joshua.minuteminder.R.id.container;

//TODO: Fix bug: time doesn't update on UI, updates fine in backend

public class MainActivity extends AppCompatActivity {

    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;

    private static final String minderPrefs = "MinderPreferences";
    private static final String calendarPrefs = "CalendarPreferences";
    private static final String historyPrefs = "HistoryPreferences";

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

        private static boolean minderToggle = false;
        private static boolean isStartPM = false;
        private static boolean isEndPM = true;

        private static Timer timer = new Timer();
        private static int startHour = 8;
        private static int endHour = 20;
        private static int startMinute = 0;
        private static int endMinute = 0;

        private NumberPicker minderFrequency;
        private Spinner frequencyUnit;
        private TextView startTimeText;
        private TextView endTimeText;

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
            final View rootView = inflater.inflate(R.layout.minder_fragment, container, false);
            SharedPreferences sharedPref = getContext().getSharedPreferences(minderPrefs, Context.MODE_PRIVATE);

            //MINDER FREQUENCY
            int startupFrequency = sharedPref.getInt("startupFrequency", 30);
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


            String[] hours = new String[12];
            for(int i = 0; i < hours.length; i++) {
                hours[i] = Integer.toString(i + 1);
            }

            String[] minutes = new String[60];
            for(int i = 0; i < minutes.length; i++) {
                minutes[i] = Integer.toString(i);
            }

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

            //TEXT BOXES
            final int startupStartHour = (sharedPref.getInt("startHour", 1) % 12);
            final int startupStartMinute = sharedPref.getInt("startMinute", 0);
            final int startupEndHour = (sharedPref.getInt("endHour", 1) % 12);
            final int startupEndMinute = sharedPref.getInt("endMinute", 0);

            String textStartMin = startupStartMinute > 9 ? "" + startupStartMinute : "0" + startupStartMinute;
            String textEndMin = startupEndMinute > 9 ? "" + startupEndMinute : "0" + startupEndMinute;
            startTimeText = (TextView) rootView.findViewById(R.id.currStartTime);
            String temp = isStartPM ? "PM" : "AM";
            startTimeText.setText("" + startupStartHour + ":" + textStartMin + " " + temp);
            endTimeText = (TextView) rootView.findViewById(R.id.currEndTime);
            String tempTwo = isEndPM ? "PM" : "AM";
            endTimeText.setText("" + startupEndHour + ":" + textEndMin + " " + tempTwo);

            //BUTTONS
            Button updateMinder = (Button) rootView.findViewById(R.id.updateMinderButton);
            updateMinder.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    onOff.setChecked(true);

                    SharedPreferences sharedPref = getContext().getSharedPreferences(minderPrefs, Context.MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPref.edit();

                    editor.putInt("startHour", startHour);
                    editor.putInt("endHour", endHour);
                    editor.putInt("startMinute", startMinute);
                    editor.putInt("endMinute", endMinute);
                    editor.putBoolean("isStartPM", isStartPM);
                    editor.putBoolean("isEndPM", isEndPM);
                    editor.apply();

                    //Convert to army time for easy calculation
                    startHour = isStartPM ? startHour + 12 : startHour;
                    endHour = isEndPM ? endHour + 12 : endHour;

                    updateMinder();
                    Snackbar.make(view, "Minder updated!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            });

            Button updateStartTime = (Button) rootView.findViewById(R.id.pickStartTime);
            updateStartTime.setOnClickListener(new View.OnClickListener() {
                @Override
                    public void onClick(View view) {
                    showStartTimePickerDialog(view);

                    //After the appropriate fragment level fields are set, set the UI text
                    String textStartMin = startMinute > 9 ? "" + startMinute : "0" + startMinute;
                    startTimeText = (TextView) rootView.findViewById(R.id.currStartTime);
                    String temp = isStartPM ? "PM" : "AM";
                    startTimeText.setText("" + startHour + ":" + textStartMin + " " + temp);
                }
            });

            Button updateEndTime = (Button) rootView.findViewById(R.id.pickEndTime);
            updateEndTime.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showEndTimePickerDialog(view);
                    String textEndMin = startupEndMinute > 9 ? "" + startupEndMinute : "0" + startupEndMinute;
                    endTimeText = (TextView) rootView.findViewById(R.id.currEndTime);
                    String tempTwo = isEndPM ? "PM" : "AM";
                    endTimeText.setText("" + startupEndHour + ":" + textEndMin + " " + tempTwo);

                }
            });

            return rootView;
        }

        //SENDING OUT MINDER NOTIFICATIONS
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

        //MINDER ACTIVE HOURS
        public void showStartTimePickerDialog(View v) {
            DialogFragment newFragment = new StartTimePickerFragment();
            newFragment.show(getFragmentManager(), "startTimePicker");
        }

        public void showEndTimePickerDialog(View v) {
            DialogFragment newFragment = new EndTimePickerFragment();
            newFragment.show(getFragmentManager(), "endTimePicker");
        }

        public static class StartTimePickerFragment extends DialogFragment
                implements TimePickerDialog.OnTimeSetListener {

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the current time as the default values for the picker
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a new instance of TimePickerDialog and return it
                return new TimePickerDialog(getActivity(), this, hour, minute,
                        DateFormat.is24HourFormat(getActivity()));
            }

            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String AMPM = hourOfDay < 13 ? "AM" : "PM";
                int hour = hourOfDay % 12;

                setStartTime(hour, minute, AMPM);
            }
        }

        public static void setStartTime(int hour, int minute, String AMPM) {
            isStartPM = !AMPM.equals("AM");
            startHour = hour;
            startMinute = minute;
        }

        public static class EndTimePickerFragment extends DialogFragment
                implements TimePickerDialog.OnTimeSetListener {

            @Override
            public Dialog onCreateDialog(Bundle savedInstanceState) {
                // Use the current time as the default values for the picker
                final Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int minute = c.get(Calendar.MINUTE);

                // Create a new instance of TimePickerDialog and return it
                return new TimePickerDialog(getActivity(), this, hour, minute,
                        DateFormat.is24HourFormat(getActivity()));
            }

            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                String AMPM = hourOfDay < 13 ? "AM" : "PM";
                int hour = hourOfDay % 12;

                setEndTime(hour, minute, AMPM);
            }
        }

        public static void setEndTime(int hour, int minute, String AMPM) {
            isEndPM = !AMPM.equals("AM");
            endHour = hour;
            endMinute = minute;
        }
    }

    public static class AutoSilenceFragment extends Fragment implements EasyPermissions.PermissionCallbacks {
        private static final String ARG_SECTION_NUMBER = "section_number";

        GoogleAccountCredential mCredential;
        private TextView mOutputText;
        private Button mCallApiButton;
        ProgressDialog mProgress;

        static final int REQUEST_ACCOUNT_PICKER = 1000;
        static final int REQUEST_AUTHORIZATION = 1001;
        static final int REQUEST_GOOGLE_PLAY_SERVICES = 1002;
        static final int REQUEST_PERMISSION_GET_ACCOUNTS = 1003;

        private static final String BUTTON_TEXT = "Call Google Calendar API";
        private static final String PREF_ACCOUNT_NAME = "accountName";
        private static final String[] SCOPES = { CalendarScopes.CALENDAR_READONLY };

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

//            LinearLayout activityLayout = new LinearLayout(getContext());
//            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,
//                    LinearLayout.LayoutParams.MATCH_PARENT);
//            activityLayout.setLayoutParams(lp);
//            activityLayout.setOrientation(LinearLayout.VERTICAL);
//            activityLayout.setPadding(16, 16, 16, 16);
//
////            ViewGroup.LayoutParams tlp = new ViewGroup.LayoutParams(
////                    ViewGroup.LayoutParams.WRAP_CONTENT,
////                    ViewGroup.LayoutParams.WRAP_CONTENT);

            mCallApiButton = (Button) rootView.findViewById(R.id.CallGoogleCalendarAPI);
            mCallApiButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mCallApiButton.setEnabled(false);
                    mOutputText.setText("");
                    getResultsFromApi();
                    mCallApiButton.setEnabled(true);
                }
            });
//            activityLayout.addView(mCallApiButton);

            mOutputText = (TextView) rootView.findViewById(R.id.APICallResults);
//            mOutputText.setLayoutParams(tlp);
//            mOutputText.setPadding(16, 16, 16, 16);
//            mOutputText.setVerticalScrollBarEnabled(true);
//            mOutputText.setMovementMethod(new ScrollingMovementMethod());
//            mOutputText.setText(
//                    "Click the \'" + BUTTON_TEXT +"\' button to test the API.");
//            activityLayout.addView(mOutputText);

            mProgress = new ProgressDialog(getContext());
            mProgress.setMessage("Calling Google Calendar API ...");

            //TODO: make this set the content of the fragment
//            getActivity().setContentView(activityLayout);

            // Initialize credentials and service object.
            mCredential = GoogleAccountCredential.usingOAuth2(getContext(), Arrays.asList(SCOPES)).setBackOff(new ExponentialBackOff());



            return rootView;
        }

        /**
         * Attempt to call the API, after verifying that all the preconditions are
         * satisfied. The preconditions are: Google Play Services installed, an
         * account was selected and the device currently has online access. If any
         * of the preconditions are not satisfied, the app will prompt the user as
         * appropriate.
         */
        private void getResultsFromApi() {
            if (! isGooglePlayServicesAvailable()) {
                acquireGooglePlayServices();
            } else if (mCredential.getSelectedAccountName() == null) {
                chooseAccount();
            } else if (! isDeviceOnline()) {
                mOutputText.setText("No network connection available.");
            } else {
                new MakeRequestTask(mCredential).execute();
            }
        }

        /**
         * Attempts to set the account used with the API credentials. If an account
         * name was previously saved it will use that one; otherwise an account
         * picker dialog will be shown to the user. Note that the setting the
         * account to use with the credentials object requires the app to have the
         * GET_ACCOUNTS permission, which is requested here if it is not already
         * present. The AfterPermissionGranted annotation indicates that this
         * function will be rerun automatically whenever the GET_ACCOUNTS permission
         * is granted.
         */
        @AfterPermissionGranted(REQUEST_PERMISSION_GET_ACCOUNTS)
        private void chooseAccount() {
            if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.GET_ACCOUNTS)) {
                String accountName = getActivity().getPreferences(Context.MODE_PRIVATE)
                        .getString(PREF_ACCOUNT_NAME, null);
                if (accountName != null) {
                    mCredential.setSelectedAccountName(accountName);
                    getResultsFromApi();
                } else {
                    // Start a dialog from which the user can choose an account
                    startActivityForResult(
                            mCredential.newChooseAccountIntent(),
                            REQUEST_ACCOUNT_PICKER);
                }
            } else {
                // Request the GET_ACCOUNTS permission via a user dialog
                EasyPermissions.requestPermissions(
                        this,
                        "This app needs to access your Google account (via Contacts).",
                        REQUEST_PERMISSION_GET_ACCOUNTS,
                        Manifest.permission.GET_ACCOUNTS);
            }
        }

        /**
         * Called when an activity launched here (specifically, AccountPicker
         * and authorization) exits, giving you the requestCode you started it with,
         * the resultCode it returned, and any additional data from it.
         * @param requestCode code indicating which activity result is incoming.
         * @param resultCode code indicating the result of the incoming
         *     activity result.
         * @param data Intent (containing result data) returned by incoming
         *     activity result.
         */
        @Override
        public void onActivityResult(
                int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            switch(requestCode) {
                case REQUEST_GOOGLE_PLAY_SERVICES:
                    if (resultCode != RESULT_OK) {
                        mOutputText.setText("This app requires Google Play Services. Please install "
                                + "Google Play Services on your device and relaunch this app.");
                    } else {
                        getResultsFromApi();
                    }
                    break;
                case REQUEST_ACCOUNT_PICKER:
                    if (resultCode == RESULT_OK && data != null &&
                            data.getExtras() != null) {
                        String accountName =
                                data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                        if (accountName != null) {
                            SharedPreferences settings = getActivity().getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = settings.edit();
                            editor.putString(PREF_ACCOUNT_NAME, accountName);
                            editor.apply();
                            mCredential.setSelectedAccountName(accountName);
                            getResultsFromApi();
                        }
                    }
                    break;
                case REQUEST_AUTHORIZATION:
                    if (resultCode == RESULT_OK) {
                        getResultsFromApi();
                    }
                    break;
            }
        }

        /**
         * Respond to requests for permissions at runtime for API 23 and above.
         * @param requestCode The request code passed in
         *     requestPermissions(android.app.Activity, String, int, String[])
         * @param permissions The requested permissions. Never null.
         * @param grantResults The grant results for the corresponding permissions
         *     which is either PERMISSION_GRANTED or PERMISSION_DENIED. Never null.
         */
        @Override
        public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
            EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
        }

        /**
         * Callback for when a permission is granted using the EasyPermissions
         * library.
         * @param requestCode The request code associated with the requested
         *         permission
         * @param list The requested permission list. Never null.
         */
        @Override
        public void onPermissionsGranted(int requestCode, List<String> list) {
            // Do nothing.
        }

        /**
         * Callback for when a permission is denied using the EasyPermissions
         * library.
         * @param requestCode The request code associated with the requested
         *         permission
         * @param list The requested permission list. Never null.
         */
        @Override
        public void onPermissionsDenied(int requestCode, List<String> list) {
            // Do nothing.
        }

        /**
         * Checks whether the device currently has a network connection.
         * @return true if the device has a network connection, false otherwise.
         */
        private boolean isDeviceOnline() {
            ConnectivityManager connMgr = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }

        /**
         * Check that Google Play services APK is installed and up to date.
         * @return true if Google Play Services is available and up to
         *     date on this device; false otherwise.
         */
        private boolean isGooglePlayServicesAvailable() {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            final int connectionStatusCode = apiAvailability.isGooglePlayServicesAvailable(getContext());
            return connectionStatusCode == ConnectionResult.SUCCESS;
        }

        /**
         * Attempt to resolve a missing, out-of-date, invalid or disabled Google
         * Play Services installation via a user dialog, if possible.
         */
        private void acquireGooglePlayServices() {
            GoogleApiAvailability apiAvailability =
                    GoogleApiAvailability.getInstance();
            final int connectionStatusCode =
                    apiAvailability.isGooglePlayServicesAvailable(getContext());
            if (apiAvailability.isUserResolvableError(connectionStatusCode)) {
                showGooglePlayServicesAvailabilityErrorDialog(connectionStatusCode);
            }
        }


        /**
         * Display an error dialog showing that Google Play Services is missing
         * or out of date.
         * @param connectionStatusCode code describing the presence (or lack of)
         *     Google Play Services on this device.
         */
        void showGooglePlayServicesAvailabilityErrorDialog(
                final int connectionStatusCode) {
            GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
            Dialog dialog = apiAvailability.getErrorDialog(getActivity(), connectionStatusCode, REQUEST_GOOGLE_PLAY_SERVICES);
            dialog.show();
        }

        /**
         * An asynchronous task that handles the Google Calendar API call.
         * Placing the API calls in their own task ensures the UI stays responsive.
         */
        private class MakeRequestTask extends AsyncTask<Void, Void, List<String>> {
            private com.google.api.services.calendar.Calendar mService = null;
            private Exception mLastError = null;

            MakeRequestTask(GoogleAccountCredential credential) {
                HttpTransport transport = AndroidHttp.newCompatibleTransport();
                JsonFactory jsonFactory = JacksonFactory.getDefaultInstance();
                mService = new com.google.api.services.calendar.Calendar.Builder(
                        transport, jsonFactory, credential)
                        .setApplicationName("Google Calendar API Android Quickstart")
                        .build();
            }

            /**
             * Background task to call Google Calendar API.
             *
             * @param params no parameters needed for this task.
             */
            @Override
            protected List<String> doInBackground(Void... params) {
                try {
                    return getDataFromApi();
                } catch (Exception e) {
                    mLastError = e;
                    cancel(true);
                    return null;
                }
            }

            /**
             * Fetch a list of the next 10 events from the primary calendar.
             *
             * @return List of Strings describing returned events.
             * @throws IOException
             */
            private List<String> getDataFromApi() throws IOException {
                // List the next 10 events from the primary calendar.
                DateTime now = new DateTime(System.currentTimeMillis());
                List<String> eventStrings = new ArrayList<String>();
                Events events = mService.events().list("primary")
                        .setMaxResults(10)
                        .setTimeMin(now)
                        .setOrderBy("startTime")
                        .setSingleEvents(true)
                        .execute();
                List<Event> items = events.getItems();

                for (Event event : items) {
                    DateTime start = event.getStart().getDateTime();
                    if (start == null) {
                        // All-day events don't have start times, so just use
                        // the start date.
                        start = event.getStart().getDate();
                    }
                    eventStrings.add(
                            String.format("%s (%s)", event.getSummary(), start));
                }
                return eventStrings;
            }

            @Override
            protected void onPreExecute() {
                mOutputText.setText("");
                mProgress.show();
            }

            @Override
            protected void onPostExecute(List<String> output) {
                mProgress.hide();
                if (output == null || output.size() == 0) {
                    mOutputText.setText("No results returned.");
                } else {
                    output.add(0, "Data retrieved using the Google Calendar API:");
                    mOutputText.setText(TextUtils.join("\n", output));
                }
            }

            @Override
            protected void onCancelled() {
                mProgress.hide();
                if (mLastError != null) {
                    if (mLastError instanceof GooglePlayServicesAvailabilityIOException) {
                        showGooglePlayServicesAvailabilityErrorDialog(((GooglePlayServicesAvailabilityIOException) mLastError)
                                .getConnectionStatusCode());
                    } else if (mLastError instanceof UserRecoverableAuthIOException) {
                        startActivityForResult(((UserRecoverableAuthIOException) mLastError).getIntent(), REQUEST_AUTHORIZATION);
                    } else {
                        mOutputText.setText("The following error occurred:\n"
                                + mLastError.getMessage());
                    }
                } else {
                    mOutputText.setText("Request cancelled.");
                }
            }
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
