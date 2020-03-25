package com.wace.webrtc_android_tutorial;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.SurfaceView;
import android.widget.Toast;

import org.webrtc.AudioSource;
import org.webrtc.AudioTrack;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

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
		PeerConnectionFactory.InitializationOptions initializationOptions =
				PeerConnectionFactory.InitializationOptions.builder(this)
						.createInitializationOptions();
		PeerConnectionFactory.initialize(initializationOptions);
		PeerConnectionFactory peerConnectionFactory =
				PeerConnectionFactory.builder().createPeerConnectionFactory();

		// 创建AudioSource
		AudioSource audioSource = peerConnectionFactory.createAudioSource(new MediaConstraints());
		AudioTrack audioTrack = peerConnectionFactory.createAudioTrack("10241", audioSource);

		// 创建SurfaceTextureHelper
		EglBase.Context eglBaseContext = EglBase.create().getEglBaseContext();
		SurfaceTextureHelper helper = SurfaceTextureHelper.create("capture", eglBaseContext);
		// 初始化renderer
		SurfaceViewRenderer localRenderer = findViewById(R.id.local_renderer);
		localRenderer.init(eglBaseContext, null);
		// 创建VideoCapturer
		VideoCapturer videoCapturer =  createCameraCapturer();
		VideoSource videoSource = peerConnectionFactory.createVideoSource(videoCapturer.isScreencast());
		videoCapturer.initialize(helper, getApplicationContext(), videoSource.getCapturerObserver());
		videoCapturer.startCapture(480, 640, 30);
		// 创建VideoTrack
		VideoTrack videoTrack = peerConnectionFactory.createVideoTrack("10241", videoSource);
		// 显示
		videoTrack.addSink(localRenderer);
	}

	private VideoCapturer createCameraCapturer() {
		VideoCapturer videoCapturer;
		CameraEnumerator enumerator;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
			enumerator = new Camera2Enumerator(this);
		} else {
			enumerator = new Camera1Enumerator(false);
		}
		final String[] deviceNames = enumerator.getDeviceNames();

		// 尝试获取前置摄像头
		for (String deviceName : deviceNames) {
			if (enumerator.isFrontFacing(deviceName)) {
				videoCapturer = enumerator.createCapturer(deviceName, null);
				if (videoCapturer != null) {
					return videoCapturer;
				}
			}
		}
		// 前置摄像头找不到，寻找其他摄像头
		for (String deviceName : deviceNames) {
			if (!enumerator.isFrontFacing(deviceName)) {
				videoCapturer = enumerator.createCapturer(deviceName, null);
				if (videoCapturer != null) {
					return videoCapturer;
				}
			}
		}
		return null;
	}

	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		super.onRequestPermissionsResult(requestCode, permissions, grantResults);
		if (requestCode==10240 && grantResults.length>0) {
			for (int result : grantResults) {
				if (result==PackageManager.PERMISSION_DENIED) {
					Toast.makeText(this,
							"部分权限被拒绝，可能会导致应用异常退出",
							Toast.LENGTH_LONG).show();
					return;
				}
			}
		}
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

