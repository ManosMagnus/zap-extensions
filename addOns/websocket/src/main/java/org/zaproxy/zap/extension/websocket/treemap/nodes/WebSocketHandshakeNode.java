package org.zaproxy.zap.extension.websocket.treemap.nodes;

import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.websocket.WebSocketChannelDTO;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.treemap.nodes.namers.WebSocketNodeNamer;

import java.util.Iterator;

public class WebSocketHandshakeNode extends TreeNode implements WebSocketTreeNode{
    
    private HistoryReference handshakeReference;
    private WebSocketChannelDTO channel;
    
    public WebSocketHandshakeNode(HistoryReference handshakeReference, WebSocketChannelDTO channel){
        this.handshakeReference = handshakeReference;
        this.channel = channel;
    }
    
    
    public HistoryReference getHandshakeHistoryReference(){
        return handshakeReference;
    }
    
    public HttpMessage getHandshakeMessage() {
        try {
            return getHandshakeHistoryReference().getHttpMessage();
        } catch (HttpMalformedHeaderException e) {
            e.printStackTrace();
        } catch (DatabaseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public  WebSocketChannelDTO getWebSocketChannelDTO(){
        return channel;
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
        name = namer.getName(this);
    }
}
