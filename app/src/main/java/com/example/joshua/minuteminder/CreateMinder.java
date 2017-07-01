package com.example.joshua.minuteminder;

import android.app.Activity;
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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;


/**
 * Created by Joshua on 6/29/2017.
 */

public class CreateMinder extends AppCompatActivity {

    private static Boolean minderToggle = false;
    private static int milliseconds = 5000;
    private Calendar calendar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_minder);



        CreateMinder.NotificationTimerTask myTask = new NotificationTimerTask();
        Timer myTimer = new Timer();

        //Scheduling parameters are task, time till start, and time upon which to repeat
        //30000 milliseconds is 30 seconds
        myTimer.schedule(myTask, 5000, milliseconds);


        // TOGGLE SWITCH NOTIFICATION
        final ToggleButton toggleSwitch = (ToggleButton) findViewById(R.id.toggleButton);
        toggleSwitch.setChecked(false);
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    minderToggle = true;
                } else {
                    minderToggle = false;
                }
            }
        });


        //SUBMITTING FREQUENCY CHANGE
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        final EditText input = (EditText) findViewById(R.id.textView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                milliseconds = Integer.parseInt(input.getText().toString()) * 1000;
                Snackbar.make(view, "Frequency set to every " + (milliseconds/1000) + " seconds.", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    //End of onCreate()
    }


    //Creates concrete class for abstract TimerTask
    class NotificationTimerTask extends TimerTask {
        public void run() {
            if (minderToggle) {
                createNotification(getApplicationContext(), getCurrTime());
            }
        }
    }

    private void createNotification(Context context, String message) {

        long when = System.currentTimeMillis();
        String appname = context.getResources().getString(R.string.app_name);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        Notification notification;
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, CreateMinder.class), 0);
        Uri uri= RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context);
        notification = builder.setContentIntent(contentIntent)
                .setSmallIcon(R.drawable.clock)
                .setTicker(appname)
                .setWhen(0)
                .setAutoCancel(true)
                .setContentTitle(appname)
                .setContentText(message)
                .setSound(uri)
                .build();
        notificationManager.notify((int) when, notification);
    }

    private String getCurrTime() {
        calendar = Calendar.getInstance();
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
