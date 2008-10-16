package org.drools.guvnor.client.explorer;

import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.packages.PackageEditor2;
import org.drools.guvnor.client.packages.SnapshotView;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.rulelist.QueryWidget;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.HistoryListener;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.core.Ext;
import com.gwtext.client.core.Function;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Container;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.Tool;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarButton;
import com.gwtext.client.widgets.Tool.ToolType;
import com.gwtext.client.widgets.event.ButtonListenerAdapter;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.event.TabPanelListener;
import com.gwtext.client.widgets.event.TabPanelListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayoutData;
import com.gwtext.client.widgets.layout.LayoutData;

/**
 * This is the tab panel manager.
 * @author Fernando Meyer, Michael Neale
 */
public class ExplorerViewCenterPanel {

	final TabPanel tp;

	private HashMap<String, Panel> 	openedTabs = new HashMap<String, Panel>();
	private String id = Ext.generateId();
	private BorderLayoutData centerLayoutData;

	/** to keep track of what is dirty, filthy */
	private Map<String, RuleViewer> openedAssetEditors = new HashMap<String, RuleViewer>();

	private Button closeAllButton;

	public ExplorerViewCenterPanel() {
		tp = new TabPanel();

        tp.setBodyBorder(false);
        tp.setEnableTabScroll(true);
        tp.setAutoDestroy(true);
        tp.setResizeTabs(true);
        tp.setLayoutOnTabChange(true);
        tp.setActiveTab(0);
        tp.setEnableTabScroll(true);
        tp.setMinTabWidth(90);

        centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);
        centerLayoutData.setMargins(new Margins(5, 0, 5, 5));

        String tok = History.getToken();

        //listener to try and stop people from forgetting to save...
        tp.addListener(new TabPanelListenerAdapter() {
        	@Override
        	public boolean doBeforeRemove(Container self, final Component component) {
        		
        		if (openedAssetEditors.containsKey(component.getId())) {
        			
        			RuleViewer rv = openedAssetEditors.get(component.getId());
        			if (rv.isDirty()) {
        				component.show();
        				return Window.confirm("Are you sure you want to close this item? Any unsaved changes will be lost.");
        			} else {
        				return true;
        			}
        		}
        		return true;
        	}
        });

        addCloseAllButton();

        openAssetByToken(tok);
	}

	private void addCloseAllButton() {
		closeAllButton = new Button("(close all items)");
        closeAllButton.addListener(new ButtonListenerAdapter() {
        	@Override
        	public void onClick(Button button, EventObject e) {
        		if (Window.confirm("Are you sure you want to close open items?")) {
        			tp.clear();
        			openedAssetEditors.clear();
        			openedTabs.clear();
        			openFind();
        		}
        	}
        });
        tp.addButton(closeAllButton);
	}

	private void openAssetByToken(String tok) {
		if (tok != null && tok.startsWith("asset=")) {
			String uuid = tok.substring(6);
			openAsset(uuid);
		}
	}

	public TabPanel getPanel() {
		return tp;
	}


	/**
	 * Add a new tab. Should only do this if have checked showIfOpen to avoid dupes being opened.
	 * @param tabname The displayed tab name.
	 * @param closeable If you can close it !
	 * @param widget The contents.
	 * @param key A key which is unique.
	 */
	public void addTab (String tabname, boolean closeable, Widget widget, final String key) {

		final String panelId = key + id;
		Panel localTP = new Panel();
		localTP.setClosable(closeable);
		localTP.setTitle(tabname);
		localTP.setId(panelId);
		localTP.setAutoScroll(true);
		localTP.add(widget);
		tp.add(localTP, this.centerLayoutData);


		localTP.addListener(new PanelListenerAdapter() {
			public void onDestroy(Component component) {
				openedTabs.remove(key).destroy();
				openedAssetEditors.remove(panelId);
			}
		});

		if (widget instanceof RuleViewer) {
			this.openedAssetEditors.put(panelId, (RuleViewer) widget);
		}

		tp.activate(localTP.getId());



		openedTabs.put(key, localTP);

	}

	/**
	 * Will open if existing. If not it will return false;
	 */
	public boolean showIfOpen(String key) {
		if (openedTabs.containsKey(key)) {
			LoadingPopup.close();

			Panel tpi = (Panel) openedTabs.get(key);
			this.tp.activate(tpi.getId());

			return true;
		} else {
			return false;
		}
	}


	public void close(String key) {
		tp.remove(key + id);
		Panel p = openedTabs.remove(key);
		if (p != null) p.destroy();
	}


    /**
     * Open an asset if it is not already open.
     */
	public void openAsset(
			final String uuid) {
		History.newItem("asset=" + uuid);
		LoadingPopup.showMessage("Loading asset...");
		if (!showIfOpen(uuid)) {
			RepositoryServiceFactory.getService().loadRuleAsset(uuid, new GenericCallback<RuleAsset>() {
				public void onSuccess(final RuleAsset a) {
					SuggestionCompletionCache.getInstance().doAction(a.metaData.packageName, new Command() {
						public void execute() {
							RuleViewer rv = new RuleViewer(a);
							addTab(a.metaData.name, true, rv, uuid);
							rv.setCloseCommand(new Command() {
								public void execute() {
									close(uuid);
								}
							});
							LoadingPopup.close();
						}
					});

				}
			});
		}
	}



	/**
	 * Open a package editor if it is not already open.
	 */
	public void openPackageEditor(final String uuid, final Command refPackageList) {

		if (!showIfOpen(uuid)) {
			LoadingPopup.showMessage("Loading package information...");
			RepositoryServiceFactory.getService().loadPackageConfig(uuid, new GenericCallback() {
				public void onSuccess(Object data) {
					PackageConfigData conf = (PackageConfigData) data;
					PackageEditor2 ed = new PackageEditor2(conf, new Command() {
						public void execute() {
							close(uuid);
						}
					},refPackageList, new EditItemEvent() {
						public void open(String uuid) {
							openAsset(uuid);
						}
					});
					addTab(conf.name, true, ed, conf.uuid);
					LoadingPopup.close();
				}
			});
		}
	}

	public void openFind() {
		if (!showIfOpen("FIND")) {
			this.addTab("Find", true, new QueryWidget(new EditItemEvent() {
				public void open(String uuid) {
					openAsset(uuid);
				}
			}), "FIND");

		}
	}


	public void openSnapshot(final SnapshotInfo snap) {

		if (!showIfOpen(snap.name + snap.uuid)) {
			LoadingPopup.showMessage("Loading snapshot...");
			RepositoryServiceFactory.getService().loadPackageConfig(snap.uuid, new GenericCallback() {
				public void onSuccess(Object data) {
					PackageConfigData conf = (PackageConfigData) data;
					addTab("Snapshot: " + snap.name, true, new SnapshotView(snap, conf, new Command() {
						public void execute() {
							close(snap.uuid);
						}
					}, ExplorerViewCenterPanel.this), snap.name + snap.uuid);
					LoadingPopup.close();
				}
			});

		}
	}




}
