package com.example.joshua.minuteminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by Joshua on 7/8/2017.
 */

public class Minder {

    private int frequency;
    private static Boolean isActive = false;
    private Calendar calendar;
    private Context context;

    public Minder(int frequency, Context context) {
        this.frequency = frequency;
        this.context = context;
    }

    //Creates concrete class for abstract TimerTask
    class NotificationTimerTask extends TimerTask {
        public void run() {
            if (isActive) {
                createNotification(context, getCurrTime());
            }
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

    //Returns current time in a formatted string
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

    public void deployMinder() {
        final NotificationTimerTask myTask = new NotificationTimerTask();
        final Timer myTimer = new Timer();

        //Scheduling parameters are task, time till start, and time upon which to repeat
        //30000 milliseconds is 30 seconds
        myTimer.schedule(myTask, frequency, frequency);
    }

    public void toggle() {
        isActive = !isActive;
    }

    //Getters and Setters
    public int getFrequency() {
        return frequency;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }

    public static Boolean getIsActive() {
        return isActive;
    }

    public static void setIsActive(Boolean isActive) {
        Minder.isActive = isActive;
    }

    public Calendar getCalendar() {
        return calendar;
    }

    public void setCalendar(Calendar calendar) {
        this.calendar = calendar;
    }
}
