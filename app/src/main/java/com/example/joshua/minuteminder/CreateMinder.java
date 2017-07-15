package com.example.joshua.minuteminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class CreateMinder extends AppCompatActivity {

    private int milliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_minder);


        // TOGGLE SWITCH NOTIFICATION - FOR NOW LETS LEAVE IT ALWAYS ON
//        final ToggleButton toggleSwitch = (ToggleButton) findViewById(R.id.toggleButton);
//        toggleSwitch.setChecked(false);
//        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked) {
////                    minder1.setIsActive(true);
//                } else {
////                    minder1.setIsActive(false);
//                }
//            }
//        });

//        //SUBMITTING FREQUENCY CHANGE
//        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
//        final EditText input = (EditText) findViewById(R.id.textView);
//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                String inputText = input.getText().toString();
//                if (isIntegerString(inputText) && !inputText.equals("")) {
//                    milliseconds = Integer.parseInt(inputText) * 1000;
////                    minder1.setFrequency(milliseconds);
////                    minder1.deployMinder();
//                    Snackbar.make(view, "Frequency set to every " + (milliseconds/1000) + " seconds.", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                } else {
//                    Snackbar.make(view, "Please enter a numeric value!", Snackbar.LENGTH_LONG)
//                            .setAction("Action", null).show();
//                }
//            }
//        });
    //End of onCreate()
        final Context context = this;

        Button createMinder = (Button) findViewById(R.id.createMinder);
        createMinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText minderNameEditText = (EditText) findViewById(R.id.minderName);
                final EditText minderFrequencyEditText = (EditText) findViewById(R.id.minderFrequency);
                String minderName = minderNameEditText.getText().toString();
                int minderFrequency = Integer.parseInt(minderFrequencyEditText.getText().toString());
                MainActivity.addMinder(new Minder(minderName, minderFrequency));

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
            }
        });
    }

    //Checks if the string is composed of only integers
    private boolean isIntegerString(String x) {
        try {
            int y = Integer.parseInt(x);
            return true;
        } catch (Exception e) {
            return false;
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
        Notification notification;
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, CreateMinder.class), 0);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        notification = builder.setContentIntent(contentIntent)
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

    public void deployMinder(Minder m) {
        if (m.getIsActive()) {
            final NotificationTimerTask myTask = new NotificationTimerTask();
            final Timer myTimer = new Timer();

            //Scheduling parameters are task, time till start, and time upon which to repeat
            //30000 milliseconds is 30 seconds
            myTimer.schedule(myTask, m.getFrequency(), m.getFrequency());
        }
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
