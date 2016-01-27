package com.makaan.response.pyr;

import com.makaan.response.BaseResponse;

/**
 * Created by proptiger on 21/1/16.
 */
public class PyrPostResponse extends BaseResponse {
    PyrData data;

    public PyrData getData() {
        return data;
    }

    public void setData(PyrData data) {
        this.data = data;
    }
}
