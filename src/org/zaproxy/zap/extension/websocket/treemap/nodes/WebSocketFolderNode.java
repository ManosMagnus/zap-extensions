package org.zaproxy.zap.extension.websocket.treemap.nodes;

import org.parosproxy.paros.Constant;
import org.zaproxy.zap.extension.websocket.treemap.WebSocketMap;
import org.zaproxy.zap.extension.websocket.treemap.WebSocketNodeType;

public class WebSocketFolderNode extends WebSocketTreeNode {
    private static final long serialVersionUID = 2311091007687312333L;
    
    public WebSocketFolderNode(WebSocketMap webSocketMap, WebSocketNodeType type, String nodeName) {
        super(webSocketMap, type, nodeName);
    }

    static public WebSocketFolderNode getRootNode(WebSocketMap  webSocketMap){
        return new WebSocketFolderNode(webSocketMap, WebSocketNodeType.FOLDER_ROOT, Constant.messages.getString("websocket.treemap.root"));
    }
}
