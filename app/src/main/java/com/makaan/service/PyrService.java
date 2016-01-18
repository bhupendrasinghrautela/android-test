package com.makaan.service;

import com.makaan.constants.ApiConstants;
import com.makaan.event.pyr.PyrPostCallBack;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.service.MakaanService;


import org.json.JSONObject;


/**
 * Created by vaibhav on 09/01/16.
 */
public class PyrService  implements MakaanService {


    public final String TAG=PyrService.class.getSimpleName();
    public void makePyrRequest(JSONObject jsonObject)
    {
        if(jsonObject!=null)
        {
            MakaanNetworkClient.getInstance().post(ApiConstants.PYR, jsonObject, new PyrPostCallBack(),TAG);
        }

    }
}
