package org.zaproxy.zap.extension.websocket.treemap;

import org.apache.log4j.Logger;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketFolderNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketHandshakeNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketTreeNode;
import org.zaproxy.zap.utils.DisplayUtils;
import org.zaproxy.zap.view.OverlayIcon;
import org.zaproxy.zap.view.SiteMapListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;

public class WebSocketMapTreeCellRender extends DefaultTreeCellRenderer {
    
    private static final long serialVersionUID = -427869101224513123L;
    
    private static final ImageIcon FOLDER_ROOT_ICON = new ImageIcon(WebSocketMapTreeCellRender.class.getResource("/resource/icon/16/094.png"));
    private static final ImageIcon FOLDER_HANSHAKE_ICON = new ImageIcon(WebSocketMapTreeCellRender.class.getResource("/org/zaproxy/zap/extension/websocket/resources/icons/hand-shake.png"));
    private static final ImageIcon FOLDER_CLOSE_ICON     = new ImageIcon(WebSocketMapTreeCellRender.class.getResource("/org/zaproxy/zap/extension/websocket/resources/icons/plug-disconnect.png"));
    private static final ImageIcon FOLDER_HEARTBEAT_ICON = new ImageIcon(WebSocketMapTreeCellRender.class.getResource("/org/zaproxy/zap/extension/websocket/resources/icons/heart.png"));
    private static final ImageIcon FOLDER_CONNECTED_CHANNEL_ICON = new ImageIcon(WebSocketMapTreeCellRender.class.getResource("/resource/icon/fugue/plug-connect.png"));
    private static final ImageIcon FOLDER_DISCONNECTED_CHANNEL_ICON = new ImageIcon(WebSocketMapTreeCellRender.class.getResource("/resource/icon/fugue/plug-disconnect.png"));
    private static final ImageIcon FOLDER_MESSAGES_ICON  = new ImageIcon(WebSocketMapTreeCellRender.class.getResource("/org/zaproxy/zap/extension/websocket/resources/icons/mail-send-receive.png")); ;
    
    private static Logger LOGGER = Logger.getLogger(WebSocketMapTreeCellRender.class);
    
    private List<SiteMapListener> listeners;
    private JPanel component;
    
    public WebSocketMapTreeCellRender(List<SiteMapListener> listeners){
        this.listeners = listeners;
        this.component = new JPanel(new FlowLayout(FlowLayout.CENTER,4,2));
        component.setOpaque(false);
    }
    
    /**
     * Sets custom tree node logos.
     */
    @Override
    public Component getTreeCellRendererComponent(JTree tree, Object value,
                                                  boolean sel, boolean expanded, boolean leaf, int row,
                                                  boolean hasFocus) {
        component.removeAll();
        WebSocketTreeNode webSocketTreeNode = null;
        if (value instanceof WebSocketFolderNode) {
            LOGGER.info("[WS-TREE]: FOLDER NODE");
            webSocketTreeNode = (WebSocketFolderNode) value;
        }else if (value instanceof WebSocketHandshakeNode) {
            LOGGER.info("[WS-TREE]: HANDSHAKE NODE");
            webSocketTreeNode = (WebSocketHandshakeNode) value;
        }else{
            LOGGER.info("[WS-TREE]: NOTHING");
            LOGGER.info("[WS-TREE]: " + value.getClass().getSimpleName() );
        }
        
        
        if(webSocketTreeNode != null) {
            setPreferredSize(null);	// clears the preferred size, making the node visible
            super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
    
            if (webSocketTreeNode.isRoot()) {
                LOGGER.info("[WS-TREE]: FOLDER ROOT");
                component.add(wrap(FOLDER_ROOT_ICON)); // 'World' icon
            }else{
                OverlayIcon icon = null;
                if(webSocketTreeNode instanceof WebSocketFolderNode){
                    WebSocketNodeType webSocketNodeType = webSocketTreeNode.getType();
                    switch (webSocketNodeType) {
                        case CHANNEL_NODE:
                            LOGGER.info("[WS-TREE]: CHANNEL NODE");
                            icon = new OverlayIcon(FOLDER_CONNECTED_CHANNEL_ICON);
                            break;
                        case FOLDER_ROOT:
                            LOGGER.info("[WS-TREE]: FOLDER ROOT");
                            icon = new OverlayIcon(FOLDER_ROOT_ICON);
                            break;
                        case FOLDER_HANDSHAKE:
                            LOGGER.info("[WS-TREE]: FOLDER HANDSHAKE");
                            icon = new OverlayIcon(FOLDER_HANSHAKE_ICON);
                            break;
                        case FOLDER_HEARTBEAT:
                            LOGGER.info("[WS-TREE]: FOLDER HEARTBEAT");
                            icon = new OverlayIcon(FOLDER_HEARTBEAT_ICON);
                            break;
                        case FOLDER_CLOSE:
                            LOGGER.info("[WS-TREE]: FOLDER CLOSE");
                            icon = new OverlayIcon(FOLDER_CLOSE_ICON);
                            break;
                    }
                }
                component.add(wrap(DisplayUtils.getScaledIcon(icon)));
            }
    
            for (ImageIcon ci : webSocketTreeNode.getCustomIcons()) {
                component.add(wrap(DisplayUtils.getScaledIcon(ci)));
            }
    
            setText(webSocketTreeNode.toString());
            setIcon(null);
            component.add(this);
    
            return component;
        }
        return this;
    }
    
    private JLabel wrap (ImageIcon icon) {
        JLabel label = new JLabel(icon);
        label.setOpaque(false);
        label.putClientProperty("html.disable", Boolean.TRUE);
        return label;
    }
    
}
