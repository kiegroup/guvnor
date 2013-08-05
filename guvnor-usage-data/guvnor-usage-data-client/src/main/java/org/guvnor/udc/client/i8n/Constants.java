/*
 * Copyright 2013 JBoss Inc
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

package org.guvnor.udc.client.i8n;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

/**
 * This uses GWT to provide client side compile time resolving of locales. See:
 * http://code.google.com/docreader/#p=google-web-toolkit-doc-1-5&s=google-web-
 * toolkit-doc-1-5&t=DevGuideInternationalization (for more information).
 * <p/>
 * Each method name matches up with a key in Constants.properties (the
 * properties file can still be used on the server). To use this, use
 * <code>GWT.create(Constants.class)</code>.
 */
public interface Constants extends Messages {

    Constants INSTANCE = GWT.create(Constants.class);

    String List_Usage_Data();

    String No_Usage_Data();

    String Key();

    String Actions();

    String User();

    String Refresh();

    String Events_Refreshed();

    String Time();

    String Export_Csv();
    
    String Status();
    
    String Component();
    
    String Clear();
    
    String Clear_Msj();
    
    String Info();
    
    String Level();
    
    String Info_Usage_Data();
    
    String Module();
    
    String No_Module_Audited();
    
    String Description();
    
    String Detail();
    
    String Title_Detail();
    
    String Vfs();
    
    String FileSystem();
    
    String FileName();
    
    String ItemPath();
    
    String Type();
    
    String Gfs_info();
    
    String Inbox_Existing();
    
    String Path();
    
    String Uri();

}