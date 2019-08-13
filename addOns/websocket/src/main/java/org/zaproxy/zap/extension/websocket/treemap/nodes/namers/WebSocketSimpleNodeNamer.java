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
package org.zaproxy.zap.extension.websocket.treemap.nodes.namers;

import org.parosproxy.paros.Constant;
import org.zaproxy.zap.extension.websocket.treemap.nodes.contents.*;
import org.zaproxy.zap.extension.websocket.utility.InvalidUtf8Exception;

public class WebSocketSimpleNodeNamer implements WebSocketNodeNamer {

    @Override
    public String getName(HandshakeContent handshakeContent) {
        return handshakeContent.getChannels().get(0).getContextUrl();
    }

    @Override
    public String getName(MessageContent messageContent) {
        String name;
        if (messageContent.getPayloadStructure() == null) {
            try {
                name = messageContent.getMessage().getReadablePayload();
            } catch (InvalidUtf8Exception e) {
                name = Constant.messages.getString("websocket.payload.unreadable_binary");
            }

        } else {
            name = messageContent.getPayloadStructure().getName();
        }
        return !name.isEmpty() ? name : Constant.messages.getString("websocket.node.empty_payload");
    }

    @Override
    public String getName(RootContent rootContent) {
        return Constant.messages.getString("websocket.treemap.folder.root");
    }

    @Override
    public String getName(HandshakeFolderContent handshakeFolderContent) {
        return Constant.messages.getString("websocket.treemap.folder.handshakes");
    }

    @Override
    public String getName(MessageFolderContent messageFolderContent) {
        switch (messageFolderContent.getType()) {
            case MESSAGES:
                return Constant.messages.getString("websocket.treemap.folder.messages");
            case CLOSE:
                return Constant.messages.getString("websocket.treemap.folder.close");
            case HEARTBEAT:
                return Constant.messages.getString("websocket.treemap.folder.heartbeats");
            default:
                throw new IllegalStateException(
                        "Unexpected value: " + messageFolderContent.getType());
        }
    }

    @Override
    public String getName(HostFolderContent hostFolderContent) {
        return hostFolderContent.getHost();
    }
}
