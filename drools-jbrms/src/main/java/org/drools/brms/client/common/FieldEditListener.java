package org.drools.brms.client.common;

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
