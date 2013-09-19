package us.happ.android.model;

public enum Duration {
	HALF_HOUR("30 minutes", "1800"),
	ONE_HOUR("1 hour", "3600"),
	TWO_HOURS("2 hours", "7200"),
	THREE_HOURS("3 hours", "10800"),
	FOUR_HOURS("4 hours", "14400"),
	FIVE_HOURS("5 hours", "18000"),
	SIX_HOURS("6 hours", "21600");
	
	public String label;
	public String valueForPost;
	
	Duration(String label, String valueForPost) {
		this.label = label;
		this.valueForPost = valueForPost;
	}
}
