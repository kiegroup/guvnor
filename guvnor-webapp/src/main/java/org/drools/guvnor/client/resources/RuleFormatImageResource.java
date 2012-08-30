/*
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
package org.drools.guvnor.client.resources;

import com.google.gwt.resources.client.ImageResource;
import com.google.gwt.user.client.ui.Image;

public class RuleFormatImageResource
    implements
    ImageResource,
    Comparable<RuleFormatImageResource> {

    private final String        format;
    private final Image imageResource;

    public RuleFormatImageResource(String format,
                                   Image imageResource) {
        this.format = format;
        this.imageResource = imageResource;
    }

    public String getName() {
        return this.imageResource.getTitle();
    }

    public int compareTo(RuleFormatImageResource o) {
        return format.compareTo( o.format );
    }

    public int getHeight() {
        return this.imageResource.getHeight();
    }

    public int getLeft() {
        return this.imageResource.getOriginLeft();
    }

    public int getTop() {
        return this.imageResource.getOriginTop();
    }

    public String getURL() {
        return this.imageResource.getUrl();
    }

    public int getWidth() {
        return this.imageResource.getWidth();
    }

    public boolean isAnimated() {
    	return true;
        //return this.imageResource.isAnimated();
    }

}
