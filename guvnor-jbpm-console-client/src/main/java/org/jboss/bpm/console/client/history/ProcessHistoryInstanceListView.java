/**
 *
 */
package org.jboss.bpm.console.client.history;

import java.util.List;

import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.mvc4g.client.Controller;
import com.mvc4g.client.Event;
import com.mvc4g.client.ViewInterface;
import org.jboss.bpm.console.client.ClientFactory;
import org.jboss.bpm.console.client.common.CustomizableListBox;
import org.jboss.bpm.console.client.common.DataDriven;
import org.jboss.bpm.console.client.common.LoadingOverlay;
import org.jboss.bpm.console.client.common.WidgetWindowPanel;
import org.jboss.bpm.console.client.model.HistoryProcessInstanceRef;
import org.jboss.bpm.console.client.model.StringRef;
import org.jboss.bpm.console.client.util.SimpleDateFormat;

/**
 * @author Jeff Yu
 * @date Mar 18, 2011
 */
public class ProcessHistoryInstanceListView implements ViewInterface, IsWidget, DataDriven {

    public static final String ID = ProcessHistoryInstanceListView.class.getName();

    private Controller controller;

    private VerticalPanel panel;

    private VerticalPanel instanceList;

    private CustomizableListBox<HistoryProcessInstanceRef> listbox;

    private SimpleDateFormat dateFormat = new SimpleDateFormat();

    private WidgetWindowPanel processEventsWindow;

    private CustomizableListBox<String> processEvents;

    private String selectedProcessInstanceId;
    private final ClientFactory clientFactory;

    public ProcessHistoryInstanceListView(ClientFactory clientFactory) {
        this.controller = clientFactory.getController();
        this.clientFactory = clientFactory;
    }

    public Widget asWidget() {

        controller.addView(ID, this);
        controller.addAction(LoadProcessInstanceEventsAction.ID, new LoadProcessInstanceEventsAction(clientFactory.getApplicationContext()));

        panel = new VerticalPanel();
        panel.setSpacing(5);

        instanceList = new VerticalPanel();
        instanceList.setSpacing(5);

        listbox = new CustomizableListBox<HistoryProcessInstanceRef>(
                new CustomizableListBox.ItemFormatter<HistoryProcessInstanceRef>() {

                    public String format(HistoryProcessInstanceRef historyProcessInstanceRef) {
                        String result = "";
                        result += historyProcessInstanceRef.getProcessInstanceId();

                        result += "";

                        result += historyProcessInstanceRef.getKey();

                        result += historyProcessInstanceRef.getState();

                        result += dateFormat.format(historyProcessInstanceRef.getStartTime());

                        result += dateFormat.format(historyProcessInstanceRef.getEndTime());

                        return result;
                    }
                }
        );

        listbox.setFirstLine("Instance Id, Correlation Key, Status, Start Time, Finish Time");

        listbox.addChangeHandler(new ChangeHandler() {
            public void onChange(ChangeEvent event) {
                int index = listbox.getSelectedIndex();
                if (index != -1) {
                    HistoryProcessInstanceRef historyInstance = listbox.getItem(index);
                    selectedProcessInstanceId = historyInstance.getProcessInstanceId();
                    createHistoryInstanceDetailWindow();
                    //controller.handleEvent(new Event(LoadProcessInstanceEventsAction.ID, selectedProcessInstanceId));
                }
            }

        });

        instanceList.add(listbox);

        panel.add(instanceList);

        return panel;
    }

    public void setController(Controller controller) {
        this.controller = controller;
    }

    public void reset() {

    }

    public void update(Object... data) {
        List<HistoryProcessInstanceRef> result = (List<HistoryProcessInstanceRef>) data[0];
        listbox.clear();

        for (HistoryProcessInstanceRef ref : result) {
            listbox.addItem(ref);
        }

        //TODO: Is this still needed? -Rikkola-
//        panel.invalidate();

    }

    public void setLoading(boolean isLoading) {
        LoadingOverlay.on(instanceList, isLoading);
    }

    public void createHistoryInstanceDetailWindow() {

        ScrollPanel layout = new ScrollPanel();
        layout.setStyleName("bpm-window-layout");

        Label header = new Label("Instance: " + selectedProcessInstanceId);
        header.setStyleName("bpm-label-header");
        layout.add(header);

        final TabPanel tabPanel = new TabPanel();

        processEvents = new CustomizableListBox<String>(
                new CustomizableListBox.ItemFormatter<String>() {
                    public String format(String item) {
                        return new HTML(item).getHTML();
                    }
                }
        );

        processEvents.setFirstLine("Process Events");

        VerticalPanel sourcePanel = new VerticalPanel();
        sourcePanel.add(processEvents);
        tabPanel.add(sourcePanel, "Activity Events");

        tabPanel.selectTab(0);

        layout.add(tabPanel);

        processEventsWindow = new WidgetWindowPanel("History Instance Activity", layout, true);

        controller.handleEvent(new Event(LoadProcessInstanceEventsAction.ID, selectedProcessInstanceId));

    }

    public void populateInstanceEvents(List<StringRef> refs) {
        processEvents.clear();
        for (StringRef value : refs) {
            processEvents.addItem(formatResult(value.getValue()));
        }
    }

    private String formatResult(String value) {
        StringBuffer sbuffer = new StringBuffer();

        String[] split = value.split("~");
        sbuffer.append(split[0] + " : ");

        for (int i = 1; i < value.length(); i++) {
            sbuffer.append("<br/>");
            sbuffer.append(split[i]);
        }

        return sbuffer.toString();
    }

}
