package com.makaan.util;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.widget.ImageView;

import java.io.File;
import java.io.FileOutputStream;

/**
 * A utility class for managing images, functionalities include 
 * downloading and saving images to external or internal storage.
 * */

public class ImageManager {
	
	private final static String PROPTIGER_IMAGE_DIR = "Proptiger";
	
	public static enum Status{
		DOES_NOT_EXIST, ALREADY_EXISTS, DOWNLOAD_SUCCESS, SHARE_SUCCESS, 
		MEDIA_UNMOUNTED, ERROR
	}
	
	/**
	 * A method for storing images to specified storage 
	 * @param context
	 * @param bitmap Bitmap to be stored
	 * @param baseName 
	 * 
	 * **/
    public static Status saveBitmap(Context context, Bitmap bitmap, String baseName) {
    	if(!isExternalStorageWritable()){
    		return Status.MEDIA_UNMOUNTED;
    	}
    	
        File pictureDir = getAlbumStorageDir(PROPTIGER_IMAGE_DIR);
        File file = null;
        
        String fileName = baseName + ".jpeg";
        file = new File(pictureDir, fileName);
        file.setLastModified(System.currentTimeMillis());
        
        if(file.exists()){
        	return Status.ALREADY_EXISTS;
        }
        
        String name = file.getAbsolutePath();
        try {
        	FileOutputStream fos = new FileOutputStream(name);
            try{
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
            }finally{
            	fos.close();
            }
            
            addImageToGallery(context, file);
            
            return Status.DOWNLOAD_SUCCESS;
        } catch (Exception e) {
        	return Status.ERROR;
        } 
    }
    
    /**
     * Check if image is present in the gallery
     * @param context
	 * @param baseName 
     * */
    public static Status doesImageExit(Context context, String baseName){
    	if(!isExternalStorageWritable()){
    		return Status.MEDIA_UNMOUNTED;
    	}
    	
        File pictureDir = getAlbumStorageDir(PROPTIGER_IMAGE_DIR);
        File file = null;
        
        String fileName = baseName + ".jpeg";
        file = new File(pictureDir, fileName);
        
        if(file.exists()){
        	return Status.ALREADY_EXISTS;
        }else{
        	return Status.DOES_NOT_EXIST;
        }
    }
    
    
    /**
     * A method for creating a directory inside picture category
     * @param albumName directory name
     * 
     * */
    private static File getAlbumStorageDir(String albumName) {
    	// Get the directory for the user's public pictures directory. 
        File file = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), albumName);
        
        if (!file.mkdirs()) {
        }
        return file;
    }

    
    /**
     *  Invokes the system's media scanner to add a photo to the 
     *  Media Provider's database
     * */
    private static Uri addImageToGallery(Context context, File f) {
    	try{
	        Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
	        Uri contentUri = Uri.fromFile(f);
	        mediaScanIntent.setData(contentUri);
	        context.sendBroadcast(mediaScanIntent);
	        return contentUri;
    	}catch(Exception e){
    		return null;
    	}
    }
    
    /**
     *  Checks if external storage is available for read and write 
     * */
    private static boolean isExternalStorageWritable() {
    	try{
	        String state = Environment.getExternalStorageState();
	        if (Environment.MEDIA_MOUNTED.equals(state)) {
	            return true;
	        }
    	}catch(Exception e){}
        return false;
    }
    
    
    public static Status shareImage(Context context, String baseName, ImageView imageView){
    	if(!isExternalStorageWritable()){
    		return Status.MEDIA_UNMOUNTED;
    	}
    	
        File pictureDir = getAlbumStorageDir(PROPTIGER_IMAGE_DIR);
        File file = null;
        
        String fileName = baseName + ".jpeg";
        file = new File(pictureDir, fileName);
        
        if(file.exists()){
        	generateShareIntent(context, getLocalBitmapUri(baseName));
        	return Status.SHARE_SUCCESS;
        }else{
            String name = file.getAbsolutePath();
            try {
            	
            	Bitmap bitmap = getBitmapFromImageView(context, imageView);
            	
            	if(bitmap==null){
            		return Status.ERROR;
            	}
            	
            	FileOutputStream fos = new FileOutputStream(name);
                try{
                	if(bitmap!=null){
                		bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                		fos.flush();
                	}
                }finally{
                	fos.close();
                }
                
                Uri uri = addImageToGallery(context, file);
                generateShareIntent(context, uri);
                
                return Status.SHARE_SUCCESS;
            } catch (Exception e) {
            	return Status.ERROR;
            } 
        }

    }
    
    private static void generateShareIntent(Context context, Uri uriToImage){
    	Intent shareIntent = new Intent();
    	shareIntent.setAction(Intent.ACTION_SEND);
    	shareIntent.putExtra(Intent.EXTRA_STREAM, uriToImage);
    	shareIntent.setType("image/jpeg");
    	context.startActivity(Intent.createChooser(shareIntent, "Share image via"));
    }
    
    private static Bitmap getBitmapFromImageView(Context context, ImageView imageView){
        // Extract Bitmap from ImageView drawable
        Drawable drawable = imageView.getDrawable();
        Bitmap bmp = drawableToBitmap(drawable);
        return bmp;
    }
    
    private static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        try{
	        int width = drawable.getIntrinsicWidth();
	        width = width > 0 ? width : 1;
	        int height = drawable.getIntrinsicHeight();
	        height = height > 0 ? height : 1;
	
	        Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
	        Canvas canvas = new Canvas(bitmap);
	        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
	        drawable.draw(canvas);
	
	        return bitmap;
        }catch(Exception e){
        	return null;
        }
    }
    
    private static Uri getLocalBitmapUri(String baseName) {

    	File pictureDir = getAlbumStorageDir(PROPTIGER_IMAGE_DIR);
        File file = null;
        
        String fileName = baseName + ".jpeg";
        file = new File(pictureDir, fileName);
        
        return Uri.fromFile(file);
    }
	
}