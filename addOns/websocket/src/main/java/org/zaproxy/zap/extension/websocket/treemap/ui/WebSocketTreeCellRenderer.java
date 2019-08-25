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

import java.awt.Component;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import org.zaproxy.zap.extension.websocket.treemap.nodes.structural.WebSocketNodeInterface;

public class WebSocketTreeCellRenderer extends DefaultTreeCellRenderer {

    private static final long serialVersionUID = 6958752713770131906L;

    private WebSocketTreeMapHelperUI helperUI;
    private JPanel panel;

    public WebSocketTreeCellRenderer(WebSocketTreeMapHelperUI helper) {
        this.helperUI = helper;
        panel = helperUI.getTreeMapCellPanel();
        helper.getTreeMapCellPanel().setOpaque(false);
    }

    @Override
    public Component getTreeCellRendererComponent(
            JTree jTree,
            Object value,
            boolean selected,
            boolean expanded,
            boolean leaf,
            int row,
            boolean hasFocus) {
        super.getTreeCellRendererComponent(
                jTree, value, selected, expanded, leaf, row, hasFocus);
        jTree.setVisible(true);
        panel.removeAll();

        WebSocketNodeInterface node = (WebSocketNodeInterface) value;
        if (node != null) {
            super.setPreferredSize(null);
            super.getTreeCellRendererComponent(
                    jTree, value, selected, expanded, leaf, row, hasFocus);

            panel.add(node.draw(this));
            return getPanel();
        }
        return this;
    }

    public JPanel getPanel() {
        return panel;
    }
}
