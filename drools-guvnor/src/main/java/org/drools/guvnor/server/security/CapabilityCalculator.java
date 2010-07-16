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

package org.drools.guvnor.server.security;

import static org.drools.guvnor.client.security.Capabilities.SHOW_CREATE_NEW_ASSET;
import static org.drools.guvnor.client.security.Capabilities.SHOW_CREATE_NEW_PACKAGE;
import static org.drools.guvnor.client.security.Capabilities.SHOW_DEPLOYMENT;
import static org.drools.guvnor.client.security.Capabilities.SHOW_DEPLOYMENT_NEW;
import static org.drools.guvnor.client.security.Capabilities.SHOW_PACKAGE_VIEW;
import static org.drools.guvnor.client.security.Capabilities.SHOW_QA;
import static org.drools.guvnor.client.security.Capabilities.all;

import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.security.Capabilities;

/**
 * Load up the capabilities from a given list of roles.
 * @author Michael Neale
 */
public class CapabilityCalculator {

	public Capabilities calcCapabilities(List<RoleBasedPermission> permissions, Map<String, String> features) {
		if (permissions.size() == 0) {
			return Capabilities.all(features);
		} else {
			Capabilities caps = new Capabilities();
			for (RoleBasedPermission p : permissions) {
				String r = p.getRole();
				if (r.equals(RoleTypes.ADMIN)) {
					return all(features);
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
            caps.prefs = features;
			return caps;
		}
	}

	private void addCap(Capabilities caps, Integer cap) {
		if (!caps.list.contains(cap)) caps.list.add(cap);
	}

}
