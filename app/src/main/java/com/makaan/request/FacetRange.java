package com.makaan.request;

import java.util.HashMap;
import static com.makaan.constants.RequestConstants.*;

/**
 * Created by vaibhav on 13/01/16.
 */
public class FacetRange {


    private HashMap<String, Object> facetRange = new HashMap<>();


    public FacetRange addField(String fieldName){
        facetRange.put(FIELD,fieldName);
        return this;
    }

    public FacetRange addStart(Double start){
        facetRange.put(START,start);
        return this;
    }

    public FacetRange addEnd(Double end){
        facetRange.put(END,end);
        return this;
    }


    public FacetRange addGap(Double gap){
        facetRange.put(GAP,gap);
        return this;
    }

    public HashMap<String, Object> getFacetRange() {
        return facetRange;
    }
}
