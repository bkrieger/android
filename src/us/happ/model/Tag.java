package us.happ.model;


public enum Tag {
	CHILL("Chill", 1),
	FOOD("Food", 2),
	MOVIE("Movie", 3),
	PARTY("Party", 4),
	SPORTS("Sports", 5);
	
	public String label;
	public int valueForPost;
	
	Tag(String label, int valueForPost) {
		this.label = label;
		this.valueForPost = valueForPost;
	}
}
