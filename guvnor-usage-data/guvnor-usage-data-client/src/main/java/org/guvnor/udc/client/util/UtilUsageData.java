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

package org.guvnor.udc.client.util;

import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.guvnor.udc.client.event.EventsUsageData;
import org.guvnor.udc.model.EventTypes;
import org.guvnor.udc.model.InfoUsageDataSummary;
import org.guvnor.udc.model.UsageEventSummary;
import org.uberfire.mvp.PlaceRequest;
import org.uberfire.mvp.impl.DefaultPlaceRequest;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gwt.i18n.client.DateTimeFormat;

public class UtilUsageData {

    private static final String INCOMING = "Show a list of changes made by others";
	private static final String RECENT_EDITED = "Show a list of recently edited assets";
	private static final String RECENT_VIEWED = "Show a list of recently opened assets";
	private static final String UDC_DESCRIPTION = "Show Events of Usage Data Collector";
	public static final String patternDateTime = "yyyy-MM-dd HH:mm:ss";
    public static final String HEADER_TITLE_CSV = "Timestamp, Module, User, Component, Description, Action, key, Level, Status";
    private static final String POPUP_DETAIL_UDC = "Detail Usage Data";

    public static String getDateTime(Date date, String pattern) {
        DateTimeFormat fmt = DateTimeFormat.getFormat(patternDateTime);
        return fmt.format(date);
    }

    public static String getComponentFormated(Set<String> setInfo) {
        StringBuilder componentFormated = new StringBuilder();
        for (String component : setInfo) {
            componentFormated.append(component);
            componentFormated.append(" ");
        }
        return componentFormated.toString();
    }

    @SuppressWarnings("unchecked")
    public static Map<String, Set<String>> getAllComponentByModule() {
        Map<String, Set<String>> auditions = Maps.newHashMap();
        for (EventsUsageData actionHuman : EventsUsageData.values()) {
            Set<String> setComponent = (Set<String>) (auditions.get(actionHuman
                    .getModule()) == null ? Sets
                    .newHashSetWithExpectedSize(EventsUsageData.values().length)
                    : auditions.get(actionHuman.getModule()));
            setComponent.add(actionHuman.getComponent());
            auditions.put(actionHuman.getModule(), setComponent);
        }
        return auditions;
    }

    public static String getRowFormatted(UsageEventSummary usage) {
        StringBuilder rowFormatted = new StringBuilder();
        rowFormatted.append("\n");
        rowFormatted.append(UtilUsageData.getDateTime(usage.getTimestamp(),
                UtilUsageData.patternDateTime) + ",");
        rowFormatted.append(usage.getModule() + ",");
        rowFormatted.append(usage.getFrom() + ",");
        rowFormatted.append(usage.getComponent() + ",");
        rowFormatted.append(usage.getDescription() + ",");
        rowFormatted.append(usage.getAction() + ",");
        rowFormatted.append(usage.getKey() + ",");
        rowFormatted.append(usage.getLevel() + ",");
        rowFormatted.append(usage.getStatus());
        return rowFormatted.toString();
    }

    public static String wrapString(String s, int maxLength) {
        return (s.length() < maxLength ? s : s.substring(0, maxLength).concat(
                "..."));
    }

    public static PlaceRequest getPlaceRequestDetailUDC(UsageEventSummary usage) {
        PlaceRequest placeRequestImpl = new DefaultPlaceRequest(POPUP_DETAIL_UDC);
        placeRequestImpl.addParameter("key", usage.getKey());
        placeRequestImpl.addParameter("module", usage.getModule());
        placeRequestImpl.addParameter("component", usage.getComponent());
        placeRequestImpl.addParameter("action", usage.getAction());
        placeRequestImpl.addParameter("user", usage.getFrom());
        placeRequestImpl.addParameter("level", usage.getLevel());
        placeRequestImpl.addParameter("status", usage.getStatus());
        placeRequestImpl.addParameter("description", usage.getDescription());
        placeRequestImpl.addParameter("fileSystem", usage.getFileSystem());
        placeRequestImpl.addParameter("fileName", usage.getFileName());
        placeRequestImpl.addParameter("ItemPath", usage.getItemPath());
        return placeRequestImpl;
    }

    public static UsageEventSummary getUsageDataParam(PlaceRequest place) {
        UsageEventSummary usage = new UsageEventSummary();
        usage.setKey(place.getParameter("key", "0"));
        usage.setComponent(place.getParameter("component", "0"));
        usage.setAction(place.getParameter("action", "0"));
        usage.setFrom(place.getParameter("user", "0"));
        usage.setStatus(place.getParameter("status", "0"));
        usage.setLevel(place.getParameter("level", "0"));
        usage.setModule(place.getParameter("module", "0"));
        usage.setDescription(place.getParameter("description", "0"));
        usage.setFileSystem(place.getParameter("fileSystem", "0"));
        usage.setFileName(place.getParameter("fileName", "0"));
        usage.setItemPath(place.getParameter("ItemPath", "0"));
        return usage;
    }
    
    public static List<InfoUsageDataSummary> getComponentsAudited(){
    	List<InfoUsageDataSummary> listComponents  = Lists.newArrayList();
    	listComponents.add(new InfoUsageDataSummary(EventTypes.USAGE_DATA.toString(), UDC_DESCRIPTION));
    	listComponents.add(new InfoUsageDataSummary(EventTypes.RECENT_VIEWED_ID.toString(), RECENT_VIEWED));
    	listComponents.add(new InfoUsageDataSummary(EventTypes.RECENT_EDITED_ID.toString(), RECENT_EDITED));
    	listComponents.add(new InfoUsageDataSummary(EventTypes.INCOMING_ID.toString(), INCOMING));
    	return listComponents;
    }
    
}