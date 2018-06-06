package org.zaproxy.zap.extension.websocket.treemap;


import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.AbstractPanel;
import org.parosproxy.paros.model.Model;
import org.zaproxy.zap.extension.websocket.ExtensionWebSocket;
import org.zaproxy.zap.extension.websocket.WebSocketMessage;
import org.zaproxy.zap.extension.websocket.WebSocketObserver;
import org.zaproxy.zap.extension.websocket.WebSocketProxy;
import org.zaproxy.zap.extension.websocket.ui.WebSocketPanel;
import org.zaproxy.zap.utils.DisplayUtils;
import org.zaproxy.zap.view.LayoutHelper;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.*;
import java.awt.*;
import java.awt.event.KeyEvent;

public class WebSocketMapPanel extends AbstractPanel implements WebSocketObserver {
    private static final long serialVersionUID =  00000001000010001L;
    
    public static final int WEBSOCKET_OBSERVING_ORDER = WebSocketPanel.WEBSOCKET_OBSERVING_ORDER + 1;
    
    public static final ImageIcon disconnectIcon;
    public static final ImageIcon connectIcon;
    
    public static final ImageIcon disconnectTargetIcon;
    public static final ImageIcon connectTargetIcon;
    
    private JToolBar panelToolbar = null;
    
    private JButton addNewConnectionButton = null;
    private JTree websocketTree = null;
    
    static {
        disconnectIcon = new ImageIcon(WebSocketMapPanel.class.getResource("/resource/icon/fugue/plug-disconnect.png"));
        connectIcon = new ImageIcon(WebSocketMapPanel.class.getResource("/resource/icon/fugue/plug-connect.png"));
        
        disconnectTargetIcon = new ImageIcon(WebSocketMapPanel.class.getResource("/resource/icon/fugue/plug-disconnect-target.png"));
        connectTargetIcon = new ImageIcon(WebSocketMapPanel.class.getResource("/resource/icon/fugue/plug-connect-target.png"));
    };
    
    
    private static final Logger LOGGER = Logger.getLogger(WebSocketMapPanel.class);
    
    private ExtensionWebSocket extensionWebSocket;
    
    private WebSocketMap webSocketMap;
    
    /**
     * Constructor which initialize the Panel
     */
    public WebSocketMapPanel(ExtensionWebSocket extensionWebSocket, WebSocketMap webSocketMap){
        super();
        this.extensionWebSocket = extensionWebSocket;
        this.webSocketMap = webSocketMap;
        initialize();
    }
    
    private void initialize(){
        this.setHideable(true);
        this.setIcon(disconnectIcon);
        this.setName(Constant.messages.getString("websocket.treemap.title"));
        this.setDefaultAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.SHIFT_DOWN_MASK, false));
    
        if (Model.getSingleton().getOptionsParam().getViewParam().getWmUiHandlingOption() == 0) {
            this.setSize(300,200);
        }
        
        this.setLayout(new GridBagLayout());
        this.add(getPanelToolbar(), LayoutHelper.getGBC(0, 0, 1, 0, new Insets(2,2,2,2)));
        this.add(new WebSocketTreePanel(getTreeSite(), "sitesPanelScrollPane"),
                LayoutHelper.getGBC(0, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, new Insets(2,2,2,2)));
    
        expandRoot();
    
    }
    
    private javax.swing.JToolBar getPanelToolbar() {
        if (panelToolbar == null) {
            
            panelToolbar = new javax.swing.JToolBar();
            panelToolbar.setLayout(new GridBagLayout());
            panelToolbar.setEnabled(true);
            panelToolbar.setFloatable(false);
            panelToolbar.setRollover(true);
            panelToolbar.setPreferredSize(new Dimension(800,30));
            panelToolbar.setName("WebSocket Toolbar");
            
            int i = 1;
            panelToolbar.add(getAddNewConnectionButton(), LayoutHelper.getGBC(i++, 0, 1, 0.0D));
            
        }
        return panelToolbar;
    }
    
    private JButton getAddNewConnectionButton(){
        if(addNewConnectionButton == null){
            addNewConnectionButton = new JButton();
            addNewConnectionButton.setIcon(DisplayUtils.getScaledIcon(new ImageIcon(WebSocketMapPanel.class.getResource("/org/zaproxy/zap/extension/websocket/resources/icons/plug--plus.png"))));
            addNewConnectionButton.setToolTipText(Constant.messages.getString("websocket.treemap.button.add_new_connection"));
            //TODO: Add Listener
        }
        return addNewConnectionButton;
        
    }
    
    /**
     * This method initializes treeSite
     *
     * @return javax.swing.JTree
     */
    public JTree getTreeSite() {
        if (websocketTree == null) {
            websocketTree = new JTree(webSocketMap);
            websocketTree.setShowsRootHandles(true);
            websocketTree.setName("treeSite");
            websocketTree.setToggleClickCount(1);
            
            // Force macOS L&F to query the row height from SiteMapTreeCellRenderer to hide the filtered nodes.
            // Other L&Fs hide the filtered nodes by default.
            LookAndFeel laf = UIManager.getLookAndFeel();
            if (laf != null && Constant.isMacOsX()
                    && UIManager.getSystemLookAndFeelClassName().equals(laf.getClass().getName())) {
                websocketTree.setRowHeight(0);
            }
            
            websocketTree.addTreeSelectionListener(new TreeSelectionListener() {
                
                @Override
                public void valueChanged(TreeSelectionEvent e) {
                
//                    WebSocketTreeNode node = (WebSocketTreeNode) websocketTree.getLastSelectedPathComponent();
//                    if (node == null) {
//                        return;
//                    }
//                    if (!node.isRoot()) {
//                        HttpMessage msg = null;
//                        try {
//                            msg = node.getHistoryReference().getHttpMessage();
//                        } catch (Exception e1) {
//                            // ZAP: Log exceptions
//                            log.warn(e1.getMessage(), e1);
//                            return;
//
//                        }
//
//                        getView().displayMessage(msg);
//
//                        // ZAP: Call SiteMapListenners
//                        for (SiteMapListener listener : listeners) {
//                            listener.nodeSelected(node);
//                        }
//                    } else {
//                        // ZAP: clear the views when the root is selected
//                        getView().displayMessage(null);
//                    }
//
                }
            });
//            websocketTree.setComponentPopupMenu(new SitesCustomPopupMenu());
            
            // ZAP: Add custom tree cell renderer.
            DefaultTreeCellRenderer renderer = new WebSocketMapTreeCellRender(null);
            websocketTree.setCellRenderer(renderer);
            
            String deleteSiteNode = "zap.delete.sitenode";
//            websocketTree.getInputMap().put(getView().getDefaultDeleteKeyStroke(), deleteSiteNode);
//            websocketTree.getActionMap().put(deleteSiteNode, new AbstractAction() {
//
//                private static final long serialVersionUID = 1L;
//
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    ExtensionHistory extHistory = Control.getSingleton().getExtensionLoader().getExtension(
//                            ExtensionHistory.class);
//                    if (extHistory == null || websocketTree.getSelectionCount() == 0) {
//                        return;
//                    }
//
//                    int result = View.getSingleton().showConfirmDialog(Constant.messages.getString("sites.purge.warning"));
//                    if (result != JOptionPane.YES_OPTION) {
//                        return;
//                    }
//
//                    SiteMap siteMap = Model.getSingleton().getSession().getSiteTree();
//                    for (TreePath path : websocketTree.getSelectionPaths()) {
//                        extHistory.purge(siteMap, (SiteNode) path.getLastPathComponent());
//                    }
//                }
//            });
        }
        return websocketTree;
    }
    
    
    public void expandRoot() {
        TreeNode root = (TreeNode) websocketTree.getModel().getRoot();
        if (root == null) {
            return;
        }
        final TreePath rootTreePath = new TreePath(root);
        
        if (EventQueue.isDispatchThread()) {
            getTreeSite().expandPath(rootTreePath);
            return;
        }
        try {
            EventQueue.invokeLater(new Runnable() {
                @Override
                public void run() {
                    getTreeSite().expandPath(rootTreePath);
                }
            });
        } catch (Exception e) {
            // ZAP: Log exceptions
            LOGGER.warn(e.getMessage(), e);
        }
    }
    
    @Override
    public int getObservingOrder() {
        return WEBSOCKET_OBSERVING_ORDER;
    }
    
    @Override
    public boolean onMessageFrame(int channelId, WebSocketMessage message) {
        return false;
    }
    
    @Override
    public void onStateChange(WebSocketProxy.State state, WebSocketProxy proxy) {
    
    }
}
