package com.makaan.cookie;

import android.content.Context;
import android.os.Build;
import android.text.TextUtils;

import com.crashlytics.android.Crashlytics;
import com.makaan.constants.ApiConstants;

import java.net.CookieStore;
import java.net.HttpCookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by sunil on 25/01/16.
 */
public class MakaanCookieStore implements CookieStore {
    private Context mContext;
    private Map<String, List<HttpCookie>> map = new HashMap<String, List<HttpCookie>>();

    /**
     * Constructor
     *
     * @param  ctx the context of the Activity
     */
    @SuppressWarnings("unchecked")
    public MakaanCookieStore(Context ctx) {
        mContext = ctx;
        Map<String, ?> prefsMap = CookiePreferences.getCookies(mContext);

        for(Map.Entry<String, ?> entry : prefsMap.entrySet()) {
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.GINGERBREAD_MR1){
                addCookieToLocalMap((String) entry.getValue(), entry);
            }else{
                for (String strCookie : (HashSet<String>) entry.getValue()) {
                    addCookieToLocalMap(strCookie, entry);
                }
            }
        }
    }

    @Override
    public void add(URI uri, HttpCookie cookie) {
        if(cookie==null || TextUtils.isEmpty(cookie.getValue())){
            return;
        }

        CopyOnWriteArrayList<HttpCookie> cookies = (CopyOnWriteArrayList<HttpCookie>) map.get(uri.getHost());
        if (cookies == null) {
            cookies = new CopyOnWriteArrayList<HttpCookie>();
            map.put(uri.getHost(), cookies);
        }else{
            HttpCookie replacedCookie = null;
            for(HttpCookie iCookie : cookies){

                if(	iCookie!=null &&
                        !TextUtils.isEmpty(iCookie.getName()) &&
                        !TextUtils.isEmpty(cookie.getName())){

                    if(iCookie.getName().equalsIgnoreCase(cookie.getName())){
                        replacedCookie = iCookie;
                        break;
                    }
                }
            }

            try {
                if (replacedCookie != null) {
                    cookies.remove(replacedCookie);
                    map.put(uri.getHost(), cookies);
                }
            } catch (Exception e) {
                Crashlytics.logException(e);
            }

        }
        cookies.add(cookie);
        CookiePreferences.setCookie(mContext, uri, cookie);
    }

    @Override
    public List<HttpCookie> get(URI uri) {
        CopyOnWriteArrayList<HttpCookie> cookies = (CopyOnWriteArrayList<HttpCookie>) map.get(uri.getHost());
        if (cookies == null) {
            cookies = new CopyOnWriteArrayList<HttpCookie>();
            map.put(uri.getHost(), cookies);
        }
        return cookies;
    }

    @Override
    public List<HttpCookie> getCookies() {
		/*Collection<List<HttpCookie>> values =map.values();*/
        CopyOnWriteArrayList<List<HttpCookie>> arrayList = new CopyOnWriteArrayList<List<HttpCookie>>(map.values());
        List<HttpCookie> result = new CopyOnWriteArrayList<HttpCookie>();
        for (List<HttpCookie> value : arrayList) {
            result.addAll(value);
        }
        return result;
    }

    @Override
    public List<URI> getURIs() {
        List<URI> uris = new ArrayList<URI>();
        try {
            uris.add(new URI(ApiConstants.BASE_URL));
        } catch (Exception e) {
            Crashlytics.logException(e);
        }
        return uris;

    }

    @Override
    public boolean remove(URI uri, HttpCookie cookie) {
        CopyOnWriteArrayList<HttpCookie> cookies =(CopyOnWriteArrayList<HttpCookie>)map.get(uri);
        if (cookies == null) {
            return false;
        }
        return cookies.remove(cookie);
    }

    @Override
    public boolean removeAll() {
        map.clear();
        return true;
    }

    private void addCookieToLocalMap(String strCookie, Map.Entry<String, ?> entry){
        if(TextUtils.isEmpty(strCookie) || entry==null){
            return;
        }

        if (!map.containsKey(entry.getKey())) {
            List<HttpCookie> lstCookies = new CopyOnWriteArrayList<HttpCookie>();
            lstCookies.addAll(HttpCookie.parse(strCookie));
            map.put(entry.getKey(), lstCookies);

        } else {
            CopyOnWriteArrayList<HttpCookie> lstCookies =(CopyOnWriteArrayList<HttpCookie>)map.get(entry.getKey());
            lstCookies.addAll(HttpCookie.parse(strCookie));
            map.put(entry.getKey(), lstCookies);
        }
    }

}