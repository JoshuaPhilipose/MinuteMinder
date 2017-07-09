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
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ToggleButton;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class CreateMinder extends AppCompatActivity {

//    private Minder minder1 = new Minder(5000, getApplicationContext());
    private int milliseconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_minder);


        // TOGGLE SWITCH NOTIFICATION
        final ToggleButton toggleSwitch = (ToggleButton) findViewById(R.id.toggleButton);
        toggleSwitch.setChecked(false);
        toggleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    minder1.setIsActive(true);
                } else {
//                    minder1.setIsActive(false);
                }
            }
        });

        //SUBMITTING FREQUENCY CHANGE
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.floatingActionButton);
        final EditText input = (EditText) findViewById(R.id.textView);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String inputText = input.getText().toString();
                if (isIntegerString(inputText) && !inputText.equals("")) {
                    milliseconds = Integer.parseInt(inputText) * 1000;
//                    minder1.setFrequency(milliseconds);
//                    minder1.deployMinder();
                    Snackbar.make(view, "Frequency set to every " + (milliseconds/1000) + " seconds.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                } else {
                    Snackbar.make(view, "Please enter a numeric value!", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
            }
        });
    //End of onCreate()
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
}
