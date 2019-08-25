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

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.treemap.nodes.contents.NodeContent;
import org.zaproxy.zap.extension.websocket.treemap.nodes.structural.WebSocketNodeInterface;

public class TreeNodeUI implements WebSocketNodeInterface {

    private WebSocketNodeInterface webSocketNode;
    private NodeCanvas nodeCanvas = null;

    public TreeNodeUI(WebSocketNodeInterface webSocketNode) {
        this.webSocketNode = webSocketNode;
    }

    public WebSocketTreeCellRenderer draw(WebSocketTreeCellRenderer treeCellRenderer) {

        treeCellRenderer.setText(webSocketNode.getName());
        treeCellRenderer.setIcon(null);
        getNodeCanvas().draw(treeCellRenderer.getPanel());

        return treeCellRenderer;
    }

    public NodeCanvas getNodeCanvas() {
        if (nodeCanvas == null) {
            nodeCanvas = new NodeCanvas();
        }
        return nodeCanvas;
    }

    @Override
    public boolean hasContent() {
        return webSocketNode.hasContent();
    }

    @Override
    public boolean isRoot() {
        return webSocketNode.isRoot();
    }

    @Override
    public WebSocketNodeInterface getParent() {
        return webSocketNode.getParent();
    }

    @Override
    public WebSocketNodeInterface getChildAt(int pos) {
        return webSocketNode.getChildAt(pos);
    }

    @Override
    public int addChild(WebSocketNodeInterface newChild) {
        return webSocketNode.addChild(newChild);
    }

    @Override
    public void addChild(int index, WebSocketNodeInterface child) {
        webSocketNode.addChild(index, child);
    }

    @Override
    public boolean isLeaf() {
        return webSocketNode.isLeaf();
    }

    @Override
    public int getIndex() {
        return webSocketNode.getIndex();
    }

    @Override
    public List<WebSocketNodeInterface> getChildren() {
        return webSocketNode.getChildren();
    }

    @Override
    public int getPosition(NodeContent nodeContent) {
        return webSocketNode.getPosition(nodeContent);
    }

    @Override
    public <T> WebSocketNodeInterface getChildrenWhen(
            Function<WebSocketNodeInterface, T> function, T when) {
        return webSocketNode.getChildrenWhen(function, when);
    }

    @Override
    public <T> List<T> iterateOverLeaf(
            WebSocketNodeInterface root,
            Function<WebSocketNodeInterface, T> function,
            List<T> list) {
        return webSocketNode.iterateOverLeaf(root, function, list);
    }

    @Override
    public <T extends Collection<C>, C> List<C> iterateOverLeafToAddAll(
            WebSocketNodeInterface root,
            Function<WebSocketNodeInterface, T> function,
            List<C> list) {
        return webSocketNode.iterateOverLeafToAddAll(root, function, list);
    }

    @Override
    public void applyToChildren(Consumer<WebSocketNodeInterface> consumer) {
        webSocketNode.applyToChildren(consumer);
    }

    @Override
    public NodeContent getContent() {
        return webSocketNode.getContent();
    }

    @Override
    public String getName() {
        return webSocketNode.getName();
    }

    @Override
    public StringBuilder getString(
            StringBuilder stringBuilder, WebSocketNodeInterface root, int depth) {
        return null;
    }

    @Override
    public WebSocketNodeInterface updateContent(NodeContent content) {
        return webSocketNode.updateContent(content);
    }

    @Override
    public WebSocketMessageDTO getMessage() {
        return webSocketNode.getMessage();
    }

    @Override
    public String getHost() {
        return webSocketNode.getHost();
    }

    @Override
    public List<WebSocketNodeInterface> getHostNodes(List<WebSocketNodeInterface> hostNodesList) {
        return webSocketNode.getHostNodes(hostNodesList);
    }

    @Override
    public HashMap<WebSocketNodeInterface, List<WebSocketMessageDTO>> getMessagesPerHost(
            HashMap<WebSocketNodeInterface, List<WebSocketMessageDTO>> messageMap) {
        return webSocketNode.getMessagesPerHost(messageMap);
    }

    @Override
    public int compareTo(WebSocketNodeInterface webSocketNodeInterface) {
        return webSocketNode.compareTo(webSocketNodeInterface);
    }
}
