package org.zaproxy.zap.extension.websocket.pscan;

import org.parosproxy.paros.db.DatabaseException;
import org.zaproxy.zap.extension.pscan.PassiveScanner;
import org.zaproxy.zap.extension.websocket.WebSocketMessage;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.WebSocketProxy;
import org.zaproxy.zap.extension.websocket.WebSocketSenderListener;
import org.zaproxy.zap.extension.websocket.db.TableWebSocket;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocketPassiveScanThread extends Thread implements WebSocketSenderListener {
    
    public static final int WEBSOCKET_LISTENER_ORDER = 20;
    private LinkedBlockingQueue<MessageForScanWrap> messagesBuffer;
    private List<WebSocketPassiveScanner> passiveScannerList;
    private volatile boolean  isPassiveScannerActive;
    private Iterator<WebSocketPassiveScanner> scannerIterator;
    private boolean isIteratorUpdated = false;
    private TableWebSocket tableWebSocket;
    
    WebSocketPassiveScanThread(boolean isPassiveScannerActive){
        messagesBuffer = new LinkedBlockingQueue<>();
        this.isPassiveScannerActive = isPassiveScannerActive;
        tableWebSocket = new TableWebSocket();
    }
    
    public void addWebSocketPassiveScanner(WebSocketPassiveScanner webSocketPassiveScanner){
        if(passiveScannerList == null){
            passiveScannerList = new CopyOnWriteArrayList<>();
            isIteratorUpdated = false;
        }
        passiveScannerList.add(webSocketPassiveScanner);
    }
    
    public void removeWebSocketPassiveScanner(WebSocketPassiveScanner webSocketPassiveScanner){
        if(passiveScannerList != null){
            passiveScannerList.remove(webSocketPassiveScanner);
            isIteratorUpdated = false;
        }
    }
    
    public Iterator<WebSocketPassiveScanner> getIterator(){
        if(isIteratorUpdated == false){
            scannerIterator = passiveScannerList.iterator();
        }
        return scannerIterator;
    }
    
    @Override
    public int getListenerOrder() {
        return WEBSOCKET_LISTENER_ORDER;
    }
    
    @Override
    public void onMessageFrame(int channelId, WebSocketMessage message, WebSocketProxy.Initiator initiator) {
        //TODO: Take care {@link WebSocketMessage#OPCODE_CONTINUATION}
        messagesBuffer.add(new MessageForScanWrap(message.getMessageId(),channelId,initiator));
        messagesBuffer.notify();
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
            if(messagesBuffer.isEmpty()){
                try {
                    messagesBuffer.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                if(!isPassiveScannerActive){
                    break;
                }
                
                messageWrap = messagesBuffer.poll();
                
                try {
                    currentMessage = tableWebSocket.getMessage(messageWrap.messageId,messageWrap.channelId);
                    Iterator<WebSocketPassiveScanner> iterator = getIterator();
                    while (iterator.hasNext()){
                        currentPassiveScanner = iterator.next();
                        if(currentPassiveScanner.isEnable() && currentPassiveScanner.isThatMessageForScan(currentMessage)){
                            currentPassiveScanner.setParent(this);
                        }
                    }
                } catch (DatabaseException e) {
                    e.printStackTrace();
                }
            }
        }
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
