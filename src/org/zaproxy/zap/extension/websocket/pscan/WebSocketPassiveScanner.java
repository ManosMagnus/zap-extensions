package org.zaproxy.zap.extension.websocket.pscan;

import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;

public interface WebSocketPassiveScanner {
    
    String getName();
    boolean applyScanToMessage(WebSocketMessageDTO webSocketMessageDTO);
    boolean isEnable();
    boolean setParent(WebSocketPassiveScanThread webSocketPassiveScanThread);
    void scanMessage(WebSocketMessageDTO webSocketMessage);
    void setEnabled (boolean enabled);
}

