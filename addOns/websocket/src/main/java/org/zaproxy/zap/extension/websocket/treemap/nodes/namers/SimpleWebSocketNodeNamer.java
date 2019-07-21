package org.zaproxy.zap.extension.websocket.treemap.nodes.namers;

import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.websocket.WebSocketChannelDTO;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketFolderNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketHandshakeNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketHostFolderNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketMessageNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketNodeUtilities;
import org.zaproxy.zap.extension.websocket.utility.InvalidUtf8Exception;

import java.util.Iterator;

public class SimpleWebSocketNodeNamer implements WebSocketNodeNamer{

    private Logger LOGGER = Logger.getLogger(SimpleWebSocketNodeNamer.class);
    
    public String getName(WebSocketHostFolderNode node){
        return node.getHostName();
    }

    @Override
    public String getName(WebSocketFolderNode node) {
        return null;
    }

    @Override
    public String getName(WebSocketMessageNode node) {
        try {
            return node.getWebSocketMessageDTO().getReadablePayload();
        } catch (InvalidUtf8Exception e) {
            return Constant.messages.getString("websocket.payload.invalid_utf8");
        }
    }

    @Override
    public String getName(WebSocketHandshakeNode node) {
        return null;
    }
}
