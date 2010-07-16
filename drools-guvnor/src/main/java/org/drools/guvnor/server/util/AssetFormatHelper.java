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

package org.drools.guvnor.server.util;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.common.AssetFormats;
import org.drools.repository.RulesRepositoryException;

/**
 * Needed to list what registered format types there are.
 * @author Michael Neale
 */
public class AssetFormatHelper {

	public static String[] listRegisteredTypes() {
		try {
			Field[] flds = AssetFormats.class.getFields();
			List<String> r = new ArrayList<String>();
			for (int i = 0; i < flds.length; i++) {
				Object val =  flds[i].get(AssetFormats.class);
				if (val instanceof String) {
					r.add((String) val);
				}

			}
			return r.toArray(new String[r.size()]);
		} catch (Exception e) {
			throw new RulesRepositoryException(e);
		}
	}

}
