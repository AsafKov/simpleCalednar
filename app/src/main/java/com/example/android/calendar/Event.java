package com.example.android.calendar;

import android.content.Context;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.UUID;

public class Event{

    public static final String[] mLabelsTypes = new String[]{"Social Event", "Training", "Eating", "Sleeping",
            "Reading", "Studying", "Break", "Traveling", "Housework", "Schoolwork", "Internet", "T.V", "Working",
            "Hobby"};

    public final static int START_TIME_KEY = 1;
    public final static int END_TIME_KEY = 2;

    private Day mDayParent;
    private String mLabel;
    private String mComment;
    private UUID mId;

    private int mStartAtMinuteInDay;
    private int mEndAtMinuteInDay;

    public Event(Day dayParent){
        mId = UUID.randomUUID();
        mDayParent = dayParent;
        mLabel = "Break";
    }

    public Event clone(){
        Event cloneEvent = new Event(this.mDayParent);
        cloneEvent.setLabel(this.mLabel);
        cloneEvent.setComment(this.mComment);

        return cloneEvent;
    }

    public void scheduleEvent(int timeKey, int hourOfDay, int minute){
        if(timeKey == START_TIME_KEY) {
            if(hourOfDay == 24)
                hourOfDay = 0;
            this.mStartAtMinuteInDay = hourOfDay * 60 + minute;
        }

        if(timeKey == END_TIME_KEY) {
            if(hourOfDay*60 + minute < mStartAtMinuteInDay || minute == 0)
            if(hourOfDay == 0)
                hourOfDay = 24;
            if(hourOfDay == 24)
                minute = 0;
            this.mEndAtMinuteInDay = hourOfDay * 60 + minute;
        }
    }

    public void setUntilNextEvent(Event event){
        if(event == null)
            mEndAtMinuteInDay = (60*24)-1;
        else
            mEndAtMinuteInDay = event.getStartTime();
    }

    public String getDurationInFormat(Context context){
        String duration = "";

        int hours = (mEndAtMinuteInDay - mStartAtMinuteInDay)/60;
        int minutes = (mEndAtMinuteInDay - mStartAtMinuteInDay)%60;

        if(hours != 0)
            duration = context.getResources().getString(R.string.durationHours, hours);
        if(minutes != 0)
            duration += context.getResources().getString(R.string.durationMinutes, minutes);

        return duration;
    }

    public int getStartTime(){ return this.mStartAtMinuteInDay; }

    public void setStartTime(int startTime){
        this.mStartAtMinuteInDay = startTime;
    }

    public void setEndTime(int endTime){
        this.mEndAtMinuteInDay = endTime;
    }

    public int getEndTime(){ return this.mEndAtMinuteInDay; }

    public String getLabel(){
        return this.mLabel;
    }

    public void setLabel(String mLabel){
        this.mLabel = mLabel;
    }

    public String getComment(){
        return this.mComment;
    }

    public void setComment(String mComment){
        this.mComment = mComment;
    }

    public UUID getId(){
        return this.mId;
    }

    public Date getTime(){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, this.mStartAtMinuteInDay/60);
        calendar.set(Calendar.MINUTE, this.mStartAtMinuteInDay%60);

        return calendar.getTime();
    }

    public Day getParent(){ return this.mDayParent; }
}