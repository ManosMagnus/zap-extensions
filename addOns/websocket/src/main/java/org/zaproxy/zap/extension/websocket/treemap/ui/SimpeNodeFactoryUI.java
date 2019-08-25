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

import org.parosproxy.paros.db.DatabaseException;
import org.parosproxy.paros.network.HttpMalformedHeaderException;
import org.zaproxy.zap.extension.websocket.WebSocketChannelDTO;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.treemap.nodes.contents.NodeContent;
import org.zaproxy.zap.extension.websocket.treemap.nodes.factories.NodeFactory;
import org.zaproxy.zap.extension.websocket.treemap.nodes.structural.WebSocketNodeInterface;

public class SimpeNodeFactoryUI implements NodeFactory {

    private NodeFactory nodeFactory;
    private TreeNodeUI rootNode = null;

    public SimpeNodeFactoryUI(NodeFactory nodeFactory) {
        this.nodeFactory = nodeFactory;
    }

    @Override
    public WebSocketNodeInterface getMessageTreeNode(WebSocketMessageDTO message) {
        return nodeFactory.getMessageTreeNode(message);
    }

    @Override
    public WebSocketNodeInterface createMessageNode(
            WebSocketNodeInterface parent, int position, NodeContent nodeContent) {
        TreeNodeUI newNode =
                new TreeNodeUI(nodeFactory.createMessageNode(parent, position, nodeContent));
        newNode.getNodeCanvas().setOverlayIcon(
                nodeContent.getMessage().isOutgoing ?
                NodeCanvas.OUTGOING_MESSAGE_ICON : NodeCanvas.INCOMING_MESSAGE_ICON);
        return newNode;
    }

    @Override
    public WebSocketNodeInterface getHostTreeNode(WebSocketChannelDTO channel)
            throws DatabaseException, HttpMalformedHeaderException {
        return nodeFactory.getHostTreeNode(channel);
    }

    @Override
    public WebSocketNodeInterface createHostNode(
            WebSocketNodeInterface parent, int position, NodeContent nodeContent) {
        TreeNodeUI newNode =
                new TreeNodeUI(nodeFactory.createHostNode(parent, position, nodeContent));
        newNode.getNodeCanvas().setOverlayIcon(NodeCanvas.FOLDER_CONNECTED_CHANNEL_ICON);
        return newNode;
    }

    @Override
    public WebSocketNodeInterface getRoot() {
        if (rootNode == null) {
            rootNode = new TreeNodeUI(nodeFactory.getRoot());
            rootNode.getNodeCanvas().setOverlayIcon(NodeCanvas.FOLDER_ROOT_ICON);
        }
        return rootNode;
    }
}
