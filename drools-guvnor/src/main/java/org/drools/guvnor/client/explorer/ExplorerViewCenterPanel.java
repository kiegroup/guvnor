package org.drools.guvnor.client.explorer;

import java.util.HashMap;

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
import org.drools.guvnor.client.rulelist.QuickFindWidget;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.Ext;
import com.gwtext.client.core.Margins;
import com.gwtext.client.core.RegionPosition;
import com.gwtext.client.widgets.Component;
import com.gwtext.client.widgets.Panel;
import com.gwtext.client.widgets.TabPanel;
import com.gwtext.client.widgets.event.PanelListenerAdapter;
import com.gwtext.client.widgets.layout.BorderLayoutData;

/**
 * This is the tab panel manager.
 * @author Fernando Meyer, Michael Neale
 */
public class ExplorerViewCenterPanel {

	final TabPanel tp;

	private HashMap 	openedTabs = new HashMap();
	private String id = Ext.generateId();
	private BorderLayoutData centerLayoutData;

	public ExplorerViewCenterPanel() {

		tp = new TabPanel();

        tp.setBodyBorder(false);
        tp.setEnableTabScroll(true);
        tp.setAutoDestroy(true);
        tp.setResizeTabs(true);
        tp.setLayoutOnTabChange(true);
        tp.setActiveTab(0);



        centerLayoutData = new BorderLayoutData(RegionPosition.CENTER);
        centerLayoutData.setMargins(new Margins(5, 0, 5, 5));


	}

	public TabPanel getPanel() {
		return tp;
	}


	/**
	 * Add a new tab. Should only do this if have checked showIfOpen to avoid dupes being opened.
	 * @param tabname The displayed tab name.
	 * @param closeable If you can close it !
	 * @param widget The contentx.
	 * @param key A key which is unique.
	 */
	public void addTab (String tabname, boolean closeable, Widget widget, final String key) {


		Panel localTP = new Panel();
		localTP.setClosable(closeable);
		localTP.setTitle(tabname);
		localTP.setId(key + id);
		localTP.setAutoScroll(true);
		localTP.add(widget);

		tp.add(localTP, this.centerLayoutData);

		localTP.addListener(new PanelListenerAdapter() {

			public void onDestroy(Component component) {
				openedTabs.remove(key);
			}
		});


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
			//tp.scrollToTab(tpi, true);

			return true;
		} else {
			return false;
		}
	}


	public void close(String key) {
		tp.remove(key + id);
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
