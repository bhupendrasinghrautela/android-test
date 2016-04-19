package com.makaan.service.download;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.crashlytics.android.Crashlytics;
import com.google.gson.reflect.TypeToken;
import com.makaan.MakaanBuyerApplication;
import com.makaan.network.JSONGetCallback;
import com.makaan.network.MakaanNetworkClient;
import com.makaan.response.ResponseError;
import com.makaan.response.assets.VersionCodes;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;

/**
 * Created by rohitgarg on 2/22/16.
 */
public class DownloadAssetService extends Service {
    public static final int REQUEST_CODE = 0x01;
    private static final String ASSETS_URL = "http://content.makaan.com.s3.amazonaws.com/app/assets/buyer/";
    private static final String VERSION_CODES_FILE_NAME = "versionCodes.json";

    private VersionCodes mServerVersionCodes;
    private VersionCodes mLocalVersionCodes;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d("DEBUG", "DownloadAssetService, oncreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        super.onStartCommand(intent, flags, startId);

        MakaanNetworkClient client = MakaanNetworkClient.getInstance();
        if(client == null) {
            MakaanNetworkClient.init(this.getApplicationContext());
            client = MakaanNetworkClient.getInstance();
        }

        if(client == null) {
            stopSelf(startId);
        }

        checkVersionInfo(startId);
        return START_STICKY;

    }

    private void checkVersionInfo(final int startId) {
        final Type versionCodesType = new TypeToken< VersionCodes>(){}.getType();
        MakaanNetworkClient.getInstance().get(ASSETS_URL.concat(VERSION_CODES_FILE_NAME), new JSONGetCallback() {
            @Override
            public void onSuccess(JSONObject responseObject) {

                if(responseObject != null) {
                    mServerVersionCodes = MakaanBuyerApplication.gson.fromJson(responseObject.toString(), versionCodesType);
                    if(mLocalVersionCodes != null) {
                        // let compare the two objects
                        for(VersionCodes.Version version : mServerVersionCodes.data) {
                            if(mLocalVersionCodes.checkIfNewVersion(version, DownloadAssetService.this.getApplicationContext())) {
                                downloadAndSave(version);
                            }
                        }
                    } else {
                        // probably file is not saved earlier, so lets save this file and download all assets
                        for(VersionCodes.Version version : mServerVersionCodes.data) {
                            downloadAndSave(version);
                        }
                    }
                    saveToFileSystem(VERSION_CODES_FILE_NAME, responseObject.toString());

                    stopSelf(startId);
                }
            }

            @Override
            public void onError(ResponseError error) {
                // TODO
                stopSelf(startId);
            }
        });

        File file = new File(getFilesDir(), VERSION_CODES_FILE_NAME);
        if(file.exists()) {
            //Read text from file
            StringBuilder text = new StringBuilder();

            try {
                BufferedReader br = new BufferedReader(new FileReader(file));
                String line;

                while ((line = br.readLine()) != null) {
                    text.append(line);
                    text.append('\n');
                }
                br.close();

                JSONArray response = null;
                try {
                    JSONObject mockFileResponse = new JSONObject(text.toString());
                    mLocalVersionCodes = MakaanBuyerApplication.gson.fromJson(mockFileResponse.toString(), versionCodesType);
                } catch (JSONException e) {
                    Crashlytics.logException(e);
                    e.printStackTrace();
                }
            }
            catch (IOException e) {
                Crashlytics.logException(e);
            }
        }
    }

    private void saveToFileSystem(String fileName, String text) {
        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput(fileName, Context.MODE_PRIVATE);
            outputStream.write(text.getBytes());
            outputStream.close();
        } catch (Exception e) {
            Crashlytics.logException(e);
            e.printStackTrace();
        }
    }

    private void downloadAndSave(final VersionCodes.Version version) {
        MakaanNetworkClient.getInstance().get(ASSETS_URL.concat(version.url), new JSONGetCallback() {
            @Override
            public void onSuccess(JSONObject responseObject) {
                saveToFileSystem(version.name, responseObject.toString());
            }

            @Override
            public void onError(ResponseError error) {
                // TODO
            }
        });
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
