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
package org.drools.ide.common.client.modeldriven.dt;

public class ActionSetFieldCol extends ActionCol {

	private static final long serialVersionUID = 510l;

	/**
	 * The bound name of the variable to be effected. If the same name appears
	 * twice, is it merged into the same action.
	 */
	private String boundName;

	/**
	 * The field on the fact being effected.
	 */
	private String factField;

	/**
	 * Same as the type in ActionFieldValue - eg, either a String, or Numeric.
	 * Refers to the data type of the literal value in the cell. These values
	 * come from SuggestionCompletionEngine.
	 */
	private String type;

	/**
	 * An optional comma separated list of values.
	 */
	private String valueList;

	/**
	 * This will be true if it is meant to be a modify to the engine, when in
	 * inferencing mode.
	 */
	private boolean update = false;

	public void setValueList(String valueList) {
		this.valueList = valueList;
	}

	public String getValueList() {
		return valueList;
	}

	public void setBoundName(String boundName) {
		this.boundName = boundName;
	}

	public String getBoundName() {
		return boundName;
	}

	public void setFactField(String factField) {
		this.factField = factField;
	}

	public String getFactField() {
		return factField;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getType() {
		return type;
	}

	public void setUpdate(boolean update) {
		this.update = update;
	}

	public boolean isUpdate() {
		return update;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof ActionSetFieldCol)) {
			return false;
		}
		ActionSetFieldCol that = (ActionSetFieldCol) obj;
		return nullOrEqual(this.boundName, that.boundName)
				&& nullOrEqual(this.factField, that.factField)
				&& nullOrEqual(this.type, that.type)
				&& nullOrEqual(this.valueList, that.valueList)
				&& this.update == that.update && super.equals(obj);
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (boundName == null ? 0 : boundName.hashCode());
		hash = hash * 31 + (factField == null ? 0 : factField.hashCode());
		hash = hash * 31 + (type == null ? 0 : type.hashCode());
		hash = hash * 31 + (valueList == null ? 0 : valueList.hashCode());
		hash = hash * 31 + (update ? 1 : 0);
		hash = hash * 31 + super.hashCode();
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
