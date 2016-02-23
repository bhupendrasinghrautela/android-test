package com.makaan.response.assets;

import java.util.ArrayList;

/**
 * Created by rohitgarg on 2/23/16.
 */
public class VersionCodes {
    public int totalCount;
    public ArrayList<Version> data;

    public boolean checkIfNewVersion(Version version) {
        if(data != null) {
            for(Version v : data) {
                if(v.name.equalsIgnoreCase(version.name)) {
                    if(version.version > v.version) {
                        return true;
                    } else {
                        return false;
                    }
                }
            }
        }
        return true;
    }

    public class Version {
        public String name;
        public String url;
        public int version;
    }
}
