package org.drools.brms.client.explorer;

import java.util.ArrayList;
import java.util.List;

public class PackageHierarchy {


	public Folder root = new Folder();

	public void addPackage(String packageName) {
		Folder folder = root;
		String[] folders = packageName.split("\\.");
		for (int i = 0; i < folders.length; i++) {
			String f = folders[i];
			Folder existing = folder.contains(f);
			if (existing == null || existing.children.size() == 0) {
				folder = folder.add(f);
			} else {
				folder = existing;
			}
		}
	}



	public static class Folder {
		String name;
		boolean hasChildren() {
			return children.size() > 0;
		}

		public Folder add(String f) {
			Folder n = new Folder();
			n.name = f;
			children.add(n);
			return n;
		}

		public String toString() {
			return name;
		}

		public Folder contains(String f) {
			for (int i = 0; i < children.size(); i++) {
				Folder fld = (Folder) children.get(i);
				if (fld.name.equals(f)) {
					return fld;
				}
			}
			return null;
		}

		public List children = new ArrayList();
	}

}
