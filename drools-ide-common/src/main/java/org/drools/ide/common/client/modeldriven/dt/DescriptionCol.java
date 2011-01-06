/**
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

/**
 * This is a rule description
 * 
 * @author manstis
 * 
 */
public class DescriptionCol extends DTColumnConfig {

	private static final long serialVersionUID = -306736594255777798L;

	private String description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (!(obj instanceof DescriptionCol)) {
			return false;
		}
		DescriptionCol that = (DescriptionCol) obj;
		return nullOrEqual(this.description, that.description)
				&& super.equals(obj);
	}

	@Override
	public int hashCode() {
		int hash = 1;
		hash = hash * 31 + (description == null ? 0 : description.hashCode());
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
