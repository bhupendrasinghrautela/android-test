package com.makaan.util;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;

/**
 * Created by sunil on 03/12/15.
 *
 * A simple Json parser using gson lib
 * */
public class JsonParser {
	

	public static <T> Object parseJson(String jsonString, Class<T> model) {
		try{
			Gson gson = new Gson();
			return gson.fromJson(jsonString, model);
		}catch(JsonSyntaxException e){
			return null;
		}
	}
	

}