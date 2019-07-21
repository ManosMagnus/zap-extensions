package org.zaproxy.zap.extension.websocket.treemap.nodes;

import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.websocket.WebSocketChannelDTO;
import org.zaproxy.zap.extension.websocket.WebSocketProxy;
import org.zaproxy.zap.extension.websocket.treemap.nodes.namers.WebSocketNodeNamer;

import java.util.Iterator;
import java.util.function.Function;

public class WebSocketHostFolderNode extends WebSocketFolderNode<WebSocketFolderNode> implements WebSocketTreeNode{
    
    private String hostName;
    private String host;
    private WebSocketFolderNode<WebSocketHandshakeNode> handshakeFolder = null;
    private WebSocketFolderNode<WebSocketMessageNode> messagesFolder = null;
    
    public WebSocketHostFolderNode(HttpMessage handshake, WebSocketProxy proxy){
        super(FolderType.HOST);
        this.host = proxy.getDTO().host;
        hostName = WebSocketNodeUtilities.getHostName(handshake, proxy.getDTO());
    }
    
    public boolean addHandshakeFolder(WebSocketFolderNode<WebSocketHandshakeNode> handshakeFolder){
        if(handshakeFolder != null){
            this.handshakeFolder = handshakeFolder;
            return true;
        }
        return false;
    }
    
    public boolean addMessagesFolder(WebSocketFolderNode<WebSocketMessageNode> messagesNode){
        if(messagesNode != null){
            this.messagesFolder = messagesNode;
            return true;
        }
        return false;
    }
    
    public boolean hasHandshakeFolder(){
        return handshakeFolder != null;
    }
    
    
    public boolean hasMessagesFolder(){
        return messagesFolder != null;
    }
    
    public WebSocketFolderNode<WebSocketHandshakeNode> getHandshakeFolder(){
        return handshakeFolder;
    }
    
    public WebSocketFolderNode<WebSocketMessageNode> getMessagesFolder(){
        return messagesFolder;
    }
    
    public WebSocketFolderNode getChild(FolderType type){
        for(WebSocketFolderNode folder : children){
            if(folder.folderType == type){
                return folder;
            }
        }
        return null;
    }
    
    public String getHostName() {
        return hostName;
    }
    
    @Override
    public boolean equals(Object o) {
        if(!(o instanceof WebSocketHostFolderNode)) {
            return false;
        }
        
        WebSocketHostFolderNode hostFolder = (WebSocketHostFolderNode) o;
        if(!this.equals(hostFolder.getHostName())){
            return false;
        }
        
        return true;
    }
    
    public boolean equals(HttpMessage handshake, WebSocketChannelDTO channel){
        return this.hostName.equals(WebSocketNodeUtilities.getHostName(handshake, channel));
    }
    
    @Override
    public void setName(WebSocketNodeNamer namer) {
        super.name = namer.getName(this);
    }

    private class HandleHost<T> implements HandleNode<WebSocketHostFolderNode, T, Function<WebSocketFolderNode, Iterator<T>>>{

        @Override
        public Iterator<T> handle(WebSocketHostFolderNode host, Function<WebSocketFolderNode, Iterator<T>> function) {
            return function.apply(host.getHandshakeFolder());
        }
    }

}
