package org.zaproxy.zap.extension.websocket.treemap;

import org.zaproxy.zap.ZAP;
import org.zaproxy.zap.eventBus.EventPublisher;

public class WebSocketMapEventPublisher implements EventPublisher {
    private static WebSocketMapEventPublisher publisher = null;
    public static final String SITE_NODE_ADDED_EVENT	= "siteNode.added";
    public static final String SITE_NODE_REMOVED_EVENT	= "siteNode.removed";
    public static final String SITE_ADDED_EVENT	= "site.added";
    public static final String SITE_REMOVED_EVENT	= "site.removed";
    
    @Override
    public String getPublisherName() {
        return WebSocketMapEventPublisher.class.getCanonicalName();
    }
    
    public static synchronized WebSocketMapEventPublisher getPublisher() {
        if (publisher == null) {
            publisher = new WebSocketMapEventPublisher();
            ZAP.getEventBus().registerPublisher(publisher,
                    new String[] {SITE_NODE_ADDED_EVENT, SITE_NODE_REMOVED_EVENT, SITE_ADDED_EVENT, SITE_REMOVED_EVENT});
            
        }
        return publisher;
    }
    
}
