/*
 * Copyright 2011 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.guvnor.structure.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.CssResource;

/**
 * Wizard resources
 */
public interface NavigatorResources
        extends
        ClientBundle {

    NavigatorResources INSTANCE = GWT.create( NavigatorResources.class );

    @Source("css/Navigator.css")
    NavigatorStyle css();

    interface NavigatorStyle extends CssResource {

        String navigator();

        String message();

        @ClassName("message-left")
        String messageLeftContainer();

        @ClassName("message-right")
        String messageRightContainer();

        @ClassName("author-date-container")
        String authorDateContainer();

        String author();

        String date();

        @ClassName("navigator-message")
        String navigatorMessage();

        @ClassName("tree-nav")
        String treeNav();
    }
}