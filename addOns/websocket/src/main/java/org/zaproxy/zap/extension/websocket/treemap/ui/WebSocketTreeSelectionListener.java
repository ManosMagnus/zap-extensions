package org.zaproxy.zap.extension.websocket.treemap.ui;

import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.zaproxy.zap.extension.websocket.treemap.nodes.structural.WebSocketNodeInterface;

public class WebSocketTreeSelectionListener  implements TreeSelectionListener {

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {

        if(treeSelectionEvent.getNewLeadSelectionPath() == null) return;

        WebSocketNodeInterface node = (WebSocketNodeInterface) treeSelectionEvent.getNewLeadSelectionPath();

        if(!node.getParent().isRoot()){

        }
    }

}
