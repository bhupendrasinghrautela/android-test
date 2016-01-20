package com.makaan.response.project;

import com.google.gson.annotations.SerializedName;

/**
 * Created by vaibhav on 17/01/16.
 */
public class ProjectStatusCount {

    @SerializedName("pre launch")
    public int preLaunch;

    @SerializedName("under construction")
    public int underConstruction;

    public int cancelled,launch,completed;

    @SerializedName("on hold")
    public int onHold;
    @SerializedName("not launched")
    public int notLaunched;


}
