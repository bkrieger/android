package us.happ.model;

public enum Group {
	FRIENDS("30 minutes", "1800");
	
	public String label;
	public String valueForPost;
	
	Group(String label, String valueForPost) {
		this.label = label;
		this.valueForPost = valueForPost;
	}
}
