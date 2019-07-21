package org.zaproxy.zap.extension.websocket.treemap.nodes;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.websocket.WebSocketChannelDTO;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.treemap.nodes.namers.WebSocketNodeNamer;

import java.util.Iterator;

public interface WebSocketTreeNode {
	
	Iterator<HttpMessage> getHandshakes();
	Iterator<WebSocketMessageDTO> getMessages();
	Iterator<WebSocketChannelDTO> getChannels();
	
	void setName(WebSocketNodeNamer namer);
}
