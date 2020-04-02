package com.wace.step_03.netty;

import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.FixedRecvByteBufAllocator;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.handler.timeout.IdleStateHandler;

/**
 * author：lwf
 * date：2020/4/1
 * description：
 */
public class UDPService {

	public void start() {
		Bootstrap bootstrap = new Bootstrap();
		EventLoopGroup loopGroup = new NioEventLoopGroup();
		bootstrap.group(loopGroup).channel(NioDatagramChannel.class)
				.option(ChannelOption.SO_BROADCAST, true)
				.option(ChannelOption.SO_RCVBUF, 1024*1024)
				.option(ChannelOption.RCVBUF_ALLOCATOR, new FixedRecvByteBufAllocator(65535))
				.handler(new ChannelInitializer<Channel>() {
					@Override
					protected void initChannel(Channel ch) throws Exception {
						ch.pipeline().addLast("heartbeat",
								new IdleStateHandler(15,
										10, 30,
										TimeUnit.SECONDS))
								.addLast("decoder", new StringDecoder())
								.addLast("encoder", new StringEncoder())
								.addLast(new MessageHandler());
					}
				});
	}
}
