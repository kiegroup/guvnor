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
public class UsageResourceEvent {
	
	private String itemPath;
	
	private String fileName;
	
	private String fileSystem;
	
	private String from;
	
	private String toUser;

	public long timestampResource;

	
	public UsageResourceEvent() {
	}

	public String getItemPath() {
		return itemPath;
	}

	public void setItemPath(String itemPath) {
		this.itemPath = itemPath;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public String getFileSystem() {
		return fileSystem;
	}

	public void setFileSystem(String fileSystem) {
		this.fileSystem = fileSystem;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public long getTimestampResource() {
		return timestampResource;
	}

	public void setTimestampResource(long timestampResource) {
		this.timestampResource = timestampResource;
	}

    public String getToUser() {
        return toUser;
    }

    public void setToUser(String toUser) {
        this.toUser = toUser;
    }

    

}
