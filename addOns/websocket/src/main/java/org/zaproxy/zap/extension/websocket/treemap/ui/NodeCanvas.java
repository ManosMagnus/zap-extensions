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

import java.util.ArrayList;
import java.util.List;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.parosproxy.paros.Constant;
import org.zaproxy.zap.utils.DisplayUtils;

public class NodeCanvas {

    private ArrayList<String> icons;
    private ImageIcon overlayIcon;

    public static final ImageIcon INCOMING_MESSAGE_ICON = new ImageIcon(NodeCanvas.class.getResource("/resource/icon/105_gray.png"));
    public static final ImageIcon OUTGOING_MESSAGE_ICON = new ImageIcon(NodeCanvas.class.getResource("/resource/icon/106_gray.png"));

    public static final ImageIcon FOLDER_ROOT_ICON =
            new ImageIcon(NodeCanvas.class.getResource("/resource/icon/16/094.png"));
    public static final ImageIcon FOLDER_CONNECTED_CHANNEL_ICON =
            new ImageIcon(NodeCanvas.class.getResource("/resource/icon/fugue/plug-connect.png"));
    public static final ImageIcon FOLDER_DISCONNECTED_CHANNEL_ICON =
            new ImageIcon(NodeCanvas.class.getResource("/resource/icon/fugue/plug-disconnect.png"));

    public NodeCanvas() {
        icons = new ArrayList<>();
    }

    public void setCustomIcons(ArrayList<String> i, ArrayList<Boolean> c) {
        synchronized (this.icons) {
            this.icons.clear();
            this.icons.addAll(i);
        }
    }

    public void addCustomIcon(String resourceName) {
        synchronized (this.icons) {
            if (!this.icons.contains(resourceName)) {
                this.icons.add(resourceName);
                //                this.nodeChanged();
            }
        }
    }

    public void removeCustomIcon(String resourceName) {
        synchronized (this.icons) {
            if (this.icons.contains(resourceName)) {
                int i = this.icons.indexOf(resourceName);
                this.icons.remove(i);
                //				this.nodeChanged();
            }
        }
    }

    public List<ImageIcon> getCustomIcons() {
        List<ImageIcon> iconList = new ArrayList<>();
        synchronized (this.icons) {
            if (!this.icons.isEmpty()) {
                for (String icon : this.icons) {
                    iconList.add(new ImageIcon(Constant.class.getResource(icon)));
                }
            }
        }
        return iconList;
    }

    public ImageIcon getOverlayIcon() {
        return overlayIcon;
    }

    public void setOverlayIcon(ImageIcon overlayIcon) {
        this.overlayIcon = overlayIcon;
    }

    private JLabel wrap(ImageIcon icon) {
        JLabel label = new JLabel(icon);
        label.setOpaque(false);
        label.putClientProperty("html.disable", Boolean.TRUE);
        return label;
    }

    public JPanel draw(JPanel jPanel) {
        jPanel.add(wrap(DisplayUtils.getScaledIcon(overlayIcon)));

        for (ImageIcon ci : getCustomIcons()) {
            jPanel.add(wrap(DisplayUtils.getScaledIcon(ci)));
        }

        return jPanel;
    }
}
