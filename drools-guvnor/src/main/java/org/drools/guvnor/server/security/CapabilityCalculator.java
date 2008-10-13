package org.drools.guvnor.server.security;

import static org.drools.guvnor.client.security.Capabilities.SHOW_CREATE_NEW_ASSET;
import static org.drools.guvnor.client.security.Capabilities.SHOW_CREATE_NEW_PACKAGE;
import static org.drools.guvnor.client.security.Capabilities.SHOW_DEPLOYMENT;
import static org.drools.guvnor.client.security.Capabilities.SHOW_DEPLOYMENT_NEW;
import static org.drools.guvnor.client.security.Capabilities.SHOW_PACKAGE_VIEW;
import static org.drools.guvnor.client.security.Capabilities.SHOW_QA;
import static org.drools.guvnor.client.security.Capabilities.all;

import java.util.List;

import org.drools.guvnor.client.security.Capabilities;

/**
 * Load up the capabilities from a given list of roles.
 * @author Michael Neale
 */
public class CapabilityCalculator {

	public Capabilities calcCapabilities(List<RoleBasedPermission> permissions) {
		if (permissions.size() == 0) {
			return new Capabilities();
		} else {
			Capabilities caps = new Capabilities();
			for (RoleBasedPermission p : permissions) {
				String r = p.getRole();
				if (r.equals(RoleTypes.ADMIN)) {
					return all();
				} else if (r.equals(RoleTypes.PACKAGE_ADMIN)) {
					addCap(caps, SHOW_PACKAGE_VIEW);
					addCap(caps, SHOW_CREATE_NEW_ASSET);
					addCap(caps, SHOW_CREATE_NEW_PACKAGE);
					addCap(caps, SHOW_DEPLOYMENT);
					addCap(caps, SHOW_DEPLOYMENT_NEW);
					addCap(caps, SHOW_QA);
				} else if (r.equals(RoleTypes.PACKAGE_DEVELOPER)) {
					addCap(caps, SHOW_PACKAGE_VIEW);
					addCap(caps, SHOW_CREATE_NEW_ASSET);
					addCap(caps, SHOW_QA);
				} else if (r.equals(RoleTypes.PACKAGE_READONLY)) {
					addCap(caps, SHOW_PACKAGE_VIEW);
				}
			}
			return caps;
		}
	}

	private void addCap(Capabilities caps, Integer cap) {
		if (!caps.list.contains(cap)) caps.list.add(cap);
	}

}
