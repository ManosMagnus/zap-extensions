package org.zaproxy.zap.extension.websocket.treemap;

import org.apache.commons.httpclient.URI;
import org.apache.commons.httpclient.URIException;
import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.model.*;
import org.parosproxy.paros.network.*;
import org.parosproxy.paros.view.View;
import org.zaproxy.zap.ZAP;
import org.zaproxy.zap.eventBus.Event;
import org.zaproxy.zap.extension.websocket.WebSocketChannelDTO;
import org.zaproxy.zap.extension.websocket.WebSocketProxy;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketFolderNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketHandshakeNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketTreeNode;
import org.zaproxy.zap.model.StructuralNode;
import org.zaproxy.zap.model.Target;


import javax.swing.tree.DefaultTreeModel;
import java.awt.EventQueue;
import java.security.InvalidParameterException;
import java.util.*;

public class WebSocketMap extends SortedTreeModel{
    
    private static final long serialVersionUID = 000001111101110112L;
    
    private Logger LOGGER = Logger.getLogger(WebSocketMap.class);
    
    private enum EventType {ADD, REMOVE};
    
    private Model model;
    
    private static Map<Integer, WebSocketTreeNode> hrefMap = new HashMap<>();
    
    private static Map<Integer, WebSocketTreeNode> channelMap = new HashMap<>();
    
    public WebSocketMap(WebSocketTreeNode root, Model model){
        super(root);
        this.model = model;
    }
    
    public static WebSocketMap createTree(Model model){
        WebSocketMap webSocketMap = new WebSocketMap(null, model);
        webSocketMap.setRoot(WebSocketFolderNode.getRootNode(webSocketMap));
        return webSocketMap;
    }
    
    
    // returns a representation of the host name in the site map
    private String getHostName(URI uri, WebSocketChannelDTO webSocketChannelDTO) throws URIException {
        StringBuilder host = new StringBuilder();
        
        String scheme = uri.getScheme();
        if (scheme == null) {
            scheme = "http";
        } else {
            scheme = scheme.toLowerCase();
        }
        host.append(scheme).append("://").append(uri.getHost());
        
        int port = webSocketChannelDTO.port;
        if (port != -1 &&
                ((port == 80 && !"ws".equals(scheme)) ||
                        (port == 443 && !"wss".equals(scheme) ||
                                (port != 80 && port != 443)))) {
            host.append(":").append(port);
        }
        
        return host.toString();
    }
    
    public synchronized WebSocketTreeNode addNewConnection(WebSocketProxy webSocketProxy, boolean newOnly){
        if (Constant.isLowMemoryOptionSet()) {
            throw new InvalidParameterException("SiteMap should not be accessed when the low memory option is set");
        }
        
        HttpMessage handshakeMessage = null;
        try{
            handshakeMessage = webSocketProxy.getHandshakeReference().getHttpMessage();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(),e);
            return null;
        }
    
        if (View.isInitialised() && Constant.isDevBuild() && ! EventQueue.isDispatchThread()) {
            // In developer mode log an error if we're not on the EDT
            // Adding to the site tree on GUI ('initial') threads causes problems
            LOGGER.error("SiteMap.addPath not on EDT " + Thread.currentThread().getName(), new Exception());
        }
        
        URI uri = handshakeMessage.getRequestHeader().getURI();
        LOGGER.info("[WS-TREEMAP] addConnection: " + uri.toString());
        WebSocketTreeNode parent = (WebSocketTreeNode) getRoot();
        WebSocketTreeNode leaf = null;
        String folder = "";
        boolean isNew = false;
        
        try {
            String host = getHostName(uri,webSocketProxy.getDTO());
            
            //add
            parent = findAndAddChild(parent,host,webSocketProxy.getChannelId(),webSocketProxy.getHandshakeReference(),handshakeMessage);
    
            List<String> path = model.getSession().getTreePath(handshakeMessage);
            LOGGER.info("[WS-TREEMAP] getTreePath");
            int j = 0;
            for(String str : path){
                LOGGER.info("[WS-TREEMAP] " + j++ + " : " + str);
            }
//            insertNodeInto(null,new WebSocketFolderNode(this,WebSocketNodeType.HANDSHAKE,"Handshake"));
            for (int i=0; path != null && i < path.size(); i++) {
                folder = path.get(i);
                if (folder != null && !folder.equals("")) {
                    if (newOnly) {
                        // Check to see if it already exists
                        String leafName = getLeafName(folder, handshakeMessage);
                        isNew = (findChild(parent, leafName) == null);
                    }
                    if (i == path.size()-1) {
                        leaf = findAndAddLeaf(parent, folder, webSocketProxy.getHandshakeReference(), handshakeMessage);
//                        webSocketProxy.getHandshakeReference().setSiteNode(leaf);
                    } else {
                        parent = findAndAddChild(parent, folder,webSocketProxy.getChannelId(), webSocketProxy.getHandshakeReference(), handshakeMessage);
                    }
                }
            }
            if (leaf == null) {
                // No leaf found, which means the parent was really the leaf
                // The parent will have been added with a 'blank' href, so replace it with the real one
//                parent.setHistoryReference(ref);
                leaf = parent;
            }
            
        } catch (URIException e) {
            e.printStackTrace();
        }
        if (! newOnly || isNew) {
            return leaf;
        }
        return null;
    }
    
    private WebSocketTreeNode findAndAddLeaf(WebSocketTreeNode parent, String nodeName, HistoryReference ref, HttpMessage msg) {
        // ZAP: Added debug
        LOGGER.debug("findAndAddLeaf " + parent.getNodeName() + " / " + nodeName);
        
        String leafName = getLeafName(nodeName, msg);
        WebSocketTreeNode node = findChild(parent, leafName);
        if (node == null) {
//            if(!ref.getCustomIcons().isEmpty()){
//                node = new WebSocketHandshakeNode(this, WebSocketNodeType.HANDSHAKE, leafName);
//                node.setCustomIcons(ref.getCustomIcons(), ref.getClearIfManual());
//            } else {
            node = new WebSocketHandshakeNode(this, WebSocketNodeType.HANDSHAKE, leafName);
//            }
//            node.setHistoryReference(ref);
            
            hrefMap.put(ref.getHistoryId(), node);
            
            int pos = parent.getChildCount();
            for (int i=0; i< parent.getChildCount(); i++) {
                if (((WebSocketTreeNode)parent.getChildAt(i)).isParentOf(nodeName)) {
                    pos = i;
                    break;
                }
            }
//            // ZAP: cope with getWebSocketTreeNode() returning null
//            if (ref.getWebSocketTreeNode() == null) {
//                ref.setWebSocketTreeNode(node);
//            }
            
            insertNodeInto(node, parent, pos);
            
//            handleEvent(parent, node, EventType.ADD);
        } else if (hrefMap.get(ref.getHistoryId()) != node) {
            
            // do not replace if
            // - use local copy (304). only use if 200
            
//            if (msg.getResponseHeader().getStatusCode() == HttpStatusCode.OK) {
//                // replace node HistoryReference to this new child if this is a spidered record.
//                node.setHistoryReference(ref);
//                ref.setWebSocketTreeNode(node);
//            } else {
//                node.getPastHistoryReference().add(ref);
//                ref.setWebSocketTreeNode(node);
//            }
//            hrefMap.put(ref.getHistoryId(), node);
        }
        return node;
    }
//
//    /**
//     * Handles the publishing of the add or remove event. Node events are always published.
//     * Site events are only published when the parent of the node is the root of the tree.
//     *
//     * @param parent relevant parent node
//     * @param node the site node the action is being carried out for
//     * @param eventType the type of event occurring (ADD or REMOVE)
//     * @see EventType
//     * @since 2.5.0
//     */
//    private void handleEvent(WebSocketTreeNode parent, WebSocketTreeNode node, EventType eventType) {
//        switch (eventType) {
//            case ADD:
//                publishEvent(SiteMapEventPublisher.SITE_NODE_ADDED_EVENT, node);
//                if (parent == getRoot()) {
//                    publishEvent(SiteMapEventPublisher.SITE_ADDED_EVENT, node);
//                }
//                break;
//            case REMOVE:
//                publishEvent(SiteMapEventPublisher.SITE_NODE_REMOVED_EVENT, node);
//                if(parent == getRoot()) {
//                    publishEvent(SiteMapEventPublisher.SITE_REMOVED_EVENT, node);
//                }
//        }
//    }
    
//    /**
//     * Publish the event being carried out.
//     *
//     * @param event the event that is happening
//     * @param node the node being acted upon
//     * @since 2.5.0
//     */
//    private static void publishEvent(String event, WebSocketTreeNode node) {
//        ZAP.getEventBus().publishSyncEvent(SiteMapEventPublisher.getPublisher(), new Event(SiteMapEventPublisher.getPublisher(), event, new Target((node)));
//    }
    
    private String getLeafName(String nodeName, HttpMessage msg) {
        // add \u007f to make GET/POST node appear at the end.
        //String leafName = "\u007f" + msg.getRequestHeader().getMethod()+":"+nodeName;
        String leafName = msg.getRequestHeader().getMethod() + ":" + nodeName;
    
        leafName = leafName + getQueryParamString(msg.getParamNameSet(HtmlParameter.Type.url));
    
        // also handle POST method query in body
        if (msg.getRequestHeader().getMethod().equalsIgnoreCase(HttpRequestHeader.POST)) {
            String contentType = msg.getRequestHeader().getHeader(HttpHeader.CONTENT_TYPE);
            if (contentType != null && contentType.startsWith("multipart/form-data")) {
                leafName = leafName + "(multipart/form-data)";
            } else {
                leafName = leafName + getQueryParamString(msg.getParamNameSet(HtmlParameter.Type.form));
            }
        }
        return leafName;
    }
    private String getQueryParamString(Map<String, String> map) {
        TreeSet<String> set = new TreeSet<>();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            set.add(entry.getKey());
        }
        return this.getQueryParamString(set);
    }
    
    private String getQueryParamString(SortedSet<String> querySet) {
        StringBuilder sb = new StringBuilder();
        Iterator<String> iterator = querySet.iterator();
        for (int i=0; iterator.hasNext(); i++) {
            String name = iterator.next();
            if (name == null) {
                continue;
            }
            if (i > 0) {
                sb.append(',');
            }
            if (name.length() > 40) {
                // Truncate
                name = name.substring(0, 40);
            }
            sb.append(name);
        }
        
        String result = "";
        if (sb.length()>0) {
            result = sb.insert(0, '(').append(')').toString();
        }
        
        return result;
    }
    
    
    private WebSocketTreeNode findAndAddChild(WebSocketTreeNode parent, String nodeName,Integer channelID, HistoryReference hRef, HttpMessage baseMsg){
        LOGGER.info("[WS-TREEMAP] findAndAddChild: " + parent.getNodeName()  + " / " + nodeName );
        WebSocketTreeNode result = findChild(parent,nodeName);
        
        //There is no children. Than means was a new WebSocket Connection
        WebSocketTreeNode channelNode = null;
        if(result == null){
            LOGGER.info("[WS-TREEMAP] I can't find any children");
            
            int pos = parent.getChildCount();
            for(int i = 0; i < parent.getChildCount(); i++){
                if(((WebSocketTreeNode)parent.getChildAt(i)).isParentOf(nodeName)){
                    pos = i;
                    break;
                }
            }
            if(parent.isRoot()){
                channelNode = new WebSocketFolderNode(this, WebSocketNodeType.CHANNEL_NODE,nodeName);
                insertNodeInto(channelNode,parent);
                channelMap.put(channelID, channelNode);
    
    
                WebSocketFolderNode handshakeFolderNode = new WebSocketFolderNode(this, WebSocketNodeType.FOLDER_HANDSHAKE,"Handshakes");
                insertNodeInto(handshakeFolderNode,channelNode);
    
                
                WebSocketHandshakeNode handshakeNode = new WebSocketHandshakeNode(this, WebSocketNodeType.HANDSHAKE,nodeName);
                insertNodeInto(handshakeNode,handshakeFolderNode);
                handleEvent(handshakeFolderNode, handshakeNode, EventType.ADD);
    
                result = handshakeNode;
    
            }else{
                WebSocketHandshakeNode handshakeNode = new WebSocketHandshakeNode(this,WebSocketNodeType.HANDSHAKE,nodeName);
                insertNodeInto(handshakeNode,parent,pos);
                handleEvent(parent, handshakeNode, EventType.ADD);
                return handshakeNode;
            }
            //
//            WebSocketHandshakeNode webSocketHandshakeNode = new WebSocketHandshakeNode(this, WebSocketNodeType.FOLDER_HANDSHAKE,nodeName);
//            insertNodeInto(webSocketHandshakeNode,webSocketHandshakeNode,pos);
//            handleEvent(parent,webSocketHandshakeNode,EventType.ADD);
//            result = webSocketHandshakeNode;
        }
        return result;
    }
    
    public void insertNodeInto(WebSocketTreeNode child, WebSocketTreeNode parent, int i) {
        // The index is useless in this model, so just ignore it.
        insertNodeInto(child, parent);
    }
    
    /**
     * Searching for child with specific node name.
     * @param parent Node whitch children is going to be searched
     * @param nodeName The name of the node
     * @return node if find the requested child. In any other case null
     */
    private WebSocketTreeNode findChild(WebSocketTreeNode parent, String nodeName){
        LOGGER.info("[WS-TREEMAP] findChild: " + parent.getNodeName() + " / " + nodeName);
        
        for(int i = 0; i < parent.getChildCount(); i++){
            WebSocketTreeNode child = (WebSocketTreeNode) parent.getChildAt(i);
            if(child.getNodeName().equals(nodeName)){
                return child;
            }
        }
        return null;
    }
    
    /**
     * Handles the publishing of the add or remove event. Node events are always published.
     * Site events are only published when the parent of the node is the root of the tree.
     *
     * @param parent relevant parent node
     * @param node the site node the action is being carried out for
     * @param eventType the type of event occurring (ADD or REMOVE)
     * @see EventType
     * @since 2.5.0
     */
    private void handleEvent(WebSocketTreeNode parent, WebSocketTreeNode node, EventType eventType) {
        switch (eventType) {
            case ADD:
                publishEvent(SiteMapEventPublisher.SITE_NODE_ADDED_EVENT, node);
                if (parent == getRoot()) {
                    publishEvent(SiteMapEventPublisher.SITE_ADDED_EVENT, node);
                }
                break;
            case REMOVE:
                publishEvent(SiteMapEventPublisher.SITE_NODE_REMOVED_EVENT, node);
                if(parent == getRoot()) {
                    publishEvent(SiteMapEventPublisher.SITE_REMOVED_EVENT, node);
                }
        }
    }
    
    /**
     * Publish the event being carried out.
     *
     * @param event the event that is happening
     * @param node the node being acted upon
     * @since 2.5.0
     */
    private static void publishEvent(String event, WebSocketTreeNode node) {
//        ZAP.getEventBus().publishSyncEvent(WebSocketMapEventPublisher.getPublisher(), new Event(SiteMapEventPublisher.getPublisher(), event, new Target(node)));
    }
    
    private void connectionInitialStructure(){
    
    }
    
}



/**
 * Based on example code from:
 * <a href="http://www.java2s.com/Code/Java/Swing-JFC/AtreemodelusingtheSortTreeModelwithaFilehierarchyasinput.htm">Sorted Tree Example</a>
 */
class SortedTreeModel extends DefaultTreeModel {
    
    private static final long serialVersionUID = 4130060741120936999L;
    private Comparator<WebSocketTreeNode> comparator;
    
    public SortedTreeModel(WebSocketTreeNode node, WebSocketTreeNodeStringComparator siteNodeStringComparator) {
        super(node);
        this.comparator = siteNodeStringComparator;
    }
    
    public SortedTreeModel(WebSocketTreeNode node) {
        super(node);
        this.comparator = new WebSocketTreeNodeStringComparator();
    }
    
    public SortedTreeModel(WebSocketTreeNode node, boolean asksAllowsChildren, Comparator<WebSocketTreeNode> aComparator) {
        super(node, asksAllowsChildren);
        this.comparator = aComparator;
    }
    
    public void insertNodeInto(WebSocketTreeNode child, WebSocketTreeNode parent) {
        int index = findIndexFor(child, parent);
        super.insertNodeInto(child, parent, index);
    }
    
     public void insertNodeInto(WebSocketTreeNode child, WebSocketTreeNode parent, int i) {
        // The index is useless in this model, so just ignore it.
        insertNodeInto(child, parent);
    }
    
    private int findIndexFor(WebSocketTreeNode child, WebSocketTreeNode parent) {
        int childCount = parent.getChildCount();
        if (childCount == 0) {
            return 0;
        }
        if (childCount == 1) {
            return comparator.compare(child, (WebSocketTreeNode) parent.getChildAt(0)) <= 0 ? 0 : 1;
        }
        return findIndexFor(child, parent, 0, childCount - 1);
    }
    
    private int findIndexFor(WebSocketTreeNode child, WebSocketTreeNode parent, int idx1, int idx2) {
        if (idx1 == idx2) {
            return comparator.compare(child, (WebSocketTreeNode) parent.getChildAt(idx1)) <= 0 ? idx1 : idx1 + 1;
        }
        int half = (idx1 + idx2) / 2;
        if (comparator.compare(child, (WebSocketTreeNode) parent.getChildAt(half)) <= 0) {
            return findIndexFor(child, parent, idx1, half);
        }
        return findIndexFor(child, parent, half + 1, idx2);
    }
}

class WebSocketTreeNodeStringComparator implements Comparator<WebSocketTreeNode> {
    
    @Override
    public int compare(WebSocketTreeNode wstn1, WebSocketTreeNode wstn2) {
        String s1 = wstn1.getName();
        String s2 = wstn2.getName();
        int initialComparison = s1.compareToIgnoreCase(s2);
    
        if (initialComparison == 0) {
            s1 = wstn1.getNodeName();
            s2 = wstn2.getNodeName();
        
            return s1.compareToIgnoreCase(s2);
        }
        return initialComparison;
    }
}
