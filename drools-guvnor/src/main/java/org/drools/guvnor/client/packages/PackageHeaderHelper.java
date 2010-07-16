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

package org.drools.guvnor.client.packages;

import java.util.Iterator;

import org.drools.guvnor.client.packages.PackageHeaderWidget.Global;
import org.drools.guvnor.client.packages.PackageHeaderWidget.Import;
import org.drools.guvnor.client.packages.PackageHeaderWidget.Types;

public class PackageHeaderHelper {

	/**
	 * Attempt to parse out a model, if it can't, it will return null in which case an "advanced" editor should be used.
	 */
	static Types parseHeader(String header) {
		if (header == null || header.equals("")) {
			Types t = new Types();
			return t;
		} else {
			Types t = new Types();

			String[] lines = header.split("\\n");

			for (int i = 0; i < lines.length; i++) {
				String tk = lines[i].trim();
				if (!tk.equals("") && !tk.startsWith("#")) {
					if (tk.startsWith("import")) {
						tk = tk.substring(6).trim();
						if (tk.endsWith(";")) {
							tk = tk.substring(0, tk.length() - 1);
						}
						t.imports.add(new Import(tk));
					} else if (tk.startsWith("global")) {
						tk = tk.substring(6).trim();
						if (tk.endsWith(";")) {
							tk = tk.substring(0, tk.length() - 1);
						}
						String[] gt = tk.split("\\s+");
						t.globals.add(new Global(gt[0], gt[1]));
					} else {
						return null;
					}
				}
			}

			return t;

		}

	}

	static String renderTypes(Types t) {
		StringBuffer sb = new StringBuffer();
		for (Iterator iterator = t.imports.iterator(); iterator.hasNext();) {
			Import i = (Import) iterator.next();
			sb.append("import " + i.type + "\n");
		}

		for (Iterator it = t.globals.iterator(); it.hasNext();) {
			Global g = (Global) it.next();
			sb.append("global " + g.type + " " + g.name);
			if (it.hasNext()) {
				sb.append('\n');
			}
		}
		return sb.toString();
	}

}
