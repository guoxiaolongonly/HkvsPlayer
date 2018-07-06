package com.standards.libhikvision.activity.widget.slice;


/**
 * @author CRAWLER
 */
public class TimebarTickCriterion {
    /**
     * Whole timebar length (Not including two extra empty part on left and right ends)
     */
    private int viewLength;

    /**
     * How many seconds can be seen on one screen
     */
    private int totalSecondsInOneScreen;

    /**
     * Time interval between two large ticks (Also known as key ticks)
     */
    private int keyTickInSecond;

    /**
     * Time interval between two small ticks
     */
    private int minTickInSecond;

    /**
     * How to format time string
     */
    private String dataPattern;

    public int getViewLength() {
        return viewLength;
    }

    public void setViewLength(int viewLength) {
        this.viewLength = viewLength;
    }

    public int getTotalSecondsInOneScreen() {
        return totalSecondsInOneScreen;
    }

    public void setTotalSecondsInOneScreen(int totalSecondsInOneScreen) {
        this.totalSecondsInOneScreen = totalSecondsInOneScreen;
    }

    public int getKeyTickInSecond() {
        return keyTickInSecond;
    }

    public void setKeyTickInSecond(int keyTickInSecond) {
        this.keyTickInSecond = keyTickInSecond;
    }

    public int getMinTickInSecond() {
        return minTickInSecond;
    }

    public void setMinTickInSecond(int minTickInSecond) {
        this.minTickInSecond = minTickInSecond;
    }

    public String getDataPattern() {
        return dataPattern;
    }

    public void setDataPattern(String dataPattern) {
        this.dataPattern = dataPattern;
    }
}
