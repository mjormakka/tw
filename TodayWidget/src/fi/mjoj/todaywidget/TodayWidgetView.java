package fi.mjoj.todaywidget;

import java.util.Calendar;
import java.util.Date;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.TimeUnit;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.res.Resources;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.RemoteViews;

public class TodayWidgetView {

	public static int CURRENT_HEIGHT = 0;
	
	private static int[] eventRowIds = { R.id.eventRow1, 
		R.id.eventRow2, 
		R.id.eventRow3,
		R.id.eventRow4,
		R.id.eventRow5 };
	
	private static int[] eventTimeIds = { R.id.eventTime1, 
		R.id.eventTime2, 
		R.id.eventTime3,
		R.id.eventTime4,
		R.id.eventTime5 };
	
	private static int[] eventTitleIds = { R.id.eventTitle1, 
		R.id.eventTitle2, 
		R.id.eventTitle3,
		R.id.eventTitle4,
		R.id.eventTitle5 };
	
	private CalendarContentResolver calendarContent;
	
	public TodayWidgetView() {
		calendarContent = CalendarContentResolver.getInstance();
	}
	
	public void update(Context context) {
		RemoteViews view = new RemoteViews(context.getPackageName(), R.layout.widget_main);
		ComponentName widget = new ComponentName(context, TodayWidget.class);
		Calendar now = Calendar.getInstance();
		view.setTextViewText(R.id.textView5, DateFormat.getDateFormat(context).format(now.getTime()));
		view.setTextViewText(R.id.textView1, context.getResources().getStringArray(R.array.week_days)[now.get(Calendar.DAY_OF_WEEK) - 1]);

		SortedMap<Date, CalendarEventEntry> nameDayEvents = new TreeMap<Date, CalendarEventEntry>();
		SortedMap<Date, CalendarEventEntry> holidayEvents = new TreeMap<Date, CalendarEventEntry>();
		SortedMap<Date, CalendarEventEntry> otherEvents = new TreeMap<Date, CalendarEventEntry>();
		nameDayEvents = calendarContent.getEventsForToday(2);
		holidayEvents = calendarContent.getEventsForToday(6);
		otherEvents = calendarContent.getEventsForToday(1);
		otherEvents.putAll(calendarContent.getEventsForToday(4));

		if (!nameDayEvents.isEmpty() || !holidayEvents.isEmpty()) {
			view.setViewVisibility(R.id.allDayEventContainer, View.VISIBLE);	
			if (!nameDayEvents.isEmpty()) {
				view.setViewVisibility(R.id.textView2, View.VISIBLE);
				view.setTextViewText(R.id.textView2, nameDayEvents.get(nameDayEvents.firstKey()).getTitle());
			}
			else {
				view.setViewVisibility(R.id.textView2, View.GONE);
			}
			if (!holidayEvents.isEmpty()) {
				view.setViewVisibility(R.id.textView2, View.VISIBLE);
				view.setTextViewText(R.id.textView3, holidayEvents.get(holidayEvents.firstKey()).getTitle());
			}
			else {
				view.setViewVisibility(R.id.textView3, View.GONE);
			}
		}
		else {
			view.setViewVisibility(R.id.allDayEventContainer, View.GONE);
		}
		
		if (!otherEvents.isEmpty()) {
			view.setViewVisibility(R.id.eventSummary, View.VISIBLE);
			Date next = null;
			for (Date key : otherEvents.keySet()) {
				next = otherEvents.get(key).getStartDate();
				if ((next.getTime() - now.getTimeInMillis()) > TodayWidget.MINUTE_IN_MILLIS) {
					break;
				}
			}
			if (CURRENT_HEIGHT < 180) {
				view.setViewVisibility(R.id.eventSummaryTextView, View.VISIBLE);
				for (int i = 0; i < eventRowIds.length; i++) {
					view.setViewVisibility(eventRowIds[i], View.GONE);
				}
				String numOfEvents = context.getResources().getQuantityString(R.plurals.event_summary_num_events, otherEvents.size(), otherEvents.size());
				String timetoNextEvent = determineTimeToNextEventString(context.getResources(), now.getTime(), next);
				if (timetoNextEvent.isEmpty()) {
					view.setTextViewText(R.id.eventSummaryTextView, numOfEvents);
				} else {
					view.setTextViewText(R.id.eventSummaryTextView, context.getResources().getString(R.string.event_summary_text, numOfEvents, timetoNextEvent));
				}
			}
			else {
				view.setViewVisibility(R.id.eventSummaryTextView, View.GONE);
				int counter = 0;
				for (Date key : otherEvents.keySet()) {
					CalendarEventEntry event =  otherEvents.get(key);
					view.setViewVisibility(eventRowIds[counter], View.VISIBLE);
					view.setTextViewText(eventTimeIds[counter], event.isAllDayEvent() ? "" : 
						DateFormat.getTimeFormat(context).format(event.getStartDate()) + " - " + 
						DateFormat.getTimeFormat(context).format(event.getEndDate()));
					view.setTextViewText(eventTitleIds[counter++], event.getTitle());
					if (counter + 1 == eventRowIds.length) {
						break;
					}
				} 
			}
		}
		else {
			view.setViewVisibility(R.id.eventSummary, View.GONE);
			view.setViewVisibility(R.id.eventSummaryTextView, View.GONE);
			for (int i = 0; i < eventRowIds.length; i++) {
				view.setViewVisibility(eventRowIds[i], View.GONE);
			}
		}
		AppWidgetManager manager = AppWidgetManager.getInstance(context);  
        manager.updateAppWidget(widget, view);
	}
	
	private String determineTimeToNextEventString(Resources resources, Date now, Date next) {
		String result = "";
		long milliseconds = next.getTime() - now.getTime();
		if (milliseconds >= TodayWidget.MINUTE_IN_MILLIS && milliseconds < TodayWidget.HOUR_IN_MILLIS) { // minuutteja
			int minutes = (int) TimeUnit.MILLISECONDS.toMinutes(milliseconds);
			result = resources.getString(R.string.event_summary_to_next_event_text_fine, resources.getQuantityString(R.plurals.minutes_until, minutes, minutes));
		}
		else if (milliseconds >= TodayWidget.HOUR_IN_MILLIS) { // tunteja
			int hours = (int) TimeUnit.MILLISECONDS.toHours(milliseconds);
			if (hours > 3) {
				result = resources.getString(R.string.event_summary_to_next_event_text_coarse, determineTimeOfDay(resources, next.getTime()));
			}
			else {
				result = resources.getString(R.string.event_summary_to_next_event_text_fine, resources.getQuantityString(R.plurals.hours_until, hours, hours));
			}
		}
		return result;
	}
	
	private String determineTimeOfDay(Resources resources, long milliseconds) {
		String result = "";
		Calendar next = Calendar.getInstance();
		next.setTimeInMillis(milliseconds);
		int hourOfDay = next.get(Calendar.HOUR_OF_DAY);
		if (hourOfDay >= 0 && hourOfDay < 6) {
			result = resources.getString(R.string.time_of_day_night);
		}
		else if (hourOfDay >= 6 && hourOfDay < 9) {
			result = resources.getString(R.string.time_of_day_morning);
		}
		else if (hourOfDay >= 9 && hourOfDay < 12) {
			result = resources.getString(R.string.time_of_day_forenoon);
		}
		else if (hourOfDay >= 12 && hourOfDay < 15) {
			result = resources.getString(R.string.time_of_day_noon);
		}
		else if (hourOfDay >= 15 && hourOfDay < 18) {
			result = resources.getString(R.string.time_of_day_afternoon);
		}
		else if (hourOfDay >= 18 && hourOfDay < 21) {
			result = resources.getString(R.string.time_of_day_evening);
		}
		else if (hourOfDay >= 21){
			result = resources.getString(R.string.time_of_day_late_evening);
		}
		return result;
	}
	
}