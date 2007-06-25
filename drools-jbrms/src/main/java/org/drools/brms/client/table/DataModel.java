package org.drools.brms.client.table;
/*
 * Copyright 2005 JBoss Inc
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



import com.google.gwt.user.client.ui.Widget;

/**
 * An optional interface to provide widgets for the body of the grid.
 * @author Michael Neale
 *
 */
public interface DataModel {

    
    /**
     * Must always provide a value. This is used for sorting (and display possibly).
     */
    public Comparable getValue(int row, int col);
    
    /**
     * optionally return a widget to display instead of the text. If null, then the text will be rendered.
     */
    public Widget getWidget(int row, int col);
    
    
    public int getNumberOfRows();
    
    public String getRowId(int row);
    
}