package com.scnu.yxp.travelapp.service;
import android.app.IntentService;
import android.content.Intent;
import android.util.Log;


public class SendService extends IntentService {

	public SendService(String name) {
		super(name);
	}

	@Override
	protected void onHandleIntent(Intent arg0) {
		Log.i("here","im service");
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
