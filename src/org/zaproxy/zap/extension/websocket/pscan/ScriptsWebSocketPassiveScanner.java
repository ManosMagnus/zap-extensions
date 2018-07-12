package org.zaproxy.zap.extension.websocket.pscan;

import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.control.Control;
import org.zaproxy.zap.extension.script.ExtensionScript;
import org.zaproxy.zap.extension.script.ScriptWrapper;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;

import java.lang.reflect.UndeclaredThrowableException;
import java.util.List;

public class ScriptsWebSocketPassiveScanner extends WebSocketPluginPassiveScanner{
    
    private static final Logger LOGGER = Logger.getLogger(ScriptsWebSocketPassiveScanner.class);
    
    private ExtensionScript extension = null;
    
    public ScriptsWebSocketPassiveScanner(){
    
    }
    
    private ExtensionScript getExtension() {
        if (extension == null) {
            extension = Control.getSingleton().getExtensionLoader().getExtension(ExtensionScript.class);
        }
        return extension;
    }
    
    @Override
    public void scanMessage(WebSocketMessageDTO webSocketMessage) {
        if(getExtension() != null){
            List<ScriptWrapper> scriptWrappers =
                    extension.getScripts(WebSocketPassiveScannerManager.SCRIPT_TYPE_WEBSOCKET_PASSIVE);
            for(ScriptWrapper scriptWrapper : scriptWrappers){
                if(scriptWrapper.isEnabled()){
                    try {
                        WebSocketPassiveScript webSocketPassiveScript =
                                extension.getInterface(scriptWrapper, WebSocketPassiveScript.class);
                        
                        if(webSocketPassiveScript != null){
                            if(appliesToCurrentMessage(scriptWrapper,webSocketPassiveScript,webSocketMessage)){
                                webSocketPassiveScript.scan(this,webSocketMessage);
                            }
                        }else{
                            extension.handleFailedScriptInterface(
                                    scriptWrapper,
                                    Constant.messages.getString("websocket.pscan.scripts.interface.passive.error",
                                            scriptWrapper.getName()));
                        }
                        
                    } catch (Exception e) {
                        extension.handleScriptException(scriptWrapper,e);
                    }
    
                }
            }
        }
        
    }
    
    private boolean appliesToCurrentMessage(ScriptWrapper scriptWrapper, WebSocketPassiveScript webSocketPassiveScript,
                                            WebSocketMessageDTO webSocketMessage){
    
        try {
            return webSocketPassiveScript.appliesScanToMessage(webSocketMessage);
        } catch (UndeclaredThrowableException e) {
            // Python script implementation throws an exception if this optional/default method is not
            // actually implemented by the script (other script implementations, Zest/ECMAScript, just
            // use the default method).
            if (e.getCause() instanceof NoSuchMethodException && "appliesToHistoryType".equals(e.getCause().getMessage())) {
                if (LOGGER.isDebugEnabled()) {
                    LOGGER.debug("Script [Name=" + LOGGER.getName() + ", Engine=" + scriptWrapper.getEngineName()
                            + "]  does not implement the optional method appliesToHistoryType: ", e);
                }
                return super.applyScanToMessage(webSocketMessage);
            }
            throw e;
        }
        
    }
    
    @Override
    public String getName() { return "ScriptPassive Scan"; } //TODO Just For Testing reasons, make it more formal
    
    @Override
    public boolean applyScanToMessage(WebSocketMessageDTO webSocketMessageDTO) {
        return false;
    }
    
    @Override
    public boolean isEnable() {
        return false;
    }
    
    @Override
    public boolean setParent(WebSocketPassiveScanThread webSocketPassiveScanThread) {
        return false;
    }
    
}
