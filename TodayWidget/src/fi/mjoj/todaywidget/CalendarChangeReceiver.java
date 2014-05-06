package fi.mjoj.todaywidget;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class CalendarChangeReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		CalendarContentResolver.getInstance().updateCalendarContentCache(context.getContentResolver());
		TodayWidget.WIDGET_VIEW.update(context);
	}

}
