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

public class ActionInsertFactCol extends ActionCol {

	/**
	 * The fact type (class) that is to be created.
	 * eg Driver, Person, Cheese.
	 */
	public String factType;

	/**
	 * The bound name of the variable to be effected.
	 * If the same name appears twice, is it merged into the same action.
	 */
	public String boundName;

	/**
	 * The field on the fact being effected.
	 */
	public String factField;

	/**
	 * Same as the type in ActionFieldValue - eg, either a String, or Numeric.
	 * Refers to the data type of the literal value in the cell.
	 * Refer to the types in SuggestionCompletionEngine.
	 */
	public String type;

	/**
	 * An optional comman separated list of values.
	 */
	public String valueList;


}
