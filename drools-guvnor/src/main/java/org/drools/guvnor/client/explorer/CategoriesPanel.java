package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.Inbox;
import org.drools.guvnor.client.rpc.*;
import org.drools.guvnor.client.ruleeditor.MultiViewRow;
import org.drools.guvnor.client.rulelist.AssetItemGrid;
import org.drools.guvnor.client.rulelist.AssetItemGridDataLoader;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.Command;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.Button;
import com.gwtext.client.widgets.Toolbar;
import com.gwtext.client.widgets.ToolbarMenuButton;
import com.gwtext.client.widgets.event.SplitButtonListenerAdapter;
import com.gwtext.client.widgets.tree.TreeNode;
import com.gwtext.client.widgets.tree.TreePanel;
import com.gwtext.client.widgets.tree.event.TreePanelListenerAdapter;

/**
 * @author Anton Arhipov
 */
public class CategoriesPanel extends GenericPanel {
    private static Constants constants = GWT.create(Constants.class);



    /** Table set up for the inboxes */
    static {
            TableConfig conf = new TableConfig();
            conf.headers = new String[2];
            conf.headers[0] = constants.Name();// "Name ";
            conf.headers[1] = constants.Date();//"Date ";
            conf.headerTypes = new String[2];
            conf.headerTypes[0] = "class java.lang.String";
            conf.headerTypes[1] = "class java.util.Calendar";
            conf.rowsPerPage = 500;
            AssetItemGrid.registerTableConf(conf, Inbox.RECENT_EDITED);
            AssetItemGrid.registerTableConf(conf, Inbox.RECENT_VIEWED);

            conf = new TableConfig();
            conf.headers = new String[3];
            conf.headers[0] = constants.Name();
            conf.headers[1] = constants.Date();
            conf.headers[2] = constants.From();
            conf.headerTypes = new String[3];
            conf.headerTypes[0] = "class java.lang.String";
            conf.headerTypes[1] = "class java.util.Calendar";
            conf.headerTypes[2] = "class java.lang.String";
            conf.rowsPerPage = 500;

            AssetItemGrid.registerTableConf(conf, Inbox.INCOMING);
    }


    public CategoriesPanel(ExplorerViewCenterPanel tabbedPanel) {
        super(constants.Browse(), tabbedPanel);
        setIconCls("nav-categories"); //NON-NLS

        
        Toolbar rulesToolBar = new Toolbar();

        final ToolbarMenuButton menuButton = new ToolbarMenuButton(constants.CreateNew(), RulesNewMenu.getMenu( this ) );
        rulesToolBar.addButton( menuButton );
        menuButton.addListener( new SplitButtonListenerAdapter() {

            public void onClick(Button button,
                                EventObject e) {
                menuButton.showMenu();
            }
        } );
        
        VerticalPanel rulesPanel = new VerticalPanel();
        if (ExplorerLayoutManager.shouldShow(Capabilities.SHOW_CREATE_NEW_ASSET)) {
            rulesPanel.add(rulesToolBar);
        }

        TreePanel categoryTree = basicTreeStructure(ExplorerNodeConfig.getRulesStructure(), new TreePanelListenerAdapter() {
            public void onClick(final TreeNode self, EventObject e) {
                
                //this refreshes the list.
                if (self.getAttribute("id").equals(ExplorerNodeConfig.CATEGORY_ID)) {  //NON-NLS
                    self.getParentNode().replaceChild(ExplorerNodeConfig.getCategoriesStructure(), self);
                } else if (self.getAttribute("id").equals(ExplorerNodeConfig.STATES_ID)) {   //NON-NLS
                    self.getParentNode().replaceChild(ExplorerNodeConfig.getStatesStructure(), self);
                } else if (self.getAttribute("id").equals("FIND")) {     //NON-NLS
                    centertabbedPanel.openFind();
                } else if (self.getAttribute("id").startsWith("inbox")) {
                    openInbox(self.getText(), (String)self.getUserObject());
                } else {
                    openStateOrCategory(self);                                 
                }

            }
        });


        rulesPanel.add(categoryTree);
        rulesPanel.setWidth("100%");
        add(rulesPanel);
    }

    /**
     * Show the inbox of the given name.
     */
    private void openInbox(String title, final String inboxName) {
        if (!centertabbedPanel.showIfOpen(inboxName)) {
            AssetItemGrid g = new AssetItemGrid(createEditEvent(), inboxName, new AssetItemGridDataLoader() {
                public void loadData(int startRow, int numberOfRows, GenericCallback<TableDataResult> cb) {
                    RepositoryServiceFactory.getService().loadInbox(inboxName, cb);
                }
            });
            centertabbedPanel.addTab(title, true, g, inboxName);
        }
    }

    /**
     * open a state or category !
     */
    private void openStateOrCategory(TreeNode self) {
        final String key = (String) self.getUserObject();
        final boolean isState = key.startsWith("-");

        if (!centertabbedPanel.showIfOpen(key)) {
            final AssetItemGrid list = new AssetItemGrid(createEditEvent(),
                    AssetItemGrid.RULE_LIST_TABLE_ID,
                    new AssetItemGridDataLoader() {
                        public void loadData(int skip, int numberOfRows, GenericCallback cb) {
                            if (isState) {
                                RepositoryServiceFactory.getService().
                                        loadRuleListForState(key.substring(1), skip,
                                                numberOfRows, AssetItemGrid.RULE_LIST_TABLE_ID, cb);
                            } else {
                                RepositoryServiceFactory.getService().
                                        loadRuleListForCategories(key, skip, numberOfRows,
                                                AssetItemGrid.RULE_LIST_TABLE_ID, cb);
                            }
                        }
                    },
                    (isState) ? null : GWT.getModuleBaseURL() + "feed/category?name=" + key + "&viewUrl=" + getSelfURL());
           final ServerPushNotification push = new ServerPushNotification() {
                public void messageReceived(PushResponse response) {
                    if (!isState) {
                        if (response.messageType.equals("categoryChange") && response.message.equals(key)) {
                            list.refreshGrid();
                        }
                    } else {
                        if (response.messageType.equals("statusChange") && ("-" + response.message).equals(key)) {
                            list.refreshGrid();
                        }
                    }
                }
            };
            PushClient.instance().subscribe(push);
            list.addUnloadListener(new Command() {
                public void execute() {
                    PushClient.instance().unsubscribe(push);
                }
            });

            centertabbedPanel.addTab(((isState) ? constants.Status() : constants.CategoryColon()) + self.getText(), true, list, key);
        }
    }

    private EditItemEvent createEditEvent() {
        return new EditItemEvent() {
            public void open(String uuid) {
                centertabbedPanel.openAsset(uuid);
            }

            public void open(MultiViewRow[] rows) {
                for ( MultiViewRow row: rows) {
                    centertabbedPanel.openAsset( row.uuid );
                }
            }
        };
    }


    /**
     * The URL that will be used to open up assets in a feed.
     * (by tacking asset id on the end, of course !). 
     */
    public static String getSelfURL() {
        String selfURL = Window.Location.getHref();
        if (selfURL.contains("#")) {
            selfURL = selfURL.substring(0, selfURL.indexOf("#"));
        }
        return selfURL;
    }

}
