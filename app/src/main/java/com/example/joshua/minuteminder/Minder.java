package com.example.joshua.minuteminder;

import java.util.Calendar;

/**
 * Created by Joshua on 7/8/2017.
 */

public class Minder {

    private String name;
    private int frequency;
    private static Boolean isActive = true;
    private Calendar calendar;

    public Minder(String name, int frequency) {
        this.name = name;
        this.frequency = frequency;
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
