package org.zaproxy.zap.extension.websocket.treemap.nodes.factors;

import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.WebSocketProxy;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketFolderNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketHostFolderNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketTreeNode;

public interface WebSocketNodeFactor {
    WebSocketTreeNode getTreeNode(WebSocketFolderNode<WebSocketHostFolderNode> root, WebSocketMessageDTO webSocketMessage);
    WebSocketTreeNode getTreeNode(WebSocketFolderNode<WebSocketHostFolderNode> root, WebSocketProxy proxy);
}
