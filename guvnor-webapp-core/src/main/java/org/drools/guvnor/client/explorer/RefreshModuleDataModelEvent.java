package org.drools.guvnor.client.explorer;

import com.google.gwt.user.client.Command;

public class RefreshModuleDataModelEvent {

    private final String moduleName;
    private Command callbackCommand = null;

    public RefreshModuleDataModelEvent(String moduleName,
                                       Command callbackCommand) {
        this.moduleName = moduleName;
        this.callbackCommand = callbackCommand;
    }

    public String getModuleName() {
        return moduleName;
    }

    public Command getCallbackCommand() {
        return callbackCommand;
    }

}
