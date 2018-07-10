package org.zaproxy.zap.extension.websocket.pscan;

public interface WebSocketPassiveScanner {
    boolean isThatMessageForScan(MessageForScanWrap message);
}
