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
package org.drools.guvnor.client.widgets.decoratedgrid;

import org.drools.guvnor.client.table.SortDirection;

/**
 * Container for sort information. Encapsulated in a single class to avoid the
 * need for multiple ValueChangeHandlers for all attributes affecting sorting on
 * a Column.
 * 
 * @author manstis
 * 
 */
public class SortConfiguration {
	private SortDirection sortDirection = SortDirection.NONE;
	private Boolean isSortable = true;
	private int sortIndex = -1;

	public SortDirection getSortDirection() {
		return sortDirection;
	}

	public void setSortDirection(SortDirection sortDirection) {
		this.sortDirection = sortDirection;
	}

	public Boolean isSortable() {
		return isSortable;
	}

	public void setSortable(Boolean isSortable) {
		this.isSortable = isSortable;
	}

	public int getSortIndex() {
		return sortIndex;
	}

	public void setSortIndex(int sortIndex) {
		this.sortIndex = sortIndex;
	}

}
