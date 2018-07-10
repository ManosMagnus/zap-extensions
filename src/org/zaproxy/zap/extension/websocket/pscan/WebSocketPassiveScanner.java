package org.zaproxy.zap.extension.websocket.pscan;

import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;

public interface WebSocketPassiveScanner {
    boolean isThatMessageForScan(WebSocketMessageDTO webSocketMessageDTO);
    boolean isEnable();
    boolean setParent(WebSocketPassiveScanThread webSocketPassiveScanThread);
}
