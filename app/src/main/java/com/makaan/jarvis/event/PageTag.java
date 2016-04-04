package com.makaan.jarvis.event;

import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunil on 23/02/16.
 */
public class PageTag {
    public List<String> city;
    public List<String> locality;
    public List<String> project;
    public List<String> suburb;

    public void addCity(String cityName){
        if(TextUtils.isEmpty(cityName)) {
            return;
        }
        if(null==city){
            city = new ArrayList<>();
        }
        city.add(cityName);
    }

    public void addLocality(String localityName){
        if(TextUtils.isEmpty(localityName)) {
            return;
        }
        if(null==locality){
            locality = new ArrayList<>();
        }
        locality.add(localityName);
    }

    public void addSuburb(String suburbName){
        if(TextUtils.isEmpty(suburbName)) {
            return;
        }
        if(null==suburb){
            suburb = new ArrayList<>();
        }
        suburb.add(suburbName);
    }

    public void addProject(String projectName){
        if(TextUtils.isEmpty(projectName)) {
            return;
        }
        if(null==project){
            project = new ArrayList<>();
        }
        project.add(projectName);
    }
}
