package com.standards.libhikvision.activity.widget.slice;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;

import com.standards.libhikvision.R;
import com.standards.libhikvision.util.DisplayUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * @author CRAWLER
 */
public class ScalableTimebarView extends View {
    /**
     * Indicates how many pixels a tick corresponds to for 1 second at the current scale criterion
     */
    private float pixelsPerSecond = 0;

    /**
     * Timebar Action Listeners
     */
    private OnBarMoveListener mOnBarMoveListener;
    private OnBarScaledListener mOnBarScaledListener;

    /**
     * Screen width and height in pixel
     */
    private int screenWidth, screenHeight;

    /**
     * Paint to draw timebar body, ticks and RecordBar
     */
    Paint timebarPaint = new Paint();

    /**
     * Textpaint to draw time text corresponding to large ticks
     */
    TextPaint keyTickTextPaint = new TextPaint();

    /**
     * Margin between large tick and time text corresponding to it in dp
     */
    private final int TICK_TEXT_TO_TICK_MARGIN_IN_DP = 2;

    /**
     * Whole height of the ScalableTimebarView (including timebar, recordbar, time text, margins, and so on) in dp
     */
    private final int VIEW_HEIGHT_IN_DP = 80;

    /**
     * Height of recordbar in dp
     */
    private final int COLORED_RECORDBAR_HEIGHT_IN_DP = 40;

    /**
     * Time text size corresponding to large ticks(key ticks) in sp
     */
    private final int KEY_TICK_TEXT_SIZE_IN_SP = 18;

    /**
     * Height of Triangle
     */
    private final int BIG_TICK_HEIGHT_IN_DP = 10;

    /**
     * Line Length of Triangle
     */
    private final int IND_TRIANGLE_BOTTOM_LINE_IN_DP = 12;

    /**
     * Line Length of Triangle
     */
    private final int IND_TRIANGLE_HEIGHT_IN_DP = 8;

    /**
     * Line Length of Triangle
     */
    private final int IND_LINE_WIDTH_IN_DP = 1;

    /**
     * Height of small tick in dp
     */
    private final int SMALL_TICK_HEIGHT_IN_DP = 6;

    /**
     * Half the width of large tick in dp
     */
    private final int BIG_TICK_HALF_WIDTH_IN_DP = 2;

    /**
     * Half the width of small tick in dp
     */
    private final int SMALL_TICK_HALF_WIDTH_IN_DP = 1;

    /**
     * Margin between recordbar and tick text in dp
     */
    private final int COLORED_RECORDBAR_TO_TICK_TEXT_MARGIN_IN_DP = 5;

    /**
     * The white cursor bitmap width to show
     */
    private final int MIDDLE_CURSOR_BITMAP_SCALED_WIDTH_IN_DP = 13;

    /**
     * Convert various dimensions from dp/sp to px
     */
    private int BIG_TICK_HALF_WIDTH = DisplayUtils.dp2px(getContext(), BIG_TICK_HALF_WIDTH_IN_DP);
    private int BIG_TICK_HEIGHT = DisplayUtils.dp2px(getContext(), BIG_TICK_HEIGHT_IN_DP);
    private int IND_TRIANGLE_BOTTOM_LINE_LENGTH = DisplayUtils.dp2px(getContext(), IND_TRIANGLE_BOTTOM_LINE_IN_DP);
    private int IND_TRIANGLE_HEIGHT = DisplayUtils.dp2px(getContext(), IND_TRIANGLE_HEIGHT_IN_DP);
    private int IND_LINE_WIDTH = DisplayUtils.dp2px(getContext(), IND_LINE_WIDTH_IN_DP);
    private int SMALL_TICK_HALF_WIDTH = DisplayUtils.dp2px(getContext(), SMALL_TICK_HALF_WIDTH_IN_DP);
    private int SMALL_TICK_HEIGHT = DisplayUtils.dp2px(getContext(), SMALL_TICK_HEIGHT_IN_DP);
    private int KEY_TICK_TEXT_SIZE = DisplayUtils.dp2px(getContext(), KEY_TICK_TEXT_SIZE_IN_SP);
    private int TICK_TEXT_TO_TICK_MARGIN = DisplayUtils.dp2px(getContext(), TICK_TEXT_TO_TICK_MARGIN_IN_DP);
    private int VIEW_HEIGHT = DisplayUtils.dp2px(getContext(), VIEW_HEIGHT_IN_DP);
    private int COLORED_RECORDBAR_TO_TICK_TEXT_MARGIN = DisplayUtils.dp2px(getContext(), COLORED_RECORDBAR_TO_TICK_TEXT_MARGIN_IN_DP);
    private int COLORED_RECORDBAR_HEIGHT = DisplayUtils.dp2px(getContext(), COLORED_RECORDBAR_HEIGHT_IN_DP);

    /**
     * color
     */
    private int UN_DISPLAY_LINE_COLOR;
    private int DISPLAY_LINE_COLOR;
    private int INDICATOR_TRIANGLE_COLOR;
    private int INDICATOR_LINE_COLOR;
    private int TICK_LINE_COLOR;
    private int TICK_TEXT_COLOR;
    private int OUT_OF_RANGE_COLOR;
    private int RECOR_COLOR;
    /**
     * Set whether middle white cursor visible.
     */
    private boolean middleCursorVisible = true;

    /**
     * Map to cache 5 scale criterions
     */
    private Map<Integer, TimebarTickCriterion> timebarTickCriterionMap = new HashMap<>();

    /**
     * Timebar scale criterion count
     */
    private int timebarTickCriterionCount = 5;

    /**
     * Indicating which scale criterions the view is using to show ticks now , corresponding to keys of "timebarTickCriterionMap"
     */
    private int currentTimebarTickCriterionIndex;

    /**
     * Storage of record data. Each time segment means one visible segment to be drawn colored on recordbar.<br>
     * This is the original recordbar data set by developer.
     */
    private List<RecordDataExistTimeSegment> recordDataExistTimeClipsList = new ArrayList<>();

    /**
     * Data in recordDataExistTimeClipsList are divided into different groups and cached in this map.<br>
     * Same-day segments are grouped together in the same group.<br>
     * Keys of this map is milliseconds corresponding to 00:00 on the date.
     */
    private Map<Long, List<RecordDataExistTimeSegment>> recordDataExistTimeClipsListMap = new HashMap<>();

    /**
     * Detector to handle scale gesture
     */
    private ScaleGestureDetector scaleGestureDetector;

    /**
     * Indicate whether width of this view has been initialized
     */
    private boolean notInited = true;

    /**
     * Current time the white cursor indicating in millisecond
     */
    private long currentTimeInMillisecond;

    /**
     * Time corresponding to the most left tick (earlist time) in whole ScalableTimebarView (maybe outside the screen display range)
     */
    private long mostLeftTimeInMillisecond;

    /**
     * Time corresponding to the most right tick (latest time) in whole ScalableTimebarView (maybe outside the screen display range)
     */
    private long mostRightTimeInMillisecond;

    /**
     * Time corresponding to the most left tick (latest time) visible on screen now
     */
    private long screenLeftTimeInMillisecond;

    /**
     * Time corresponding to the most right tick (latest time) visible on screen now
     */
    private long screenRightTimeInMillisecond;

    /**
     * Flag indicating whether onMeasure() is called because of scale operation (scale operation can be performed by scale gesture or clicking on zoom buttons)
     */
    private boolean justScaledByPressingButton = false;

    /**
     * Seconds in one day
     */
    public final static int SECONDS_PER_DAY = 24 * 60 * 60;

    /**
     * The length of the entire timebar view, excluding the extra half-screen empty width at both ends.
     */
    private long WHOLE_TIMEBAR_TOTAL_SECONDS;

    public ScalableTimebarView(Context context) {
        super(context);
        init(null);
    }

    public ScalableTimebarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(attrs);
    }

    public ScalableTimebarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    public List<RecordDataExistTimeSegment> getRecordDataExistTimeClipsList() {
        return recordDataExistTimeClipsList;
    }

    public void setRecordDataExistTimeClipsList(List<RecordDataExistTimeSegment> recordDataExistTimeClipsList) {
        this.recordDataExistTimeClipsList = recordDataExistTimeClipsList;
        arrangeRecordDataExistTimeClipsIntoMap(recordDataExistTimeClipsList);
    }

    /**
     * Get most left (earlist) time in this whole view (including the invisible part outside the screen)
     */
    public long getMostLeftTimeInMillisecond() {
        return mostLeftTimeInMillisecond;
    }

    /**
     * Get most right (latest) time in this whole view (including the invisible part outside the screen)
     */
    public long getMostRightTimeInMillisecond() {
        return mostRightTimeInMillisecond;
    }

    public long getScreenLeftTimeInMillisecond() {
        screenLeftTimeInMillisecond = (long) (getCurrentTimeInMillisecond() - (long) ((float) screenWidth * 1000f / 2f / pixelsPerSecond));

        return screenLeftTimeInMillisecond;
    }

    public long getScreenRightTimeInMillisecond() {
        screenRightTimeInMillisecond = (long) (getCurrentTimeInMillisecond() + (long) (screenWidth * 1000f / 2f / pixelsPerSecond));
        return screenRightTimeInMillisecond;
    }

    private void arrangeRecordDataExistTimeClipsIntoMap(List<RecordDataExistTimeSegment> clipsList) {
        recordDataExistTimeClipsListMap = new HashMap<>();

        if (clipsList != null) {
            for (RecordDataExistTimeSegment clipItem : clipsList) {
                for (Long dateZeroOClockItem : clipItem.getCoverDateZeroOClockList()) {
                    List<RecordDataExistTimeSegment> list = null;
                    if ((list = recordDataExistTimeClipsListMap.get(dateZeroOClockItem)) == null) {
                        list = new ArrayList<>();
                        recordDataExistTimeClipsListMap.put(dateZeroOClockItem, list);
                    }
                    list.add(clipItem);
                }

            }
        }

        invalidate();
    }

    /**
     * Initializes start time, end time and current cursor time of ScalableTimebarView.
     * This method is called only when the view is initialized.
     * If you just need to move the current time cursor position, call setCurrentTimeInMillisecond() instead.
     * <p>
     *
     * @param mostLeftTime  Time corresponding to the most left tick (earlist time) in whole ScalableTimebarView (maybe outside the screen display range)
     * @param mostRightTime Time corresponding to the most right tick (latest time) in whole ScalableTimebarView (maybe outside the screen display range)
     * @param currentTime   Current time the white cursor indicating in millisecond
     */
    public void initTimebarLengthAndPosition(long mostLeftTime, long mostRightTime, long currentTime) {
        this.mostLeftTimeInMillisecond = mostLeftTime;
        this.mostRightTimeInMillisecond = mostRightTime;
        this.currentTimeInMillisecond = currentTime;
        WHOLE_TIMEBAR_TOTAL_SECONDS = (mostRightTime - mostLeftTime) / 1000;
        initTimebarTickCriterionMap();
    }

    public int getCurrentTimebarTickCriterionIndex() {
        return currentTimebarTickCriterionIndex;
    }

    public void setCurrentTimebarTickCriterionIndex(int currentTimebarTickCriterionIndex) {
        this.currentTimebarTickCriterionIndex = currentTimebarTickCriterionIndex;
    }

    private void init(AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ScalableTimebarView);

        BIG_TICK_HALF_WIDTH = (int) a.getDimension(R.styleable.ScalableTimebarView_bigTickWidth, BIG_TICK_HALF_WIDTH);
        BIG_TICK_HEIGHT = (int) a.getDimension(R.styleable.ScalableTimebarView_bigTickHeight, BIG_TICK_HEIGHT);
        IND_TRIANGLE_BOTTOM_LINE_LENGTH = (int) a.getDimension(R.styleable.ScalableTimebarView_bottomTriangleWidth, IND_TRIANGLE_BOTTOM_LINE_LENGTH);
        IND_TRIANGLE_HEIGHT = (int) a.getDimension(R.styleable.ScalableTimebarView_bottomTriangleHeight, IND_TRIANGLE_HEIGHT);
        IND_LINE_WIDTH = (int) a.getDimension(R.styleable.ScalableTimebarView_lineWidth, IND_LINE_WIDTH);

        SMALL_TICK_HALF_WIDTH = (int) a.getDimension(R.styleable.ScalableTimebarView_smallTickWidth, SMALL_TICK_HALF_WIDTH);
        SMALL_TICK_HEIGHT = (int) a.getDimension(R.styleable.ScalableTimebarView_smallTickHeight, SMALL_TICK_HEIGHT);
        KEY_TICK_TEXT_SIZE = (int) a.getDimension(R.styleable.ScalableTimebarView_tickTextSize, KEY_TICK_TEXT_SIZE);
        TICK_TEXT_TO_TICK_MARGIN = (int) a.getDimension(R.styleable.ScalableTimebarView_bigTickToTickText, TICK_TEXT_TO_TICK_MARGIN);
        COLORED_RECORDBAR_HEIGHT = (int) a.getDimension(R.styleable.ScalableTimebarView_colorBarHeight, COLORED_RECORDBAR_HEIGHT);
        VIEW_HEIGHT = BIG_TICK_HEIGHT + SMALL_TICK_HEIGHT + KEY_TICK_TEXT_SIZE + TICK_TEXT_TO_TICK_MARGIN + COLORED_RECORDBAR_HEIGHT + IND_TRIANGLE_HEIGHT;


        UN_DISPLAY_LINE_COLOR = a.getColor(R.styleable.ScalableTimebarView_unDisplayLineColor, getResources().getColor(R.color.colorLightBlue));
        DISPLAY_LINE_COLOR = a.getColor(R.styleable.ScalableTimebarView_unDisplayLineColor, getResources().getColor(R.color.colorMainBlue));
        INDICATOR_TRIANGLE_COLOR = a.getColor(R.styleable.ScalableTimebarView_indicateTriangleColor, getResources().getColor(R.color.colorMainBlue));
        INDICATOR_LINE_COLOR = a.getColor(R.styleable.ScalableTimebarView_indicateLineColor, getResources().getColor(R.color.color_gray));
        TICK_LINE_COLOR = a.getColor(R.styleable.ScalableTimebarView_tickLineColor, getResources().getColor(R.color.color_gray));
        TICK_TEXT_COLOR = a.getColor(R.styleable.ScalableTimebarView_tickTextColor, getResources().getColor(R.color.colorMainText));
        OUT_OF_RANGE_COLOR = a.getColor(R.styleable.ScalableTimebarView_outOfRangeColor, getResources().getColor(R.color.color_gray));
        RECOR_COLOR = a.getColor(R.styleable.ScalableTimebarView_recordColor, getResources().getColor(R.color.colorRecordGreen));
        a.recycle();


        screenWidth = DisplayUtils.getScreenWidthPixels(getContext());
        screenHeight = DisplayUtils.getScreenHeightPixels(getContext());

        // By default we use scale criterion[3] to show ticks.
        // By default, the current cursor is set to 3 hours before System.currentTimeMillis(),
        // and the mostRightTimeInMillisecond of the timebar is set System.currentTimeMillis()
        currentTimeInMillisecond = System.currentTimeMillis() - 3 * 3600 * 1000;
        mostRightTimeInMillisecond = currentTimeInMillisecond + 3 * 3600 * 1000;

        // By default the whole view width (including the parts outside the screen) is set to 7 days length
        mostLeftTimeInMillisecond = mostRightTimeInMillisecond - 2 * 24 * 3600 * 1000;
        WHOLE_TIMEBAR_TOTAL_SECONDS = (mostRightTimeInMillisecond - mostLeftTimeInMillisecond) / 1000;

        pixelsPerSecond = (float) (getWidth() - screenWidth) / (float) WHOLE_TIMEBAR_TOTAL_SECONDS;

        initTimebarTickCriterionMap();
        setCurrentTimebarTickCriterionIndex(3);
        keyTickTextPaint.setTextSize(KEY_TICK_TEXT_SIZE);
        keyTickTextPaint.setTypeface(Typeface.DEFAULT);
        keyTickTextPaint.setColor(TICK_TEXT_COLOR);

        ScaleGestureDetector.OnScaleGestureListener scaleGestureListener = new ScaleGestureDetector.OnScaleGestureListener() {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scaleTimebarByFactor(detector.getScaleFactor(), false);
                return true;
            }

            @Override
            public boolean onScaleBegin(ScaleGestureDetector detector) {
                return true;
            }

            @Override
            public void onScaleEnd(ScaleGestureDetector detector) {
                justScaledByPressingButton = true;
            }


        };
        scaleGestureDetector = new ScaleGestureDetector(getContext(), scaleGestureListener);

    }

    /**
     * Scale the view by factor
     *
     * @param scaleFactor        scale factor
     * @param scaleByClickButton true:scale view by click zoom buttons
     *                           false：scale vie by scale gesture
     */
    public void scaleTimebarByFactor(float scaleFactor, boolean scaleByClickButton) {
        int newWidth = (int) ((getWidth() - screenWidth) * scaleFactor);

        if (newWidth > timebarTickCriterionMap.get(0).getViewLength()) {
            setCurrentTimebarTickCriterionIndex(0);
            newWidth = timebarTickCriterionMap.get(0).getViewLength();

        } else if (newWidth < timebarTickCriterionMap.get(0).getViewLength()
                && newWidth >= getAverageWidthForTwoCriterion(0, 1)) {
            setCurrentTimebarTickCriterionIndex(0);

        } else if (newWidth < getAverageWidthForTwoCriterion(0, 1)
                && newWidth >= getAverageWidthForTwoCriterion(1, 2)) {
            setCurrentTimebarTickCriterionIndex(1);

        } else if (newWidth < getAverageWidthForTwoCriterion(1, 2)
                && newWidth >= getAverageWidthForTwoCriterion(2, 3)) {
            setCurrentTimebarTickCriterionIndex(2);

        } else if (newWidth < getAverageWidthForTwoCriterion(2, 3)
                && newWidth >= timebarTickCriterionMap.get(3).getViewLength()) {
            setCurrentTimebarTickCriterionIndex(3);
        } else if (newWidth < getAverageWidthForTwoCriterion(3, 4)
                && newWidth >= timebarTickCriterionMap.get(4).getViewLength()) {
            setCurrentTimebarTickCriterionIndex(4);
        } else if (newWidth < timebarTickCriterionMap.get(4).getViewLength()) {
            setCurrentTimebarTickCriterionIndex(4);
            newWidth = timebarTickCriterionMap.get(4).getViewLength();
        }


        if (scaleByClickButton) {
            justScaledByPressingButton = true;
        }

        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = newWidth;
        setLayoutParams(params);

    }

    private float getAverageWidthForTwoCriterion(int criterion1Index, int criterion2Index) {
        int width1 = timebarTickCriterionMap.get(criterion1Index).getViewLength();
        int width2 = timebarTickCriterionMap.get(criterion2Index).getViewLength();
        return (width1 + width2) / 2;
    }

    /**
     * <p>
     * According to the level of scaling, preset 5 scale criterions to show ticks on timebar, respectively, as follows:
     * <p>
     * [<b>Glossary</b>]<br/>
     * LoTSW: The Length of Time can be displayed throughout the Screen Width   <br/>
     * TILT: The Time Interval between two Large Ticks(Also called key ticks)  <br/>
     * TIST: The Time Interval between two Small Ticks  <br/>
     * <pre>
     * （0）LoTSW：10 minutes      TILT：1 minutes          TIST：6 seconds
     * （1）LoTSW：1 hours         TILT：10 minutes         TIST：1 minutes
     * （2）LoTSW：6 hours         TILT：1 hours            TIST：5 minutes
     * （3）LoTSW：36 hours        TILT：6 hours            TIST：30 minutes
     * （4）LoTSW：6 days          TILT：1 days             TIST：2 hours
     * </pre>
     */
    private void initTimebarTickCriterionMap() {
        TimebarTickCriterion t0 = new TimebarTickCriterion();
        t0.setTotalSecondsInOneScreen(10 * 60);
        t0.setKeyTickInSecond(1 * 60);
        t0.setMinTickInSecond(6);
        t0.setDataPattern("HH:mm");
        t0.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t0.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(0, t0);

        TimebarTickCriterion t1 = new TimebarTickCriterion();
        t1.setTotalSecondsInOneScreen(60 * 60);
        t1.setKeyTickInSecond(10 * 60);
        t1.setMinTickInSecond(60);
        t1.setDataPattern("HH:mm");
        t1.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t1.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(1, t1);

        TimebarTickCriterion t2 = new TimebarTickCriterion();
        t2.setTotalSecondsInOneScreen(6 * 60 * 60);
        t2.setKeyTickInSecond(60 * 60);
        t2.setMinTickInSecond(5 * 60);
        t2.setDataPattern("HH:mm");
        t2.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t2.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(2, t2);

        TimebarTickCriterion t3 = new TimebarTickCriterion();
        t3.setTotalSecondsInOneScreen(24 * 60 * 60);
        t3.setKeyTickInSecond(6 * 60 * 60);
        t3.setMinTickInSecond(30 * 60);
        t3.setDataPattern("HH:mm");
        t3.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t3.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(3, t3);


        TimebarTickCriterion t4 = new TimebarTickCriterion();
        t4.setTotalSecondsInOneScreen(6 * 24 * 60 * 60);
        t4.setKeyTickInSecond(24 * 60 * 60);
        t4.setMinTickInSecond(2 * 60 * 60);
        t4.setDataPattern("MM.dd");
        // t4.dataPattern = "MM.dd HH:mm:ss";
        t4.setViewLength((int) ((float) screenWidth * WHOLE_TIMEBAR_TOTAL_SECONDS / (float) t4.getTotalSecondsInOneScreen()));
        timebarTickCriterionMap.put(4, t4);

        timebarTickCriterionCount = timebarTickCriterionMap.size();
    }


    /**
     * Reset view width to default value
     */
    private void resetToStandardWidth() {
        setCurrentTimebarTickCriterionIndex(3);
        ViewGroup.LayoutParams params = getLayoutParams();
        params.width = timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getViewLength();
        setLayoutParams(params);
    }

    /**
     * Get current time indicating by white cursor bitmap
     */
    public long getCurrentTimeInMillisecond() {
        return currentTimeInMillisecond;
    }

    /**
     * Set current time cursor to specific time point.
     * If you are playing a video and use this ScalableTimebarView to indicate play progress,
     * then you should call this method every minute to update the showing current time.
     *
     * @param currentTimeInMillisecond current time you want to indicate by cursor
     */
    public void setCurrentTimeInMillisecond(long currentTimeInMillisecond) {
        this.currentTimeInMillisecond = currentTimeInMillisecond;
        invalidate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measureWidth(widthMeasureSpec), VIEW_HEIGHT);

        if (justScaledByPressingButton && mOnBarScaledListener != null) {
            justScaledByPressingButton = false;
            mOnBarScaledListener.onBarScaleFinish(getScreenLeftTimeInMillisecond(), getScreenRightTimeInMillisecond(), currentTimeInMillisecond);
        }
    }


    /**
     * Measure width of this view.
     * Left end and right end are both reserved for half the width of the screen
     *
     * @param widthMeasureSpec widthMeasureSpec
     * @return measured width
     */
    private int measureWidth(int widthMeasureSpec) {
        int measureMode = MeasureSpec.getMode(widthMeasureSpec);
        int measureSize = MeasureSpec.getSize(widthMeasureSpec);
        int result = getSuggestedMinimumWidth();
        switch (measureMode) {
            case MeasureSpec.AT_MOST:
            case MeasureSpec.EXACTLY:
                result = measureSize + screenWidth;
                pixelsPerSecond = measureSize / (float) WHOLE_TIMEBAR_TOTAL_SECONDS;
                if (mOnBarScaledListener != null) {
                    mOnBarScaledListener.onBarScaled(getScreenLeftTimeInMillisecond(), getScreenRightTimeInMillisecond(), currentTimeInMillisecond);
                }
                break;
            default:
                break;
        }

        return result;
    }

    /**
     * Convert time in millisecond to display string.
     * The display string style is controlled by current scale criterion.
     *
     * @param value time in millisecond need to convert
     * @return display string
     */
    private String getTimeStringFromLong(long value) {
        SimpleDateFormat timeFormat = new SimpleDateFormat(timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getDataPattern());
        return timeFormat.format(value);
    }

    /**
     * Set if you want middle cursor bitmap to be visible
     *
     * @param middleCursorVisible true:visible
     *                            false:invisible
     */
    public void setMiddleCursorVisible(boolean middleCursorVisible) {
        this.middleCursorVisible = middleCursorVisible;
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (notInited) {
            notInited = false;
            resetToStandardWidth();
            return;
        }
        pixelsPerSecond = (float) (getWidth() - screenWidth) / (float) WHOLE_TIMEBAR_TOTAL_SECONDS;

        //Get the number of milliseconds between the phone's local time zone and UTC
        Calendar cal = Calendar.getInstance();
        int zoneOffsetInSeconds = cal.get(Calendar.ZONE_OFFSET) / 1000;
        long forStartUTC = (long) (currentTimeInMillisecond / 1000 - screenWidth / pixelsPerSecond / 2 - timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond());
        long forEndUTC = (long) (currentTimeInMillisecond / 1000 + screenWidth / pixelsPerSecond / 2 + timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond());

        long forStartLocalTimezone = forStartUTC + zoneOffsetInSeconds;
        long forEndLocalTimezone = forEndUTC + zoneOffsetInSeconds;

        //Look for the first tick to show on screen
        long firstTickToSeeInSecondUTC = -1;
        for (long i = forStartLocalTimezone; i <= forEndLocalTimezone; i++) {
            if (i % timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond() == 0) {
                firstTickToSeeInSecondUTC = i - zoneOffsetInSeconds;
                break;
            }
        }

        /**
         * Draw timebar body , large ticks, small ticks, and time text corresponding to large ticks
         */
        int totalTickToDrawInOneScreen = (int) (screenWidth / pixelsPerSecond / timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond()) + 2;
        float keytextY = getHeight() - IND_TRIANGLE_HEIGHT - COLORED_RECORDBAR_HEIGHT - BIG_TICK_HEIGHT - TICK_TEXT_TO_TICK_MARGIN;
        for (int i = -20; i <= totalTickToDrawInOneScreen + 10; i++) {
            long drawTickTimeInSecondUTC = firstTickToSeeInSecondUTC + i * timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond();
            long drawTickTimeInSecondLocalTimezone = drawTickTimeInSecondUTC + zoneOffsetInSeconds;

            //关键刻度
            if (drawTickTimeInSecondLocalTimezone % timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getKeyTickInSecond() == 0) {
                //draw large ticks
                timebarPaint.setColor(TICK_LINE_COLOR);
                timebarPaint.setStyle(Paint.Style.FILL);
                float startX = pixelsPerSecond * (drawTickTimeInSecondUTC - mostLeftTimeInMillisecond / 1000) + screenWidth / 2f;
                RectF largeTickRect = new RectF(startX - BIG_TICK_HALF_WIDTH / 2, getHeight() - BIG_TICK_HEIGHT - IND_TRIANGLE_HEIGHT - COLORED_RECORDBAR_HEIGHT, (startX + BIG_TICK_HALF_WIDTH / 2), getHeight() - COLORED_RECORDBAR_HEIGHT - IND_TRIANGLE_HEIGHT);
                canvas.drawRect(largeTickRect, timebarPaint);

                //draw time text
                String keytext = getTimeStringFromLong(drawTickTimeInSecondUTC * 1000);
                float keyTextWidth = keyTickTextPaint.measureText(keytext);
                float keytextX = startX - keyTextWidth / 2;

                canvas.drawText(keytext,
                        keytextX,
                        keytextY,
                        keyTickTextPaint);
            } else if (drawTickTimeInSecondLocalTimezone % timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond() == 0) {
                //draw small ticks
                timebarPaint.setColor(TICK_LINE_COLOR);
                timebarPaint.setStyle(Paint.Style.FILL);
                float startX = pixelsPerSecond * (drawTickTimeInSecondUTC - mostLeftTimeInMillisecond / 1000) + screenWidth / 2f;
                RectF smallTickRect = new RectF(startX - SMALL_TICK_HALF_WIDTH / 2, getHeight() - SMALL_TICK_HEIGHT - IND_TRIANGLE_HEIGHT - COLORED_RECORDBAR_HEIGHT, (startX + SMALL_TICK_HALF_WIDTH / 2), getHeight() - IND_TRIANGLE_HEIGHT - COLORED_RECORDBAR_HEIGHT);
                canvas.drawRect(smallTickRect, timebarPaint);
            }
        }

        /**
         * Draw recordbar
         */
        //Draw grey background
        long startDrawTimeInSeconds = firstTickToSeeInSecondUTC + (-20) * timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond();//灰色背景从哪个时间点开始，单位是毫秒
        float startX = pixelsPerSecond * (startDrawTimeInSeconds - mostLeftTimeInMillisecond / 1000) + screenWidth / 2f;
        float endX = (startX + screenWidth + timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond() * 40 * pixelsPerSecond);
        float startY = getHeight() - IND_TRIANGLE_HEIGHT - COLORED_RECORDBAR_HEIGHT;
        float endY = getHeight() - IND_TRIANGLE_HEIGHT;


        RectF cloudRecordTimeClipsBarBackgroundRectF = new RectF(startX,
                startY,
                endX,
                endY);
        timebarPaint.setColor(DISPLAY_LINE_COLOR);
        timebarPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(cloudRecordTimeClipsBarBackgroundRectF, timebarPaint);

        //绘制不可选取灰色区域
        drawRange(canvas, 0, screenWidth / 2, startY, endY, OUT_OF_RANGE_COLOR);
//        绘制不可选取灰色区域
        drawRange(canvas, getWidth() - screenWidth / 2, getWidth(), startY, endY, OUT_OF_RANGE_COLOR);
        //浅蓝色未播放区域
        float unPlayStartX = (currentTimeInMillisecond - mostLeftTimeInMillisecond) / 1000 * pixelsPerSecond + screenWidth / 2;
        drawRange(canvas, unPlayStartX,
                getWidth() - screenWidth / 2, startY, endY, UN_DISPLAY_LINE_COLOR);

        if (recordDataExistTimeClipsList != null && recordDataExistTimeClipsList.size() > 0) {
            //绘制特殊时间片
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat zeroTimeFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            String startDrawTimeDateString = dateFormat.format(startDrawTimeInSeconds * 1000);
            String zeroTimeString = startDrawTimeDateString + " 00:00:00";

            long screenLastSecondToSee = (long) (startDrawTimeInSeconds + screenWidth / pixelsPerSecond + 30 * timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond()) * 1000L;

            Date startDate;
            try {

                //Find the date of most left (earlist) time on screen. Then get the list in cloudRecordExistTimeClipsListMap whose key is 00:00 of this date.
                //Get the first item in the list, using it as start condition to search which record segment should be the first to draw on recordbar.
                startDate = zeroTimeFormat.parse(zeroTimeString);
                List<RecordDataExistTimeSegment> startList = recordDataExistTimeClipsListMap.get(startDate.getTime());
                if (startList == null) {
                    int afterFindDays = 1;
                    long findTimeInMilliseconds = startDate.getTime();
                    long newFindStartMilliseconds = findTimeInMilliseconds;
                    while (startList == null && newFindStartMilliseconds < screenLastSecondToSee) {
                        newFindStartMilliseconds = findTimeInMilliseconds + (long) SECONDS_PER_DAY * 1000L * (long) afterFindDays;
                        startList = recordDataExistTimeClipsListMap.get(newFindStartMilliseconds);
                        afterFindDays++;
                    }
                }

                if (startList != null && startList.size() > 0) {
                    int thisDateFirstClipStartIndex = recordDataExistTimeClipsList.indexOf(startList.get(0));

                    //The first record segment should be drawn is found. Now let's start to draw colorful record segments!
                    long endDrawTimeInSeconds = (long) (startDrawTimeInSeconds
                            + screenWidth / pixelsPerSecond
                            + timebarTickCriterionMap.get(currentTimebarTickCriterionIndex).getMinTickInSecond() * 30);

                    timebarPaint.setColor(RECOR_COLOR);
                    timebarPaint.setStyle(Paint.Style.FILL);

                    for (int i = thisDateFirstClipStartIndex; i < recordDataExistTimeClipsList.size(); i++) {
                        float leftX = pixelsPerSecond * (recordDataExistTimeClipsList.get(i).getStartTimeInMillisecond() - mostLeftTimeInMillisecond) / 1000 + screenWidth / 2f;
                        float rightX = pixelsPerSecond * (recordDataExistTimeClipsList.get(i).getEndTimeInMillisecond() - mostLeftTimeInMillisecond) / 1000 + screenWidth / 2f;
                        RectF rectF = new RectF(leftX,
                                startY,
                                rightX,
                                endY);
                        canvas.drawRect(rectF, timebarPaint);
                        if (recordDataExistTimeClipsList.get(i).getEndTimeInMillisecond() >= endDrawTimeInSeconds * 1000) {
                            break;
                        }
                    }
                }

            } catch (ParseException e) {
                e.printStackTrace();
            }
        }

        /**
         * Draw white cursor bitmap indicating current time in the middle of screen
         */
        if (middleCursorVisible) {
            timebarPaint.setColor(INDICATOR_TRIANGLE_COLOR);
            timebarPaint.setStyle(Paint.Style.FILL);
            Path path = new Path();
            path.moveTo((currentTimeInMillisecond / 1000L - mostLeftTimeInMillisecond / 1000L) * pixelsPerSecond + screenWidth / 2f - IND_TRIANGLE_BOTTOM_LINE_LENGTH / 2, getHeight());// 此点为多边形的起点
            path.lineTo((currentTimeInMillisecond / 1000L - mostLeftTimeInMillisecond / 1000L) * pixelsPerSecond + screenWidth / 2f + IND_TRIANGLE_BOTTOM_LINE_LENGTH / 2, getHeight());
            path.lineTo((currentTimeInMillisecond / 1000L - mostLeftTimeInMillisecond / 1000L) * pixelsPerSecond + screenWidth / 2f, getHeight() - IND_TRIANGLE_HEIGHT);
            path.close(); // 使这些点构成封闭的多边形
            canvas.drawPath(path, timebarPaint);
            timebarPaint.setStrokeWidth(IND_LINE_WIDTH);
            timebarPaint.setColor(INDICATOR_LINE_COLOR);
            canvas.drawLine((currentTimeInMillisecond / 1000L - mostLeftTimeInMillisecond / 1000L) * pixelsPerSecond + screenWidth / 2f,
                    getHeight() - IND_TRIANGLE_HEIGHT,
                    (currentTimeInMillisecond / 1000L - mostLeftTimeInMillisecond / 1000L) * pixelsPerSecond + screenWidth / 2f,
                    getHeight() - IND_TRIANGLE_HEIGHT - COLORED_RECORDBAR_HEIGHT, timebarPaint);
        }


        /**
         * According to the currentTimeInMillisecond variable, layout the view in appropriate location
         */
        layout((int) (0 - (currentTimeInMillisecond - mostLeftTimeInMillisecond) / 1000 * pixelsPerSecond),
                getTop(),
                getWidth() - (int) ((currentTimeInMillisecond - mostLeftTimeInMillisecond) / 1000 * pixelsPerSecond),
                getTop() + getHeight());
    }


    private void drawRange(Canvas canvas, float startX, float endX, float startY, float endY, int color) {
        RectF cloudRecordTimeClipsBarBackgroundRectF = new RectF(startX,
                startY,
                endX,
                endY);
        timebarPaint.setColor(color);
        timebarPaint.setStyle(Paint.Style.FILL);
        canvas.drawRect(cloudRecordTimeClipsBarBackgroundRectF, timebarPaint);
    }


    /**
     * Coordinates when draging timebar
     */
    float lastX, lastY;

    /**
     * Current operation on this view
     */
    private int mode = NONE;
    private static final int NONE = 0;
    private static final int DRAG = 1;
    private static final int ZOOM = 2;


    @Override
    public boolean onTouchEvent(MotionEvent event) {

        scaleGestureDetector.onTouchEvent(event);

        if (scaleGestureDetector.isInProgress()) {
            return true;
        }

        switch (event.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_DOWN:
                mode = DRAG;
                lastX = event.getRawX();
                lastY = event.getRawY();
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                mode = ZOOM;
                break;
            case MotionEvent.ACTION_MOVE:
                if (mode == ZOOM) {

                } else if (mode == DRAG) {
                    int dx = (int) (event.getRawX() - lastX);
                    int dy = (int) (event.getRawY() - lastY);

                    if (dx == 0) {
                        return true;
                    }

                    int top = getTop();
                    int left = getLeft() + dx;
                    int right = left + getWidth();

                    if (left >= 0) {
                        left = 0;
                        right = getWidth();
                    }

                    if (right < screenWidth) {
                        right = screenWidth;
                        left = right - getWidth();
                    }

                    layout(left, top, right, top + getHeight());
                    invalidate();

                    lastX = event.getRawX();
                    lastY = event.getRawY();

                    //pixels to move
                    int deltaX = (0 - left);
                    int timeBarLength = getWidth() - screenWidth;
                    currentTimeInMillisecond = mostLeftTimeInMillisecond + deltaX * WHOLE_TIMEBAR_TOTAL_SECONDS * 1000 / timeBarLength;

                    if (mOnBarMoveListener != null) {
                        mOnBarMoveListener.onBarMove(getScreenLeftTimeInMillisecond(), getScreenRightTimeInMillisecond(), currentTimeInMillisecond);
                    }

                }
                break;
            case MotionEvent.ACTION_UP:
                if (mode == DRAG) {
                    int deltaX_up = (0 - getLeft());
                    int timeBarLength_up = getWidth() - screenWidth;
                    currentTimeInMillisecond = mostLeftTimeInMillisecond + deltaX_up * WHOLE_TIMEBAR_TOTAL_SECONDS * 1000 / timeBarLength_up;
                    if (mOnBarMoveListener != null) {
                        mOnBarMoveListener.OnBarMoveFinish(getScreenLeftTimeInMillisecond(), getScreenRightTimeInMillisecond(), currentTimeInMillisecond);
                    }
                }
                mode = NONE;
                break;
            default:
                break;
        }
        return true;
    }


    /**
     * Listener while moving and finish move
     */
    public interface OnBarMoveListener {
        /**
         * Callback when the timebar is being moved horizontal(slide)
         *
         * @param screenLeftTime  most left time on screen (ony visible part of this view)
         * @param screenRightTime most right time on screen (only visible part of this view)
         * @param currentTime     current time
         */
        void onBarMove(long screenLeftTime, long screenRightTime, long currentTime);

        /**
         * Callback when the timebar finish moving
         *
         * @param screenLeftTime  most left time on screen (only visible part of this view)
         * @param screenRightTime most right time on screen (only visible part of this view)
         * @param currentTime     current time
         */
        void OnBarMoveFinish(long screenLeftTime, long screenRightTime, long currentTime);
    }

    public void setOnBarMoveListener(OnBarMoveListener onBarMoveListener) {
        mOnBarMoveListener = onBarMoveListener;
    }

    /**
     * Listener while scale and finish scale
     */
    public interface OnBarScaledListener {
        /**
         * Called when timebar is being scaled
         *
         * @param screenLeftTime  most left time on screen (only visible part of this view)
         * @param screenRightTime most right time on screen (only visible part of this view)
         * @param currentTime     current time
         */
        void onBarScaled(long screenLeftTime, long screenRightTime, long currentTime);

        /**
         * Called when finish scaling
         *
         * @param screenLeftTime  most left time on screen (only visible part of this view)
         * @param screenRightTime most right time on screen (only visible part of this view)
         * @param currentTime     current time
         */
        void onBarScaleFinish(long screenLeftTime, long screenRightTime, long currentTime);
    }

    public void setOnBarScaledListener(OnBarScaledListener onBarScaledListener) {
        mOnBarScaledListener = onBarScaledListener;
    }


}

