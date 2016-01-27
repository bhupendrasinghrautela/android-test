package com.makaan.constants;

import java.util.HashMap;

/**
 * Created by vaibhav on 25/01/16.
 */
public class ImageConstants {

    public static HashMap<String, Integer> ENTITY_MAP = new HashMap<>();
    public static HashMap<String, Integer> PROJECT_IMAGE_TYPE_MAP = new HashMap<>();
    public static HashMap<String, Integer> PROPERTY_IMAGE_TYPE_MAP = new HashMap<>();
    public static HashMap<String, Integer> LOCALITY_IMAGE_TYPE_MAP = new HashMap<>();
    public static HashMap<String, Integer> LISTING_IMAGE_TYPE_MAP = new HashMap<>();

    public static final String LISTING = "listing";
    public static final String LOCALITY = "locality";
    public static final String PROJECT = "project";
    public static final String PROPERTY = "property";
    public static final String VIDEO = "video";
    public static final String BANK = "bank";

    /**
     * property
     */
    public static final String MAIN_IMAGE = "mainImage";
    public static final String FLOOR_PLAN = "floorPlan";
    public static final String THREED_FLOOR_PLAN = "3DFloorPlan";
    public static final String VIDEO_WALKTHROUGH = "VideoWalkthrough";
    public static final String BATHROOM = "bathroom";
    public static final String BALCONY = "balcony";
    public static final String BEDROOM = "bedroom";
    public static final String DINING = "dining";
    public static final String KITCHEN = "kitchen";
    public static final String LIVING = "living";

    /**
     * for project
     */
    public static final String CLUSTER_PLAN = "clusterPlan";
    public static final String CONSTRUCTION_STATUS = "constructionStatus";
    public static final String LAYOUT_PLAN = "layoutPlan";
    public static final String MAIN = "main";
    public static final String MASTER_PLAN = "masterPlan";
    public static final String PAYMENT_PLAN = "paymentPlan";
    public static final String AMENITIES = "amenities";
    public static final String MAIN_OTHER = "mainOther";
    public static final String THRRED_MASTER_PLAN = "3DMasterPlan";
    public static final String GALLERY_IMAGES_BIG = "galleryImagesBig";
    public static final String GALLERY_IMAGES_SMALL = "galleryImagesSmall";


    /**
     * locality
     */
    public static final String MALL = "mall";
    public static final String ROAD = "road";
    public static final String SCHOOL = "school";
    public static final String HOSPITAL = "hospital";
    public static final String OTHER = "other";
    public static final String HEROSHOT = "heroshot";

    static {
        ENTITY_MAP.put(LISTING, 17);
        ENTITY_MAP.put(LOCALITY, 4);
        ENTITY_MAP.put(PROJECT, 1);
        ENTITY_MAP.put(PROPERTY, 2);
        ENTITY_MAP.put(VIDEO, 18);
        ENTITY_MAP.put(BANK, 5);

    }


}
