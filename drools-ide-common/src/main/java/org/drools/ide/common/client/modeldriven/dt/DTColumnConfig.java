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

import org.drools.ide.common.client.modeldriven.brl.PortableObject;

public class DTColumnConfig implements PortableObject {

	/**
	 * If this is not -1, then this is the width which will be displayed.
	 */
	public int width = -1;


    /**
     * For a default value ! Will still be in the array of course, just use this value if its empty.
     */
    public String defaultValue = null;

    /**
     * to hide the column (eg if it has a mandatory default).
     */
    public boolean hideColumn = false;
    
    /**
     * to use the row number as number for the salience attribute.
     */
    public boolean useRowNumber = false;

}
