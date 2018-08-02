package org.zaproxy.zap.extension.websocket.treemap.nodes;

import org.apache.commons.httpclient.URI;
import org.parosproxy.paros.model.HistoryReference;
import org.zaproxy.zap.extension.websocket.WebSocketChannelDTO;
import org.zaproxy.zap.extension.websocket.WebSocketMessageDTO;

import java.util.Iterator;
import java.util.List;

public interface StructuralWebSocketNode{
    
    
    StructuralWebSocketNode getParent();
    
    Iterator<StructuralWebSocketNode> getChildrenIterator();
    
    WebSocketMessageDTO getWebSocketMessageDTO();
    
    WebSocketChannelDTO getWebSocketChannelDTO();
    
    List<HistoryReference> getHandshakeMessage();
    
    List<StructuralWebSocketNode> getChildren();
    
    String getNodeName();
	
	void setNodeName(String nodeName);
    
    URI getURI();
    
    boolean isRoot();
    
    boolean isLeaf();
    
    WebSocketNodeType getNodeType();
    
    boolean isDataDriven();
    
    int getChildCount();
    
    boolean addChild(StructuralWebSocketNode child);
    
    boolean addParent(StructuralWebSocketNode parent);
    
    boolean addChildAt(int pos, StructuralWebSocketNode webSocketNode);
    
    boolean removeChildAt(int pos);
    
    boolean removeChild(StructuralWebSocketNode structuralWebSocketNode);
    
    StructuralWebSocketNode getChildAt(int i);
    
    StructuralWebSocketNode getFirstTypeTopDown(WebSocketNodeType webSocketNodeType);
    
    StructuralWebSocketNode getFirstTypeBottomUp(WebSocketNodeType webSocketNodeType);
    
    void setNodeIndex(int[] nodeIndex);
    
    int[] getNodeIndex();
}
