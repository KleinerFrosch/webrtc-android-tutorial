package com.wace.step_03.rtc;

import android.content.Context;

import org.webrtc.MediaConstraints;
import org.webrtc.PeerConnectionFactory;
import org.webrtc.SoftwareVideoDecoderFactory;
import org.webrtc.SoftwareVideoEncoderFactory;
import org.webrtc.VideoDecoderFactory;
import org.webrtc.VideoEncoderFactory;

/**
 * author：lwf
 * date：2020/4/1
 * description：PeerConnectionFactory代理
 */
public class PCFactoryProxy {

	private PeerConnectionFactory pcFactory;

	public PCFactoryProxy(Context context) {
		PeerConnectionFactory.InitializationOptions.Builder builder =
				PeerConnectionFactory.InitializationOptions.builder(context);
		builder.setEnableInternalTracer(true);
		PeerConnectionFactory.InitializationOptions initializationOptions =
				builder.createInitializationOptions();
		PeerConnectionFactory.initialize(initializationOptions);
		PeerConnectionFactory.Options options = new PeerConnectionFactory.Options();
		VideoEncoderFactory encoderFactory = new SoftwareVideoEncoderFactory();
		VideoDecoderFactory decoderFactory = new SoftwareVideoDecoderFactory();
		pcFactory = PeerConnectionFactory.builder()
				.setVideoEncoderFactory(encoderFactory)
				.setVideoDecoderFactory(decoderFactory)
				.setOptions(options)
				.createPeerConnectionFactory();
	}

	public PeerConnectionFactory getPcFactory() {
		return pcFactory;
	}

}
