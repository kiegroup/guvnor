/*
 * Copyright 2010 JBoss Inc
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

package org.drools.ide.common.client.modeldriven.dt;

import org.drools.ide.common.client.modeldriven.brl.PortableObject;

public class DTColumnConfig implements PortableObject {

	private static final long serialVersionUID = 510l;

	/**
	 * If this is not -1, then this is the width which will be displayed.
	 */
	private int width = -1;

	/**
	 * For a default value ! Will still be in the array of course, just use this
	 * value if its empty.
	 */
	private String defaultValue = null;

	/**
	 * to hide the column (eg if it has a mandatory default).
	 */
	private boolean hideColumn = false;

	/**
	 * to use the row number as number for the salience attribute.
	 */
	private boolean useRowNumber = false;

	/**
	 * to use the reverse order of the row number as the salience attribute.
	 */
	private boolean reverseOrder = false;

	public void setWidth(int width) {
		this.width = width;
	}

	public int getWidth() {
		return width;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setHideColumn(boolean hideColumn) {
		this.hideColumn = hideColumn;
	}

	public boolean isHideColumn() {
		return hideColumn;
	}

	public void setUseRowNumber(boolean useRowNumber) {
		this.useRowNumber = useRowNumber;
	}

	public boolean isUseRowNumber() {
		return useRowNumber;
	}

	public void setReverseOrder(boolean reverseOrder) {
		this.reverseOrder = reverseOrder;
	}

	public boolean isReverseOrder() {
		return reverseOrder;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DTColumnConfig)) {
			return false;
		}
		DTColumnConfig that = (DTColumnConfig) obj;
		return this.width == that.width
				&& nullOrEqual(this.defaultValue, that.defaultValue)
				&& this.hideColumn == that.hideColumn
				&& this.useRowNumber == that.useRowNumber
				&& this.reverseOrder == that.reverseOrder;
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + width;
		hash = hash * 31 + (defaultValue == null ? 0 : defaultValue.hashCode());
		hash = hash * 31 + (hideColumn ? 1 : 0);
		hash = hash * 31 + (useRowNumber ? 1 : 0);
		hash = hash * 31 + (reverseOrder ? 1 : 0);
		return hash;
	}

	private boolean nullOrEqual(Object thisAttr, Object thatAttr) {
		if (thisAttr == null && thatAttr == null) {
			return true;
		}
		if (thisAttr == null && thatAttr != null) {
			return false;
		}
		return thisAttr.equals(thatAttr);
	}

}
