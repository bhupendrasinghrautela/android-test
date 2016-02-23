package com.makaan.jarvis.event;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunil on 23/02/16.
 */
public class PageTag {
    private List<String> city;
    private List<String> locality;
    private List<String> project;
    private List<String> suburb;

    public void addCity(String cityName){
        if(null==city){
            city = new ArrayList<>();
        }
        city.add(cityName);
    }

    public void addLocality(String localityName){
        if(null==locality){
            locality = new ArrayList<>();
        }
        locality.add(localityName);
    }

    public void addSuburb(String suburbName){
        if(null==suburb){
            suburb = new ArrayList<>();
        }
        suburb.add(suburbName);
    }

    public void addProject(String projectName){
        if(null==project){
            project = new ArrayList<>();
        }
        project.add(projectName);
    }
}
