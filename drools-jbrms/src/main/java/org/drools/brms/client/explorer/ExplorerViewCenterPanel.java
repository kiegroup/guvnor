package org.drools.brms.client.explorer;

import java.util.HashMap;

import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.packages.PackageEditor2;
import org.drools.brms.client.packages.SnapshotView;
import org.drools.brms.client.packages.SuggestionCompletionCache;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.SnapshotInfo;
import org.drools.brms.client.ruleeditor.RuleViewer;
import org.drools.brms.client.rulelist.EditItemEvent;
import org.drools.brms.client.rulelist.QuickFindWidget;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.Ext;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.TabPanelItem;
import com.gwtext.client.widgets.event.TabPanelItemListenerAdapter;
import com.gwtext.client.widgets.layout.ContentPanel;

/**
 * This is the tab panel manager.
 * @author Fernando Meyer, Michael Neale
 */
public class ExplorerViewCenterPanel extends ContentPanel {

	final TabPanel tp;
	private int index = 0;
	private HashMap 	openedTabs = new HashMap();
	private String id = Ext.generateId();

	public ExplorerViewCenterPanel() {

		super(Ext.generateId());


		tp = new TabPanel(id);

		tp.setWidth("100%");
		tp.setHeight("100%");

		tp.autoSizeTabs();
		tp.setResizeTabs(true);

		add(tp);
	}


	/**
	 * Add a new tab. Should only do this if have checked showIfOpen to avoid dupes being opened.
	 * @param tabname The displayed tab name.
	 * @param closeable If you can close it !
	 * @param widget The contentx.
	 * @param key A key which is unique.
	 */
	public void addTab (String tabname, boolean closeable, Widget widget, final String key) {
		TabPanelItem localTP = tp.addTab(key + id, tabname, closeable);
		SimplePanel sp = new SimplePanel();
		sp.add(widget);

		localTP.setContent(sp);


		localTP.addTabPanelItemListener(new TabPanelItemListenerAdapter() {
			public void onClose(TabPanelItem tab) {
				openedTabs.remove(key);
			}
		});
		tp.activate(tp.getCount()-1);
		openedTabs.put(key, localTP);
	}

	/**
	 * Will open if existing. If not it will return false;
	 */
	public boolean showIfOpen(String key) {
		if (openedTabs.containsKey(key)) {
			LoadingPopup.close();
			TabPanelItem tpi = (TabPanelItem) openedTabs.get(key);
			tpi.activate();
			return true;
		} else {
			return false;
		}
	}


	public void close(String key) {
		tp.removeTab(key + id);
		openedTabs.remove(key);
	}


    /**
     * Open an asset if it is not already open.
     */
	public void openAsset(
			final String uuid) {
		LoadingPopup.showMessage("Loading asset...");
		if (!showIfOpen(uuid)) {
			RepositoryServiceFactory.getService().loadRuleAsset(uuid, new GenericCallback() {
				public void onSuccess(Object data) {
					final RuleAsset a = (RuleAsset) data;
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
			this.addTab("Find", true, new QuickFindWidget(new EditItemEvent() {
				public void open(String uuid) {
					openAsset(uuid);
				}
			}), "FIND");
		}
	}


	public void openSnapshot(final SnapshotInfo snap) {

		if (!showIfOpen(snap.uuid)) {
			LoadingPopup.showMessage("Loading snapshot...");
			RepositoryServiceFactory.getService().loadPackageConfig(snap.uuid, new GenericCallback() {
				public void onSuccess(Object data) {
					PackageConfigData conf = (PackageConfigData) data;
					addTab("Snapshot: " + snap.name, true, new SnapshotView(snap, conf, new Command() {
						public void execute() {
							close(snap.uuid);
						}
					}), snap.uuid);
					LoadingPopup.close();
				}
			});

		}
	}




}
