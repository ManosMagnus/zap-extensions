package org.zaproxy.zap.extension.websocket.treemap.nodes;

import org.parosproxy.paros.Constant;
import org.zaproxy.zap.extension.websocket.treemap.WebSocketMap;
import org.zaproxy.zap.extension.websocket.treemap.WebSocketNodeTypes;

public class WebSocketFolderNode extends WebSocketTreeNode {
    private static final long serialVersionUID = 2311091007687312333L;
    
    public WebSocketFolderNode(WebSocketMap webSocketMap, WebSocketNodeTypes type, String nodeName) {
        super(webSocketMap, type, nodeName);
    }
    
    static public WebSocketTreeNode getRootNode(WebSocketMap  webSocketMap){
        return new WebSocketTreeNode(webSocketMap, WebSocketNodeTypes.ROOT, Constant.messages.getString("websocket.treemap.root"));
    }
}
