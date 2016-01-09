package com.makaan.service;

import com.makaan.request.selector.Selector;
import static  com.makaan.constants.RequestConstants.*;

/**
 * Created by vaibhav on 09/01/16.
 */
public class LocalityService {


    public void getLocalityById(Long localityId){
        Selector localitySelector = new Selector();
        localitySelector.fields(new String[]{});
    }
}
