package us.happ.android.model;

import us.happ.android.R;

public enum Tag {
	CHILL("Chill", "1"),
	FOOD("Food", "2"),
	MOVIE("Movie", "3"),
	PARTY("Party", "4"),
	SPORTS("Sports", "5");
	
	public String label;
	public String valueForPost;
	
	Tag(String label, String valueForPost) {
		this.label = label;
		this.valueForPost = valueForPost;
	}
}
