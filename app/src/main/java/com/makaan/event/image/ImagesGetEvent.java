package com.makaan.event.image;

import com.makaan.event.MakaanEvent;
import com.makaan.response.image.Image;

import java.util.ArrayList;

/**
 * Created by vaibhav on 17/01/16.
 */
public class ImagesGetEvent extends MakaanEvent{

    public Long objectId;
    public Integer objectType;
    public String imageType;

    public ArrayList<Image> images;


}
