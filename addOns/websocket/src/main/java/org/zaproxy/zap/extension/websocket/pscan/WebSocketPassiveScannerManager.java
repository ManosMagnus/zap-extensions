/*
 * Zed Attack Proxy (ZAP) and its related class files.
 *
 * ZAP is an HTTP/HTTPS proxy for assessing web application security.
 *
 * Copyright 2018 The ZAP Development Team
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
package org.zaproxy.zap.extension.websocket.pscan;

import java.util.Iterator;
import java.util.concurrent.CopyOnWriteArraySet;
import org.apache.log4j.Logger;
import org.zaproxy.zap.extension.websocket.WebSocketObserver;
import org.zaproxy.zap.extension.websocket.WebSocketSenderListener;
import org.zaproxy.zap.extension.websocket.alerts.AlertManager;
import org.zaproxy.zap.extension.websocket.db.TableWebSocket;

/**
 * Manages all stuff related with the WebSocket Passive Scanning. Manager is able to open a
 * background thread {@link WebSocketPassiveScanThread} so as to run passive scans. In addition, all
 * {@link WebSocketPassiveScannerPlugin} implementations should be registered and enabled/disabled
 * by this class. Manager keeps the WebSocket Scanners into thread safe list. Finally, informs every
 * Passive Scanners (should implement {@link WebSocketSenderListener}) about messages with
 * method{@link WebSocketPassiveScannerManager#getWebSocketScannerObserver()}
 */
public class WebSocketPassiveScannerManager {

    private static final Logger LOGGER = Logger.getLogger(WebSocketPassiveScannerManager.class);

    /** The background thread where the passive scans are happening */
    private WebSocketPassiveScanThread passiveScanThread;

    /** {@code false} if passive scans are disabled */
    private boolean passiveScanEnabled = false;

    /** Used to raise Alert Messages */
    private AlertManager alertManager;

    /** List stores passive scanners */
    private CopyOnWriteArraySet<WebSocketPassiveScanner> passiveScannersSet;

    /** {@code False} if iterator is out-of-date */
    private boolean isIteratorUpdated = false;

    /** Iterator for {@code passiveScannersSet} */
    private Iterator<WebSocketPassiveScanner> iterator;

    /** True if server proxies should be ignored */
    private boolean isServerModeIgnored = true;

    /**
     * Initiate a Passive Scanner Manager. By default passive scans are disabled. In order to enable
     * all passive scanners {@see
     * WebSocketPassiveScannerManager#setAllPluginPassiveScannersEnabled}. In addition if WebSocket
     * Proxy Mode equals to {@link org.zaproxy.zap.extension.websocket.WebSocketProxy.Mode#SERVER} ,
     * proxy's messages, by default, are ignored to passive scan .
     */
    public WebSocketPassiveScannerManager(AlertManager alertManager) {
        this.alertManager = alertManager;
    }

    /** Listening WebSocketMessages */
    public WebSocketObserver getWebSocketScannerObserver() {
        if (passiveScanThread == null) {
            passiveScanThread = new WebSocketPassiveScanThread(this);
        }
        return passiveScanThread;
    }

    /**
     * Initialize background thread {@link WebSocketPassiveScanThread}. In addition, used as {@link
     * org.zaproxy.zap.extension.websocket.WebSocketSenderListener}
     *
     * @return the background thread
     */
    public WebSocketPassiveScanThread getWebSocketPassiveScanThread() {
        if (passiveScanThread == null) {
            passiveScanThread = new WebSocketPassiveScanThread(this);
        }
        return passiveScanThread;
    }

    /**
     * Begin and activate the background thread where passive scans runs. Do nothing if the
     * background thread have already been running
     */
    public void startWebSocketPassiveScanThread() {
        if (!passiveScanThread.isAlive()) {
            passiveScanThread.setPassiveScannerActive(true);
            passiveScanThread.start();
        }
    }

    /**
     * Initiate or returns the list of WebSocket Passive Scanners
     *
     * @return list of the passive scanners
     */
    private CopyOnWriteArraySet<WebSocketPassiveScanner> getPassiveScannersSet() {
        if (passiveScannersSet == null) {
            passiveScannersSet = new CopyOnWriteArraySet<>();
        }
        return passiveScannersSet;
    }

    /**
     * Sets the {@link TableWebSocket} if have not been yet. In order to force manager to update the
     * table use {@link WebSocketPassiveScannerManager#setTableWebSocket(TableWebSocket)}
     *
     * @param tableWebSocket the table
     */
    public void setTableWebSocketIfNot(TableWebSocket tableWebSocket) {
        if (!getWebSocketPassiveScanThread().hasTableWebSocket()) {
            passiveScanThread.setTableWebSocket(tableWebSocket);
        }
    }

    /** Setting the table WebSocket */
    public void setTableWebSocket(TableWebSocket tableWebSocket) {
        passiveScanThread.setTableWebSocket(tableWebSocket);
    }

    /** Adds the WebSocketPassive Scanner if not null */
    public synchronized WebSocketPassiveScannerPlugin addPassiveScannerPlugin(
            WebSocketPassiveScanner passiveScanner) {
        if (passiveScanner == null) {
            throw new IllegalArgumentException("Parameter passiveScanner must not be null.");
        }
        WebSocketPassiveScannerPlugin wsPlugin = new WebSocketPassiveScannerPlugin(passiveScanner);
        return add(new WebSocketPassiveScannerPlugin(passiveScanner)) ? wsPlugin : null;
    }

    /**
     * Enables or disables all WebSocket Passive Scanners
     *
     * @param enabled {@code true} if the scanners should be enabled, {@code false} otherwise
     */
    public void setAllPluginPassiveScannersEnabled(boolean enabled) {
        Iterator<WebSocketPassiveScanner> iterator = getIterator();
        WebSocketPassiveScannerPlugin scanner;
        while (iterator.hasNext()) {
            scanner = (WebSocketPassiveScannerPlugin) iterator.next();
            scanner.setEnabled(enabled);
        }
    }

    /** Shut down the background thread if any. */
    private void stopWebSocketPassiveScanThread() {
        if (this.passiveScanThread != null) {
            passiveScanThread.shutdown();
        }
    }

    /**
     * Activating or Deactivating the passive scanning. That's not related with any {@link
     * WebSocketPassiveScannerPlugin} just with background thread {@link WebSocketPassiveScanThread}
     *
     * @param enabled if true activates the background thread
     */
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

    public AlertManager getAlertManager() {
        return alertManager;
    }

    /**
     * Add a passive scanner to thread safe list
     *
     * @return {@code true} if passive scanner is added to list successfully.
     */
    private boolean add(WebSocketPassiveScanner passiveScanner) {
        if (getPassiveScannersSet().contains(passiveScanner)) {
            LOGGER.warn(
                    "Insertion of "
                            + passiveScanner.getName()
                            + " is prevent in order to avoid the duplication");
            return false;
        }
        boolean result = getPassiveScannersSet().add(passiveScanner);
        if (result) {
            isIteratorUpdated = false;
        }
        return result;
    }

    /**
     * Drop the passive scanner from the list
     *
     * @return {@code true} if passive scanner is dropped from list successfully.
     */
    public synchronized boolean removeScanner(WebSocketPassiveScanner passiveScanner) {
        return getPassiveScannersSet().remove(passiveScanner);
    }

    /** @return an up-to-date iterator from websocket passive scanner list */
    protected Iterator<WebSocketPassiveScanner> getIterator() {
        if (!isIteratorUpdated) {
            iterator = getPassiveScannersSet().iterator();
        }
        return iterator;
    }

    public boolean isScannerContained(WebSocketPassiveScanner webSocketPassiveScanner) {
        return getPassiveScannersSet().contains(webSocketPassiveScanner);
    }

    public int getScannerListSize() {
        return getPassiveScannersSet().size();
    }

    public boolean isServerModeIgnored() {
        return isServerModeIgnored;
    }

    public void setServerModeIgnored(boolean serverModeIgnored) {
        isServerModeIgnored = serverModeIgnored;
    }
}
