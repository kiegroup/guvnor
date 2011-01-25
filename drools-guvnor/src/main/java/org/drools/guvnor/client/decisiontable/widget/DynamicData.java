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

import java.util.ArrayList;

/**
 * A simple container for rows of data.
 * 
 * @author manstis
 * 
 */
public class DynamicData extends ArrayList<DynamicDataRow> {

	private static final long serialVersionUID = -3710491920672816057L;

	public CellValue<? extends Comparable<?>> get(Coordinate c) {
		return this.get(c.getRow()).get(c.getCol());
	}

	public void set(Coordinate c, Comparable<?> value) {
		this.get(c.getRow()).get(c.getCol()).setValue(value);
	}

}
