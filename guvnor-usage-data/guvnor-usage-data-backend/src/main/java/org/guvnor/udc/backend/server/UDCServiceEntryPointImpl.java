/*
 * Copyright 2013 JBoss by Red Hat.
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

package org.guvnor.udc.backend.server;

import java.util.Queue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.udc.model.EventTypes;
import org.guvnor.udc.model.GfsSummary;
import org.guvnor.udc.model.UsageEventSummary;
import org.guvnor.udc.service.UDCServiceEntryPoint;
import org.guvnor.udc.service.UDCVfsService;
import org.jboss.errai.bus.server.annotations.Service;
import org.kie.commons.validation.PortablePreconditions;

@Service
@ApplicationScoped
public class UDCServiceEntryPointImpl implements UDCServiceEntryPoint {

	// this service stored in session
	//@Inject
	//private UDCSessionService udcStorageService;

	@Inject
	private UDCVfsService udcStorageService;

	@Override
	public void auditEventUDC(UsageEventSummary usageDataEvent) {
		PortablePreconditions.checkNotNull("usageDataEvent", usageDataEvent);
		if (validatePermissions(usageDataEvent.getModule())) {
			addToUsageData(usageDataEvent);
		}
	}

	@Override
	public Queue<UsageEventSummary> readUsageDataCollector() {
		return udcStorageService.readUsageDataCollector();
	}

	@Override
	public void addToUsageData(UsageEventSummary usageDataEvent) {
		PortablePreconditions.checkNotNull("usageDataEvent", usageDataEvent);
		udcStorageService.addToUsageData(usageDataEvent);
	}

	@Override
	public void removeEventsByFilter(EventTypes eventType, String userName) {
		PortablePreconditions.checkNotNull("eventType", eventType);
		PortablePreconditions.checkNotNull("userName", userName);
		udcStorageService.removeEventsByFilter(eventType, userName);
	}
	
	@Override
	public Queue<UsageEventSummary> readEventsByFilter(EventTypes eventType, String userName) {
		PortablePreconditions.checkNotNull("eventType", eventType);
		PortablePreconditions.checkNotNull("userName", userName);
		return udcStorageService.readEventsByFilter(eventType, userName);
	}
	
	@Override
    public GfsSummary getInfoGfs() {
        return udcStorageService.getInfoGfs();
    }
	
	private boolean validatePermissions(String module) {
		// TODO validate permissions
		return true;
	}
	
}
