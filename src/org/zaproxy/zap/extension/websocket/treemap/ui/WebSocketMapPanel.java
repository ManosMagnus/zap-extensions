package org.zaproxy.zap.extension.websocket.treemap.ui;

import org.apache.log4j.Logger;
import org.parosproxy.paros.Constant;
import org.parosproxy.paros.extension.AbstractPanel;
import org.parosproxy.paros.model.Model;
import org.zaproxy.zap.extension.websocket.ExtensionWebSocket;
import org.zaproxy.zap.extension.websocket.WebSocketMessage;
import org.zaproxy.zap.extension.websocket.WebSocketObserver;
import org.zaproxy.zap.extension.websocket.WebSocketProxy;
import org.zaproxy.zap.extension.websocket.treemap.nodes.WebSocketTreeNode;
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
	private JTree treeContext = null;
	private JTree treeSite = null;
	private DefaultTreeModel contextTree = null;
	
	static {
		disconnectIcon = new ImageIcon(WebSocketMapPanel.class.getResource("/resource/icon/fugue/plug-disconnect.png"));
		connectIcon = new ImageIcon(WebSocketMapPanel.class.getResource("/resource/icon/fugue/plug-connect.png"));
		
		disconnectTargetIcon = new ImageIcon(WebSocketMapPanel.class.getResource("/resource/icon/fugue/plug-disconnect-target.png"));
		connectTargetIcon = new ImageIcon(WebSocketMapPanel.class.getResource("/resource/icon/fugue/plug-connect-target.png"));
	};
	
	
	private static final Logger LOGGER = Logger.getLogger(WebSocketMapPanel.class);
	
	private ExtensionWebSocket extensionWebSocket;
	
	private WebSocketMapUI webSocketMapUI;
	
	/**
	 * Constructor which initialize the Panel
	 */
	public WebSocketMapPanel(ExtensionWebSocket extensionWebSocket, WebSocketMapUI webSocketMapUI){
		super();
		this.extensionWebSocket = extensionWebSocket;
		this.webSocketMapUI = webSocketMapUI;
		initialize();
		this.setVisible(true);
	}
	
	private void initialize(){
		this.setHideable(false);
		this.setIcon(disconnectIcon);
		this.setName(Constant.messages.getString("websocket.treemap.title"));
		this.setDefaultAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask() | KeyEvent.SHIFT_DOWN_MASK, false));
		
		if (Model.getSingleton().getOptionsParam().getViewParam().getWmUiHandlingOption() == 0) {
			this.setSize(300,200);
		}
		
		this.setLayout(new GridBagLayout());
		this.add(getPanelToolbar(), LayoutHelper.getGBC(0, 0, 1, 0, new Insets(2,2,2,2)));
		this.add(new WebSocketTreePanel(getTreeSite(), "sitesPanelScrollPane"), LayoutHelper.getGBC(0, 1, 1, 1.0, 1.0, GridBagConstraints.BOTH, new Insets(2,2,2,2)));
		
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
			//TODO: Check Those References
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
		if (treeContext == null) {
			
			treeContext = new JTree(webSocketMapUI);
			treeContext.setShowsRootHandles(true);
			treeContext.setName("treeSite");
			treeContext.setToggleClickCount(1);
			
			// Force macOS L&F to query the row height from SiteMapTreeCellRenderer to hide the filtered nodes.
			// Other L&Fs hide the filtered nodes by default.
			LookAndFeel laf = UIManager.getLookAndFeel();
			if (laf != null && Constant.isMacOsX()
					&& UIManager.getSystemLookAndFeelClassName().equals(laf.getClass().getName())) {
				treeContext.setRowHeight(0);
			}
			
			treeContext.addTreeSelectionListener(new TreeSelectionListener() {
				
				@Override
				public void valueChanged(TreeSelectionEvent e) {

//                    WebSocketTreeNode node = (WebSocketTreeNode) treeContext.getLastSelectedPathComponent();
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
//            treeContext.setComponentPopupMenu(new SitesCustomPopupMenu());
			
			// ZAP: Add custom tree cell renderer.
			DefaultTreeCellRenderer renderer = new WebSocketMapTreeCellRender();
			treeContext.setCellRenderer(renderer);
			
			String deleteSiteNode = "zap.delete.sitenode";
//            treeContext.getInputMap().put(getView().getDefaultDeleteKeyStroke(), deleteSiteNode);
//            treeContext.getActionMap().put(deleteSiteNode, new AbstractAction() {
//
//                private static final long serialVersionUID = 1L;
//
//                @Override
//                public void actionPerformed(ActionEvent e) {
//                    ExtensionHistory extHistory = Control.getSingleton().getExtensionLoader().getExtension(
//                            ExtensionHistory.class);
//                    if (extHistory == null || treeContext.getSelectionCount() == 0) {
//                        return;
//                    }
//
//                    int result = View.getSingleton().showConfirmDialog(Constant.messages.getString("sites.purge.warning"));
//                    if (result != JOptionPane.YES_OPTION) {
//                        return;
//                    }
//
//                    SiteMap siteMap = Model.getSingleton().getSession().getSiteTree();
//                    for (TreePath path : treeContext.getSelectionPaths()) {
//                        extHistory.purge(siteMap, (SiteNode) path.getLastPathComponent());
//                    }
//                }
//            });
		}
		return treeContext;
	}
	
	private JTree getTreeContext() {
		if (treeContext == null) {
			reloadContextTree();
			treeContext = new JTree(this.contextTree);
			treeContext.setShowsRootHandles(true);
//			treeContext.setName(CONTEXT_TREE_COMPONENT_NAME);
			treeContext.setToggleClickCount(1);
			treeContext.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
			
			treeContext.addMouseListener(new java.awt.event.MouseAdapter() {
				@Override
				public void mousePressed(java.awt.event.MouseEvent e) {
				}
				
				@Override
				public void mouseReleased(java.awt.event.MouseEvent e) {
					mouseClicked(e);
				}
				
				@Override
				public void mouseClicked(java.awt.event.MouseEvent e) {
					if (treeSite.getLastSelectedPathComponent() != null) {
						// They selected a context node, deselect any context
						getTreeSite().clearSelection();
					}
					TreePath path = treeContext.getClosestPathForLocation(e.getX(), e.getY());
					if (path != null && !treeContext.isPathSelected(path)) {
						treeContext.setSelectionPath(path);
					}
					if (e.getClickCount() > 1) {
						// Its a double click - show the relevant context dialog
						WebSocketNodeUI node = (WebSocketNodeUI) treeContext.getLastSelectedPathComponent();
						if (node != null && node.getUserObject() != null) {
//							Target target = (Target)node.getUserObject();
//							String panelName = ContextGeneralPanel.getPanelName(target.getContext());
//							getView().getSessionDialog().expandParamPanelNode(panelName);
//							if (getView().getSessionDialog().isParamPanelOrChildSelected(panelName)) {
//								panelName = null;
//							}
//							getView().showSessionDialog(Model.getSingleton().getSession(), panelName);
						}
					}
				}
			});
//			treeContext.setComponentPopupMenu(new ContextsCustomPopupMenu());
			
//			treeContext.setCellRenderer(new ContextsTreeCellRenderer());
//			DeleteContextAction delContextAction = new DeleteContextAction() {
//
//				private static final long serialVersionUID = 1L;
//
//				@Override
//				protected Context getContext() {
//					return getSelectedContext();
//				}
//			};
//			treeContext.getInputMap().put(
//					(KeyStroke) delContextAction.getValue(DeleteContextAction.ACCELERATOR_KEY),
//					DeleteContextAction.ACTION_NAME);
//			treeContext.getActionMap().put(DeleteContextAction.ACTION_NAME, delContextAction);
		}
		return treeContext;
	}
	
	public void reloadContextTree() {
		WebSocketNodeUI root;
		if (this.contextTree == null) {
			root = new WebSocketNodeUI((WebSocketTreeNode) webSocketMapUI.getRoot());
			this.contextTree = new DefaultTreeModel(root);
		} else {
			root = (WebSocketNodeUI)this.contextTree.getRoot();
			root.removeAllChildren();
		}
		this.contextTree.nodeStructureChanged(root);
	}
	
	public void expandRoot() {
		TreeNode root = (TreeNode) treeContext.getModel().getRoot();
		if (root == null) {
			return;
		}
		final TreePath rootTreePath = new TreePath(root);
		
		if (EventQueue.isDispatchThread()) {
			getTreeSite().expandPath(rootTreePath);
			return;
		}
		try {
			EventQueue.invokeLater(() -> getTreeSite().expandPath(rootTreePath));
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
