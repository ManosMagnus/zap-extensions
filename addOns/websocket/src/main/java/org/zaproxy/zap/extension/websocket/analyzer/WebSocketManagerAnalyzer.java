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
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.log4j.Logger;
import org.zaproxy.zap.extension.websocket.analyzer.analyzer.PayloadAnalyzer;
import org.zaproxy.zap.extension.websocket.analyzer.analyzer.PayloadAnalyzerDecoration;
import org.zaproxy.zap.extension.websocket.pscan.WebSocketPassiveScanner;
import org.zaproxy.zap.extension.websocket.treemap.nodes.contents.WebSocketContent;

public class WebSocketManagerAnalyzer {

    private static final Logger LOGGER = Logger.getLogger(WebSocketManagerAnalyzer.class);

    private CopyOnWriteArraySet<PayloadAnalyzerDecoration> analyzersSet;
    private PayloadAnalyzerThread analyzerThread = null;

    private CopyOnWriteArraySet<PayloadAnalyzerDecoration> getAnalyzersSet() {
        if (analyzersSet == null) {
            analyzersSet = new CopyOnWriteArraySet<>();
        }
        return analyzersSet;
    }

    public synchronized boolean add(PayloadAnalyzer payloadAnalyzer) {
        if (payloadAnalyzer == null) {
            throw new IllegalArgumentException("Parameter payloadAnalyzer must not be null.");
        }

        PayloadAnalyzerDecoration payloadAnalyzerPlugin =
                new PayloadAnalyzerDecoration(payloadAnalyzer);
        return addPlugin(payloadAnalyzerPlugin);
    }

    public boolean addPlugin(PayloadAnalyzerDecoration payloadAnalyzer) {
        if (getAnalyzersSet().contains(payloadAnalyzer)) {
            LOGGER.warn(
                    "Insert of "
                            + payloadAnalyzer.getName()
                            + " is prevented in order to avoid duplication");
            return false;
        }
        return getAnalyzersSet().add(payloadAnalyzer);
    }

    public void setAllEnable(boolean enabled) {
        Iterator<PayloadAnalyzerDecoration> iterator = getIterator();
        while (iterator.hasNext()) {
            iterator.next().setEnabled(enabled);
        }
    }

    private void setEnable(WebSocketPassiveScanner scanner, boolean enabled) {
        Iterator<PayloadAnalyzerDecoration> iterator = getIterator();
        PayloadAnalyzerDecoration itScanner;
        while (iterator.hasNext()) {
            itScanner = iterator.next();
            if (itScanner.equals(scanner)) {
                itScanner.setEnabled(enabled);
                return;
            }
        }
    }

    public void startThread() {
        if (!getAnalyzerThread().isActive()) {
            analyzerThread.start();
        } else {
            LOGGER.info("Payload Analyzer thread had already been closed");
        }
    }

    /** Shut down the background thread if it have been activated. */
    public void shutdownThread() {
        if (getAnalyzerThread().isActive()) {
            analyzerThread.shutdown();
        } else {
            LOGGER.info("Analyzer Threa scan thread had already been closed");
        }
    }

    public Iterator<PayloadAnalyzerDecoration> getIterator() {
        return getAnalyzersSet().iterator();
    }

    private PayloadAnalyzerThread getAnalyzerThread() {
        if (analyzerThread == null) {
            analyzerThread = new PayloadAnalyzerThread(this);
        }
        return analyzerThread;
    }

    public void addMessageContent(WebSocketContent messageContent) {
        analyzerThread.addMessage(messageContent);
    }
}
