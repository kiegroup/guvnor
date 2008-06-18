package org.drools.brms.client.explorer;

import java.util.ArrayList;
import java.util.List;

import org.drools.brms.client.rpc.PackageConfigData;

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
		String name;
		PackageConfigData conf;

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
