package com.makaan.event.pyr;

import com.makaan.event.MakaanEvent;
import com.makaan.response.pyr.TopAgentsData;
import com.makaan.response.pyr.TopAgentsResponse;

import java.util.List;

/**
 * Created by makaanuser on 9/1/16.
 */
public class TopAgentsGetEvent extends MakaanEvent{

    private TopAgentsResponse topAgentsResponse;

    public TopAgentsResponse getTopAgentsResponse() {
        return topAgentsResponse;
    }

    public void setTopAgentsResponse(TopAgentsResponse topAgentsResponse) {
        this.topAgentsResponse = topAgentsResponse;
    }
}
