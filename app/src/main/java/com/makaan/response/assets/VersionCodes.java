package com.makaan.response.assets;

import android.content.Context;

import com.makaan.service.download.DownloadAssetService;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by rohitgarg on 2/23/16.
 */
public class VersionCodes {
    public int totalCount;
    public ArrayList<Version> data;

    public boolean checkIfNewVersion(Version version, Context context) {
        if(data != null) {
            for(Version v : data) {
                if(v.name.equalsIgnoreCase(version.name)) {
                    if(version.version > v.version) {
                        File file = new File(context.getFilesDir(), version.name);
                        if(file.exists()) {
                            return true;
                        } else {
                            return false;
                        }
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
