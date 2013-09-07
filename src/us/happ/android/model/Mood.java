package us.happ.android.model;

import java.sql.Date;

public class Mood {

	private String number;
	private String message;
	private int duration;
	private Date timestamp;
	
	public Mood(String number, String message, long timestamp, int duration) {
		this.number = number;
		this.message = message;
		this.timestamp = new Date(timestamp);
		this.duration = duration;
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
}
