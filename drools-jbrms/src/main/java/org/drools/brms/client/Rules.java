package org.drools.brms.client;

import org.drools.brms.client.categorynav.CategoryExplorerWidget;
import org.drools.brms.client.categorynav.CategorySelectHandler;
import org.drools.brms.client.ruleeditor.NewRuleWizard;
import org.drools.brms.client.ruleeditor.RuleView;
import org.drools.brms.client.rulelist.EditItemEvent;
import org.drools.brms.client.rulelist.RuleItemListViewer;

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
public class Rules extends JBRMSFeature {

    public static final int       EDITOR_TAB         = 1;
    
	public static ComponentInfo init() {
		return new ComponentInfo("Rules", "Find and edit rules.") {
			public JBRMSFeature createInstance() {
				return new Rules();
			}

			public Image getImage() {
				return new Image("images/rules.gif");
			}
		};
	}

    private TabPanel tab;
	
	public Rules() {
        tab = new TabPanel();
        tab.setWidth("100%");
        tab.setHeight("100%");        

        FlexTable explorePanel = doExplore(tab);        
        //RuleView ruleViewer = doRuleViewer();
        
        tab.add(explorePanel, "Explore");
        //tab.add(ruleViewer, "Author");
        
        tab.selectTab(0);
        
		initWidget(tab);
	}
    
    
    
    

    private RuleView doRuleViewer() {
        RuleView ruleViewer = new RuleView();
		ruleViewer.setWidth("100%");
		ruleViewer.setHeight("100%");
        return ruleViewer;
    }

    /** This will setup the explorer tab */
	private FlexTable doExplore(final TabPanel tab) {
		FlexTable  table = new FlexTable();
        
        //setup the list
//        final RuleListView list = new RuleListView(new EditItemEvent() {
//
//            public void open(String[] rowData) {
//                tab.selectTab( EDITOR_TAB );                
//            }
//            
//        });         
        
        final RuleItemListViewer list = new RuleItemListViewer(new EditItemEvent() {

            

            public void open(String key,
                             String type,
                             String name) {

                System.out.println("opening key [" + key + "]");
                System.out.println("opening type [" + type + "]");
                System.out.println("opening name [" + name + "]");
                RuleView view = new RuleView();
                
                String ruleName = name;
                tab.add( view, "<img src='images/drools.gif'>" + ruleName, true );
                tab.selectTab( tab.getWidgetIndex( view ) );
                view.loadUUID(key);
                
            }
            
        });    
        
        //setup the nav, which will drive the list
		CategoryExplorerWidget nav = new CategoryExplorerWidget(new CategorySelectHandler() {

            public void selected(String selectedPath) {
                  
                list.loadRulesForCategoryPath(selectedPath);
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
                int left = 70;//w.getAbsoluteLeft() - 10;
                int top = 100; //w.getAbsoluteTop() - 10;
                
              NewRuleWizard pop = new NewRuleWizard();
              pop.setPopupPosition( left, top );
              
              pop.show();
            }
            
        });
        
        table.setWidget( 1, 0, newRule);
        formatter.setHeight( 1, 0, "5%" );
        formatter.setAlignment( 1, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_TOP);
        formatter.setStyleName( 1, 0, "rule-explorer-NewPopups" );
        
        

		return table;
	}

}
