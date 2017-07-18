package com.testjni2;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class MyBreakReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		if (isOrderedBroadcast()) {
			return;
		} else {
			Intent intent1 = new Intent(context, FileService.class);
			context.startService(intent1);
		}
	}
}
