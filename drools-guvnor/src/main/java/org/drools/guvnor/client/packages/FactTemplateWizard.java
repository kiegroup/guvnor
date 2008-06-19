package org.drools.guvnor.client.packages;
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



import org.drools.guvnor.client.common.FormStylePopup;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * For building fact templates,. as a wizard. 
 * Sheesh you would think the name says enough.
 * 
 * @author Michael Neale
 */
public class FactTemplateWizard extends FormStylePopup {

    
    
    private FlexTable attributes;

    private Command okClick;

    private TextBox name;
    
    public FactTemplateWizard() {
        

        super("images/new_wiz.gif", "Create a new fact template");
        attributes = new FlexTable();
        name = new TextBox();
        addAttribute( "Name:", name );
        addAttribute( "Fact attributes:", attributes );
        Image newAttr = new Image("images/new_item.gif");
        newAttr.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                addAttribute();
            }
        } );
        addAttribute( "Add a new attribute", newAttr );
        
        Button ok = new Button("Create");
        ok.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                okClick.execute();
                hide();
            }
        } );
        
        addAttribute("", ok );
    }
    
    public void setOKClick(Command com) {
        this.okClick = com;
    }
    
    private void addAttribute() {
        int row = attributes.getRowCount();
        attributes.setWidget( row, 0, new TextBox());
        attributes.setWidget( row, 1, typeList() );
        
    }
    
    private Widget typeList() {
        ListBox list = new ListBox();
        list.addItem( "String" );
        list.addItem( "Integer" );
        list.addItem( "Float" );
        list.addItem( "Date" );
        list.addItem( "Boolean" );
        return list;
    }

    /**
     * This will return a text version of the template to add in.
     */
    public String getTemplateText() {
        String result = "template " + name.getText() + "\n"; 
        for (int i = 0; i < attributes.getRowCount(); i++) {
            ListBox type = (ListBox) attributes.getWidget( i, 1 );
            String typeName = type.getItemText( type.getSelectedIndex() );
            String slotName = ((TextBox) attributes.getWidget( i, 0 )).getText();
            result = result + "\t" + typeName + " " + slotName + "\n";
        }

        return result + "end";
    }


}