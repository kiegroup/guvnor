package org.drools.guvnor.client.explorer;

import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rulelist.AssetItemGrid;
import org.drools.guvnor.client.rulelist.AssetItemGridDataLoader;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.security.Capabilities;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.Window;
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

    public CategoriesPanel(ExplorerViewCenterPanel tabbedPanel) {
        super(constants.Assets(), tabbedPanel);
        setIconCls("nav-categories"); //NON-NLS

        
        Toolbar rulesToolBar = new Toolbar();

        final ToolbarMenuButton menuButton = new ToolbarMenuButton(constants.CreateNew(),
                                                            RulesNewMenu.getMenu( this ) );
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

        TreePanel categoryTree = basicTreeStructure(ExplorerNodeConfig
                .getRulesStructure(), new TreePanelListenerAdapter() {

            public void onClick(final TreeNode self, EventObject e) {

                //this refreshes the list.
                if (self.getAttribute("id").equals(ExplorerNodeConfig.CATEGORY_ID)) {  //NON-NLS
                    self.getParentNode().replaceChild(ExplorerNodeConfig.getCategoriesStructure(), self);
                } else if (self.getAttribute("id").equals(ExplorerNodeConfig.STATES_ID)) {   //NON-NLS
                    self.getParentNode().replaceChild(ExplorerNodeConfig.getStatesStructure(), self);
                } else if (self.getAttribute("id").equals("FIND")) {     //NON-NLS
                    centertabbedPanel.openFind();
                } else {
                    final String key = (String) self.getUserObject();
                    final boolean isState = key.startsWith("-");

                    if (!centertabbedPanel.showIfOpen(key)) {

                        AssetItemGrid list = new AssetItemGrid(new EditItemEvent() {
                            public void open(String uuid) {
                                centertabbedPanel.openAsset(uuid);
                            }
                        },
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
                                (isState) ? null : GWT.getModuleBaseURL() + "feed/category?name=" + key + "&viewUrl=" + Window.Location.getHref());

                        centertabbedPanel.addTab(((isState) ? constants.Status() : constants.CategoryColon()) + self.getText(), true, list, key);
                    }

                }

            }
        });


        rulesPanel.add(categoryTree);
        rulesPanel.setWidth("100%");
        add(rulesPanel);
    }

}
