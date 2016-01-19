package com.makaan.response.pyr;

import com.makaan.response.BaseResponse;

/**
 * Created by makaanuser on 9/1/16.
 */
public class TopAgentsResponse extends BaseResponse{

    TopAgentsData [] data;

    public TopAgentsData[] getData() {
        return data;
    }

    public void setData(TopAgentsData[] data) {
        this.data = data;
    }
}
