package com.makaan.service.leakcanary;

import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.squareup.leakcanary.AnalysisResult;
import com.squareup.leakcanary.DisplayLeakService;
import com.squareup.leakcanary.HeapDump;

/**
 * Created by rohitgarg on 2/9/16.
 */
public class TemporaryLeakUploadService extends DisplayLeakService {
    @Override protected void afterDefaultHandling(HeapDump heapDump, AnalysisResult result, String leakInfo) {
//        Log.d("DEBUG", "leak detected 1");
        if (!result.leakFound || result.excludedLeak) {
            return;
        }
//        Log.d("DEBUG", "leak detected 2");
        Crashlytics.log("*** Memory Leak ***");
        for(String s : leakInfo.split("\n")) {
            Crashlytics.log(s);
        }
        Crashlytics.log("*** End Of Leak ***");

        String name = classSimpleName(result.className);
        if (!heapDump.referenceName.equals("")) {
            name += "(" + heapDump.referenceName + ")";
        }
        Crashlytics.logException(new Exception(name + " has leaked"));
    }

    private static String classSimpleName(String className) {
        int separator = className.lastIndexOf('.');
        return separator == -1 ? className : className.substring(separator + 1);
    }
}
