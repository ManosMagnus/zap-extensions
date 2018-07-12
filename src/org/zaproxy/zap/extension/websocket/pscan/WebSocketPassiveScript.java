package org.zaproxy.zap.extension.websocket.pscan;

import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;

import javax.script.ScriptException;

public interface WebSocketPassiveScript {
    
    void scan(ScriptsWebSocketPassiveScanner scriptsPassiveScanner,
              WebSocketMessageDTO msg) throws ScriptException;
    
    /**
     * By Default Applies to all Messages
     * @param webSocketMessage
     * @return
     */
    default boolean appliesScanToMessage(WebSocketMessageDTO webSocketMessage){
        return true;
    }
}
