/*
 * Copyright 2006 Google Inc.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package org.drools.brms.client;

import org.drools.brms.client.common.SortableGrid;

import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Grid;

/**
 * Demonstrates {@link com.google.gwt.user.client.ui.Table}.
 */
public class Search extends JBRMSFeature {

  public static ComponentInfo init() {
    return new ComponentInfo(
      "Search",
      "Find the rules you want to edit and manage.") {
      public JBRMSFeature createInstance() {
        return new Search();
      }

	public Image getImage() {
 
		return new Image("images/drools.gif");
	}
    };
  }


  public Search() {
      
      String[][] data = new String[][] {
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },                                        
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },                                        
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },                                        
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },                                        
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },  
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },
                                        {"1", "rule", "another thing" },                                          
      };
      
      SortableGrid grid = new SortableGrid(data, new String[] {"a","b","c"});
      initWidget( grid );
  }

  public void onShow() {
  }
}
