package com.example.workoutpixel;

public class Widget {
    private int mAppWidgetId;
    private String mTitle;
    private long mLastWorkout;
    private int mIntervalBlue;
    private int mIntervalRed;
    private boolean mShowDate;
    private boolean mShowTime;
    private String mStatus;

    public Widget(int appWidgetId, String title, long lastWorkout, int intervalBlue, int intervalRed, boolean showDate, boolean showTime, String status) {
        mAppWidgetId = appWidgetId;
        mTitle = title;
        mLastWorkout = lastWorkout;
        mIntervalBlue = intervalBlue;
        mIntervalRed = intervalRed;
        mShowDate = showDate;
        mShowTime = showTime;
        mStatus = status;
    }

    public int getAppWidgetId() {
        return mAppWidgetId;
    }
    public String getTitle() { return mTitle; }
    public long getLastWorkout() { return mLastWorkout; }
    public int getIntervalBlue() { return mIntervalBlue; }
    public int getIntervalRed() { return mIntervalRed; }
    public boolean getShowDate() { return mShowDate; }
    public boolean getShowTime() { return mShowTime; }
    public String getStatus() { return mStatus; }

    public void setAppWidgetId(int appWidgetId) { mAppWidgetId = appWidgetId; }
    public void setTitle(String title) { mTitle = title; }
    public void setLastWorkout(long lastWorkout) { mLastWorkout = lastWorkout; }
    public void setIntervalBlue(int intervalBlue) { mIntervalBlue = intervalBlue; }
    public void setIntervalRed(int intervalRed) { mIntervalRed = intervalRed; }
    public void setShowDateAndTime(boolean showDateAndTime) { mShowDate = showDateAndTime; }
    public void setStatus(String status) { mStatus = status; }
}