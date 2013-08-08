package com.shnu.lbsshnu;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

/**
 * ���ڿ�������LbsService
 */
public class BootReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		context.startService(new Intent(context, LbsService.class));
		Log.d("BootReceiver", "onReceive");
	}
}
