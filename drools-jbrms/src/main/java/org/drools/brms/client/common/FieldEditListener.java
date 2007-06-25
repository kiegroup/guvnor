package org.drools.brms.client.common;
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



import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Widget;

/**
 * This is a utility listener for trapping text field edits,
 * and can be used for instance to stretch the size of a text box.
 * 
 * Responds to the key up event.
 * 
 * @author Michael Neale
 *
 */
public class FieldEditListener
    implements
    KeyboardListener {
    
    private Command command;

    public FieldEditListener(Command command) {
        this.command = command;
    }
    

    public void onKeyDown(Widget arg0,
                          char arg1,
                          int arg2) {


    }

    public void onKeyPress(Widget arg0,
                           char arg1,
                           int arg2) {

    }

    public void onKeyUp(Widget arg0,
                        char arg1,
                        int arg2) {
        this.command.execute();
    }

}