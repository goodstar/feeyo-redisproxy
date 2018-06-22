package com.feeyo.kafka.net.backend;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.feeyo.net.nio.NIOHandler;
import com.feeyo.net.nio.NetFlowMonitor;
import com.feeyo.net.nio.util.StringUtil;
import com.feeyo.redis.net.front.RedisFrontConnection;

public class KafkaBackendConnectionHandler implements NIOHandler<KafkaBackendConnection> {

	private static Logger LOGGER = LoggerFactory.getLogger( KafkaBackendConnectionHandler.class );
	
	@Override
	public void onConnected(KafkaBackendConnection con) throws IOException {	
        // 已经连接成功
		con.getCallback().connectionAcquired( con );		
	}
	
	@Override
	public void handleReadEvent(KafkaBackendConnection con, byte[] data) throws IOException {
		
		// 日志HEX
		if ( LOGGER.isDebugEnabled() ) {
			final String hexs = StringUtil.dumpAsHex(data, 0, data.length);
			LOGGER.debug("C#{} backend response len = {},  buffer bytes\n {}", 
					new Object[]{ con.getId(), data.length, hexs });
		}
		
		// 回调
		con.getCallback().handleResponse(con, data);	
		return;	
	}	

	@Override
	public void onClosed(KafkaBackendConnection con, String reason) {
		if ( con.getCallback() != null )
			con.getCallback().connectionClose(con, reason);
	}

	@Override
	public void onConnectFailed(KafkaBackendConnection con, Exception e) {
		if ( con.getCallback() != null )
			con.getCallback().connectionError(e, con);		
	}

	@Override
	public boolean handleNetFlow(KafkaBackendConnection con, int dataLength) throws IOException {
		if (con.getAttachement() instanceof RedisFrontConnection) {
			RedisFrontConnection rfc = (RedisFrontConnection) con.getAttachement();
			NetFlowMonitor nfm = con.getNetFlowMonitor();
			if (nfm != null)
				return nfm.pool(rfc.getPassword(), dataLength);
		}
		return false;
	}	

}