package com.testjni2;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.text.format.Time;

public class JNIClient extends IntentService {

	private final JNISender mJNISender = new JNISender();

	public JNIClient() {
		super("JNIClient");
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		mJNISender.main(intent, this.getApplicationContext());
		scheduleNextUpdate();
	}

	public void scheduleNextUpdate() {
		Intent intent = new Intent(this, this.getClass());
		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

		long currentTimeMillis = System.currentTimeMillis();
		long nextUpdateTimeMillis = currentTimeMillis + 1 * DateUtils.MINUTE_IN_MILLIS;
		Time nextUpdateTime = new Time();
		nextUpdateTime.set(nextUpdateTimeMillis);

		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
	}
}
