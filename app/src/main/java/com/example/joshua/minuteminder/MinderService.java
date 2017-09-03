package com.example.joshua.minuteminder;

import android.app.IntentService;
import android.content.Intent;

import java.util.Calendar;

/**
 * Created by Joshua on 9/2/2017.
 */

public class MinderService extends IntentService {

    public MinderService() {
        super("MinderService");
    }

    @Override
    protected void onHandleIntent(Intent workIntent) {
        // Gets data from the incoming Intent
        String dataString = workIntent.getDataString();

        // Do work here, based on the contents of dataString

    }
}
