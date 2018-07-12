package org.zaproxy.zap.extension.websocket.pscan;

import org.apache.log4j.Logger;
import org.parosproxy.paros.db.DatabaseException;
import org.zaproxy.zap.extension.websocket.WebSocketMessage;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.WebSocketProxy;
import org.zaproxy.zap.extension.websocket.WebSocketSenderListener;
import org.zaproxy.zap.extension.websocket.db.TableWebSocket;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocketPassiveScanThread extends Thread implements WebSocketSenderListener {
    
    private static final Logger LOGGER = Logger.getLogger(WebSocketPassiveScanThread.class);
    
    public static final int WEBSOCKET_LISTENER_ORDER = 20;
    private LinkedBlockingQueue<MessageForScanWrap> messagesBuffer;
    private volatile boolean  isPassiveScannerActive;
    private TableWebSocket tableWebSocket;
    WebSocketPassiveScannerManager passiveScannerManager;;
    
    public WebSocketPassiveScanThread (WebSocketPassiveScannerManager passiveScannerManager) {
        this.passiveScannerManager = passiveScannerManager;
        this.isPassiveScannerActive = true;
        messagesBuffer = new LinkedBlockingQueue<>();
    }
    
    
    
    public void setTableWebSocket(TableWebSocket tableWebSocket) {
        this.tableWebSocket = tableWebSocket;
    }
    
    @Override
    public int getListenerOrder() {
        return WEBSOCKET_LISTENER_ORDER;
    }
    
    @Override
    public void onMessageFrame(int channelId, WebSocketMessage message, WebSocketProxy.Initiator initiator) {
        
        //TODO: Take care {@link WebSocketMessage#OPCODE_CONTINUATION}
        messagesBuffer.add(new MessageForScanWrap(message.getMessageId(),channelId,initiator));
    }
    
    @Override
    public void onStateChange(WebSocketProxy.State state, WebSocketProxy proxy) {
    
    }
    
    @Override
    public void run() {
        MessageForScanWrap messageWrap;
        WebSocketPassiveScanner currentPassiveScanner;
        WebSocketMessageDTO currentMessage;
        while (isPassiveScannerActive){
            if (messagesBuffer.isEmpty() || tableWebSocket == null) {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if (!isPassiveScannerActive) {
                    break;
                }
            }else{
                messageWrap = messagesBuffer.poll();
                
                try {
                    currentMessage = tableWebSocket.getMessage(messageWrap.messageId, messageWrap.channelId);
                    Iterator<WebSocketPassiveScanner> iterator = passiveScannerManager.getIterator();
                    while (iterator.hasNext()) {
                        currentPassiveScanner = iterator.next();
                        LOGGER.info("Current Script: " + currentPassiveScanner.getName());
                        if (currentPassiveScanner.isEnable() && currentPassiveScanner.applyScanToMessage(currentMessage)) {
                            currentPassiveScanner.setParent(this);
                            currentPassiveScanner.scanMessage(currentMessage);
                        }
                    }
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }
            
        }
    }
    
    public void shutdown(){
        isPassiveScannerActive = false;
    }
    
    
    public class MessageForScanWrap {
        public int messageId;
        public int channelId;
        public WebSocketProxy.Initiator proxyInitiator;
        
        MessageForScanWrap(int messageId, int channelId, WebSocketProxy.Initiator proxyInitiator){
            this.messageId = messageId;
            this.channelId = channelId;
            this.proxyInitiator = proxyInitiator;
        }
    }
    
}
