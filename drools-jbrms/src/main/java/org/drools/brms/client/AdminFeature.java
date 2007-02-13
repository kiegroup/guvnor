package org.drools.brms.client;

import org.drools.brms.client.admin.CategoryManager;

import com.google.gwt.user.client.ui.TabPanel;

/**
 * This feature contains the administrative functions of the BRMS.
 */
public class AdminFeature extends JBRMSFeature {

  private TabPanel tab;

  public AdminFeature() {
      tab = new TabPanel();
      tab.setWidth( "100%" );
      tab.setHeight( "100%" );

      tab.add( new CategoryManager(), "<img src='images/category_small.gif'/>Manage categories", true ); 
            
      tab.selectTab( 0 );
      

      
      initWidget( tab );
      
  }

  
  public static ComponentInfo init() {
    return new ComponentInfo("Admin",
      "Administer the repository") {
      public JBRMSFeature createInstance() {
        return new AdminFeature();
      }

    };
  }


  public void onShow() {
  }


}
