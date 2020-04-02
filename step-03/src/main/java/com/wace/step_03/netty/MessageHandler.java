package com.wace.step_03.netty;

import com.alibaba.fastjson.JSON;
import com.wace.step_03.entity.Message;
import com.wace.step_03.utils.Common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;

import static java.nio.charset.StandardCharsets.UTF_8;

/**
 * author：lwf
 * date：2020/4/1
 * description：
 */
public class MessageHandler extends ChannelHandlerAdapter {

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		super.channelRead(ctx, msg);
		DatagramPacket packet = (DatagramPacket) msg;
		ByteBuf buf = packet.copy().content();
		byte[] req = new byte[buf.readableBytes()];
		buf.readBytes(req);
		String msgContent = new String(req, UTF_8);
		Message message = JSON.parseObject(msgContent, Message.class);
		switch (message.getMethod()) {
			// 接收到offer消息，发送自身offer给对方
			case Common.TYPE_OFFER:
				if (!message.getDevId().equals(SessionManager.getInstance().getDevId())) {

				}
				break;
		}
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		super.channelActive(ctx);
		SessionManager.getInstance().setChannel(ctx.channel());
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		super.exceptionCaught(ctx, cause);
		System.err.println(cause.getMessage());
	}
}
