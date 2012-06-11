/**
 *
 */
package org.jboss.bpm.console.client.history;

import java.util.Date;
import java.util.List;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.MenuBar;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.datepicker.client.DateBox;
import com.mvc4g.client.Controller;
import com.mvc4g.client.Event;
import com.mvc4g.client.ViewInterface;
import org.jboss.bpm.console.client.BpmConsoleClientFactory;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;

/**
 * @author Jeff Yu
 * @date: Mar 02, 2011
 */
public class ProcessHistorySearchView implements IsWidget, ViewInterface {

    public static final String ID = ProcessHistorySearchView.class.getName();

    private final VerticalPanel panel = new VerticalPanel();

    private Controller controller;

    private ListBox processStatusList;

    private ListBox definitionList;

    private TextBox correlationKey;

    private DateBox startTime;

    private DateBox endTime;

    public ProcessHistorySearchView(BpmConsoleClientFactory clientFactory) {

        this.controller = clientFactory.getController();

        this.controller.addView(ID, this);
        this.controller.addAction(LoadProcessHistoryAction.ID, new LoadProcessHistoryAction(clientFactory.getApplicationContext()));
        this.controller.addAction(LoadProcessDefinitionsAction.ID, new LoadProcessDefinitionsAction(clientFactory.getApplicationContext()));
    }

    public Widget asWidget() {

        controller.handleEvent(new Event(LoadProcessDefinitionsAction.ID, null));
        return panel;
    }

    public Widget initialize(final List<ProcessDefinitionRef> processDefinitions) {

        panel.setSpacing(5);

        final MenuBar toolbar = new MenuBar();
        panel.add(toolbar);

        toolbar.addItem(
                "Search",
                new Command() {

                    public void execute() {

                        if (definitionList.getItemCount() < 1) {
                            return;
                        }

                        String proDef = definitionList.getValue(definitionList.getSelectedIndex());

                        String definitionId = null;

                        for (ProcessDefinitionRef ref : processDefinitions) {
                            if (proDef.equals(ref.getName())) {
                                definitionId = ref.getId();
                            }
                        }

                        String theStatus = processStatusList.getValue(processStatusList.getSelectedIndex());
                        Date theDate = startTime.getValue();
                        if (theDate == null) {
                            theDate = new Date(103, 1, 1);
                        }
                        Date edate = endTime.getValue();
                        if (edate == null) {
                            edate = new Date();
                        }
                        String ckey = correlationKey.getValue();

                        ProcessSearchEvent event = new ProcessSearchEvent();
                        event.setDefinitionKey(definitionId);
                        event.setStatus(theStatus);
                        event.setStartTime(theDate.getTime());
                        event.setEndTime(edate.getTime());
                        event.setKey(ckey);

                        controller.handleEvent(new Event(LoadProcessHistoryAction.ID, event));
                    }

                });

        final VerticalPanel formPanel = new VerticalPanel();
        panel.add(formPanel);

        final VerticalPanel processDefBox = new VerticalPanel();
        processDefBox.add(new Label("Process Definition: "));

        definitionList = new ListBox();
        for (ProcessDefinitionRef ref : processDefinitions) {
            definitionList.addItem(ref.getName());
        }
        processDefBox.add(definitionList);

        formPanel.add(processDefBox);
        formPanel.add(createProcessStatusListBox());
        formPanel.add(createCorrelationKeyTextBox());
        formPanel.add(createStartTimeDateBox());
        formPanel.add(createEndTimeDateBox());

        //TODO: Dead code? -Rikkola-
//        ProcessHistoryInstanceListView listview = new ProcessHistoryInstanceListView();
//        final TabPanel tabPanel = new TabPanel();
//        listview.provideWidget(new ProvisioningCallback() {
//
//            @Override
//            public void onSuccess(Widget instance) {
//                tabPanel.add(instance, "History Instances");
//            }
//
//            @Override
//            public void onUnavailable() {
//
//            }
//
//        });

//        panel.add(tabPanel);

        return panel;
    }

    private VerticalPanel createEndTimeDateBox() {
        VerticalPanel box4 = new VerticalPanel();
        endTime = new DateBox();
        endTime.setWidth("550px");
        box4.add(new Label("End Time: "));
        box4.add(endTime);
        return box4;
    }

    private VerticalPanel createStartTimeDateBox() {
        VerticalPanel box3 = new VerticalPanel();
        startTime = new DateBox();
        startTime.setWidth("550px");
        box3.add(new Label("Start Time: "));
        box3.add(startTime);
        return box3;
    }

    private VerticalPanel createCorrelationKeyTextBox() {
        VerticalPanel box2 = new VerticalPanel();
        correlationKey = new TextBox();
        correlationKey.setWidth("550px");
        box2.add(new Label("Correlation Key: "));
        box2.add(correlationKey);
        box2.add(new Label(" format: correlation name = [correlation value], e.g Session=[1]"));
        return box2;
    }

    private VerticalPanel createProcessStatusListBox() {
        VerticalPanel box1 = new VerticalPanel();
        processStatusList = new ListBox();
        processStatusList.addItem("COMPLETED");
        processStatusList.addItem("FAILED");
        processStatusList.addItem("TERMINATED");
        box1.add(new Label("Process Status: "));
        box1.add(processStatusList);
        return box1;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

}
