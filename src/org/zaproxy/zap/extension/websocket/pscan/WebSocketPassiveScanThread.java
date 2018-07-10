package org.zaproxy.zap.extension.websocket.pscan;

import org.zaproxy.zap.extension.pscan.PassiveScanner;
import org.zaproxy.zap.extension.websocket.WebSocketMessage;
import org.zaproxy.zap.extension.websocket.WebSocketProxy;
import org.zaproxy.zap.extension.websocket.WebSocketSenderListener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;

public class WebSocketPassiveScanThread extends Thread implements WebSocketSenderListener {
    
    public static final int WEBSOCKET_LISTENER_ORDER = 20;
    private LinkedBlockingQueue<MessageForScanWrap> messagesBuffer;
    private List<WebSocketPassiveScanner> passiveScannerList;
    private volatile boolean  isPassiveScannerActive;
    private Iterator<WebSocketPassiveScanner> scannerIterator;
    private boolean isIteratorUpdated = false;
    
    WebSocketPassiveScanThread(boolean isPassiveScannerActive){
        messagesBuffer = new LinkedBlockingQueue<>();
        this.isPassiveScannerActive = isPassiveScannerActive;
    }
    
    public void addWebSocketPassiveScanner(WebSocketPassiveScanner webSocketPassiveScanner){
        if(passiveScannerList == null){
            passiveScannerList = new ArrayList<>();
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
        messagesBuffer.add(new MessageForScanWrap(message,channelId,initiator));
        messagesBuffer.notify();
    }
    
    @Override
    public void onStateChange(WebSocketProxy.State state, WebSocketProxy proxy) {
    
    }
    
    @Override
    public void run() {
        MessageForScanWrap messageWrap;
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
                while (getIterator().hasNext()){
                
                }
                
            }
            
        
        }
        
    }
}
