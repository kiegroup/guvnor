/*
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.client.explorer;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.images.Images;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.packages.PackageEditor;
import org.drools.guvnor.client.packages.SnapshotView;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.ruleeditor.GuvnorEditor;
import org.drools.guvnor.client.ruleeditor.MultiViewEditor;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.rulelist.QueryWidget;
import org.drools.guvnor.client.util.Format;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.History;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabLayoutPanel;
import com.google.gwt.user.client.ui.Widget;

import com.gwtext.client.core.Ext;



/**
 * This is the tab panel manager.
 * @author Fernando Meyer, Michael Neale
 */
public class ExplorerViewCenterPanel {
    private Constants constants = ((Constants)GWT.create(Constants.class));
    private static Images images = (Images)GWT.create(Images.class);       

    final TabLayoutPanel tp;

    private MultiKeyMap<Panel> openedTabs = new MultiKeyMap<Panel>();
    private String id = Ext.generateId();

    /** to keep track of what is dirty, filthy */
    private Map<String, GuvnorEditor> openedAssetEditors = new HashMap<String, GuvnorEditor>();
    private Map<String, PackageEditor> openedPackageEditors = new HashMap<String, PackageEditor>();
    
    private Map<Panel, String[]> itemWidgets = new HashMap<Panel, String[]>();

    //private Button                      closeAllButton;

    public ExplorerViewCenterPanel() {
        tp = new TabLayoutPanel(2, Unit.EM);

        //TODO: Dirtyable does not work.  
        //listener to try and stop people from forgetting to save...
/*        tp.addListener( new TabPanelListenerAdapter() {
            @Override
            public boolean doBeforeRemove(Container self,
                                          final Component component) {

                if ( openedAssetEditors.containsKey( component.getId() ) ) {

                    GuvnorEditor rv = openedAssetEditors.get( component.getId() );
                    if ( rv.isDirty() ) {
                        component.show();
                        return Window.confirm( constants.AreYouSureCloseWarningUnsaved() );
                    } else {
                        return true;
                    }
                }
                return true;
            }
        } );*/

        addCloseAllButton();
    }

    //TODO:
    private void addCloseAllButton() {
/*        closeAllButton = new Button( constants.CloseAllItems() );
        closeAllButton.addListener( new ButtonListenerAdapter() {
            @Override
            public void onClick(Button button,
                                EventObject e) {
                if ( Window.confirm( constants.AreYouSureYouWantToCloseOpenItems() ) ) {
                    tp.clear();
                    openedAssetEditors.clear();
                    openedPackageEditors.clear();
                    openedTabs.clear();
                    openFind();
                }
            }
        } );
        tp.addButton( closeAllButton );*/
    }

    public TabLayoutPanel getPanel() {
        return tp;
    }

    /**
     * Add a new tab. Should only do this if have checked showIfOpen to avoid dupes being opened.
     * @param tabname The displayed tab name.
     * @param widget The contents.
     * @param key A key which is unique.
     */
    public void addTab(final String tabname,
                       Widget widget,
                       final String key) {
        addTab(tabname,
               widget,
               new String[]{key});
    }

    /**
     * Add a new tab. Should only do this if have checked showIfOpen to avoid dupes being opened.
     * @param tabname The displayed tab name.
     * @param widget The contents.
     * @param keys An array of keys which are unique.
     */
    public void addTab(final String tabname,
                       Widget widget,
                       final String[] keys) {
        final String panelId = (keys.length == 1 ? keys[0] + id : Arrays.toString(keys) + id);
        
        ScrollPanel localTP = new ScrollPanel();
        localTP.add(widget);
        tp.add(localTP, newClosableLabel(localTP, tabname));
        tp.selectTab(localTP);

        //TODO: Dirtyable
/*        localTP.ad( new PanelListenerAdapter() {
            public void onDestroy(Component component) {
                Panel p = openedTabs.remove( keys );
                if ( p != null ) {
                    p.destroy();
                }
                openedAssetEditors.remove( panelId );
                openedPackageEditors.remove( tabname );
            }
        } );
*/
        if (widget instanceof GuvnorEditor) {
            this.openedAssetEditors.put(panelId, (GuvnorEditor)widget);
        } else if (widget instanceof PackageEditor) {
            this.openedPackageEditors.put( tabname,(PackageEditor)widget);
        }

        openedTabs.put(keys, localTP);
        itemWidgets.put(localTP, keys);
    }
    
	private Widget newClosableLabel(final Panel panel, final String title) {
		final HorizontalPanel hPanel = new HorizontalPanel();
		final Label label = new Label(title);
		DOM.setStyleAttribute(label.getElement(), "whiteSpace", "nowrap");
		//ImageButton closeBtn = new ImageButton(images.backupSmall().getURL());
		Button closeBtn = new Button("x");
		closeBtn.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent arg0) {
				int widgetIndex = tp.getWidgetIndex(panel);
				if (widgetIndex == tp.getSelectedIndex()) {
					tp.selectTab(widgetIndex - 1);
				}
				tp.remove(widgetIndex);
				String[] keys = itemWidgets.remove(panel);
				openedTabs.remove(keys);				
			}			
		});

		hPanel.add(label);
		hPanel.add(new HTML("&nbsp&nbsp&nbsp"));
		hPanel.add(closeBtn);
		return hPanel;
	}
    
    /**
     * Will open if existing. If not it will return false;
     */
    public boolean showIfOpen(String key) {
        if (openedTabs.containsKey(key)) {
            LoadingPopup.close();
            Panel tpi = (Panel)openedTabs.get(key);
            tp.selectTab(tpi);      
            return true;
        } 
        return false;
    }

    public void close(String key) {
        //tp.remove( key + id );
        Panel tpi = openedTabs.remove(key);
        
        int widgetIndex = tp.getWidgetIndex(tpi);
        if (widgetIndex == tp.getSelectedIndex()) {
            tp.selectTab(widgetIndex - 1);
        }
        
        tp.remove(widgetIndex);
		itemWidgets.remove(tpi);
    }

    /**
     * Open an asset if it is not already open.
     */
	public void openAsset(final String uuid) {
		if (uuid.contains("<")) {
			return;
		}
		History.newItem("asset=" + uuid); // NON-NLS

		if (!showIfOpen(uuid)) {

			final boolean[] loading = {true};

			Timer t = new Timer() {
				public void run() {
					if (loading[0]) {
						LoadingPopup.showMessage(constants.LoadingAsset());
					}
				}
			};
			t.schedule(200);

			RepositoryServiceFactory.getService().loadRuleAsset(uuid, new GenericCallback<RuleAsset>() {
				public void onSuccess(final RuleAsset a) {
					SuggestionCompletionCache.getInstance().doAction(a.metaData.packageName, new Command() {
						public void execute() {
							loading[0] = false;
							EditItemEvent edit = new EditItemEvent() {
								public void open(String key) {
									openAsset(key);
								}

								public void open(MultiViewRow[] rows) {
									for (MultiViewRow row : rows) {
										openAsset(row.uuid);
									}
								}
							};
							RuleViewer rv = new RuleViewer(a, edit);
							addTab(a.metaData.name, rv, uuid);
							rv.setCloseCommand(new Command() {
								public void execute() {
									close(uuid);
								}
							});

							// When model is saved update the package view if it is opened.
							if (a.metaData.format.equals(AssetFormats.MODEL)) {
							    Command command =new Command() {
                                    public void execute() {
                                        PackageEditor packageEditor = openedPackageEditors.get(a.metaData.packageName);
                                        if (packageEditor != null) {
                                            packageEditor.reload();
                                        }
                                    }
                                };
								rv.setCheckedInCommand(command);
								rv.setArchiveCommand(command);
							}

							LoadingPopup.close();
						}
					});
				}
			});
		}
	}

    public void openAssets(MultiViewRow[] rows) {

        String blockingAssetName = null;
        final String[] uuids = new String[rows.length];
        final String[] names = new String[rows.length];

        for (int i = 0; i < rows.length; i++) {
            // Check if any of these assets are already opened.
            if (showIfOpen(rows[i].uuid)) {
                blockingAssetName = rows[i].name;
                break;
            }
            uuids[i] = rows[i].uuid;
            names[i] = rows[i].name;
        }

        if (blockingAssetName != null) {
            FormStylePopup popup = new FormStylePopup("images/information.gif", //NON-NLS
                                                      Format.format( constants.Asset0IsAlreadyOpenPleaseCloseItBeforeOpeningMultiview(),
                                                                      blockingAssetName));
            popup.show();
            return;
        }

        MultiViewEditor multiview = new MultiViewEditor(rows,
                                                        new EditItemEvent() {
                                                            public void open(String key) {
                                                                openAsset(key);
                                                            }

                                                             public void open(MultiViewRow[] rows) {
                                                                 for (MultiViewRow row : rows) {
                                                                     openAsset(row.uuid);
                                                                 }
                                                             }
                                                         } );

        multiview.setCloseCommand(new Command() {
            public void execute() {
                close(Arrays.toString(uuids));
            }
        });

        addTab(Arrays.toString(names),
               multiview,
               uuids );

    }

    /**
     * Open a package editor if it is not already open.
     */
	public void openPackageEditor(final String uuid, final Command refPackageList) {

		if (!showIfOpen(uuid)) {
			LoadingPopup.showMessage(constants.LoadingPackageInformation());
			RepositoryServiceFactory.getService().loadPackageConfig(uuid, new GenericCallback<PackageConfigData>() {
				public void onSuccess(PackageConfigData conf) {
					PackageEditor ed = new PackageEditor(conf, new Command() {
						public void execute() {
							close(uuid);
						}
					}, refPackageList, new EditItemEvent() {
						public void open(String uuid) {
							openAsset(uuid);
						}

						public void open(MultiViewRow[] rows) {
							for (MultiViewRow row : rows) {
								openAsset(row.uuid);
							}
						}
					});
					addTab(conf.name, ed, conf.uuid);
					LoadingPopup.close();
				}
			});
		}
	}

    public void openFind() {
        if ( !showIfOpen( "FIND" ) ) { //NON-NLS
            this.addTab( constants.Find(),
                         new QueryWidget( new EditItemEvent() {
                             public void open(String uuid) {
                                 openAsset( uuid );
                             }

                             public void open(MultiViewRow[] rows) {
                                 for ( MultiViewRow row : rows ) {
                                     openAsset( row.uuid );
                                 }
                             }
                         } ),
                         "FIND" ); //NON-NLS

        }
    }

    public void openSnapshot(final SnapshotInfo snap) {
        //make this refresh the 'snap'

        if (!showIfOpen( snap.name + snap.uuid)) {
            LoadingPopup.showMessage(constants.LoadingSnapshot());
            RepositoryServiceFactory.getService().loadPackageConfig(snap.uuid,
                                                                    new GenericCallback<PackageConfigData>() {
                                                                        public void onSuccess(PackageConfigData conf) {
                                                                             addTab(Format.format(constants.SnapshotLabel(),
                                                                                                  snap.name),
                                                                                                  new SnapshotView(snap,
                                                                                                  conf,
                                                                                                  new Command() {
                                                                                                      public void execute() {
                                                                                                          close( snap.name + snap.uuid );
                                                                                                      }
                                                                                                  },
                                                                                                  ExplorerViewCenterPanel.this),
                                                                                     snap.name + snap.uuid);
                                                                             LoadingPopup.close();
                                                                         }
                                                                     } );

        }
    }

}
