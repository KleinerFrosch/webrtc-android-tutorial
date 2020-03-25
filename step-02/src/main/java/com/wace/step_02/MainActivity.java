package com.wace.step_02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 申请权限
		requestPermissions();
		// 创建PeerConnectionFactory

	}

	private void requestPermissions() {
		List<String> permissionssList = new ArrayList<>();
		// 检查是否授权
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.INTERNET)
				!= PackageManager.PERMISSION_GRANTED) {
			permissionssList.add(Manifest.permission.INTERNET);
		}

		if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
				!= PackageManager.PERMISSION_GRANTED) {
			permissionssList.add(Manifest.permission.CAMERA);
		}
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
				!= PackageManager.PERMISSION_GRANTED) {
			permissionssList.add(Manifest.permission.RECORD_AUDIO);
		}
		if (!permissionssList.isEmpty()) {
			ActivityCompat.requestPermissions(this,
					permissionssList.toArray(new String[permissionssList.size()]), 10240);
		}
	}
}
