package org.zaproxy.zap.extension.websocket.treemap.nodes;

import org.zaproxy.zap.extension.websocket.treemap.WebSocketMap;
import org.zaproxy.zap.extension.websocket.treemap.WebSocketNodeTypes;

public class WebSocketHandshakeNode extends WebSocketTreeNode {
    
    private static final long serialVersionUID = 2311091007687312322L;
    
    public WebSocketHandshakeNode(WebSocketMap webSocketMap, WebSocketNodeTypes type, String nodeName) {
        super(webSocketMap, type, nodeName);
    }
}
