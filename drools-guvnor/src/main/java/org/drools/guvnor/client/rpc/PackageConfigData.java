package org.drools.guvnor.client.rpc;
/*
 * Copyright 2005 JBoss Inc
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



import java.util.Date;
import java.util.HashMap;

import com.google.gwt.user.client.rpc.IsSerializable;

/**
 * This contains data for a package configuration.
 * @author Michael Neale
 *
 */
public class PackageConfigData
    implements
    IsSerializable {

	public PackageConfigData() {}
	public PackageConfigData(String name) {
		this.name = name;
	}

    public String uuid;
    public String header;
    public String externalURI;
    public String name;
    public String description;
    public Date   lastModified;
    public String lasContributor;
    public String state;
    public boolean archived = false;
    public boolean isSnapshot = false;
    public String snapshotName;
    public Date dateCreated;
    public String checkinComment;
    public HashMap catRules;
}