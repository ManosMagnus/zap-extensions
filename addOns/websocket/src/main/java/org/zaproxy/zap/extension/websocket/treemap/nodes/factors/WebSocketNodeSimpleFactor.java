package org.zaproxy.zap.extension.websocket.treemap.nodes.factors;

import org.apache.log4j.Logger;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.WebSocketProxy;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketFolderNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketHandshakeNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketHostFolderNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketTreeNode;
import org.zaproxy.zap.extension.websocket.treemap.nodes.namers.WebSocketNodeNamer;

public class WebSocketNodeSimpleFactor implements WebSocketNodeFactor {

    private static final Logger LOGGER = Logger.getLogger(WebSocketNodeSimpleFactor.class);

    private WebSocketNodeNamer namer;
    
    public WebSocketNodeSimpleFactor(WebSocketNodeNamer namer){
        this.namer = namer;
    }

    @Override
    public WebSocketTreeNode getTreeNode(WebSocketFolderNode<WebSocketHostFolderNode> root,
                                         WebSocketMessageDTO webSocketMessage) {
        return null;
    }
    
    @Override
    public WebSocketTreeNode getTreeNode(WebSocketFolderNode<WebSocketHostFolderNode> root,
                                         WebSocketProxy proxy) {
        
        try {
            HttpMessage handshake = proxy.getHandshakeReference().getHttpMessage();
            WebSocketHostFolderNode hostFolderNode = getHostFolder(root, handshake, proxy);
            
            if(!hostFolderNode.hasHandshakeFolder()){
                hostFolderNode.addHandshakeFolder(WebSocketFolderNode.getHandshakeFolderNode(namer));
            }

            WebSocketHandshakeNode newHandshakeNode = new WebSocketHandshakeNode(proxy.getHandshakeReference(), proxy.getDTO());
            hostFolderNode.getHandshakeFolder().addChild(newHandshakeNode);
            return newHandshakeNode;

        } catch (Exception e) {
            LOGGER.info("Can't get Http Handshake request from History Reference", e);
            return null;
        }
    }
    
    private WebSocketHostFolderNode getHostFolder(WebSocketFolderNode<WebSocketHostFolderNode> root,
                                                  HttpMessage handshakeMessage,
                                                  WebSocketProxy proxy){

        for(WebSocketHostFolderNode hostNode : root.getChildren()){
            if(hostNode.equals(handshakeMessage, proxy.getDTO())){
                return hostNode;
            }
        }
        
        WebSocketHostFolderNode hostNode = new WebSocketHostFolderNode(handshakeMessage, proxy);
        hostNode.setName(namer);
        
        return hostNode;
    }
}
