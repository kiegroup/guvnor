package org.drools.guvnor.client.explorer.navigation.processes;

import org.drools.guvnor.client.explorer.navigation.reporting.ReportingHeaderView;
import org.drools.guvnor.client.explorer.navigation.reporting.ReportingTreeView;
import org.drools.guvnor.client.explorer.navigation.settings.SettingsHeaderView;
import org.drools.guvnor.client.explorer.navigation.settings.SettingsTreeView;
import org.drools.guvnor.client.explorer.navigation.tasks.TasksHeaderView;
import org.drools.guvnor.client.explorer.navigation.tasks.TasksTreeView;

public interface ProcessNavigationViewFactory {

    ProcessesHeaderView getProcessesHeaderView();

    ProcessesTreeView getProcessesTreeView();

    ReportingHeaderView getReportingHeaderView();

    ReportingTreeView getReportingTreeView();

    TasksHeaderView getTasksHeaderView();

    TasksTreeView getTasksTreeView();

    SettingsHeaderView getSettingsHeaderView();

    SettingsTreeView getSettingsTreeView();

}
