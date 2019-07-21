package org.zaproxy.zap.extension.websocket.treemap;

import org.zaproxy.zap.extension.websocket.WebSocketMessage;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.WebSocketObserver;
import org.zaproxy.zap.extension.websocket.WebSocketProxy;

public class WebSocketMap implements WebSocketObserver {

    private static final int OBSERVER_ORDER = 30;

    /**
     * Adding a WebSocket Message in the Tree Map.
     */
    protected synchronized void addMessage(WebSocketMessageDTO webSocketMessage){

    }

    protected synchronized void addConnection(WebSocketProxy webSocketProxy){

    }

    @Override
    public int getObservingOrder() {
        return OBSERVER_ORDER;
    }

    @Override
    public boolean onMessageFrame(int channelId, WebSocketMessage message) {
        addMessage(message.getDTO());
        return true; //TODO: I should look at
    }

    @Override
    public void onStateChange(WebSocketProxy.State state, WebSocketProxy proxy) {
        if( state == WebSocketProxy.State.CONNECTING){
            addConnection(proxy);
        }

    }
}
