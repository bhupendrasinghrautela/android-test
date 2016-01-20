package com.makaan.util;

import android.content.SharedPreferences;
import android.os.Build;

import com.makaan.constants.StringConstants;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

/**
 * Created by rohitgarg on 1/10/16.
 */
public class Preference {


    public static HashSet<String> getStringSet(final SharedPreferences sharedPreferences, final String key) {
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1){
            final HashSet<String> out = new HashSet<String>();
            final String base = sharedPreferences.getString(key, null);
            if (base != null) {
                out.addAll(Arrays.asList(base.split(StringConstants.COMMA)));
            }
            return out;
        } else{
            return (HashSet<String>) sharedPreferences.getStringSet(key, new HashSet<String>());
        }
    }


    public static void putStringSet(final SharedPreferences.Editor editor, final String key,
                                     final HashSet<String> stringSet) {
        if(editor==null || stringSet==null){
            return;
        }
        if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1){
            Iterator<String> cookieSet = stringSet.iterator();
            int i = 0;
            StringBuilder cookieBuilder = new StringBuilder();
            while(cookieSet.hasNext()){
                if(i>0){
                    cookieBuilder.append(StringConstants.COMMA);
                }
                cookieBuilder.append(cookieSet.next());
                i++;
            }
            editor.putString(key, cookieBuilder.toString());
        } else{
            editor.putStringSet(key, stringSet);
        }
    }


    public static void putString(final SharedPreferences.Editor editor, final String key,
                                    final String string) {
        if(editor==null || string==null){
            return;
        }
        editor.putString(key, string);
    }


    public static String getString(final SharedPreferences preferences, final String key) {
        if(preferences==null || key==null){
            return null;
        }
        return preferences.getString(key, null);
    }
}
