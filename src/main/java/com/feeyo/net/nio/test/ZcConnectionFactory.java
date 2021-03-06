package com.feeyo.net.nio.test;

import java.io.IOException;
import java.nio.channels.SocketChannel;

import com.feeyo.net.nio.ClosableConnection;
import com.feeyo.net.nio.ConnectionFactory;
import com.feeyo.net.nio.NetSystem;

public class ZcConnectionFactory extends ConnectionFactory {

	@Override
	public ClosableConnection make(SocketChannel channel) throws IOException {
		ZcConnection c = new ZcConnection(channel);
		NetSystem.getInstance().setSocketParams(c, true);	// 设置连接的参数
		c.setHandler( new ZcConnectionHandler() );	// 设置NIOHandler
		return c;
	}

}
