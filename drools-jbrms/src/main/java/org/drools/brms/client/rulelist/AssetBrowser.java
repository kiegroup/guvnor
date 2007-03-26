package org.drools.brms.client.rulelist;

import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.categorynav.CategoryExplorerWidget;
import org.drools.brms.client.categorynav.CategorySelectHandler;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.ImageButton;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.ruleeditor.EditorLauncher;
import org.drools.brms.client.ruleeditor.NewAssetWizard;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This is the category based asset browser (ie it has a category tree browser, as well as 
 * a search box).
 * 
 * @author Michael Neale
 */
public class AssetBrowser extends Composite {
    
    public static final int       EDITOR_TAB         = 1;
    private TabPanel tab;
    private Map openedViewers = new HashMap();
    private AssetItemListViewer list;
    
    public AssetBrowser() {
        tab = new TabPanel();
        tab.setWidth("100%");
        tab.setHeight("100%");        

        FlexTable explorePanel = doExplorer();        
        
        tab.add(explorePanel, "Explore");

        tab.selectTab(0);
        
        initWidget(tab);    
    }
    
    /** This will setup the explorer tab */
    private FlexTable doExplorer() {
        final FlexTable  table = new FlexTable();
        //and the the delegate to open an editor for a rule resource when
        //chosen to
        list = new AssetItemListViewer(new EditItemEvent() {
            public void open(String key) {                  
                showLoadEditor( key );
            }
        });    
        
        
        FlexCellFormatter formatter = table.getFlexCellFormatter();

        
        //setup the nav, which will drive the list
        CategoryExplorerWidget nav = new CategoryExplorerWidget(new CategorySelectHandler() {
            public void selected(final String selectedPath) {
                Command load = getRuleListLoadingCommand( list,
                                           selectedPath );
                table.setWidget( 0, 1, list );
                DeferredCommand.add( load );
                list.setRefreshCommand(load);                
            }

        });     
        
        final QuickFindWidget quick = new QuickFindWidget(new EditItemEvent() {
            public void open(String key) {                  
                showLoadEditor( key );
            }
        });
        
        table.setWidget( 0, 0, nav );
        table.setWidget( 0, 1, quick);
        
        formatter.setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
        formatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );        
        formatter.setRowSpan( 0, 1, 3 );
        formatter.setWidth( 0, 0, "30%" );
        formatter.setWidth( 0, 1, "70%" );

        formatter.setHeight( 0, 0, "90%" );

        table.setText( 2, 0, "" );
        
        Image newRule = new ImageButton("images/new_rule.gif");
        newRule.setTitle( "Create new rule" );

        newRule.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
              showNewAssetWizard();
            }
        });
        
        Image showFinder = new ImageButton("images/find_items.gif");
        showFinder.setTitle( "Show the name finder." );
        showFinder.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                table.setWidget( 0, 1, quick );
            }
        } );
        
        HorizontalPanel actions = new HorizontalPanel();
        actions.add( showFinder );
        actions.add( newRule );
        table.setWidget( 1, 0, actions);
        formatter.setHeight( 1, 0, "5%" );
        formatter.setAlignment( 1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP);
        formatter.setStyleName( 1, 0, "new-asset-Icons" );
        
        return table;
    }

    private Command getRuleListLoadingCommand(final AssetItemListViewer list,
                                              final String selectedPath) {
        return new Command() {
            public void execute() {
              RepositoryServiceFactory.getService().loadRuleListForCategories( selectedPath,
              new GenericCallback() {
                      public void onSuccess(Object o) {
                          TableDataResult result = (TableDataResult) o;
                          list.loadTableData( result );                                                                                 
                      }

                  } );                    
              }                    
        };
    }

    public void showLoadEditor(String uuid) {
        EditorLauncher.showLoadEditor( openedViewers, tab, uuid, false );
    }

    private void showNewAssetWizard() {
        int left = 70;
          int top = 100;
            
          NewAssetWizard pop = new NewAssetWizard(new EditItemEvent() {
              public void open(String key) {                  
                  showLoadEditor( key );
                  
              }
          }, true, null, "Create a new rule");
          pop.setPopupPosition( left, top );
          
          pop.show();
    }
    
    
    
}
