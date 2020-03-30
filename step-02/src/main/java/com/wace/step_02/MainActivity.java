package com.wace.step_02;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;

import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.DefaultVideoDecoderFactory;
import org.webrtc.DefaultVideoEncoderFactory;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;
import org.webrtc.VideoTrack;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

	private PeerConnectionFactory peerConnectionFactory;
	private PeerConnection localPeerConnection;
	private PeerConnection remotePeerConnection;
	private SurfaceViewRenderer localRenderer;
	private SurfaceViewRenderer remoteRenderer;
	private MediaStream localStream;
	private MediaStream remoteStream;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// 申请权限
		requestPermissions();
		// 创建context
		EglBase.Context context = EglBase.create().getEglBaseContext();
		// 创建PeerConnectionFactory
		PeerConnectionFactory.InitializationOptions initializationOptions =
				PeerConnectionFactory.InitializationOptions.builder(this)
						.createInitializationOptions();
		PeerConnectionFactory.initialize(initializationOptions);
		PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
		// 编解码
		DefaultVideoDecoderFactory decoderFactory =
				new DefaultVideoDecoderFactory(context);
		DefaultVideoEncoderFactory encoderFactory =
				new DefaultVideoEncoderFactory(context,
						false,
						true);
		peerConnectionFactory = PeerConnectionFactory.builder()
				.setOptions(options)
				.setVideoDecoderFactory(decoderFactory)
				.setVideoEncoderFactory(encoderFactory)
				.createPeerConnectionFactory();
		// 创建SurfaceTextureHelper
		SurfaceTextureHelper localHelper = SurfaceTextureHelper.create("local_capture", context);
		// 创建VideoCapturer,本地用前置摄像头
		VideoCapturer localCapturer = createCameraCapturer(Camera.CameraInfo.CAMERA_FACING_FRONT);
		VideoSource localSource = peerConnectionFactory.createVideoSource(localCapturer.isScreencast());
		localCapturer.initialize(localHelper, this, localSource.getCapturerObserver());
		localCapturer.startCapture(720, 1280, 30);
		// 创建local renderer
		localRenderer = findViewById(R.id.local_renderer);
		localRenderer.init(context, null);
		// 创建VideoTrack
		VideoTrack localTrack = peerConnectionFactory.createVideoTrack("local", localSource);

		// remote的SurfaceTextureHelper
		SurfaceTextureHelper remoteHelper = SurfaceTextureHelper.create("remote_capture", context);
		// remote VideoCapturer（这里用后置摄像头）
		VideoCapturer remoteCapturer = createCameraCapturer(Camera.CameraInfo.CAMERA_FACING_BACK);
		VideoSource remoteSource = peerConnectionFactory.createVideoSource(remoteCapturer.isScreencast());
		remoteCapturer.initialize(remoteHelper, this, remoteSource.getCapturerObserver());
		remoteCapturer.startCapture(720, 1280, 30);
		// remote renderer
		remoteRenderer = findViewById(R.id.remote_renderer);
		remoteRenderer.init(context, null);
		// remote VideoTrack
		VideoTrack remoteTrack = peerConnectionFactory.createVideoTrack("remote", remoteSource);

		localStream = peerConnectionFactory.createLocalMediaStream("local_stream");
		localStream.addTrack(localTrack);
		// 渲染自身本地流
		localTrack.addSink(localRenderer);

		remoteStream = peerConnectionFactory.createLocalMediaStream("remote_stream");
		remoteStream.addTrack(remoteTrack);
		// 渲染自身本地流
		remoteTrack.addSink(remoteRenderer);

		findViewById(R.id.btn_start_call).setOnClickListener(v -> {
			localTrack.removeSink(localRenderer);
			remoteTrack.removeSink(remoteRenderer);
			startCall();
		});
	}

	/**
	 * 模拟呼叫，前置作为local，后置作为remote，将彼此的sdp信息发送
	 */
	private void startCall() {
		List<PeerConnection.IceServer> iceServerList = new ArrayList<>();
		localPeerConnection = peerConnectionFactory.createPeerConnection(iceServerList,
				new CustomPeerConnectionObserver("local_observer") {
					@Override
					public void onIceCandidate(IceCandidate iceCandidate) {
						super.onIceCandidate(iceCandidate);
						// 3. local发送ice给到了remote，remote保存下来
						remotePeerConnection.addIceCandidate(iceCandidate);
					}

					@Override
					public void onAddStream(MediaStream mediaStream) {
						super.onAddStream(mediaStream);
						// 5. local获取到remote的媒体流，渲染
						runOnUiThread(() -> {
							VideoTrack remoteTrack = mediaStream.videoTracks.get(0);
							remoteTrack.addSink(localRenderer);
						});
					}
				});
		localPeerConnection.addStream(localStream);

		remotePeerConnection = peerConnectionFactory.createPeerConnection(iceServerList,
				new CustomPeerConnectionObserver("remote_observer"){
					@Override
					public void onIceCandidate(IceCandidate iceCandidate) {
						super.onIceCandidate(iceCandidate);
						// 4. remote发送ice给到了local， local保存下来
						localPeerConnection.addIceCandidate(iceCandidate);
					}

					@Override
					public void onAddStream(MediaStream mediaStream) {
						super.onAddStream(mediaStream);
						// 5. remote获取到local的媒体流，渲染
						runOnUiThread(() -> {
							VideoTrack localTrack = mediaStream.videoTracks.get(0);
							localTrack.addSink(remoteRenderer);
						});

					}
				});

		// 1.创建offer
		localPeerConnection.createOffer(new CustomSdpObserver("local create offer") {
			@Override
			public void onCreateSuccess(SessionDescription sessionDescription) {
				super.onCreateSuccess(sessionDescription);
				// 1.1 创建后，假定发送了offer并且remote收到并回复
				// 1.2 local保存自己的sdp
				localPeerConnection.setLocalDescription(new CustomSdpObserver("local set self sdp"), sessionDescription);
				// 1.3 remote add stream
				remotePeerConnection.addStream(remoteStream);
				// 1.4 remote 保存local的sdp
				remotePeerConnection.setRemoteDescription(new CustomSdpObserver("remote set local_sdp"), sessionDescription);

				// 2.创建answer回复
				remotePeerConnection.createAnswer(new CustomSdpObserver("remote create answer") {
					@Override
					public void onCreateSuccess(SessionDescription sessionDescription) {
						super.onCreateSuccess(sessionDescription);
						// 2.1 remote保存自己的sdp
						remotePeerConnection.setLocalDescription(new CustomSdpObserver("remote set self sdp"), sessionDescription);
						// 2.2 local收到answer，保存remote的sdp
						localPeerConnection.setRemoteDescription(new CustomSdpObserver("local set remote_sdp"), sessionDescription);
					}
				}, new MediaConstraints());
			}
		}, new MediaConstraints());

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

	private VideoCapturer createCameraCapturer(int cameraType) {
		VideoCapturer videoCapturer;
		CameraEnumerator enumerator;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
			enumerator = new Camera2Enumerator(this);
		} else {
			enumerator = new Camera1Enumerator(false);
		}
		final String[] deviceNames = enumerator.getDeviceNames();

		if (cameraType == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			// 尝试获取前置摄像头
			for (String deviceName : deviceNames) {
				if (enumerator.isFrontFacing(deviceName)) {
					videoCapturer = enumerator.createCapturer(deviceName, null);
					if (videoCapturer != null) {
						return videoCapturer;
					}
				}
			}
		} else {
			for (String deviceName : deviceNames) {
				if (!enumerator.isFrontFacing(deviceName)) {
					videoCapturer = enumerator.createCapturer(deviceName, null);
					if (videoCapturer != null) {
						return videoCapturer;
					}
				}
			}
		}
		return null;
	}
}
