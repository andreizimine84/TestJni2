package com.helloworld.com;

import java.io.IOException;
import java.io.InputStream;

import android.app.AlarmManager;
import android.app.IntentService;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.text.format.DateUtils;
import android.text.format.Time;

/**
 * 
 * @author andreizimine FileService med SeleceAndShareService klasserna skapar
 *         med hj�lp av broadcastreceivers fr�n MainActivity OnCreate metoden en
 *         alarm funktionalitet d� FileService skickar filerna till server varje
 *         t.ex minut och SelectAndShareService som skapar filerna i intern
 *         minne p� telefonen. B�da service anv�nder sig av hj�lpklasser i form
 *         av objekt och dessa i behov anropar andra objekt som dem beh�ver
 *         hj�lp med att handskas med t. ex offline funtionalitet.
 *
 * 
 */

public class FileService extends IntentService {

	String dirName = "tempDirectory";
	byte[] buffer = null;
	InputStream is = null;
	private final FileSender mFileSender = new FileSender();

	public FileService() {
		super("FileService");
	}

	protected void onHandleIntent(Intent intent) throws NullPointerException {
		try {
			mFileSender.main(this.getApplicationContext());
		} catch (SecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (ClassCastException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
		Intent intentValue = new Intent(this, this.getClass());
		scheduleNextUpdate(intentValue);
	}

	public void scheduleNextUpdate(Intent intentValue) {

		PendingIntent pendingIntent = PendingIntent.getService(this, 0, intentValue, PendingIntent.FLAG_UPDATE_CURRENT);

		long currentTimeMillis = System.currentTimeMillis();
		long nextUpdateTimeMillis = currentTimeMillis + 1 * DateUtils.MINUTE_IN_MILLIS;
		Time nextUpdateTime = new Time();
		nextUpdateTime.set(nextUpdateTimeMillis);

		AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
		alarmManager.set(AlarmManager.RTC, nextUpdateTimeMillis, pendingIntent);
	}
}
