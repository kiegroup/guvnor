package org.drools.brms.client.qa;

import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * The parent host widget for all QA schtuff.
 * QA for the purposes of this is testing and analysis tools.
 * @author Michael Neale
 */
public class QAManagerWidget extends Composite {


	public QAManagerWidget() {
		TabPanel tab = new TabPanel();
        tab.setWidth("100%");
        tab.setHeight("30%");

        tab.add( new ScenarioWidget(),  "<img src='images/test_manager.gif'/>Test", true);
        tab.add(new Label("TODO"), "<img src='images/analyze.gif'/>Analyze", true);
        tab.selectTab( 0 );

        initWidget(tab);
	}


}
