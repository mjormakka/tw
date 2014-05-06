package fi.mjoj.todaywidget;

import java.util.Date;

public class CalendarEventEntry {

	private Date startDate;
	private Date endDate;
	private String title;
	private boolean allDayEvent;

	public CalendarEventEntry(Date startDate, Date endDate, String title) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.title = title;
		this.allDayEvent = false;
	}
	
	public CalendarEventEntry(Date startDate, Date endDate, String title, boolean allDayEvent) {
		this.startDate = startDate;
		this.endDate = endDate;
		this.title = title;
		this.allDayEvent = allDayEvent;
	}

	public Date getStartDate() {
		return startDate;
	}

	public void setStartDate(Date startDate) {
		this.startDate = startDate;
	}

	public Date getEndDate() {
		return endDate;
	}

	public void setEndDate(Date endDate) {
		this.endDate = endDate;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}
	
	public boolean isAllDayEvent() {
		return allDayEvent;
	}

	public void setAllDayEvent(boolean allDayEvent) {
		this.allDayEvent = allDayEvent;
	}
		
}
