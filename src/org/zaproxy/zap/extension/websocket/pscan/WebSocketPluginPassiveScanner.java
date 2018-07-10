package org.zaproxy.zap.extension.websocket.pscan;

import org.zaproxy.zap.extension.pscan.PassiveScanner;
import org.zaproxy.zap.utils.Enableable;

public abstract class WebSocketPluginPassiveScanner extends Enableable implements PassiveScanner{
    public WebSocketPluginPassiveScanner(){
        super(true);
    }
    
    
}
