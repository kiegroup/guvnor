/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.drools.guvnor.client.decisiontable.widget;

import org.drools.guvnor.client.resources.DecisionTableResources;
import org.drools.guvnor.client.resources.DecisionTableResources.DecisionTableStyle;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Composite;

/**
 * A widget used to display the DecisionTable's "sidebar". A
 * VerticalDecisionTable would display this to the left of the table of data
 * whereas a HorizontalDecisionTable would display this above the data. 
 * 
 * @author manstis
 * 
 */
public abstract class DecisionTableSidebarWidget extends Composite {

	protected DecisionTableWidget dtable;

	// Resources
	protected static final DecisionTableResources resource = GWT
			.create(DecisionTableResources.class);
	protected static final DecisionTableStyle style = resource.cellTableStyle();

	/**
	 * Construct a "Sidebar" for the provided DecisionTable
	 * 
	 * @param decisionTable
	 */
	public DecisionTableSidebarWidget(DecisionTableWidget dtable) {
		this.dtable = dtable;
		style.ensureInjected();
	}

	/**
	 * Append a selector widget to the Sidebar. A selector widget can implement any
	 * row-level operation, such as selecting, inserting new (positional) etc.
	 * It is intended that this is called as each row to MergableGridWidget is
	 * added.
	 * 
	 * @param row
	 *            The row for which the selector will be added
	 */
	public abstract void appendSelector(DynamicDataRow row);

	/**
	 * Delete a Selector at the given index.
	 * 
	 * @param index
	 */
	public abstract void deleteSelector(int index);

	/**
	 * Initialise the sidebar, this normally involves clearing any content and
	 * setting up any formatting requirements before calls to addSelector are
	 * made. I.E. ensure the sidebar is empty before items are added to it.
	 */
	public abstract void initialise();

	/**
	 * Insert a Selector before the given index.
	 * 
	 * @param row
	 *            The row for which the selector will be added
	 * @param index
	 */
	public abstract void insertSelectorBefore(DynamicDataRow row, int index);

	/**
	 * Set scroll position to enable some degree of synchronisation between
	 * DecisionTable and DecisionTableSidebar
	 * 
	 * @param position
	 */
	public abstract void setScrollPosition(int position);

}
