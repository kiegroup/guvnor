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

package org.drools.guvnor.client.explorer;

import java.util.ArrayList;
import java.util.List;

import org.drools.guvnor.client.rpc.PackageConfigData;

public class PackageHierarchy {


	public Folder root = new Folder();

	public void addPackage(PackageConfigData conf) {
		Folder folder = root;
		String[] folders = conf.name.split("\\.");
		for (int i = 0; i < folders.length; i++) {
			String f = folders[i];
			Folder existing = folder.contains(f);
			if (existing == null || existing.children.size() == 0) {
				if (i == folders.length - 1) {
					//leaf
					folder = folder.add(f, conf);
				} else {
					folder = folder.add(f, null);
				}

			} else {
				folder = existing;
			}
		}
	}



	public static class Folder {
		public String name;
		public PackageConfigData conf;

		public Folder add(String f, PackageConfigData conf) {
			Folder n = new Folder();
			n.name = f;
			n.conf = conf;
			children.add(n);
			return n;
		}

		public String toString() {
			return name;
		}

		public Folder contains(String f) {
            for (Folder fld : children) {
                if (fld.name.equals(f)) {
                    return fld;
                }
            }
			return null;
		}

		public List<Folder> children = new ArrayList<Folder>();
	}

}
