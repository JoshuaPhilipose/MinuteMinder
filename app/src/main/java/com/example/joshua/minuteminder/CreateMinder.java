package com.example.joshua.minuteminder;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

public class CreateMinder extends AppCompatActivity {

    private Minder m;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_minder);

        final Context context = this;

        Button createMinder = (Button) findViewById(R.id.createMinder);
        createMinder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final EditText minderNameEditText = (EditText) findViewById(R.id.minderName);
                final EditText minderFrequencyEditText = (EditText) findViewById(R.id.minderFrequency);
                String minderName = "" + minderNameEditText.getText().toString();
                int minderFrequency = Integer.parseInt(minderFrequencyEditText.getText().toString());
                m = new Minder(minderName, minderFrequency);
                MainActivity.setMinder(m);

                Intent intent = new Intent(context, MainActivity.class);
                startActivity(intent);
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
