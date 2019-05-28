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
package org.zaproxy.zap.extension.websocket.alerts;

import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.core.scanner.Alert;
import org.parosproxy.paros.model.HistoryReference;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.utility.InvalidUtf8Exception;

/** Wrapper for Alerts. This wrapper used to construct alerts for WebSocket */
public class WebSocketAlertWrapper {

    private static final Logger LOGGER = Logger.getLogger(WebSocketAlertWrapper.class);

    private static final String OTHER_INFO_LABEL = "[WebSocket Message] ";

    private Alert alert;

    /**
     * Initialize a new {@link Alert}.
     *
     * @see {@link Alert#Alert(int, int, int, String)}
     */
    public WebSocketAlertWrapper(int pluginId, int risk, int confidence, String name) {
        alert = new Alert(pluginId, risk, confidence, name);
    }

    /**
     * Sets the details of the Alert
     *
     * @see {@link Alert#setDetail(String, String, String, String, String, String, String, String,
     *     int, int, HttpMessage)}
     * @param description the description of the alert
     * @param param the parameter that has the issue
     * @param attack the attack triggers the issue
     * @param webSocketMessage WebSocket message triggers the issue
     * @param solution solution for the issue
     * @param evidence evidence (in the WebSocket message) that the issue exists
     * @param reference references about the issue
     * @param cweIdm the CWE ID of the issue
     * @param wascId the WASC ID of the issue
     */
    public void setDetail(
            String description,
            String param,
            String attack,
            WebSocketMessageDTO webSocketMessage,
            String solution,
            String evidence,
            String reference,
            int cweIdm,
            int wascId) {
        HttpMessage handshakeMessage;
        try {
            handshakeMessage = webSocketMessage.channel.getHandshakeReference().getHttpMessage();
        } catch (Exception e) {
            LOGGER.info("Couldn't get the Handshake Http Message for this specific channel", e);
            return;
        }
        try {
            alert.setDetail(
                    description,
                    handshakeMessage.getRequestHeader().getURI().toString(),
                    param,
                    attack,
                    OTHER_INFO_LABEL + webSocketMessage.getReadablePayload(),
                    solution,
                    reference,
                    evidence,
                    cweIdm,
                    wascId,
                    handshakeMessage);
        } catch (InvalidUtf8Exception e) {
            alert.setDetail(
                    description,
                    handshakeMessage.getRequestHeader().getURI().toString(),
                    param,
                    attack,
                    OTHER_INFO_LABEL
                            + Constant.messages.getString("websocket.payload.invalid_utf8"),
                    solution,
                    reference,
                    evidence,
                    cweIdm,
                    wascId,
                    handshakeMessage);
        }
    }

    public void setSource(Alert.Source source) {
        alert.setSource(source);
    }

    public HistoryReference getHandshakeReference() {
        return alert.getHistoryRef();
    }

    public Alert getAlert() {
        return alert;
    }
}
