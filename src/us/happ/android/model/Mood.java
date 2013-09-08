package us.happ.android.model;

import java.sql.Date;
import java.util.HashMap;

import us.happ.android.R;

public class Mood {
	
	private static final HashMap<Integer, Integer> map;
	static {
		map = new HashMap<Integer, Integer>();
		map.put(1, R.drawable.ic_tag_chill);
		map.put(2, R.drawable.ic_tag_food);
		map.put(3, R.drawable.ic_tag_movie);
		map.put(4, R.drawable.ic_tag_party);
		map.put(5, R.drawable.ic_tag_sport);
	}

	private String number;
	private String message;
	private int duration;
	private Date timestamp;
	private int resId;
	
	public Mood(String number, String message, long timestamp, int duration, int tagId) {
		this.number = number;
		this.message = message;
		this.timestamp = new Date(timestamp);
		this.duration = duration;
		this.setResId(map.get(tagId));
	}
	
	public static int resIdFromTag(int tagId){
		return map.get(tagId);
	}
	
	// Getters and Setters
	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
		this.timestamp = timestamp;
	}
	
	public int getResId() {
		return resId;
	}

	public void setResId(int resId) {
		this.resId = resId;
	}
}
