package com.makaan.constants;

import java.util.HashMap;

/**
 * Created by vaibhav on 25/01/16.
 */
public class ImageConstants {

    public static HashMap<String, Long> ENTITY_MAP = new HashMap<>();

    static{
        ENTITY_MAP.put("listing",17L);
        ENTITY_MAP.put("locality",4L);
        ENTITY_MAP.put("project",4L);
        ENTITY_MAP.put("property",4L);
        ENTITY_MAP.put("video",4L);
        ENTITY_MAP.put("bank",5L);
    }



        public static final Integer ENTITY_listing = 17;
        public static final Integer ENTITY_locality = 4;
        public static final Integer ENTITY_project = 1;
        public static final Integer ENTITY_property = 2;
        public static final Integer ENTITY_video = 18;
        public static final Integer ENTITY_bank = 5;



    public static final Integer clusterPlan = 2;
    public static final Integer constructionStatus = 3;
    public static final Integer layoutPlan = 4;
    public static final Integer main = 6;
    public static final Integer masterPlan = 7;
    public static final Integer paymentPlan = 8;
    public static final Integer amenities = 80;
    public static final Integer mainOther = 81;
    public static final Integer three_D_MasterPlan = 107;
    public static final Integer galleryImagesBig = 262;
    public static final Integer galleryImagesSmall = 261;

    public static final Integer floorPlan = 12;
    public static final Integer three_D_FloorPlan = 89;
    public static final Integer VideoWalkthrough = 106;


    public static final Integer mall = 14;
    public static final Integer road = 15;
    public static final Integer school = 16;
    public static final Integer hospital = 17;
    public static final Integer other = 18;
    public static final Integer heroshot = 92;


    public static final Integer bathroom = 96;
    public static final Integer balcony = 99;
    public static final Integer bedroom = 95;
    public static final Integer dining = 98;
    public static final Integer kitchen = 101;
    public static final Integer living = 97;

    public static final Integer VideoThumbnail = 111;
}
