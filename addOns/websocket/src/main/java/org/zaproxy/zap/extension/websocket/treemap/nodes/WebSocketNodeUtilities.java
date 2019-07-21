package org.zaproxy.zap.extension.websocket.treemap.nodes;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.websocket.WebSocketChannelDTO;

public class WebSocketNodeUtilities {
	
	public static String getHostName(HttpMessage handshakeMessage, WebSocketChannelDTO channel){
		StringBuilder host = new StringBuilder();
		
		int port = channel.port != -1 ? channel.port : handshakeMessage.getRequestHeader().getURI().getPort();
		String scheme;
		if (port == 443 || handshakeMessage.getRequestHeader().isSecure()) {
			scheme = "wss";
		} else {
			scheme = "ws";
		}
		host.append(scheme).append("://").append(channel.host);
		
		if ((port != 80 && port != 443)) {
			host.append(":").append(port);
		}
		
		return host.toString();
	}
}
