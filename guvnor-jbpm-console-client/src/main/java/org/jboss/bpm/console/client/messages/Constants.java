package org.jboss.bpm.console.client.messages;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.Messages;

public interface Constants
        extends
        Messages {

    Constants INSTANCE = GWT.create(Constants.class);

    String PersonalTasks();

    String GroupTasks();

    String ReportTemplates();

    String ExecutionHistory();

    String System();

    String ProcessOverview();

}
