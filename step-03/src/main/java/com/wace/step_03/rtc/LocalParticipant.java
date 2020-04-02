package com.wace.step_03.rtc;

import org.webrtc.EglBase;
import org.webrtc.SessionDescription;
import org.webrtc.SurfaceViewRenderer;
import org.webrtc.VideoCapturer;

/**
 * author：lwf
 * date：2020/4/1
 * description：
 */
public class LocalParticipant extends Participant {

	private SurfaceViewRenderer rendererLocal;
	private VideoCapturer videoCapturer;
	private SessionDescription sdpLocal;

	public void startCamera() {
		EglBase.Context context = EglBase.create().getEglBaseContext();

	}

}
