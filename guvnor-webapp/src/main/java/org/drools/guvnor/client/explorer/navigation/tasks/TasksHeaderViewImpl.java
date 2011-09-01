package org.drools.guvnor.client.explorer.navigation.tasks;

import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.common.StackItemHeaderViewImpl;
import org.drools.guvnor.client.messages.Constants;

public class TasksHeaderViewImpl extends StackItemHeaderViewImpl implements TasksHeaderView {

    private static Constants constants = GWT.create(Constants.class);

    public TasksHeaderViewImpl() {
        setText(constants.Tasks());
    }

}
