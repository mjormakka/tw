package fi.mjoj.todaywidget;

import java.util.Calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class TodayWidget extends AppWidgetProvider {

	public static long MINUTE_IN_MILLIS = (1000 * 60);
	public static long HOUR_IN_MILLIS = (1000 * 60 * 60);

	static TodayWidgetView WIDGET_VIEW = new TodayWidgetView();
	
	private PendingIntent service = null;
	
	@Override
	public void onEnabled(Context context) {
		final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        final Calendar TIME = Calendar.getInstance();  
        TIME.set(Calendar.MINUTE, 0);  
        TIME.set(Calendar.SECOND, 0);  
        TIME.set(Calendar.MILLISECOND, 0);
        final Intent i = new Intent(context, TodayWidgetService.class);  
        if (service == null) {  
            service = PendingIntent.getService(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);  
        }
        m.setRepeating(AlarmManager.RTC, TIME.getTime().getTime(), MINUTE_IN_MILLIS, service);
        CalendarContentResolver.getInstance().updateCalendarContentCache(context.getContentResolver());
        WIDGET_VIEW.update(context);
	}
	
	@Override
	public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
		WIDGET_VIEW.update(context);
	}
	
	@Override
	public void onDeleted(Context context, int[] appWidgetIds) {
		// TODO Lis‰‰ t‰h‰n koodia, jos on jotakin mit‰ pit‰‰ deletoida, kun on monta widget-instanssia
	}
	
	@Override  
    public void onDisabled(Context context) {  
        final AlarmManager m = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        m.cancel(service);  
    }
	
	@Override
	public void onAppWidgetOptionsChanged(Context context, AppWidgetManager appWidgetManager, int appWidgetId, Bundle newOptions) {
		TodayWidgetView.CURRENT_HEIGHT = newOptions.getInt(AppWidgetManager.OPTION_APPWIDGET_MIN_HEIGHT);
		WIDGET_VIEW.update(context);
	}

	public static boolean isAndroidEmulator() {
		String model = Build.MODEL;
		String product = Build.PRODUCT;
		boolean isEmulator = false;
		if (product != null) {
			isEmulator = product.equals("sdk") || product.contains("_sdk")
					|| product.contains("sdk_");
		}
		return isEmulator;
	}

}
