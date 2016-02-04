package com.makaan.request.upload;


import android.net.Uri;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by rohitgarg on 2/4/16.
 */
public class ImageUploadDto {

    private final static String OBJECT_TYPE = "objectType";
    private final static String OBJECT_ID = "objectId";
    private final static String TAKEN_AT = "takenAt";
    private final static String IMAGE_TYPE = "imageType";
    private final static String TITLE = "title";
    private final static String JSON_DUMP = "jsonDump";

    private File imageFile;
    private Uri imageUri;
    private String filePath;
    private String objectType;
    private Integer objectId;
    private String takenAt;
    private String imageType;
    private Integer imageTypeId;
    private String jsonDump;
    private String title;
    private boolean isDefaultImage;

    private final Map<String, String> params = new HashMap<>();

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getObjectType() {
        return objectType;
    }

    public void setObjectType(String objectType) {
        this.objectType = objectType;
        params.put(OBJECT_TYPE, objectType);
    }

    public String getImageType() {
        return imageType;

    }

    public void setImageType(String imageType) {
        this.imageType = imageType;
        params.put(IMAGE_TYPE, imageType);
    }

    public String getJsonDump() {
        return jsonDump;
    }

    public void setJsonDump(String jsonDump) {
        this.jsonDump = jsonDump;
        params.put(JSON_DUMP, jsonDump);
    }


    public File getImageFile() {
        return imageFile;
    }

    public void setImageFile(File imageFile) {
        this.imageFile = imageFile;
    }

    public Integer getObjectId() {
        return objectId;
    }

    public void setObjectId(Integer objectId) {
        this.objectId = objectId;
        params.put(OBJECT_ID, String.valueOf(objectId));
    }

    public String getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(String takenAt) {
        this.takenAt = takenAt;
        params.put(TAKEN_AT, takenAt);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        params.put(TITLE, title);
    }

    public Map<String, String> getParams() {

        return params;
    }

    public Uri getImageUri() {
        return imageUri;
    }

    public void setImageUri(Uri imageUri) {
        this.imageUri = imageUri;
    }

    public boolean isDefaultImage() {
        return isDefaultImage;
    }

    public void setIsDefaultImage(boolean isDefaultImage) {
        this.isDefaultImage = isDefaultImage;
    }

    public Integer getImageTypeId() {
        return imageTypeId;
    }

    public void setImageTypeId(Integer imageTypeId) {
        this.imageTypeId = imageTypeId;
    }
}
