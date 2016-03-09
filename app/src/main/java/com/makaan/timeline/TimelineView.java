package com.makaan.timeline;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

import com.makaan.R;

import net.danlew.android.joda.JodaTimeAndroid;

import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.Months;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TimelineView extends View{

    private Paint myPaint;
    private GestureDetector mDetector = new GestureDetector(this.getContext(), new mListener());
    private  Drawable timelineSelected;
    private final Drawable timelineFirstAndLast;
    private final Drawable timelineUnSelected;
    private List<TimelineDataItem> items;
    private int numberOfMonths;
    private List<Rect> itemBoundsList;
    private int interval = 1;
    private int currentItemPos = 0;
    private OnTimeLineChangeListener onTimeLineChangeListener;
    private List<TimelineDataItem> originalList;
    private DateTime firstDateTime;
    private Rect first;
    private Rect second;
    private boolean isOnlyOneEpochTimeInList;

    public TimelineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        JodaTimeAndroid.init(context);
        timelineFirstAndLast = ContextCompat.getDrawable(context, R.drawable.icon_timeline_first_and_last);
        timelineSelected = ContextCompat.getDrawable(context, R.drawable.icon_timeline_selected);
        timelineUnSelected = ContextCompat.getDrawable(context, R.drawable.icon_timeline_unselected);
        setUpPaint();
    }

    public static int dpToPixel(Context context, float dp) {
        DisplayMetrics metrics = context.getResources().getDisplayMetrics();
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dp, metrics);
    }

    private void setUpRectangles(int width) {
        if(!isOnlyOneEpochTimeInList){
            first = new Rect(50 - dpToPixel(getContext(),8), 50 - dpToPixel(getContext(),8), 50 + dpToPixel(getContext(),8), 50 + dpToPixel(getContext(),8));
            second = new Rect(width - (50 + dpToPixel(getContext(),8)), 50 - dpToPixel(getContext(),8), width - (50 - dpToPixel(getContext(),8)), 50 + dpToPixel(getContext(),8));
        }
    }

    private int getNumberOfDistinctEpochItems(){
        Set<TimelineDataItem> timelineDataItemSet = new HashSet<>();
        for(TimelineDataItem item :originalList){
            timelineDataItemSet.add(item);
        }
        return timelineDataItemSet.size();
    }

    private void setUpPaint() {
        myPaint = new Paint();
        myPaint.setColor(Color.LTGRAY);
        myPaint.setAntiAlias(true);
        myPaint.setStrokeWidth(3);
    }

    public void bindData(List<TimelineDataItem> items, OnTimeLineChangeListener onTimeLineChangeListener) {
        this.originalList = items;
        getOnlyMonthsList(originalList);
        this.onTimeLineChangeListener = onTimeLineChangeListener;
        numberOfMonths = calculateNumberOfMonthsBetweenFirstAndLastData(items.get(0), items.get(items.size() - 1));
        invalidate();
    }

    private void getOnlyMonthsList(List<TimelineDataItem> items) {
        LocalDate lastKnownDate = new LocalDate(items.get(0).epochTime);
        this.items = new ArrayList<>();
        this.items.add(new TimelineDataItem(lastKnownDate.toDate().getTime()));
        for(int i = 1; i< items.size(); i++){
            int monthDiff = getMonthsDifference(lastKnownDate,new LocalDate(items.get(items.size()-1).epochTime));
            if(Math.abs(monthDiff) >0){
                this.items.add(new TimelineDataItem(items.get(i).epochTime));
                lastKnownDate = new LocalDate(items.get(i).epochTime);
            }else {
                lastKnownDate = new LocalDate(items.get(i).epochTime);
            }
        }
    }

    private int calculateNumberOfMonthsBetweenFirstAndLastData(TimelineDataItem timelineDataItem, TimelineDataItem timelineDataItem1) {
        return getMonthsDifference(new LocalDate(timelineDataItem.epochTime),new LocalDate(timelineDataItem1.epochTime));
    }

    public  final int getMonthsDifference(LocalDate date1, LocalDate date2) {
        LocalDate localDate1 = new LocalDate(date1.withDayOfMonth(1));
        LocalDate localDate2 = new LocalDate(date2.withDayOfMonth(1));
        return Months.monthsBetween(localDate1,localDate2).getMonths();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(numberOfMonths!=0){
            drawView(canvas);
        }
    }

    public void setIsOnlyOneEpochTimeInList() {
        this.isOnlyOneEpochTimeInList = getNumberOfDistinctEpochItems() == 1;
    }

    private void drawView(Canvas canvas) {
        itemBoundsList = new ArrayList<>();
        int width = canvas.getWidth();
        setIsOnlyOneEpochTimeInList();
        setUpRectangles(width);
        drawLineBetweenFirstAndLast(canvas, width);
        drawFirstItem(canvas);
        setupIntervalAndFirstDate(width);
        for(int i = 1;i < items.size()-1; i++)
            drawCurrentItem(canvas, i);
        drawLastItem(canvas);
    }

    private void drawCurrentItem(Canvas canvas, int i) {
        int positionToDraw = Months.monthsBetween(firstDateTime, new DateTime(items.get(i).epochTime)).getMonths() * interval;
        Rect current = new Rect(positionToDraw, 50 - dpToPixel(getContext(),8), positionToDraw + dpToPixel(getContext(),16), 50 + dpToPixel(getContext(),8));
        if(i == currentItemPos) {
            timelineSelected.setBounds(current);
            timelineSelected.draw(canvas);
        }else{
            timelineUnSelected.setBounds(current);
            timelineUnSelected.draw(canvas);
        }
        itemBoundsList.add(current);
    }

    private void setupIntervalAndFirstDate(int width) {
        interval = width / numberOfMonths;
        firstDateTime = new DateTime(items.get(0).epochTime);
    }

    private void drawLineBetweenFirstAndLast(Canvas canvas, int width) {
        if(!isOnlyOneEpochTimeInList) {
            canvas.drawLine(50, 50, width - 50, 51, myPaint);
        }
    }

    private void drawLastItem(Canvas canvas) {
        if(!isOnlyOneEpochTimeInList) {
            if (currentItemPos == items.size() - 1) {
                timelineSelected.setBounds(second);
                timelineSelected.draw(canvas);
            } else {
                timelineFirstAndLast.setBounds(second);
                timelineFirstAndLast.draw(canvas);
            }
            itemBoundsList.add(second);
        }
    }

    private void drawFirstItem(Canvas canvas) {
        if(currentItemPos == 0) {
            timelineSelected.setBounds(first);
            timelineSelected.draw(canvas);
        }else{
            timelineFirstAndLast.setBounds(first);
            timelineFirstAndLast.draw(canvas);
        }
        itemBoundsList.add(first);
    }

    public void setNewPosition(int position){
        if(position > -1 && position < originalList.size()) {
            DateTime date = new DateTime(originalList.get(position).epochTime);
            currentItemPos = getPositionInList(date);
            invalidate();
        }
    }

    private int getPositionInList(DateTime date) {
        TimelineDataItem timelineDataItem = new TimelineDataItem(date.toDate().getTime());
        for(int i = 0; i < items.size(); i++){
            TimelineDataItem timelineDataItemInList = new TimelineDataItem(items.get(i).epochTime);
            if(timelineDataItem.equals(timelineDataItemInList))
                return i;
        }
        Log.e("timeline","returning -1 for "+date.toString());
        return -1;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        boolean result = mDetector.onTouchEvent(event);
        if (!result) {
            if (event.getAction() == MotionEvent.ACTION_UP) {
                try {
                    Boolean x = isTouchInsideAnyOfTheRectangles(event);
                    if (x != null) return x;
                }catch (Exception e){}
            }
        }
        return result;
    }

    @Nullable
    private Boolean isTouchInsideAnyOfTheRectangles(MotionEvent event) {
        int x = (int) event.getX(), y = (int) event.getY();
        for (int i = 0;i < itemBoundsList.size(); i ++) {
            Rect rectangle = itemBoundsList.get(i);
            if (x > rectangle.left-30 && x < rectangle.right+30 && y > rectangle.top-30 && y < rectangle.bottom+30) {
                if(currentItemPos!=i) {
                    try {
                        onTimeLineChangeListener.onChange(getPositionFromOriginalList(i));
                        currentItemPos = i;
                        invalidate();
                        return true;
                    }catch (Exception e){
                        Log.e("Timeline", ""+e);
                        return false;
                    }
                }
            }
        }
        return null;
    }

    private int getPositionFromOriginalList(int positionInMonthsList) throws IndexOutOfBoundsException{
        long epoch = items.get(positionInMonthsList).epochTime;
        for (int i = 0; i < originalList.size(); i++) {
            if (epoch == originalList.get(i).epochTime)
                return i;
        }
        return 0;
    }

    public static class TimelineDataItem implements Comparable<TimelineDataItem>{
        public Long epochTime;
        public String url;
        private TimelineDataItem dataItem;
        public TimelineDataItem(Long epochTime){
            this.epochTime = epochTime;
        }
        public TimelineDataItem(Long epochTime, String url){
            this.epochTime = epochTime;
            this.url = url;
        }

        @Override
        public String toString() {
            return new DateTime(epochTime).toString();
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            TimelineDataItem that = (TimelineDataItem) o;

            DateTime lhsTime = new DateTime(this.epochTime);
            DateTime rhsTime = new DateTime(that.epochTime);

            if(lhsTime.getYear() == rhsTime.getYear()) {
                return lhsTime.getMonthOfYear() == rhsTime.getMonthOfYear();
            }else{
                return false;
            }
        }

        @Override
        public int hashCode() {
            DateTime lhsTime = new DateTime(this.epochTime);
            return lhsTime.getMonthOfYear();
        }


        @Override
        public int compareTo(TimelineDataItem rhs) {
            DateTime lhsTime = new DateTime(this.epochTime);
            DateTime rhsTime = new DateTime(rhs.epochTime);
            //compare years
            if(lhsTime.getYear()< rhsTime.getYear())
                return 1;
            if(lhsTime.getYear()> rhsTime.getYear())
                return -1;
            //same years, compare months
            if (lhsTime.getMonthOfYear()  < rhsTime.getMonthOfYear() )
                return 1;
            if (lhsTime.getMonthOfYear()  > rhsTime.getMonthOfYear())
                return -1;
            return 0;
        }
    }

    private class mListener extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }

    public interface OnTimeLineChangeListener{
        void onChange(int position);
    }
}
