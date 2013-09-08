package us.happ.android.model;

import us.happ.android.R;

public enum Tag {
	CHILL("Chill", "1"),
	FOOD("Food", "2"),
	MOVIE("Movie", "4"),
	PARTY("Party", "8"),
	SPORTS("Sports", "16");
	
	public String label;
	public String valueForPost;
	
	Tag(String label, String valueForPost) {
		this.label = label;
		this.valueForPost = valueForPost;
	}
}
