package org.drools.brms.client.qa;

import java.util.Collections;
import java.util.Map;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.common.DirtyableComposite;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.AnalysisReport;
import org.drools.brms.client.rpc.BulkTestRunResult;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.ruleeditor.EditorLauncher;
import org.drools.brms.client.ruleeditor.NewAssetWizard;
import org.drools.brms.client.rulelist.AssetItemListViewer;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * The parent host widget for all QA schtuff. QA for the purposes of this is
 * testing and analysis tools.
 *
 * @author Michael Neale
 */
public class QAManagerWidget extends Composite {

	private final TabPanel tab;
	private Map openedViewers = Collections.EMPTY_MAP;
	private EditItemEvent editEvent;
	private String currentlySelectedPackage;
	private AssetItemListViewer listView;
	private String currentUUID;


	public QAManagerWidget() {
		tab = new TabPanel();
		tab.setWidth("100%");
		tab.setHeight("30%");

		editEvent = new EditItemEvent() {
			public void open(String key) {
				EditorLauncher.showLoadEditor(openedViewers, tab, key, false);
			}
		};

		tab.add(getScenarioListView(),
				"<img src='images/test_manager.gif'/>Scenarios", true);
		tab.selectTab(0);

		initWidget(tab);
	}

	private Widget getScenarioListView() {

		VerticalPanel vert = new VerticalPanel();
		HorizontalPanel actions = new HorizontalPanel();

		final ListBox packageSelector = new ListBox();
		LoadingPopup.showMessage("Loading package list...");
		RepositoryServiceFactory.getService().listPackages(
				new GenericCallback() {

					public void onSuccess(Object o) {

						PackageConfigData[] list = (PackageConfigData[]) o;
						packageSelector
								.addItem("--- please choose package ---");
						for (int i = 0; i < list.length; i++) {
							packageSelector.addItem(list[i].name, list[i].uuid);
						}
						packageSelector.setSelectedIndex(0);
						LoadingPopup.close();
					}

				});
		actions.add(packageSelector);
		Button newTest = new Button("Create new scenario");
		newTest.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				launchWizard(AssetFormats.TEST_SCENARIO, "Create a new test scenario.");
			}
		});
		actions.add(newTest);


		Button runAll = new Button("Run all scenarios");
		runAll.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				runAll();
			}
		});

		Button analysePackage = new Button("Analyse package");
		analysePackage.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				runAnalysis();
			}
		});

		actions.add(runAll);
		actions.add(analysePackage);
		vert.add(actions);

		listView = new AssetItemListViewer(editEvent,
				AssetItemListViewer.RULE_LIST_TABLE_ID);
		vert.add(listView);

		packageSelector.addChangeListener(new ChangeListener() {

			public void onChange(Widget w) {
				if (packageSelector.getSelectedIndex() == 0)
					return;
				refreshList(listView, packageSelector.getValue(packageSelector
						.getSelectedIndex()));
				currentlySelectedPackage = packageSelector.getItemText(packageSelector.getSelectedIndex());
			}
		});

		return vert;
	}


	private void runAnalysis() {
		LoadingPopup.showMessage("Analysing package...");
		RepositoryServiceFactory.getService().analysePackage(this.currentUUID, new GenericCallback() {
			public void onSuccess(Object data) {
				AnalysisReport rep = (AnalysisReport) data;
				AnalysisResultWidget w = new AnalysisResultWidget(currentlySelectedPackage, rep);
				tab.add(w, "<img src='images/package_build.gif'/>" + currentlySelectedPackage, true);
				tab.selectTab(tab.getWidgetIndex(w));
				LoadingPopup.close();
			}
		});

	}

	/**
	 * Run all the scenarios, obviously !
	 */
	private void runAll() {
		LoadingPopup.showMessage("Building and running scenarios... ");
		RepositoryServiceFactory.getService().runScenariosInPackage(currentUUID, new GenericCallback() {
			public void onSuccess(Object data) {
				BulkTestRunResult d = (BulkTestRunResult) data;
				BulkRunResultWidget w = new BulkRunResultWidget(d, editEvent);
				tab.add(w, "<img src='images/tick_green.gif'/>" + currentlySelectedPackage, true);
				tab.selectTab(tab.getWidgetIndex(w));
				LoadingPopup.close();
			}
		});
	}

	/**
	 *
	 */
	private void refreshList(final AssetItemListViewer listView,
			final String uuid) {

		if (uuid == "")
			return;
		this.currentUUID = uuid;

		LoadingPopup.showMessage("Loading list of scenarios.");
		final GenericCallback cb = new GenericCallback() {
			public void onSuccess(Object data) {
				final TableDataResult table = (TableDataResult) data;
				listView.loadTableData(table);
				listView.setWidth("100%");
				LoadingPopup.close();
			}
		};

		RepositoryServiceFactory.getService().listAssets(uuid,
				new String[] { AssetFormats.TEST_SCENARIO }, -1, -1, cb);
	}



	private void launchWizard(String format, String title) {
		NewAssetWizard pop = new NewAssetWizard(new EditItemEvent() {
			public void open(String key) {
				refreshList(listView, currentUUID);
				editEvent.open(key);
			}
		}, false, format, title, currentlySelectedPackage);


		pop.show();
	}

}
