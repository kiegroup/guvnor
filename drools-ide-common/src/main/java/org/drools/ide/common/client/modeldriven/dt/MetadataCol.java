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

/**
 * This is a rule metadata - eg @foo(bar) etc.
 * 
 * @author Michael Rhoden
 * 
 */
public class MetadataCol extends DTColumnConfig {

	public String attr;

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof MetadataCol)) {
			return false;
		}
		MetadataCol that = (MetadataCol) obj;
		return nullOrEqual(this.attr, that.attr) && super.equals(obj);
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (attr == null ? 0 : attr.hashCode());
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
