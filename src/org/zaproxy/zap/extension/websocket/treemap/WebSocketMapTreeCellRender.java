package org.zaproxy.zap.extension.websocket.treemap;

import org.apache.log4j.Logger;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketFolderNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketHandshakeNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketTreeNode;
import org.zaproxy.zap.utils.DisplayUtils;
import org.zaproxy.zap.view.SiteMapListener;

import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;
import java.awt.Component;
import java.awt.FlowLayout;
import java.util.List;

public class WebSocketMapTreeCellRender extends DefaultTreeCellRenderer {
    
    private static final long serialVersionUID = -427869101224513123L;
    
    private static final ImageIcon ROOT_ICON = new ImageIcon(WebSocketMapTreeCellRender.class.getResource("/resource/icon/16/094.png"));
    
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
        WebSocketTreeNode webSocketTreeNode = (WebSocketTreeNode) value;
        if(value instanceof WebSocketTreeNode){
            LOGGER.info("[WS-TREE]: TREENODE");
            webSocketTreeNode = (WebSocketTreeNode) value;
        }else if (value instanceof WebSocketHandshakeNode){
            LOGGER.info("[WS-TREE]: HANDSHAKE");
        }else  if(value instanceof  WebSocketFolderNode){
            LOGGER.info("[WS-TREE]: Foldr");
        }
        
        setPreferredSize(null);	// clears the preferred size, making the node visible
        super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
        
        if(webSocketTreeNode != null) {
            if (webSocketTreeNode.isRoot()) {
                component.add(wrap(ROOT_ICON)); // 'World' icon
            }
    
            for (ImageIcon ci : webSocketTreeNode.getCustomIcons()) {
                component.add(wrap(DisplayUtils.getScaledIcon(ci)));
            }
    
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
