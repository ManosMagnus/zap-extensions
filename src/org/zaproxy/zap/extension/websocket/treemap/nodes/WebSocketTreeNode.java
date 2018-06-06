package org.zaproxy.zap.extension.websocket.treemap.nodes;

import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.extension.websocket.treemap.WebSocketMap;
import org.zaproxy.zap.extension.websocket.treemap.WebSocketNodeType;
import org.zaproxy.zap.model.StructuralNode;

import javax.swing.ImageIcon;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import java.awt.EventQueue;
import java.util.*;

public abstract class WebSocketTreeNode extends DefaultMutableTreeNode implements StructuralNode {
    
    private static final long serialVersionUID = 230091007687312311L;
    
    private String nodeName = null;
    private String hierarchicNodeName = null;
    private boolean dataDriven = false;
    private WebSocketNodeType type;
    
    private WebSocketMap webSocketMap = null;
    private ArrayList<String> icons = null;
    
    private static Logger LOGGER = Logger.getLogger(WebSocketTreeNode.class);
    
    public WebSocketTreeNode(WebSocketMap webSocketMap, WebSocketNodeType type, String nodeName) {
        super();
        this.webSocketMap = webSocketMap;
        this.nodeName = nodeName;
        this.type = type;
        this.icons = new ArrayList<>();
    }
    
    public void setCustomIcons(ArrayList<String> i, ArrayList<Boolean> c) {
        synchronized (this.icons) {
            this.icons.clear();
            this.icons.addAll(i);
        }
    }
    
    public void addCustomIcon(String resourceName, boolean clearIfManual) {
        synchronized (this.icons) {
            if (! this.icons.contains(resourceName)) {
                this.icons.add(resourceName);
                this.nodeChanged();
            }
        }
    }
    
    public void removeCustomIcon(String resourceName) {
        synchronized (this.icons) {
            if (this.icons.contains(resourceName)) {
                int i = this.icons.indexOf(resourceName);
                this.icons.remove(i);
                this.nodeChanged();
            }
        }
    }
    
    /**
     * Gets any custom icons that have been set for this node
     * @return any custom icons that have been set for this node
     * @since 2.6.0
     */
    public List<ImageIcon> getCustomIcons() {
        List<ImageIcon> iconList = new ArrayList<ImageIcon>();
        synchronized (this.icons) {
            if (!this.icons.isEmpty()) {
                for(String icon : this.icons) {
                    iconList.add(new ImageIcon(Constant.class.getResource(icon)));
                }
            }
        }
        return iconList;
    }
    
    
    public String getHierarchicNodeName() {
        return getHierarchicNodeName(true);
    }
    
    public String getHierarchicNodeName(boolean specialNodesAsRegex) {
        if (hierarchicNodeName != null && specialNodesAsRegex) {
            // The regex version is used most frequently, so cache
            return hierarchicNodeName;
        }
        
        if (this.isRoot()) {
            hierarchicNodeName = "";
        } else if (this.getParent().isRoot()) {
            hierarchicNodeName = this.getNodeName();
        } else {
            String name =
                    this.getParent().getHierarchicNodeName(specialNodesAsRegex) + "/" +
                            this.getCleanNodeName(specialNodesAsRegex);
            if (!specialNodesAsRegex) {
                // Dont cache the non regex version
                return name;
            }
            hierarchicNodeName = name;
        }
        return hierarchicNodeName;
    }
    
    
    public boolean isParentOf (String nodeName) {
        if (nodeName == null) {
            return false;
        }
        return nodeName.compareTo(this.nodeName) < 0;
    }
    
    public String getNodeName() {
        return this.nodeName;
    }
    
    /**
     * Returns the node's name without the HTTP Method (verb) prefixing the string.
     *
     * @return the name of the site node
     * @see #getNodeName()
     * @see #getCleanNodeName()
     * @see #getCleanNodeName(boolean)
     * @since 2.7.0
     */
    public String getName() {
        String name = this.getNodeName();
        if (this.isLeaf()) {
            int colonIndex = name.indexOf(":");
            if (colonIndex > 0) {
                // Strip the GET/POST etc off
                name = name.substring(colonIndex+1);
            }
        }
        return name;
    }
    
    public String getCleanNodeName() {
        return getCleanNodeName(true);
    }
    
    public String getCleanNodeName(boolean specialNodesAsRegex) {
        String name = this.getNodeName();
        if (specialNodesAsRegex && this.isDataDriven()) {
            // Non-greedy regex pattern
            name = "(.+?)";
            
        } else if (this.isLeaf()) {
            int colonIndex = name.indexOf(":");
            if (colonIndex > 0) {
                // Strip the GET/POST etc off
                name = name.substring(colonIndex+1);
            }
            int bracketIndex = name.lastIndexOf("(");
            if (bracketIndex > 0) {
                // Strip the param summary off
                name = name.substring(0, bracketIndex);
            }
            int quesIndex = name.indexOf("?");
            if (quesIndex > 0) {
                // Strip the parameters off
                name = name.substring(0, quesIndex);
            }
        }
        return name;
    }
    
    
    private void nodeChanged() {
        if (this.webSocketMap == null || !View.isInitialised()) {
            return;
        }
        if (EventQueue.isDispatchThread()) {
            nodeChangedEventHandler();
        } else {
            try {
                EventQueue.invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        nodeChangedEventHandler();
                    }
                });
            } catch (Exception e) {
                LOGGER.error(e.getMessage(), e);
            }
        }
    }
    
    
    private void nodeChangedEventHandler() {
        this.webSocketMap.nodeChanged(this);
    }
    
    public boolean isDataDriven() {
        return dataDriven;
    }
    
    /**
     * Returns this node's parent or null if this node has no parent.
     *
     * @return  this node's parent SiteNode, or null if this node has no parent
     */
    @Override
    public WebSocketTreeNode getParent() {
        return (WebSocketTreeNode)super.getParent();
    }
    
    @Override
    public void setParent(MutableTreeNode newParent) {
        if (newParent == this) {
            return;
        }
        super.setParent(newParent);
    }
    
    public WebSocketNodeType getType() {
        return type;
    }
    
    public void setType(WebSocketNodeType type) {
        this.type = type;
    }
    
    @Override
    public String toString() {
        return nodeName;
    }
}
