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
package org.zaproxy.zap.extension.websocket.treemap.ui;

import javax.swing.tree.TreePath;
import org.zaproxy.zap.extension.websocket.WebSocketMessage;
import org.zaproxy.zap.extension.websocket.WebSocketObserver;
import org.zaproxy.zap.extension.websocket.WebSocketProxy;
import org.zaproxy.zap.extension.websocket.treemap.TreeMap;
import org.zaproxy.zap.extension.websocket.treemap.WebSocketTreeMap;
import org.zaproxy.zap.extension.websocket.treemap.nodes.structural.WebSocketNodeInterface;

public class WebSocketTreeMapModel extends WebSocketTreeModelAbstract implements TreeMap{

    private WebSocketTreeMapObserver webSocketTreeMapObserver = null;
    private WebSocketTreeMap webSocketTreeMap;

    public WebSocketTreeMapModel(WebSocketTreeMap webSocketTreeMap) {
        super();
        this.webSocketTreeMap = webSocketTreeMap;
    }

    @Override
    public WebSocketNodeInterface addMessage(WebSocketMessage message) {
        WebSocketNodeInterface node = webSocketTreeMap.addMessage(message);

    }

    @Override
    public WebSocketNodeInterface addConnection(WebSocketProxy proxy) {
        return webSocketTreeMap.addConnection(proxy);
    }

    @Override
    public WebSocketNodeInterface getRootNode() {
        return webSocketTreeMap.getRootNode();
    }

    @Override
    public WebSocketObserver getWebSocketObserver() {
        if (webSocketTreeMapObserver == null) {
            webSocketTreeMapObserver = new WebSocketTreeMapObserver();
        }
        return webSocketTreeMapObserver;
    }

    @Override
    public Object getRoot() {
        return this.getRootNode();
    }

    @Override
    public Object getChild(Object o, int i) {
        return ((WebSocketNodeInterface) o).getChildAt(i);
    }

    @Override
    public int getChildCount(Object o) {
        return ((WebSocketNodeInterface) o).getChildren().size();
    }

    @Override
    public boolean isLeaf(Object o) {
        return ((WebSocketNodeInterface) o).isLeaf();
    }

    @Override
    public int getIndexOfChild(Object parent, Object child) {
        return ((WebSocketNodeInterface) child).getIndex();
    }

    @Override
    public void valueForPathChanged(TreePath treePath, Object o) {}

    private class WebSocketTreeMapObserver implements WebSocketObserver {

        @Override
        public int getObservingOrder() {
            return webSocketTreeMap.getObservingOrder();
        }

        @Override
        public boolean onMessageFrame(int channelId, WebSocketMessage message) {
            addMessage(message);
            return true;
        }

        @Override
        public void onStateChange(WebSocketProxy.State state, WebSocketProxy proxy) {
            if (state == WebSocketProxy.State.CONNECTING) {
                addConnection(proxy);
            }
        }
    }
}
