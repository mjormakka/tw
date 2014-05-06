package fi.mjoj.todaywidget;

public class CalendarEntry {

	private int id;
	private String displayName;
	
	public CalendarEntry(int id, String displayName) {
		super();
		this.id = id;
		this.displayName = displayName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	
	
}
