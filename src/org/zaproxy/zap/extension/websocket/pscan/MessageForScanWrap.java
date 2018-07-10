package org.zaproxy.zap.extension.websocket.pscan;

import org.zaproxy.zap.extension.websocket.WebSocketMessage;
import org.zaproxy.zap.extension.websocket.WebSocketProxy;

public class MessageForScanWrap {
    public WebSocketMessage webSocketMessage;
    public int channelId;
    public WebSocketProxy.Initiator proxyInitiator;
    
    MessageForScanWrap(WebSocketMessage webSocketMessage, int channelId, WebSocketProxy.Initiator proxyInitiator){
        this.webSocketMessage = webSocketMessage;
        this.channelId = channelId;
        this.proxyInitiator = proxyInitiator;
    }
}
