package org.zaproxy.zap.extension.websocket.pscan;

import org.zaproxy.zap.control.AddOn;
import org.zaproxy.zap.extension.pscan.PassiveScanner;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.utils.Enableable;

import javax.security.auth.login.Configuration;

public abstract class WebSocketPluginPassiveScanner extends Enableable implements WebSocketPassiveScanner{
    
    /**
     * The configuration key used to save/load the ID of a passive scanner.
     */
    private static final String ID_KEY = "id";
    
    /**
     * The configuration key used to load the classname of a passive scanner, used only for backwards compatibility.
     */
    private static final String CLASSNAME_KEY = "classname";
    
    /**
     * The configuration key used to save/load the alert threshold of a passive scanner.
     * <p>
     * To be replaced by {@link #ALERT_THRESHOLD_KEY}.
     */
    private static final String LEVEL_KEY = "level";
    
    /**
     * The configuration key used to save/load the alert threshold of a passive scanner.
     */
    private static final String ALERT_THRESHOLD_KEY = "alertthreshold";
    
    /**
     * The configuration key used to save/load the enabled state of a passive scanner.
     */
    private static final String ENABLED_KEY = "enabled";
    
    private Configuration config = null;
    
    private AddOn.Status status = AddOn.Status.unknown;
    
    public WebSocketPluginPassiveScanner(){
        super(true);
    }
    
    //TODO Take care about that. Maybe need to add more cases
    @Override
    public boolean equals(Object object){
        return getName().equals(((WebSocketPassiveScanner) object).getName());
    }
    
    @Override
    public int hashCode(){
        return 31 * (null == getName() ? 0 : getName().hashCode());
    }
    
    @Override
    public boolean applyScanToMessage(WebSocketMessageDTO webSocketMessageDTO){
        return true;
    }
    
}
