package com.makaan.timeline;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.makaan.R;
import com.makaan.network.MakaanNetworkClient;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Timeline extends LinearLayout implements TimelineView.OnTimeLineChangeListener {
    private final Context context;
    private RecyclerView recyclerView;
    private TextView startDateTv, endDateTv;
    private TimelineView timelineView;
    private LinearLayoutManager mLayoutManager;
    private TimelineleAdapter mAdapter;
    private int firstVisibleItem, visibleItemCount, totalItemCount, currentItemVisible;
    private TextView currentDateTv;
    private List<TimelineView.TimelineDataItem> list;
    private int numberOfScrolls;
    private OnTimelineScrollListener timelineScrollListener;

    public Timeline(Context context) {
        this(context, null);
    }

    public Timeline(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Timeline(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }


    public void bindView(List<TimelineView.TimelineDataItem> list, Timeline container, OnTimelineScrollListener timelineScrollListener) {
        if (list != null && list.size() > 0) {
            Collections.sort(list);
            this.timelineScrollListener = timelineScrollListener;
            this.list = list;
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(context.LAYOUT_INFLATER_SERVICE);
            View timeline = inflater.inflate(R.layout.timeline, null);
            container.addView(timeline);
            initTimelineViews(timeline);
            setDates(list);
            timelineView.bindData(list, this);
            initRecycler(list);
        }
    }

    private void setDates(List<TimelineView.TimelineDataItem> list) {
        currentDateTv.setText(DateTimeFormat.forPattern("d MMMM, YYYY").print(new DateTime(list.get(0).epochTime)).toLowerCase());
        if (getNumberOfDistinctEpochItems() != 1) {
            startDateTv.setText(DateTimeFormat.forPattern("MMMM, YYYY").print(new DateTime(list.get(0).epochTime)).toLowerCase());
            endDateTv.setText(DateTimeFormat.forPattern("MMMM, YYYY").print(list.get(list.size() - 1).epochTime).toString().toLowerCase());
        }
    }

    private void initTimelineViews(View timeline) {
        recyclerView = (RecyclerView) timeline.findViewById(R.id.rv_timeline);
        startDateTv = (TextView) timeline.findViewById(R.id.tv_timeline_start_date);
        endDateTv = (TextView) timeline.findViewById(R.id.tv_timeline_end_date);
        currentDateTv = (TextView) timeline.findViewById(R.id.tv_timeline_current_date);
        timelineView = (TimelineView) timeline.findViewById(R.id.timeline);
    }

    private void initRecycler(List<TimelineView.TimelineDataItem> list) {
        recyclerView.setHasFixedSize(true);
        mLayoutManager = new LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false);
        recyclerView.setLayoutManager(mLayoutManager);
        recyclerView.addOnScrollListener(new RecyclerScrollListener());
        mAdapter = new TimelineleAdapter(context, list);
        recyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onChange(int position) {
        mLayoutManager.scrollToPositionWithOffset(position, 20);
        currentDateTv.setText(DateTimeFormat.forPattern("d MMMM, YYYY").print(new DateTime(list.get(position).epochTime)).toLowerCase());
    }

    public int dpToPx(int dp) {
        DisplayMetrics displayMetrics = getContext().getResources().getDisplayMetrics();
        int px = Math.round(dp * (displayMetrics.xdpi / DisplayMetrics.DENSITY_DEFAULT));
        return px;
    }

    private int getNumberOfDistinctEpochItems() {
        Set<TimelineView.TimelineDataItem> timelineDataItemSet = new HashSet<>();
        for (TimelineView.TimelineDataItem item : list) {
            timelineDataItemSet.add(item);
        }
        return timelineDataItemSet.size();
    }

    private class TimelineleAdapter extends RecyclerView.Adapter<TimelineleAdapter.ViewHolder> {
        private final Context context;
        private List<TimelineView.TimelineDataItem> list;
        private ImageLoader imageLoader;


        public class ViewHolder extends RecyclerView.ViewHolder {
            // each data item is just a string in this case
            public NetworkImageView timelineIv;

            public ViewHolder(View v) {
                super(v);
                timelineIv = (NetworkImageView) v.findViewById(R.id.iv_timeline);
            }
        }

        public TimelineleAdapter(Context context, List<TimelineView.TimelineDataItem> list) {
            this.list = list;
            this.context = context;
        }

        @Override
        public TimelineleAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                               int viewType) {
            View v = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.row_timeline, parent, false);

            makeViewHolderFitScreenWidth(v);
            ViewHolder vh = new ViewHolder(v);
            return vh;
        }

        private void makeViewHolderFitScreenWidth(View v) {
            WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
            Point point = new Point();
            windowManager.getDefaultDisplay().getSize(point);
            int width = point.x - dpToPx(40);
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(width, RecyclerView.LayoutParams.MATCH_PARENT);
            layoutParams.setMargins(dpToPx(20), 0, dpToPx(20), 0);
            v.setLayoutParams(layoutParams);
        }

        private int pixelToDp(Context context, int px) {
            DisplayMetrics metrics = context.getResources().getDisplayMetrics();
            int dp = (int) (px / (metrics.densityDpi / 160f));
            return dp;
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            TimelineView.TimelineDataItem item = list.get(position);
            imageLoader = MakaanNetworkClient.getInstance().getImageLoader();
            imageLoader.get(item.url, ImageLoader.getImageListener(holder.timelineIv, R.drawable.placeholder_timeline, android.R.drawable.ic_dialog_alert));
            holder.timelineIv.setImageUrl(item.url, imageLoader);

            //Picasso.with(context).load(item.url).placeholder(R.drawable.placeholder_timeline).into(holder.timelineIv);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

    }

    public class RecyclerScrollListener extends RecyclerView.OnScrollListener {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (dx != 0) {
                visibleItemCount = mLayoutManager.getChildCount();
                totalItemCount = mLayoutManager.getItemCount();
                firstVisibleItem = mLayoutManager.findFirstVisibleItemPosition();
                if (firstVisibleItem != -1 && currentItemVisible != firstVisibleItem) {
                    numberOfScrolls++;
//                    Log.e("scrolled", " positions: visibleItemCount " + visibleItemCount + ", totalItemCount" + totalItemCount + ", firstVisibleItemCount " + firstVisibleItem);
                    currentItemVisible = firstVisibleItem;
                    timelineView.setNewPosition(firstVisibleItem);
                    timelineScrollListener.onTimelineScroll(firstVisibleItem, numberOfScrolls, totalItemCount);
                    if (list != null)
                        currentDateTv.setText(DateTimeFormat.forPattern("d MMMM, YYYY").print(new DateTime(list.get(firstVisibleItem).epochTime)).toLowerCase());
                }
            }
        }
    }

    public interface OnTimelineScrollListener {
        void onTimelineScroll(int position, int totalNumberOfScrolls, int totalTimelineImages);
    }
}

