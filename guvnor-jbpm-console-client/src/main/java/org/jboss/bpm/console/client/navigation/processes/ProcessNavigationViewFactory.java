package org.jboss.bpm.console.client.navigation.processes;

import org.jboss.bpm.console.client.navigation.reporting.ReportingHeaderView;
import org.jboss.bpm.console.client.navigation.reporting.ReportingTreeView;
import org.jboss.bpm.console.client.navigation.settings.SettingsHeaderView;
import org.jboss.bpm.console.client.navigation.settings.SettingsTreeView;
import org.jboss.bpm.console.client.navigation.tasks.TasksHeaderView;
import org.jboss.bpm.console.client.navigation.tasks.TasksTreeView;

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
