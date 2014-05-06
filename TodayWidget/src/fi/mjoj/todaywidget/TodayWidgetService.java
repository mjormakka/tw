package fi.mjoj.todaywidget;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class TodayWidgetService extends Service {
	
	
	@Override
	public void onCreate()
	{
		super.onCreate();
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
	    //buildUpdate();
		TodayWidget.WIDGET_VIEW.update(this);
	    return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public IBinder onBind(Intent intent)
	{
	    return null;
	}
	
}
