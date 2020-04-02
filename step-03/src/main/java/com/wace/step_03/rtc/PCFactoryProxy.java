package com.wace.step_03.rtc;

import android.content.Context;

import org.webrtc.PeerConnectionFactory;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;
import org.webrtc.audio.AudioDeviceModule;

/**
 * author：lwf
 * date：2020/4/1
 * description：
 */
public class PCFactoryProxy {

	private VideoEncoderFactory encoderFactory;
	private VideoDecoderFactory decoderFactory;
	private PeerConnectionFactory pcFactory;

	public PCFactoryProxy(Context context) {
		PeerConnectionFactory.InitializationOptions.Builder builder =
				PeerConnectionFactory.InitializationOptions.builder(context);
		builder.setEnableInternalTracer(true);
		PeerConnectionFactory.InitializationOptions initializationOptions =
				builder.createInitializationOptions();
		PeerConnectionFactory.initialize(initializationOptions);
		PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
		encoderFactory = new SoftwareVideoEncoderFactory();
		decoderFactory = new SoftwareVideoDecoderFactory();
		pcFactory = PeerConnectionFactory.builder()
				.setVideoEncoderFactory(encoderFactory)
				.setVideoDecoderFactory(decoderFactory)
				.setOptions(options)
				.createPeerConnectionFactory();
	}
}
