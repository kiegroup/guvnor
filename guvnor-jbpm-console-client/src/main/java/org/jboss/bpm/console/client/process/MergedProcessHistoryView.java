package org.jboss.bpm.console.client.process;

import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.SplitLayoutPanel;
import com.google.gwt.user.client.ui.Widget;
import org.jboss.bpm.console.client.ClientFactory;

/**
 * @author Maciej Swiderski <swiderski.maciej@gmail.com>
 */
public class MergedProcessHistoryView implements IsWidget {

    DefinitionHistoryListView definitionView;
    HistoryInstanceListView instanceView;
    private final ClientFactory clientFactory;

    public MergedProcessHistoryView(ClientFactory clientFactory) {
        this.clientFactory = clientFactory;
    }

    public Widget asWidget() {

        SimplePanel panel = new SimplePanel();

        definitionView = new DefinitionHistoryListView();
        instanceView = new HistoryInstanceListView(clientFactory);

        final SplitLayoutPanel splitPanel = new SplitLayoutPanel();

        splitPanel.addWest(definitionView.asWidget(), 250);

        splitPanel.add(instanceView.asWidget());

        panel.add(splitPanel);

        return panel;
    }
}
