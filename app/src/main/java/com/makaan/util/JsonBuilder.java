package com.makaan.util;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by sunil on 03/12/15.
 *
 * A convenience class for building Json object
 * */
public class JsonBuilder {
	
	/**
	 * A method to convert a model object to json string
	 * @param payloadObject model Object
	 * 
	 * @return jObject the JSONObject 
	 * @throws JSONException
	 * */
	public static final JSONObject toJson(Object payloadObject) throws JSONException {
		if(payloadObject == null){
			throw new IllegalArgumentException("Invalid argument");
		}
		Gson gson = new Gson();
        String payload = gson.toJson(payloadObject);
        JSONObject jObject = new JSONObject(payload);
        return jObject;
	}

	
	/**
	 * A method to convert a model object to json string
	 * @param payloadObject model Object
	 * 
	 * @return jObject the JSONObject 
	 * @throws JSONException
	 * */
	public static final JSONArray toJsonArray(Object payloadObject) throws JSONException {
		if(payloadObject == null){
			throw new IllegalArgumentException("Invalid argument");
		}
		Gson gson = new Gson();
        String payload = gson.toJson(payloadObject);
        JSONArray jArray = new JSONArray(payload);
        return jArray;
	}
	
}