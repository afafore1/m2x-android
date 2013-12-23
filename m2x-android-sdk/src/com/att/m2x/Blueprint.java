package com.att.m2x;

import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.att.m2x.helpers.JSONHelper;

import android.content.Context;
import android.os.Parcel;

public final class Blueprint extends com.att.m2x.Feed {

	public interface BlueprintsListener {
		public void onSuccess(ArrayList<Blueprint> blueprints);
		public void onError(String errorMessage);		
	}

	public interface BlueprintListener {
		public void onSuccess(Blueprint blueprint);
		public void onError(String errorMessage);		
	}

	public interface BasicListener {
		public void onSuccess();
		public void onError(String errorMessage);				
	}
	
	protected static final String SERIAL = "serial";
	protected static final String PAGE_KEY = "blueprints";
	
	private String serial;

	public Blueprint() {
		
	}
	
	public Blueprint(Parcel in) {
		super(in);
		serial = in.readString();
	}	

	public Blueprint(JSONObject obj) {
		super(obj);
		this.setSerial(JSONHelper.stringValue(obj, SERIAL, ""));
	}
	
	@Override
	public void writeToParcel(Parcel dest, int flags) {
		super.writeToParcel(dest, flags);
		dest.writeString(serial);
	}

	public static void getBlueprints(Context context, String feedKey, final BlueprintsListener callback) {
		
		M2XHttpClient client = M2X.getInstance().getClient();
		String path = "/blueprints";
		
		client.get(context, feedKey, path, null, new M2XHttpClient.Handler() {

			@Override
			public void onSuccess(int statusCode, JSONObject object) {

				ArrayList<Blueprint> array = new ArrayList<Blueprint>();
				try {
					JSONArray blueprints = object.getJSONArray(PAGE_KEY);
					for (int i = 0; i < blueprints.length(); i++) {
						Blueprint blueprint = new Blueprint(blueprints.getJSONObject(i));
						array.add(blueprint);
					}
				} catch (JSONException e) {
					Log.d("Failed to parse Blueprint JSON objects");
				}
				callback.onSuccess(array);
				
			}

			@Override
			public void onFailure(int statusCode, String body) {
				callback.onError(body);
			}
			
		});
		
	}
	
	public static void getBlueprint(Context context, String feedKey, String blueprintId, final BlueprintListener callback) {
		
		M2XHttpClient client = M2X.getInstance().getClient();
		String path = "/blueprints/" + blueprintId;
		
		client.get(context, feedKey, path, null, new M2XHttpClient.Handler() {

			@Override
			public void onSuccess(int statusCode, JSONObject object) {
				Blueprint blueprint = new Blueprint(object);
				callback.onSuccess(blueprint);
				
			}

			@Override
			public void onFailure(int statusCode, String body) {
				callback.onError(body);
			}
			
		});
		
	}
	
	public void create(Context context, String feedKey, final BlueprintListener callback) {
		
		M2XHttpClient client = M2X.getInstance().getClient();
		String path = "/blueprints";
		JSONObject content = this.toJSONObject();
		client.post(context, feedKey, path, content, new M2XHttpClient.Handler() {

			@Override
			public void onSuccess(int statusCode, JSONObject object) {
				Blueprint blueprint = new Blueprint(object);
				callback.onSuccess(blueprint);				
			}

			@Override
			public void onFailure(int statusCode, String message) {
				callback.onError(message);
			}
			
		});
		
	}
	
	public void update(Context context, String feedKey, final BasicListener callback) {

		M2XHttpClient client = M2X.getInstance().getClient();
		String path = "/blueprints/" + this.getId();
		JSONObject content = this.toJSONObject();
		client.put(context, feedKey, path, content, new M2XHttpClient.Handler() {

			@Override
			public void onSuccess(int statusCode, JSONObject object) {
				callback.onSuccess();				
			}

			@Override
			public void onFailure(int statusCode, String message) {
				callback.onError(message);
			}
			
		});

	}
	
	public String getSerial() {
		return serial;
	}

	public void setSerial(String serial) {
		this.serial = serial;
	}
	
	public JSONObject toJSONObject() {
		JSONObject obj = new JSONObject();		
		JSONHelper.put(obj, NAME, this.getName());
		JSONHelper.put(obj, DESCRIPTION, this.getDescription());
		JSONHelper.put(obj, VISIBILITY, this.getVisibility());
		
		StringBuilder sb = new StringBuilder();
		for (String tag : this.getTags())
		{
		    sb.append(tag);
		    sb.append(",");
		}
		sb.replace(sb.length(), sb.length(), "");
		JSONHelper.put(obj, TAGS, sb.toString());
		
		return obj;
	}

	public String toString() {
		return String.format(Locale.US, "M2X Blueprint - %s %s (serial: %s)", 
				this.getId(), 
				this.getName(), 
				this.getSerial() ); 
	}

}
