package org.drools.brms.client;

import java.util.HashMap;
import java.util.Map;

import org.drools.brms.client.categorynav.CategoryExplorerWidget;
import org.drools.brms.client.categorynav.CategorySelectHandler;
import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.ruleeditor.NewAssetWizard;
import org.drools.brms.client.ruleeditor.RuleViewer;
import org.drools.brms.client.rulelist.AssetItemListViewer;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;

/**
 * This controls the "Rules manager" top level feature.
 * @author Michael Neale
 */
public class RulesFeature extends JBRMSFeature {

    public static final int       EDITOR_TAB         = 1;
    private TabPanel tab;
    private Map openedViewers = new HashMap();
    private AssetItemListViewer list;

    
	public static ComponentInfo init() {
		return new ComponentInfo("Rules", "Find and edit rules.") {
			public JBRMSFeature createInstance() {
				return new RulesFeature();
			}

		};
	}


	
	public RulesFeature() {
        tab = new TabPanel();
        tab.setWidth("100%");
        tab.setHeight("100%");        

        FlexTable explorePanel = doExplore();        
        
        tab.add(explorePanel, "Explore");
        tab.selectTab(0);
        
		initWidget(tab);
	}
    
    

    /** This will setup the explorer tab */
	private FlexTable doExplore() {
		FlexTable  table = new FlexTable();
        //and the the delegate to open an editor for a rule resource when
        //chosen to
        list = new AssetItemListViewer(new EditItemEvent() {
            public void open(String key) {                  
                showLoadEditor( key );
                
            }
        });    
        //list.loadTableData( null );
        
        //setup the nav, which will drive the list
		CategoryExplorerWidget nav = new CategoryExplorerWidget(new CategorySelectHandler() {
            public void selected(final String selectedPath) {
                Command load = getRuleListLoadingCommand( list,
                                           selectedPath );
                DeferredCommand.add( load );
                list.setRefreshCommand(load);                
            }

        });		
        
        
        FlexCellFormatter formatter = table.getFlexCellFormatter();
        
        table.setWidget( 0, 0, nav );
		table.setWidget( 0, 1, list);
        
        formatter.setAlignment( 0, 1, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );
        formatter.setAlignment( 0, 0, HasHorizontalAlignment.ALIGN_LEFT, HasVerticalAlignment.ALIGN_TOP );        
        formatter.setRowSpan( 0, 1, 3 );
        formatter.setWidth( 0, 0, "30%" );
        formatter.setWidth( 0, 1, "70%" );

        formatter.setHeight( 0, 0, "90%" );

        table.setText( 2, 0, "" );
        
        Image newRule = new Image("images/new_rule.gif");
        newRule.setTitle( "Create new rule" );

        newRule.addClickListener( new ClickListener() {

            public void onClick(Widget w) {
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
            
        });
        
        table.setWidget( 1, 0, newRule);
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
        showLoadEditor( openedViewers, tab, uuid, false );
    }

    /**
     * This will show the rule viewer. If it was previously opened, it will show that dialog instead
     * of opening it again.
     */
    public static void showLoadEditor(final Map openedViewers, final TabPanel tab,  final String uuid, final boolean readonly) {
      
        
      if (openedViewers.containsKey( uuid )) {
          tab.selectTab( tab.getWidgetIndex( (Widget) openedViewers.get( uuid ) ));
          LoadingPopup.close();
          return;
      }
        
      RepositoryServiceFactory.getService().loadRuleAsset( uuid,
      new AsyncCallback() {
          public void onFailure(Throwable e) {
              ErrorPopup.showMessage( e.getMessage() );
          }

          public void onSuccess(Object o) {
              RuleAsset asset = (RuleAsset) o;
              final RuleViewer view = new RuleViewer(asset, readonly);
              
              String displayName = asset.metaData.name;
              if (displayName.length() > 10) {
                  displayName = displayName.substring( 0, 7 ) + "...";
              }
              String icon = "rule_asset.gif";
              if (asset.metaData.format.equals( AssetFormats.DRL )) {
                  icon = "technical_rule_assets.gif";
              } else if (asset.metaData.format.equals( AssetFormats.DSL )) {
                  icon = "dsl.gif";
              } else if (asset.metaData.format.equals( AssetFormats.FUNCTION )) {
                  icon = "function_assets.gif";
              } else if (asset.metaData.format.equals( AssetFormats.MODEL )) {
                  icon = "model_asset.gif";
              }
              tab.add( view, "<img src='images/" + icon + "'>" + displayName, true );
              
              openedViewers.put(uuid, view);
              
              view.setCloseCommand( new Command() {
                  public void execute() {
                    tab.remove( tab.getWidgetIndex( view ) ); 
                    tab.selectTab( 0 );
                    openedViewers.remove( uuid );
                    
                  }
              });
              tab.selectTab( tab.getWidgetIndex( view ) );
          }

      } );
        
        

    }

}
