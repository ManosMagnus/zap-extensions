package org.zaproxy.zap.extension.websocket.treemap.nodes;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.websocket.WebSocketChannelDTO;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.treemap.nodes.namers.WebSocketNodeNamer;

import java.util.Iterator;

public class WebSocketMessageNode extends TreeNode implements WebSocketTreeNode{

    private WebSocketMessageDTO webSocketMessage;

    public WebSocketMessageNode(WebSocketMessageDTO webSocketMessage){
        this.webSocketMessage = webSocketMessage;
    }

    public WebSocketMessageDTO getWebSocketMessageDTO(){
        return webSocketMessage;
    }

    public String getName(WebSocketNodeNamer namer){
        return namer.getName(this);
    }
    
    @Override
    public Iterator<HttpMessage> getHandshakes() {
        return null;
    }
    
    @Override
    public Iterator<WebSocketMessageDTO> getMessages() {
        return null;
    }
    
    @Override
    public Iterator<WebSocketChannelDTO> getChannels() {
        return null;
    }

    @Override
    public void setName(WebSocketNodeNamer namer) {

    }
}
