package us.happ.model;

import java.util.Date;
import java.util.HashMap;

import us.happ.R;

public class Mood {
	
	private static final HashMap<Integer, Integer> moodIconMap;
	static {
		moodIconMap = new HashMap<Integer, Integer>();
		moodIconMap.put(1, R.drawable.ic_tag_chill);
		moodIconMap.put(2, R.drawable.ic_tag_food);
		moodIconMap.put(3, R.drawable.ic_tag_movie);
		moodIconMap.put(4, R.drawable.ic_tag_party);
		moodIconMap.put(5, R.drawable.ic_tag_sport);
	}

	private static final HashMap<Integer, Integer> moodIconInverseMap;
	static {
		moodIconInverseMap = new HashMap<Integer, Integer>();
		moodIconInverseMap.put(1, R.drawable.ic_tag_chill_i);
		moodIconInverseMap.put(2, R.drawable.ic_tag_food_i);
		moodIconInverseMap.put(3, R.drawable.ic_tag_movie_i);
		moodIconInverseMap.put(4, R.drawable.ic_tag_party_i);
		moodIconInverseMap.put(5, R.drawable.ic_tag_sport_i);
	}
	
	public static final HashMap<Integer, String> moodActionMap;
	static {
		moodActionMap = new HashMap<Integer, String>();
		moodActionMap.put(1, "to chill with");
		moodActionMap.put(2, "to get food with");
		moodActionMap.put(3, "to watch a movie with");
		moodActionMap.put(4, "to party with");
		moodActionMap.put(5, "to play sports with");
	}

	private String number;
	private String message;
	private int duration;
	private Date timestamp;
	private int resId;
	private boolean checked;
	
	public Mood(String number, String message, long timestamp, int duration, int tagId) {
		this.number = number;
		this.message = message;
		this.timestamp = new Date(timestamp);
		this.duration = duration;
		this.setResId(moodIconMap.get(tagId));
		this.checked = false;
	}
	
	public static String getNotificationText(int tagId){
		return "wants " + Mood.moodActionMap.get(tagId) + " you";
	}
	
	public static int resIdFromTag(int tagId){
		return resIdFromTag(tagId, false);
	}
	
	public static int resIdFromTag(int tagId, boolean inverse){
		if (inverse){
			return moodIconInverseMap.get(tagId);
		}
		return moodIconMap.get(tagId);
	}
	
	public static float getDecay(int duration, long timestamp){
		return (float) (timestamp + duration*1000 - new Date().getTime())/(duration*1000);
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
	
	public void setChecked(boolean checked){
		this.checked = checked;
	}
	
	public boolean getChecked(){
		return this.checked;
	}
}
