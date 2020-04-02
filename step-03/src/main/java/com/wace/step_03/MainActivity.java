package com.wace.step_03;

import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;

import com.wace.step_03.netty.SessionManager;
import com.wace.step_03.utils.Common;
import com.wace.step_03.utils.SharedPreferencesUtil;

import java.util.UUID;

public class MainActivity extends AppCompatActivity {

	private String devId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		SharedPreferencesUtil preferencesUtil = SharedPreferencesUtil.getInstance(this);
		devId = preferencesUtil.getString(Common.devId, null);
		if (devId == null) {
			devId = UUID.randomUUID().toString().replace("-", "");
			preferencesUtil.putString(Common.devId, devId);
		}
		SessionManager.getInstance().setDevId(devId);
	}
}
