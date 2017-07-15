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

    @Override
    public String toString() {
        return name;
    }


    public void toggle() {
        isActive = !isActive;
    }

    //Getters and Setters

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

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
