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

package org.drools.resource;

/**
 * The repository elements that can be referenced via URL will typically 
 * have several attributes including a unique URL, resource type, name, version
 * and of course content. This meta-data object just describes what the URL 
 * resource is. Single Rules, DRLs, functions, DSLs and spreadsheets all need
 * to be supported.
 * 
 * @author James Williams (james.williams@redhat.com)
 *
 */
public class RepositoryBean {

    private String version = "-1";
    private String name;
    private ResourceType    resourceType;
    private String resourceContent;

    public String getResourceContent() {
        return resourceContent;
    }

    public void setResourceContent(String resourceContent) {
        this.resourceContent = resourceContent;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ResourceType getResourceType() {
        return resourceType;
    }

    public void setResourceType(ResourceType resourceType) {
        this.resourceType = resourceType;
    }

    public String getVersion() {
        return version;
    }

    public long getVersionInLong() {
        return Long.valueOf( this.version ).longValue();
    }

    public void setVersion(long version) {
        this.version = String.valueOf( version );
    }

    public void setVersion(String version) {
        this.version = version;
    }
}
