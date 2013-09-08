package us.happ.android.model;

public enum Duration {
	HALF_HOUR("half hour", "1800"),
	ONE_HOUR("one hour", "3600"),
	TWO_HOURS("two hours", "7200"),
	THREE_HOURS("three hours", "10800"),
	FOUR_HOURS("four hours", "14400");
	
	public String label;
	public String valueForPost;
	
	Duration(String label, String valueForPost) {
		this.label = label;
		this.valueForPost = valueForPost;
	}
}
