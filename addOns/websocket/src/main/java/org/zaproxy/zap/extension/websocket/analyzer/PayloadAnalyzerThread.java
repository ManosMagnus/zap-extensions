/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2019 The ZAP Development Team
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.zaproxy.zap.extension.websocket.analyzer;

import java.util.Iterator;
import java.util.concurrent.LinkedBlockingQueue;
import org.apache.log4j.Logger;
import org.zaproxy.zap.extension.websocket.analyzer.analyzer.PayloadAnalyzerDecoration;
import org.zaproxy.zap.extension.websocket.treemap.nodes.contents.WebSocketContent;

public class PayloadAnalyzerThread extends Thread {

    private static final Logger LOGGER = Logger.getLogger(PayloadAnalyzerThread.class);

    public static final int SLEEP_TIME = 5000;

    /** Thread safe Linked Queue */
    private LinkedBlockingQueue<WebSocketContent> messagesBuffer;

    /** {@code True} to enable the payload analyzer Thread */
    private volatile boolean isActive;

    private WebSocketManagerAnalyzer managerAnalyzer;

    public PayloadAnalyzerThread(WebSocketManagerAnalyzer managerAnalyzer) {
        super("ZAP-WS-PayloadAnalyzer");
        setDaemon(true);

        this.isActive = false;
        messagesBuffer = new LinkedBlockingQueue<>();
        this.managerAnalyzer = managerAnalyzer;
    }

    public boolean isActive() {
        return isActive;
    }

    public void addMessage(WebSocketContent messageContent) {
        messagesBuffer.add(messageContent);
    }

    @Override
    public void run() {
        WebSocketContent messageContent;
        PayloadAnalyzerDecoration currentAnalyzer;
        Iterator<PayloadAnalyzerDecoration> iterator;

        while (isActive) {
            if (messagesBuffer.isEmpty()) {
                try {
                    Thread.sleep(SLEEP_TIME);

                } catch (InterruptedException e) {
                    LOGGER.info("Sleeping was interrupted", e);
                }
                if (!isActive) break;
            } else {
                messageContent = messagesBuffer.poll();

                iterator = managerAnalyzer.getIterator();

                while (iterator.hasNext()) {
                    if ((currentAnalyzer = iterator.next()).isEnabled()) {
                        currentAnalyzer.parse(messageContent.getMessage());
                    }
                }
            }
        }
    }

    public void shutdown() {
        isActive = false;
    }

    @Override
    public synchronized void start() {
        isActive = true;
        super.start();
    }
}
