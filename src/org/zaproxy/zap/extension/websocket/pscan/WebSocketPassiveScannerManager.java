package org.zaproxy.zap.extension.websocket.pscan;

import org.apache.log4j.Logger;
import org.parosproxy.paros.control.Control;
import org.parosproxy.paros.extension.Extension;
import org.parosproxy.paros.extension.SessionChangedListener;
import org.parosproxy.paros.model.Session;
import org.zaproxy.zap.extension.alert.ExtensionAlert;
import org.zaproxy.zap.extension.pscan.PassiveScanner;
import org.zaproxy.zap.extension.pscan.PluginPassiveScanner;
import org.zaproxy.zap.extension.script.ExtensionScript;
import org.zaproxy.zap.extension.script.ScriptType;
import org.zaproxy.zap.extension.websocket.db.TableWebSocket;

import javax.swing.ImageIcon;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class WebSocketPassiveScannerManager implements SessionChangedListener{
    
    private static final Logger LOGGER = Logger.getLogger(WebSocketPassiveScannerManager.class);
    
    public static final String SCRIPT_TYPE_WEBSOCKET_PASSIVE = "websocket_passive";
    
    private WebSocketPassiveScannerList passiveScannerList;
    private TableWebSocket tableWebSocket = null;
    private WebSocketPassiveScanThread passiveScanThread = null;
    private boolean passiveScanEnabled;
    private ExtensionScript passiveWebSocketScript = null;
    
    private boolean uiRelated = true; //TODO should be removed
    
    private static final List<Class<? extends Extension>> DEPENDENCIES;
    
    public WebSocketPassiveScannerManager(){
        passiveScannerList = new WebSocketPassiveScannerList();
        passiveScanThread = new WebSocketPassiveScanThread(this);
        
        passiveScanEnabled = true;
        
        registerNewScriptType();
    }
    
    
    static {
        List<Class<? extends Extension>> dep = new ArrayList<>(1);
        dep.add(ExtensionAlert.class);
        
        DEPENDENCIES = Collections.unmodifiableList(dep);
    }
    
    public WebSocketPassiveScanThread getWebSocketPassiveScanThread() {
        return passiveScanThread;
    }
    
    public void startWebSocketPassiveScanThread(){
        passiveScanThread.start();
        
    }
    
    /**
     * TODO UI Related
     * @return
     */
    public ExtensionScript registerNewScriptType(){
        ExtensionScript extScript = Control.getSingleton().getExtensionLoader().getExtension(ExtensionScript.class);
        if (extScript != null) {
            extScript.registerScriptType(
                    new ScriptType(SCRIPT_TYPE_WEBSOCKET_PASSIVE, "websocket.pscan.scripts.type.passive", createScriptIcon(), true));
            LOGGER.info(SCRIPT_TYPE_WEBSOCKET_PASSIVE + " Established");
            return extScript;
        }
        return null;
        
    }
    
    public ExtensionScript getScriptPlugIn(){
        if(passiveWebSocketScript == null){
            passiveWebSocketScript = registerNewScriptType();
        }
        return passiveWebSocketScript;
    }
    
    private WebSocketPassiveScannerList getPassiveScannerList() {
        if (passiveScannerList == null) {
            passiveScannerList = new WebSocketPassiveScannerList();
        }
        return passiveScannerList;
    }
    
    public TableWebSocket getTableWebSocket(){
        return tableWebSocket;
    }
    
    
    //DONE: Should run only Once
    public void setTableWebSocket(TableWebSocket tableWebSocket){
        if(this.tableWebSocket == null){
            LOGGER.info("New Table Instance ");
            this.tableWebSocket = tableWebSocket;
            passiveScanThread.setTableWebSocket(tableWebSocket);
        }
    }
    
    
    /**
     * TODO Ui Related
     * @return
     */
    private ImageIcon createScriptIcon() {
        if (!uiRelated) {
            return null;
        }
        return new ImageIcon(WebSocketPluginPassiveScanner.class.getResource(
                "/resource/icon/fugue/plug-connect.png")); //TODO: Find another Image
    }
    
    
    /**
     * TODO Add JavaDoc
     */
    public boolean addPassiveScanner(WebSocketPassiveScanner passiveScanner) {
        if (passiveScanner == null) {
            throw new IllegalArgumentException("Parameter passiveScanner must not be null.");
        }
        
        if (passiveScanner instanceof WebSocketPluginPassiveScanner) {
            return addPluginPassiveScannerImpl((WebSocketPluginPassiveScanner) passiveScanner);
        }
        return false;
    }
    
    private boolean addPluginPassiveScannerImpl(WebSocketPluginPassiveScanner scanner) {
        return addPassiveScannerImpl(scanner);
    }
    
    public Iterator<WebSocketPassiveScanner> getIterator(){
        return passiveScannerList.getIterator();
    }
    
    private boolean addPassiveScannerImpl(WebSocketPassiveScanner passiveScanner) {
        LOGGER.info("Add Passive Scan to list: " + passiveScanner.getName()); //TODO: Maybe log on debug mode only
        return passiveScannerList.add(passiveScanner);
    }
    
    
    /**
     * Sets whether or not all plug-in passive scanners are {@code enabled}.
     *
     * @param enabled {@code true} if the scanners should be enabled, {@code false} otherwise
     */
    void setAllPluginPassiveScannersEnabled(boolean enabled) {
        Iterator<WebSocketPassiveScanner> iterator = passiveScannerList.getIterator();
        WebSocketPassiveScanner scanner;
        while (iterator.hasNext()) {
            scanner = iterator.next();
            scanner.setEnabled(enabled);
        }
    }
    
    private void stopWebSocketPassiveScanThread() {
        if (this.passiveScanThread != null) {
            passiveScanThread.shutdown();
            this.passiveScanThread = null;
        }
    }
    
    
    public void setPassiveScanEnabled(boolean enabled) {
        if (passiveScanEnabled != enabled) {
            passiveScanEnabled = enabled;
            if (enabled) {
                startWebSocketPassiveScanThread();
            } else {
                stopWebSocketPassiveScanThread();
            }
        }
    }
    
    @Override
    public void sessionChanged(Session session) {
    
    }
    
    @Override
    public void sessionAboutToChange(Session session) {
    
    }
    
    @Override
    public void sessionScopeChanged(Session session) {
    
    }
    
    @Override
    public void sessionModeChanged(Control.Mode mode) {
    
    }
}
