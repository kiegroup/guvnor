/*
 * Copyright 2012 JBoss Inc
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

package org.drools.guvnor.client.asseteditor.drools.enums;

import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Created with IntelliJ IDEA.
 * User: raymondefa
 * Date: 6/6/12
 * Time: 1:34 PM
 * To change this template use File | Settings | File Templates.
 */
public class EnumRow {

    private String fieldName="";
    private String factName="";
    private String context="";

    public EnumRow(String line) {

        String text=line;
         if(text==""){
             factName="";
             fieldName="";
             context="";
         }
        else{
        factName= text.substring(1,text.indexOf("."));

        fieldName = text.substring(text.indexOf(".") +1,text.indexOf("':"));
         context = text.substring(text.indexOf(":") +1 ).trim();
         }
    }



    public String getText() {
        if(factName==""){
            return "";
        }
        else {
        return "'" + factName + "." + fieldName + "': " + context;
        }
    }


    public String getFactName() {
        return factName;  //To change body of created methods use File | Settings | File Templates.
    }

    public String getFieldName() {
        return fieldName;  //To change body of created methods use File | Settings | File Templates.
    }

    public String getContext() {
        return context;  //To change body of created methods use File | Settings | File Templates.
    }

    public void setFactName(String factName) {
        this.factName=factName;

    }

    public void setFieldName(String fieldName) {
        this.fieldName=fieldName;
    }

    public void setContext(String context) {
        this.context=context;
    }
}
