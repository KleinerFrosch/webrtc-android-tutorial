package com.wace.step_03.netty;

import android.util.Log;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.channel.Channel;
import io.netty.channel.socket.DatagramPacket;

/**
 * author：lwf
 * date：2020/4/1
 * description：
 */
public class SessionManager {

	private Channel channel;
	private String devId;

	private SessionManager(){}

	private static class SessionManagerHolder {
		private static final SessionManager INSTANCE = new SessionManager();
	}

	public static SessionManager getInstance() {
		return SessionManagerHolder.INSTANCE;
	}

	public void setChannel(Channel channel) {
		this.channel = channel;
	}

	public void writeAndFlush(DatagramPacket msg) {
		if (channel == null) return;
		ByteBuf byteBuf = msg.copy().content();
		byte[] req = new byte[byteBuf.readableBytes()];
		byteBuf.readBytes(req);
		String str = new String(req, StandardCharsets.UTF_8);
		Log.i("msg send", str);
		channel.writeAndFlush(msg);
	}

	public void write(Object msg) {
		if (channel == null) return;
		channel.write(msg);
	}

	public String getDevId() {
		return devId;
	}

	public void setDevId(String devId) {
		this.devId = devId;
	}
}
