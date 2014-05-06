package fi.mjoj.todaywidget;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.database.Cursor;
import android.net.Uri;
import android.provider.CalendarContract;

public class CalendarContentResolver {
	
	private static CalendarContentResolver instance;
	
	private List<CalendarEntry> configuredCalendars;
	private Map<Integer, SortedMap<Date, CalendarEventEntry>> calendarContentCache;
	
	public static CalendarContentResolver getInstance() {
		if (instance == null) {
			instance = new CalendarContentResolver();
		}
		return instance;
	}
	
	public CalendarContentResolver() {
		configuredCalendars = new ArrayList<CalendarEntry>();
		calendarContentCache = new HashMap<Integer, SortedMap<Date, CalendarEventEntry>>();
	}

	public void updateCalendarContentCache(ContentResolver resolver) {
		if (configuredCalendars.isEmpty()) {
			configuredCalendars = cacheConfiguredCalendars(resolver);
		}
		calendarContentCache.clear();
		for (CalendarEntry calendar : configuredCalendars) {
			calendarContentCache.put(calendar.getId(), cacheEventsForToday(resolver, calendar.getId()));
		}
	}
	
	// TODO Konfigurointiaktiviteetti käyttää tätä. Erottele laitteen kaikki kalenterit ja konfiguroidut toisistaan.
	public List<CalendarEntry> getConfiguredCalendars() {
		return configuredCalendars;
	}
	
	public SortedMap<Date, CalendarEventEntry> getEventsForToday(int calendarId) {
		return calendarContentCache.get(calendarId);
	}
	
	private List<CalendarEntry> cacheConfiguredCalendars(ContentResolver resolver) {
		List<CalendarEntry> result = new ArrayList<CalendarEntry>();
		Cursor cursor = resolver.query(Uri.parse("content://com.android.calendar/calendars"), new String[] { CalendarContract.Calendars._ID, CalendarContract.Calendars.CALENDAR_DISPLAY_NAME }, null, null, null);
		try {
			if (cursor.getCount() > 0) {
				while (cursor.moveToNext()) {
					result.add(new CalendarEntry(cursor.getInt(0), cursor.getString(1)));
				}
			}
			else if (TodayWidget.isAndroidEmulator()) {
				result.add(new CalendarEntry(2, "Nimipäiväkalenteri"));
				result.add(new CalendarEntry(5, "Liputuspäiväkalenteri"));
				result.add(new CalendarEntry(3, "Juhlapyhäkalenteri 1"));
				result.add(new CalendarEntry(6, "Juhlapyhäkalenteri 2"));
				result.add(new CalendarEntry(1, "Henkilökohtainen kalenteri 1"));
				result.add(new CalendarEntry(4, "Henkilökohtainen kalenteri 2"));
			}
		} catch (AssertionError ex) {
			// TODO: log exception and bail
		}
		return result;
	}
	
	private SortedMap<Date, CalendarEventEntry> cacheEventsForToday(ContentResolver resolver, int calendarId) {
		
		SortedMap<Date, CalendarEventEntry> result = new TreeMap<Date, CalendarEventEntry>();
		
		Calendar startOfToday = Calendar.getInstance();
		//startOfToday.add(Calendar.DATE, -1);
		startOfToday.set(Calendar.HOUR_OF_DAY, 2);
		startOfToday.set(Calendar.MINUTE, 1);
		startOfToday.set(Calendar.SECOND, 0);
		startOfToday.set(Calendar.MILLISECOND, 0);
		Calendar endOfToday = Calendar.getInstance();
		endOfToday.add(Calendar.DATE, 1);
		endOfToday.set(Calendar.HOUR_OF_DAY, 1);
		endOfToday.set(Calendar.MINUTE, 59);
		endOfToday.set(Calendar.SECOND, 0);
		endOfToday.set(Calendar.MILLISECOND, 0);
		
		// Allday + recurring (tsekkaa all_day flagi)
		
		final String[] projection = new String[] { CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND, CalendarContract.Events.ALL_DAY };
		
		Uri.Builder eventsUriBuilder = CalendarContract.Instances.CONTENT_URI.buildUpon();
		ContentUris.appendId(eventsUriBuilder, startOfToday.getTimeInMillis());
		ContentUris.appendId(eventsUriBuilder, endOfToday.getTimeInMillis());
		Uri eventsUri = eventsUriBuilder.build();
		Cursor mCursor = null;
		mCursor = resolver.query(eventsUri, projection, CalendarContract.Instances.CALENDAR_ID  + " = ?", new String[]{ Integer.toString(calendarId) }, CalendarContract.Instances.DTSTART + " ASC");
		
		
        /*Cursor mCursor = null;
        final String[] projection = new String[] { CalendarContract.Events.TITLE, CalendarContract.Events.DTSTART, CalendarContract.Events.DTEND };

        mCursor = resolver.query(
        		Uri.parse("content://com.android.calendar/events"), 
                projection, 
                Instances.CALENDAR_ID  + " = ? AND ((" + CalendarContract.Events.DTSTART + " >= ?) AND (" + CalendarContract.Events.DTEND + " <= ?))",
                new String[]{ Integer.toString(calendarId), Long.toString(startOfToday.getTimeInMillis()) , Long.toString(endOfToday.getTimeInMillis()) }, null);*/
        try {
			if (mCursor.getCount() > 0) {
				while (mCursor.moveToNext()) {
					Calendar startDate = Calendar.getInstance();
					startDate.setTimeInMillis(mCursor.getLong(1));
					Calendar endDate = Calendar.getInstance();
					endDate.setTimeInMillis(mCursor.getLong(2));
					result.put(startDate.getTime(), new CalendarEventEntry(startDate.getTime(), endDate.getTime(), mCursor.getString(0), mCursor.getInt(3) == 1 ? true : false));
				}
			}
			else if (TodayWidget.isAndroidEmulator()) {
				if (calendarContentCache.get(calendarId) == null || calendarContentCache.get(calendarId).isEmpty()) {
					if (calendarId == 6 || calendarId == 3) {
						result.put(new Date(), new CalendarEventEntry(new Date(), new Date(), "Itsenäisyyspäivä"));
					}
					if (calendarId == 5) {
						result.put(new Date(), new CalendarEventEntry(new Date(), new Date(), "Liplip"));
					}
					if (calendarId == 2) {
						result.put(new Date(), new CalendarEventEntry(new Date(), new Date(), "Matti, Matias"));
					}
					if (calendarId == 1) {
						Calendar date1 = Calendar.getInstance();
						date1.add(Calendar.HOUR_OF_DAY, -5);
						Calendar date2 = Calendar.getInstance();
						date2.add(Calendar.HOUR_OF_DAY, 6);
						Calendar date3 = Calendar.getInstance();
						date3.add(Calendar.HOUR_OF_DAY, 4);
						Calendar date4 = Calendar.getInstance();
						date4.add(Calendar.MINUTE, 2);
						result.put(date1.getTime(), new CalendarEventEntry(date1.getTime(), date1.getTime(), "Tapaaminen 1"));
						result.put(date2.getTime(), new CalendarEventEntry(date2.getTime(), date2.getTime(), "Tapaaminen 2"));
						result.put(date3.getTime(), new CalendarEventEntry(date3.getTime(), date3.getTime(), "Tapaaminen 4"));
						result.put(date4.getTime(), new CalendarEventEntry(date4.getTime(), date4.getTime(), "Tapaaminen 5"));
					}
					if (calendarId == 4) {
						result.put(new Date(), new CalendarEventEntry(new Date(), new Date(), "Tapaaminen 3"));
					}
				}
				else {
					result = calendarContentCache.get(calendarId);
				}
			}
		} catch (AssertionError ex) {
		}
        return result;
	}
}