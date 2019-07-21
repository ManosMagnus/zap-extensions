package org.zaproxy.zap.extension.websocket.treemap.nodes;

import com.google.common.collect.Iterators;
import org.parosproxy.paros.network.HttpMessage;
import org.zaproxy.zap.extension.websocket.WebSocketChannelDTO;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;
import org.zaproxy.zap.extension.websocket.treemap.nodes.namers.WebSocketNodeNamer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.function.Function;

public class WebSocketFolderNode<P extends TreeNode, C extends TreeNode> extends TreeNode<P, C> implements WebSocketTreeNode{
    
    public enum FolderType{
        ROOT, HOST, HANDSHAKE, MESSAGES, HEARTBEAT, CLOSE
    }
    
    protected FolderType folderType;
    protected FolderHandler<HttpMessage> handshakesHandler;
    protected FolderHandler<WebSocketChannelDTO> channelsHandler;
    
    public static WebSocketFolderNode<WebSocketHostFolderNode, WebSocketHandshakeNode>
    getHandshakeFolderNode(WebSocketNodeNamer namer){
        WebSocketFolderNode<WebSocketHostFolderNode, WebSocketHandshakeNode> handshakeFolder = new WebSocketFolderNode<>(FolderType.HANDSHAKE);
        handshakeFolder.setName(namer);
        return handshakeFolder;
    }

    public static WebSocketFolderNode<TreeNode,WebSocketHostFolderNode>
    getRootNode(WebSocketNodeNamer namer){
        WebSocketFolderNode<TreeNode,WebSocketHostFolderNode> rootNode = new WebSocketFolderNode<>(FolderType.ROOT);
        rootNode.setName(namer);
        return rootNode;
    }

    public static WebSocketFolderNode<WebSocketHostFolderNode, WebSocketMessageNode>
    getMessagesFolderNode(WebSocketNodeNamer namer){
        WebSocketFolderNode<WebSocketHostFolderNode, WebSocketMessageNode> messagesNode = new WebSocketFolderNode<>(FolderType.MESSAGES);
        messagesNode.setName(namer);
        return messagesNode;
    }

    protected void setHandshakesHandler(FolderHandler<HttpMessage> handshakesHandler) {
        this.handshakesHandler = handshakesHandler;
    }

    protected void setChannelsHandler(FolderHandler<WebSocketChannelDTO> channelsHandler) {
        this.channelsHandler = channelsHandler;
    }

    protected WebSocketFolderNode(FolderType type){
        this.folderType = type;
    }
    
    public FolderType getFolderType() {
        return folderType;
    }


    
    @Override
    public P getParent() {
        return super.getParent();
    }
    
    @Override
    public List<C> getChildren() {
        return super.getChildren();
    }
    
    
    @Override
    public Iterator<HttpMessage> getHandshakes() {
        return null;
    }
    
    @Override
    public Iterator<WebSocketMessageDTO> getMessages() {
        return null;
    }
    
    @Override
    public Iterator<WebSocketChannelDTO> getChannels() {
        return null;
    }
    
    @Override
    public void setName(WebSocketNodeNamer namer) {
        super.name = namer.getName(this);
    }


    /**
     *
     * @param <T> The type Which returns after employing {@link FolderHandler#handle(WebSocketFolderNode, Function)}
     * @param <C> The type of node's child/parent
     * @param <F> The type tha the {@link Function} returns
     */
    private abstract class FolderHandler<T, C extends TreeNode, F> implements HandleNode<WebSocketFolderNode<P, C>, T, Function<C, F>>{
        @Override
        abstract public Iterator<T> handle(WebSocketFolderNode<P, C> root, Function<C, F> function);
    }


    private class HandleRoot<T> extends FolderHandler<T, WebSocketHostFolderNode<>, Iterator<T>>{
        @Override
        public Iterator<T> handle(WebSocketFolderNode<P, WebSocketHostFolderNode> root, Function<WebSocketHostFolderNode, Iterator<T>> function) {
            Iterator<T> resultIterator = Collections.emptyIterator();

            for(WebSocketHostFolderNode host : root.getChildren()){
                resultIterator = Iterators.concat(resultIterator, function.apply(host));
            }

            return resultIterator;
        }
    }

    private class HandleHandshakeBranch<T> extends FolderHandler<T, WebSocketHandshakeNode, T>{

        @Override
        public Iterator<T> handle(WebSocketFolderNode<P, WebSocketHandshakeNode> root, Function<WebSocketHandshakeNode, T> function) {
            List<T> list = new ArrayList<>();
            for(WebSocketHandshakeNode child : root.getChildren()){
                list.add(function.apply(child));
                return list.iterator();
            }
            return Collections.emptyIterator();
        }
    }


    private class HandleSiblings<T> extends FolderHandler<T, WebSocketHostFolderNode, Iterator<T>>{

        @Override
        public Iterator<T> handle(WebSocketFolderNode<WebSocketHostFolderNode, C> root, Function<P, Iterator<T>> function) {

        }

        @Override
        public Iterator<T> handle(WebSocketFolderNode<W, WebSocketHostFolderNode> root, Function<WebSocketHostFolderNode, Iterator<T>> function) {
            return function.apply((P) root.getParent());
            return null;
        }
    }
    
}
