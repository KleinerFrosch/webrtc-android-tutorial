package com.wace.step_03.rtc.participant;

import android.content.Context;
import android.os.Build;

import com.wace.step_03.rtc.observer.CustomPeerConnectionObserver;
import com.wace.step_03.rtc.observer.CustomSdpObserver;

import org.webrtc.AudioSource;
import org.webrtc.Camera1Enumerator;
import org.webrtc.Camera2Enumerator;
import org.webrtc.CameraEnumerator;
import org.webrtc.EglBase;
import org.webrtc.IceCandidate;
import org.webrtc.MediaConstraints;
import org.webrtc.MediaStream;
import org.webrtc.PeerConnection;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceTextureHelper;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;
import org.webrtc.VideoSource;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * author：lwf
 * date：2020/4/1
 * description：
 */
public class LocalParticipant extends Participant {

	private SurfaceViewRenderer rendererLocal;
	private VideoCapturer videoCapturer;
	private Map<String, RemoteParticipant> remoteParticipantMap = new LinkedHashMap<>();
	private SurfaceTextureHelper textureHelper;

	public LocalParticipant(String connectionId, String participantName, Context context) {
		super(connectionId, participantName, context);
	}

	public void startCamera(int w, int h, int fps) {
		EglBase.Context eglBaseContext = EglBase.create().getEglBaseContext();
		// create audioTrack
		AudioSource audioSource = pcFactoryProxy.getPcFactory()
				.createAudioSource(new MediaConstraints());
		audioTrack = pcFactoryProxy.getPcFactory().createAudioTrack("10241", audioSource);
		// create Capturer
		textureHelper = SurfaceTextureHelper
				.create("local_capture_thread", eglBaseContext);
		videoCapturer = createCameraCapturer();
		// create VideoTrack
		VideoSource videoSource = pcFactoryProxy.getPcFactory()
				.createVideoSource(videoCapturer.isScreencast());
		videoCapturer.initialize(textureHelper, context, videoSource.getCapturerObserver());
		videoCapturer.startCapture(w, h, fps);
		videoTrack = pcFactoryProxy.getPcFactory().createVideoTrack("10242", videoSource);
		// display
		videoTrack.addSink(rendererLocal);

	}

	private VideoCapturer createCameraCapturer() {
		VideoCapturer videoCapturer;
		CameraEnumerator enumerator;
		if (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP) {
			enumerator = new Camera2Enumerator(context);
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
		// 找不到，获取其他摄像头
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

	public PeerConnection createLocalPeerConnection() {
		List<PeerConnection.IceServer> iceServers = new ArrayList<>();
		PeerConnection.IceServer iceServer = PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer();
		iceServers.add(iceServer);

		return pcFactoryProxy.getPcFactory().createPeerConnection(iceServers,
				new CustomPeerConnectionObserver("local_observer") {
					@Override
					public void onIceCandidate(IceCandidate iceCandidate) {
						super.onIceCandidate(iceCandidate);
						// TODO: 2020/4/2 发送ice
					}
				});
	}

	public void createRemotePeerConnection(String connectionId) {
		List<PeerConnection.IceServer> iceServers = new ArrayList<>();
		PeerConnection.IceServer iceServer = PeerConnection.IceServer.builder("stun:stun.l.google.com:19302").createIceServer();
		iceServers.add(iceServer);

		PeerConnection peerConnection = pcFactoryProxy.getPcFactory().createPeerConnection(iceServers,
				new CustomPeerConnectionObserver("remote_observer") {
					@Override
					public void onIceCandidate(IceCandidate iceCandidate) {
						super.onIceCandidate(iceCandidate);
						// TODO: 2020/4/3 发送ice
					}

					@Override
					public void onAddStream(MediaStream mediaStream) {
						super.onAddStream(mediaStream);
						// TODO: 2020/4/3 渲染媒体流
					}

					@Override
					public void onSignalingChange(PeerConnection.SignalingState signalingState) {
						super.onSignalingChange(signalingState);
						// TODO: 2020/4/3 移除远端流
					}
				});

		MediaStream mediaStream = pcFactoryProxy.getPcFactory().createLocalMediaStream("10240");
		mediaStream.addTrack(audioTrack);
		mediaStream.addTrack(videoTrack);
		peerConnection.addStream(mediaStream);

		remoteParticipantMap.get(connectionId).setPeerConnection(peerConnection);
	}

	public void createLocalOffer(MediaConstraints constraints){
		peerConnection.createOffer(new CustomSdpObserver("local_observer") {
			@Override
			public void onCreateSuccess(SessionDescription sessionDescription) {
				super.onCreateSuccess(sessionDescription);
				peerConnection.setLocalDescription(new CustomSdpObserver("set_local_sdp"),
						sessionDescription);
				// TODO: 2020/4/3 setLocalDescription并发送offer
			}
		}, constraints);
	}

	public void addRemoteParticipant(RemoteParticipant remoteParticipant) {
		remoteParticipantMap.put(remoteParticipant.getConnectionId(), remoteParticipant);
	}

	public RemoteParticipant getRemoteParticipant(String connectionId) {
		return remoteParticipantMap.get(connectionId);
	}

	@Override
	public void dispose() {
		super.dispose();
		if (videoTrack != null) {
			videoTrack.removeSink(rendererLocal);
			videoCapturer.dispose();
			videoCapturer = null;
		}
		if (textureHelper != null) {
			textureHelper.dispose();
			textureHelper = null;
		}
	}
}
