package com.makaan.fragment.project;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.analytics.MakaanEventPayload;
import com.makaan.analytics.MakaanTrackerConstants;
import com.makaan.event.image.ImagesGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.image.Image;
import com.makaan.service.ImageService;
import com.makaan.service.MakaanServiceFactory;
import com.segment.analytics.Properties;
import com.squareup.otto.Subscribe;
import com.tusharchoudhary.timeline.Timeline;
import com.tusharchoudhary.timeline.TimelineView;

import java.util.ArrayList;
import java.util.List;

import butterknife.Bind;

/**
 * Created by tusharchaudhary on 1/27/16.
 */
public class ConstructionTimelineFragment extends MakaanBaseFragment implements Timeline.OnTimelineScrollListener {
    @Bind(R.id.header_text)
    TextView titleTv;
    @Bind(R.id.timeline_main_view)
    Timeline timeline;
    @Bind(R.id.timeline_container)
    LinearLayout container;
    private int totalImagesSeen = 0;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_project_construction_timeline;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        String title = null;
        Long projectId = null;
        if(getArguments() != null) {
            title = getArguments().getString("title");
            projectId = getArguments().getLong("projectId");

            titleTv.setText(title);
        }
        ((ImageService) MakaanServiceFactory.getInstance().getService(ImageService.class)).getProjectTimelineImages(projectId);
        return view;
    }

    @Subscribe
    public void onResult(ImagesGetEvent imagesGetEvent) {
        if (null == imagesGetEvent || null != imagesGetEvent.error) {
            //TODO handle error
            return;
        }
        List<TimelineView.TimelineDataItem> list = new ArrayList<>();
        for (Image image : imagesGetEvent.images)
            list.add(new TimelineView.TimelineDataItem(image.createdAt, image.absolutePath));//TODO: replace createdAt with imageTakenAt when ever it is available
        if (imagesGetEvent.images.size() > 0)
            timeline.bindView(list, timeline, this);
        else {
            container.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Bundle bundle = getArguments();
        Properties properties = MakaanEventPayload.beginBatch();
        properties.put(MakaanEventPayload.CATEGORY, MakaanTrackerConstants.Category.buyerProject);
        properties.put(MakaanEventPayload.LABEL, bundle.get("projectId") + "_" + totalImagesSeen);
        MakaanEventPayload.endBatch(getActivity(), MakaanTrackerConstants.Action.clickProjectConstructionImages);
    }

    @Override
    public void onTimelineScroll(int position, int totalNumberOfScrolls, int totalTimelineImages) {
        totalImagesSeen = totalNumberOfScrolls;
    }
}
