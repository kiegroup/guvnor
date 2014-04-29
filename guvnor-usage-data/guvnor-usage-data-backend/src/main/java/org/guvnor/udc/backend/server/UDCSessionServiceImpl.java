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

import java.util.LinkedList;
import java.util.Queue;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import org.guvnor.udc.model.EventTypes;
import org.guvnor.udc.model.GfsSummary;
import org.guvnor.udc.model.UsageEventSummary;
import org.guvnor.udc.service.UDCSessionService;
import org.jboss.errai.bus.server.annotations.Service;

import com.google.common.collect.Lists;

/**
 * This service stored in a QueueSession all Usage Data.   
 * recent_edited, recent_viewed and icoming are stored in vfs
 */
@Service
@ApplicationScoped
public class UDCSessionServiceImpl extends UDCSessionManager implements
		UDCSessionService {

	private static final String KEY_EVENTS = "UDC";

	@Inject
	private UDCSessionService udcStorageService;

	@Override
	@SuppressWarnings("unchecked")
	public Queue<UsageEventSummary> readUsageDataCollector() {
		return (super.getSession().getAttribute(KEY_EVENTS) != null) ? (Queue<UsageEventSummary>) super
				.getSession().getAttribute(KEY_EVENTS) : null;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void addToUsageData(UsageEventSummary usageData) {
		Queue<UsageEventSummary> points = (super.getSession().getAttribute(
				KEY_EVENTS) == null) ? new LinkedList<UsageEventSummary>()
				: (Queue<UsageEventSummary>) super.getSession().getAttribute(
						KEY_EVENTS);
		points.add(usageData);
		super.getSession().setAttribute(KEY_EVENTS, points);
	}

	@Override
	public void removeEventsByFilter(EventTypes eventType, String userName) {
		switch (eventType) {
		case USAGE_DATA:
			super.getSession().removeAttribute(KEY_EVENTS);
		default:
			// call vfsService
			udcStorageService.removeEventsByFilter(eventType, userName);
			break;
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public UsageEventSummary getUsageDataByKey(String key) {
		UsageEventSummary usageData = null;
		Queue<UsageEventSummary> points = (Queue<UsageEventSummary>) super
				.getSession().getAttribute(KEY_EVENTS);
		for (UsageEventSummary ud : points) {
			if (ud.getKey().equals(key)) {
				usageData = ud;
				break;
			}
		}
		return usageData;
	}

	@Override
	public Queue<UsageEventSummary> readEventsByFilter(EventTypes eventType,
			String userName) {
		Queue<UsageEventSummary> usages = Lists.newLinkedList();
		switch (eventType) {
		case USAGE_DATA:
			usages = readUsageDataCollector();
		default:
			// call vfsService
			usages = udcStorageService.readEventsByFilter(eventType, userName);
			break;
		}
		return usages;
	}

    @Override
    public GfsSummary getInfoGfs() {
        // TODO Auto-generated method stub
        return null;
    }
}
