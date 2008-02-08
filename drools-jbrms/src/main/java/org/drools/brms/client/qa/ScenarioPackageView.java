package org.drools.brms.client.qa;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.common.PrettyFormLayout;
import org.drools.brms.client.explorer.ExplorerViewCenterPanel;
import org.drools.brms.client.rpc.BulkTestRunResult;
import org.drools.brms.client.rpc.RepositoryServiceFactory;
import org.drools.brms.client.rulelist.AssetItemGrid;
import org.drools.brms.client.rulelist.AssetItemGridDataLoader;
import org.drools.brms.client.rulelist.EditItemEvent;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * This shows a list of scenarios in a package.
 * And allows them to be run in bulk.
 * @author Michael Neale
 */
public class ScenarioPackageView extends Composite {


	private EditItemEvent editEvent;

	private VerticalPanel layout;

	private AssetItemGrid grid;

	public ScenarioPackageView(final String packageUUID, String packageName, EditItemEvent editEvent, ExplorerViewCenterPanel centerPanel) {
		this.editEvent = editEvent;

		grid = new AssetItemGrid(editEvent, AssetItemGrid.RULE_LIST_TABLE_ID, new AssetItemGridDataLoader() {
			public void loadData(int startRow, int numberOfRows,
					GenericCallback cb) {
				RepositoryServiceFactory.getService().listAssets(packageUUID, new String[] {AssetFormats.TEST_SCENARIO},
						startRow, numberOfRows, cb);
			}
		});

		layout = new VerticalPanel();
		layout.setWidth("100%");
		PrettyFormLayout pf = new PrettyFormLayout();

		VerticalPanel vert = new VerticalPanel();
		vert.add(new HTML("<b>Scenarios for package: </b>" + packageName));
		Button run = new Button("Run all scenarios");
		run.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				runAllScenarios(packageUUID);
			}
		});

		vert.add(run);


		pf.addHeader("images/scenario_large.png", vert);

		layout.add(pf);
		layout.add(grid);

		initWidget(layout);




	}

	private void refreshShowGrid() {
		layout.remove(1);
		layout.add(grid);
	}


	/**
	 * Run all the scenarios, obviously !
	 */
	private void runAllScenarios(String uuid) {
		LoadingPopup.showMessage("Building and running scenarios... ");
		RepositoryServiceFactory.getService().runScenariosInPackage(uuid, new GenericCallback() {
			public void onSuccess(Object data) {
				BulkTestRunResult d = (BulkTestRunResult) data;
				BulkRunResultWidget w = new BulkRunResultWidget(d, editEvent, new Command() {
					public void execute() {
						refreshShowGrid();
					}

				});
				layout.remove(1);
				layout.add(w);
				LoadingPopup.close();
			}
		});
	}

}
