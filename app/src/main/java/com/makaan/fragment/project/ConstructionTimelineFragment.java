package com.makaan.fragment.project;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.makaan.R;
import com.makaan.constants.ImageConstants;
import com.makaan.event.image.ImagesGetEvent;
import com.makaan.fragment.MakaanBaseFragment;
import com.makaan.response.image.Image;
import com.makaan.service.ImageService;
import com.makaan.service.MakaanServiceFactory;
import com.squareup.otto.Subscribe;
import com.tusharchoudhary.timeline.Timeline;
import com.tusharchoudhary.timeline.TimelineView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import butterknife.Bind;

/**
 * Created by tusharchaudhary on 1/27/16.
 */
public class ConstructionTimelineFragment extends MakaanBaseFragment{
    @Bind(R.id.header_text)
    TextView titleTv;
    @Bind(R.id.timeline_main_view)
    Timeline timeline;

    @Override
    protected int getContentViewId() {
        return R.layout.fragment_project_construction_timeline;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        titleTv.setText(getArguments().getString("title"));
        Long projectId = getArguments().getLong("projectId");
        ((ImageService)MakaanServiceFactory.getInstance().getService(ImageService.class)).getProjectTimelineImages(projectId);
        return view;
    }

    @Subscribe
    public void onResult(ImagesGetEvent imagesGetEvent){
        List<TimelineView.TimelineDataItem> list = new ArrayList<>();
        for(Image image : imagesGetEvent.images)
            list.add(new TimelineView.TimelineDataItem(image.createdAt, image.absolutePath));//TODO: replace createdAt with imageTakenAt when ever it is available
        if(imagesGetEvent.images.size()>0)
            timeline.bindView(list, timeline);
        else
            timeline.bindView(getDummy(), timeline);
    }

    private List<TimelineView.TimelineDataItem> getDummy() {
        List<TimelineView.TimelineDataItem> list = new ArrayList<>();
        list.add(new TimelineView.TimelineDataItem(1451606400000l,"http://www.bendigohospitalproject.org.au/wp-content/uploads/2015/01/construction_update_feb_2015_v1.jpg"));
        list.add(new TimelineView.TimelineDataItem(1448573476000l,"https://upload.wikimedia.org/wikipedia/commons/3/34/Havelock_City,_Sri_Lanka,_003.jpg"));
        list.add(new TimelineView.TimelineDataItem(1445895076000l,"http://www.rbdpropertyandinvestment.com/oline_ser/6.jpg"));
        list.add(new TimelineView.TimelineDataItem(1443303076000l,"http://work.chron.com/DM-Resize/photos.demandstudios.com/getty/article/129/48/89513823.jpg?w=650&h=406&keep_ratio=1&webp=1"));
        list.add(new TimelineView.TimelineDataItem(1430083876000l,"https://lametthesource.files.wordpress.com/2015/07/la-brea-abuttment-2-stem-wall-pour.jpg"));
        list.add(new TimelineView.TimelineDataItem(1424986276000l,"https://c1.staticflickr.com/9/8049/8119382290_e28b0eb43a_b.jpg"));
        list.add(new TimelineView.TimelineDataItem(1417037476000l,"http://www.nkcproject.com/images/yamuna-expressway-project.jpg"));
        list.add(new TimelineView.TimelineDataItem(1411767076000l, "https://media.licdn.com/mpr/mpr/AAEAAQAAAAAAAAXBAAAAJGJkYjdlNDNhLTJhMzYtNGI1ZC1hZGZkLTZhMjk4ZWQ4NzQyMQ.jpg"));
        return list;
    }

}
