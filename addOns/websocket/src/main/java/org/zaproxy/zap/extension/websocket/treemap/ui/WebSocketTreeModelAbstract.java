package org.zaproxy.zap.extension.websocket.treemap.ui;

import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.tree.TreeModel;
import java.util.ArrayList;
import java.util.List;

abstract public class WebSocketTreeModelAbstract implements TreeModel {

    private List<TreeModelListener> treeModelListeners;

    public WebSocketTreeModelAbstract() {
        this.treeModelListeners = new ArrayList<>();
    }

    protected void fireTreeNodesChanged(TreeModelEvent event){
        for(TreeModelListener listener : treeModelListeners){
            listener.treeNodesChanged(event);
        }
    }

    protected void fireTreeNodesInserted(TreeModelEvent event){
        for(TreeModelListener listener : treeModelListeners){
            listener.treeNodesInserted(event);
        }
    }

    protected void fireTreeNodesRemoved(TreeModelEvent event){
        for(TreeModelListener listener : treeModelListeners){
            listener.treeNodesRemoved(event);
        }
    }

    protected void fileTreeStructureChanged(TreeModelEvent event){
        for(TreeModelListener listener : treeModelListeners){
            listener.treeNodesRemoved(event);
        }
    }

    @Override
    public void addTreeModelListener(TreeModelListener treeModelListener) {
        treeModelListeners.add(treeModelListener);
    }

    @Override
    public void removeTreeModelListener(TreeModelListener treeModelListener) {
        treeModelListeners.remove(treeModelListener);
    }

}
