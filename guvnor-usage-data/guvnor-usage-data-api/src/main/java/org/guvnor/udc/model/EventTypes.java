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

package org.guvnor.udc.model;

import org.jboss.errai.common.client.api.annotations.Portable;

@Portable
public enum EventTypes {
	
	USAGE_DATA("USAGE DATA", ""),
	
	RECENT_EDITED_ID("RECENT EDITED", "recentEdited"),
	
	RECENT_VIEWED_ID("RECENT VIEWED", "recentViewed"),
	
	INCOMING_ID("INCOMING", "incoming"),
	
	ALL("ALL EVENTS", "");
	
	private String description;
	private String inboxName;
	
	EventTypes(String description, String inboxName){
		this.description = description;
		this.inboxName = inboxName;
	}

	public String getDescription() {
		return description;
	}

	public String getInboxName() {
		return inboxName;
	}
	
}
