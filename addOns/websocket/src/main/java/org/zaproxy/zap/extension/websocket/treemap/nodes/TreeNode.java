package org.zaproxy.zap.extension.websocket.treemap.nodes;

import java.util.Iterator;
import java.util.List;

abstract public class TreeNode<P extends TreeNode, C extends TreeNode> {

    protected String name;
    protected P parent;
    protected List<C> children;

    public boolean isRoot() {
        return (parent==null);
    }

    public P getParent() {
        return parent;
    }

    public List<C> getChildren() {
        return children;
    }

    public int getChildCount() {
        return children.size();
    }

    public boolean isLeaf(){
        return children.isEmpty();
    }

    public boolean addChild(C child){
        if(!children.contains(child)){
            children.add(child);
            return true;
        }
        return false;
    }

    public boolean addChild(int at, C child){
        if(!children.contains(child)){
            children.add(at, child);
            return true;
        }
        return false;
    }
    
    public List<TreeNode> getSiblings(){
        return parent.getChildren();
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }

    protected interface HandleNode<R, T, F>{
        Iterator<T> handle(R root, F function);
    }
}
