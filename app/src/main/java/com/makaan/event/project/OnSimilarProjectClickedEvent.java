package com.makaan.event.project;

/**
 * Created by tusharchaudhary on 1/27/16.
 */
public class OnSimilarProjectClickedEvent {
    public Long id;
    public Integer clickedPosition;
    public OnSimilarProjectClickedEvent(Long id,Integer clickedPosition) {
    this.id = id;
    this.clickedPosition=clickedPosition;
    }
}
