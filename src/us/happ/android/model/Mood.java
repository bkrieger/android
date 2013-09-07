package us.happ.android.model;

public class Mood {

	private String number;
	private String message;
	
	public Mood(String number, String message) {
		this.number = number;
		this.message = message;
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
}
