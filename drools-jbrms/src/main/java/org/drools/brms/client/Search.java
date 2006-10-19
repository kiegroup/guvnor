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

import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rulelist.RuleItemListViewer;
import org.drools.brms.client.table.SortableTable;

import com.google.gwt.user.client.ui.Image;

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
//      TableDataRow row = new TableDataRow();
//      row.key = "DFFD6767FDS";
//      row.values = new String[] {"one", "two", "three" };
//      
//      TableDataResult resultset = new TableDataResult();
//      resultset.data = new TableDataRow[] {row};
//      
//      TableConfig config = new TableConfig();
//      config.headers = new String[] {"foo", "bar", "baz" };
//      config.rowsPerPage = 50;
//       
//      SortableGrid grid = new SortableGrid(resultset, config);
//      initWidget( grid );
      
        final SortableTable sortableTable = new SortableTable(500, 4);
        
        sortableTable.setHiddenColumn( 0 );
        
        sortableTable.setWidth( "100%" );
        sortableTable.addColumnHeader("Employee",  0);
        sortableTable.addColumnHeader("Days", 1);
        sortableTable.addColumnHeader("Hire Date", 2);
        sortableTable.addColumnHeader("Bonus", 3);
        
        // The rowIndex should begin with 1 as rowIndex 0 is for the Header
        // Any row with index == 0 will not be displayed.
        sortableTable.setValue(1, 0, "Parvinder Thapar");
        sortableTable.setValue(1, 1, new Integer(28));
        sortableTable.setValue(1, 2, "2005, 10, 25");
        sortableTable.setValue(1, 3, new Float("125.27"));

        sortableTable.setValue(2, 0, "David Brooks");
        sortableTable.setValue(2, 1, new Integer(32));
        sortableTable.setValue(2, 2, "2000, 4, 1");
        sortableTable.setValue(2, 3, new Float("105.78"));

        sortableTable.setValue(3, 0, "Raj Rajendran");
        sortableTable.setValue(3, 1, new Integer(30));
        sortableTable.setValue(3, 2, "2001, 12, 9");
        sortableTable.setValue(3, 3, new Float("236.82"));

        sortableTable.setValue(4, 0, "Brian Foley");
        sortableTable.setValue(4, 1, new Integer(38));
        sortableTable.setValue(4, 2, "2003, 2, 24");
        sortableTable.setValue(4, 3, new Float("489.29"));

        sortableTable.setValue(5, 0, "Visala Dhara");
        sortableTable.setValue(5, 1, new Integer(30));
        sortableTable.setValue(5, 2, "2001, 4, 23");
        sortableTable.setValue(5, 3, new Float("892.72"));

        sortableTable.setValue(6, 0, "Wasim Khan");
        sortableTable.setValue(6, 1, new Integer(35));
        sortableTable.setValue(6, 2, "1999, 7, 10");
        sortableTable.setValue(6, 3, new Float("1242.89"));

        sortableTable.setValue(7, 0, "Bob Hammel");
        sortableTable.setValue(7, 1, new Integer(56));
        sortableTable.setValue(7, 2, "1995, 2, 14");
        sortableTable.setValue(7, 3, new Float("107.21"));

        sortableTable.setValue(8, 0, "Jeanie Sa-ville");
        sortableTable.setValue(8, 1, new Integer(58));
        sortableTable.setValue(8, 2, "1989, 6, 1");
        sortableTable.setValue(8, 3, new Float("2372.42"));

        sortableTable.setValue(9, 0, "Scott Loyet");
        sortableTable.setValue(9, 1, new Integer(42));
        sortableTable.setValue(9, 2, "1992, 2, 29");
        sortableTable.setValue(9, 3, new Float("896.74"));

        sortableTable.setValue(10, 0, "Dennis Twiss");
        sortableTable.setValue(10, 1, new Integer(59));
        sortableTable.setValue(10, 2, "1990, 4, 15");
        sortableTable.setValue(10, 3, new Float("1896.74"));
        
        sortableTable.setValue(11, 0, "Mike McIntosh");
        sortableTable.setValue(11, 1, new Integer(76));
        sortableTable.setValue(11, 2, "1982, 5, 25");
        sortableTable.setValue(11, 3, new Float("689.77"));
        
        sortableTable.setValue(12, 0, "Andrews Andy");
        sortableTable.setValue(12, 1, new Integer(62));
        sortableTable.setValue(12, 2, "1994, 1, 15");
        sortableTable.setValue(12, 3, new Float("829.24"));

        sortableTable.setValue(13, 0, "Bob Regent");
        sortableTable.setValue(13, 1, new Integer(29));
        sortableTable.setValue(13, 2, "1996, 3, 12");
        sortableTable.setValue(13, 3, new Float("621.52"));
        
        sortableTable.setValue(14, 0, "Chris Chalmers");
        sortableTable.setValue(14, 1, new Integer(32));
        sortableTable.setValue(14, 2, "1997, 4, 1");
        sortableTable.setValue(14, 3, new Float("804.26"));
        
        sortableTable.setValue(15, 0, "Christopher Mathrusse");
        sortableTable.setValue(15, 1, new Integer(64));
        sortableTable.setValue(15, 2, "2005, 9, 10");
        sortableTable.setValue(15, 3, new Float("761.25"));
        
        sortableTable.setValue(16, 0, "John Smith");
        sortableTable.setValue(16, 1, new Integer(56));
        sortableTable.setValue(16, 2, "1992, 3, 16");
        sortableTable.setValue(16, 3, new Float("789.29"));
        
        sortableTable.setValue(17, 0, "Jane Smith");
        sortableTable.setValue(17, 1, new Integer(45));
        sortableTable.setValue(17, 2, "1989, 7, 25");
        sortableTable.setValue(17, 3, new Float("2254.87"));
        
        sortableTable.setValue(18, 0, "Jason Chen");
        sortableTable.setValue(18, 1, new Integer(37));
        sortableTable.setValue(18, 2, "1995, 8, 24");
        sortableTable.setValue(18, 3, new Float("978.32"));
        
        sortableTable.setValue(19, 0, "Tina Matt");
        sortableTable.setValue(19, 1, new Integer(49));
        sortableTable.setValue(19, 2, "1998, 9, 15");
        sortableTable.setValue(19, 3, new Float("189.64"));
        
        sortableTable.setValue(20, 0, "Roxanne Rocks");
        sortableTable.setValue(20, 1, new Integer(43));
        sortableTable.setValue(20, 2, "1992, 11, 12");
        sortableTable.setValue(20, 3, new Float("1209.73"));

//        for (int i = 21; i <= 500; i++) {
//            sortableTable.setValue(i, 0, "Roxanne Rocks " + i);
//            sortableTable.setValue(i, 1, new Integer(43));
//            sortableTable.setValue(i, 2, "1992, 11, 12");
//            sortableTable.setValue(i, 3, new Float("1209.73"));
//            
//        }
        
        TableConfig conf = new TableConfig();
        conf.headers = new String[] {"name", "last modified", "status", "version" };
        
//        NewView view = new NewView(conf);
//        initWidget( view );
  }

  public void onShow() {
  }
}
