package org.zaproxy.zap.extension.websocket.treemap.nodes.namers;

import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketFolderNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketHandshakeNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketHostFolderNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketMessageNode;

public interface WebSocketNodeNamer {
    String getName(WebSocketFolderNode node);
    String getName(WebSocketMessageNode node);
    String getName(WebSocketHostFolderNode node);
    String getName(WebSocketHandshakeNode node);
}
