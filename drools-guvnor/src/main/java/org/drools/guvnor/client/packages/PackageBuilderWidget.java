package org.drools.guvnor.client.packages;

/*
 * Copyright 2005 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.drools.guvnor.client.common.FormStyleLayout;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.InfoPopup;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.PackageConfigData;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.rulelist.EditItemEvent;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.core.EventObject;
import com.gwtext.client.data.ArrayReader;
import com.gwtext.client.data.FieldDef;
import com.gwtext.client.data.MemoryProxy;
import com.gwtext.client.data.Record;
import com.gwtext.client.data.RecordDef;
import com.gwtext.client.data.Store;
import com.gwtext.client.data.StringFieldDef;
import com.gwtext.client.widgets.grid.CellMetadata;
import com.gwtext.client.widgets.grid.ColumnConfig;
import com.gwtext.client.widgets.grid.ColumnModel;
import com.gwtext.client.widgets.grid.GridPanel;
import com.gwtext.client.widgets.grid.Renderer;
import com.gwtext.client.widgets.grid.event.GridRowListenerAdapter;
import com.gwtext.client.util.Format;

/**
 * This is the widget for building packages, validating etc. Visually decorates
 * or wraps a rule editor widget with buttons for this purpose.
 *
 * @author Michael Neale
 */
public class PackageBuilderWidget extends Composite {

	public FormStyleLayout layout;
	private PackageConfigData conf;
	private EditItemEvent editEvent;
    private static Constants constants;

    public PackageBuilderWidget(final PackageConfigData conf,
			EditItemEvent editEvent) {
		layout = new FormStyleLayout();
		this.conf = conf;
		this.editEvent = editEvent;

		final SimplePanel buildResults = new SimplePanel();


        final TextBox selector = new TextBox();
        constants = ((Constants) GWT.create(Constants.class));
        final Button b = new Button(constants.BuildPackage());
		b.setTitle(constants.ThisWillValidateAndCompileAllTheAssetsInAPackage());
		b.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				doBuild(buildResults, selector.getText());
			}
		});


        HorizontalPanel buildStuff = new HorizontalPanel();
        buildStuff.add( b );
        buildStuff.add( new HTML("&nbsp;&nbsp;<i>" + constants.OptionalSelectorName() + ": </i>") ); //NON-NLS
        buildStuff.add( selector );
        buildStuff.add( new InfoPopup(constants.CustomSelector(), constants.SelectorTip()) );

		layout.addAttribute(constants.BuildBinaryPackage(), buildStuff);
		layout
				.addRow(new HTML("<i><small>" + constants.BuildingPackageNote() + "</small></i>"));//NON-NLS
		layout.addRow(buildResults);

		Button snap = new Button(constants.CreateSnapshotForDeployment());
		snap.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				showSnapshotDialog(conf.name);
			}
		});
		layout.addAttribute(constants.TakeSnapshot(), snap);


		//layout.setStyleName("package-Editor");

		layout.setWidth("100%");

		initWidget(layout);
	}

	/**
	 * Actually build the source, and display it.
	 */
	public static void doBuildSource(final String uuid, final String name) {
		LoadingPopup.showMessage(((Constants) GWT.create(Constants.class)).AssemblingPackageSource());
		DeferredCommand.addCommand(new Command() {
			public void execute() {
				RepositoryServiceFactory.getService().buildPackageSource(uuid,
						new GenericCallback() {
							public void onSuccess(Object data) {
								String content = (String) data;
								showSource(content, name);
							}
						});
			}
		});
	}

	/**
	 * Popup the view source dialog, showing the given content.
	 */
	public static void showSource(final String content, String name) {
        Constants constants = GWT.create(Constants.class);
        final FormStylePopup pop = new FormStylePopup("images/view_source.gif", //NON-NLS
                Format.format(constants.ViewingSourceFor0(), name), new Integer(600), Boolean.FALSE);
		final TextArea area = new TextArea();
		area.setVisibleLines(30);
		area.setWidth("100%");
		area.setCharacterWidth(80);
		pop.addRow(area);
		area.setText(content);
		area.setEnabled(true);
		area.setTitle(constants.ReadOnlySourceNote());

		area.addKeyboardListener(new KeyboardListener() {

			public void onKeyDown(Widget arg0, char arg1, int arg2) {
				area.setText(content);
			}

			public void onKeyPress(Widget arg0, char arg1, int arg2) {
				area.setText(content);
			}

			public void onKeyUp(Widget arg0, char arg1, int arg2) {
				area.setText(content);
			}

		});


		LoadingPopup.close();

		pop.show();

	}

	/**
	 * Actually do the building.
	 *
	 * @param buildResults
	 *            The panel to stuff the results in.
	 * @param selectorName
	 */
	private void doBuild(final Panel buildResults, final String selectorName) {

		buildResults.clear();

		final HorizontalPanel busy = new HorizontalPanel();
		busy.add(new Label(constants.ValidatingAndBuildingPackagePleaseWait()));
		busy.add(new Image("images/red_anime.gif")); //NON-NLS

        //LoadingPopup.showMessage(constants.PleaseWaitDotDotDot());
		buildResults.add(busy);

		DeferredCommand.addCommand(new Command() {
			public void execute() {
				RepositoryServiceFactory.getService().buildPackage(conf.uuid, selectorName, true,
						new GenericCallback<BuilderResult[]>() {
							public void onSuccess(BuilderResult[] result) {
                                LoadingPopup.close();
								if (result == null) {
									showSuccessfulBuild(buildResults);
								} else {
									showBuilderErrors(result, buildResults, editEvent);
								}
							}

							public void onFailure(Throwable t) {
								buildResults.clear();
								super.onFailure(t);
							}
						});
			}
		});

	}

	/**
	 * This is called to display the success (and a download option).
	 *
	 * @param buildResults
	 */
	private void showSuccessfulBuild(Panel buildResults) {
		buildResults.clear();
		VerticalPanel vert = new VerticalPanel();

		vert.add(new HTML(
						"<img src='images/tick_green.gif'/><i>" + constants.PackageBuiltSuccessfully() + "</i>"));
		final String hyp = getDownloadLink(this.conf);

		HTML html = new HTML("<a href='" + hyp
				+ "' target='_blank'>" + constants.DownloadBinaryPackage() + "</a>");

		vert.add(html);

		buildResults.add(vert);
	}

	/**
	 * Get a download link for the binary package.
	 */
	public static String getDownloadLink(PackageConfigData conf) {
		String hurl = GWT.getModuleBaseURL() + "package/" + conf.name;    //NON-NLS
		if (!conf.isSnapshot) {
			hurl = hurl + "/" + SnapshotView.LATEST_SNAPSHOT;
		} else {
			hurl = hurl + "/" + conf.snapshotName;
		}
		final String uri = hurl;
		return uri;
	}

	/**
	 * This is called in the unhappy event of there being errors.
	 */
	public static void showBuilderErrors(BuilderResult[] results, Panel buildResults, final EditItemEvent editEvent) {
		buildResults.clear();



		Object[][] data = new Object[results.length][4];
		for (int i = 0; i < results.length; i++) {
			BuilderResult res = results[i];
			data[i][0] = res.uuid;
			data[i][1] = res.assetName;
			data[i][2] = res.assetFormat;
			data[i][3] = res.message;
		}


		MemoryProxy proxy = new MemoryProxy(data);
		RecordDef recordDef = new RecordDef(
				new FieldDef[]{
						new StringFieldDef("uuid"),             //NON-NLS
						new StringFieldDef("assetName"),        //NON-NLS
						new StringFieldDef("assetFormat"),      //NON-NLS
						new StringFieldDef("message")           //NON-NLS
				}
		);

		ArrayReader reader = new ArrayReader(recordDef);
		Store store = new Store(proxy, reader);
		store.load();


		ColumnModel cm  = new ColumnModel(new ColumnConfig[] {
			new ColumnConfig() {
				{
					setHidden(true);
					setDataIndex("uuid"); //NON-NLS
				}
			},
			new ColumnConfig() {
				{
					setHeader(constants.Name());
					setSortable(true);
					setDataIndex("assetName"); //NON-NLS
					setRenderer(new Renderer() {
						public String render(Object value,
								CellMetadata cellMetadata, Record record,
								int rowIndex, int colNum, Store store) {
							return "<img src='images/error.gif'/>" + value; //NON-NLS
						}

					});
				}
			},
			new ColumnConfig() {
				{
					setHeader(constants.Format());
					setSortable(true);
					setDataIndex("assetFormat"); //NON-NLS
				}
			},
			new ColumnConfig() {
				{
					setHeader(constants.Message1());
					setSortable(true);
					setDataIndex("message");              //NON-NLS
					setWidth(300);

				}
			}
		});


		//Grid g = new Grid(Ext.generateId(), "600px", "300px", store, cm);
		GridPanel g = new GridPanel(store, cm);
		g.setWidth(600);
		g.setHeight(300);


        g.addGridRowListener(new GridRowListenerAdapter() {
            public void onRowDblClick(GridPanel grid, int rowIndex, EventObject e) {
            	if (!grid.getSelectionModel().getSelected().getAsString("assetFormat").equals("Package")) { //NON-NLS
                    String uuid = grid.getSelectionModel().getSelected().getAsString("uuid");               //NON-NLS
                    editEvent.open(uuid);
            	}
            }
        });

		buildResults.add(g);



	}

	/**
	 * This will display a dialog for creating a snapshot.
	 */
	public static void showSnapshotDialog(final String packageName) {
		LoadingPopup.showMessage(constants.LoadingExistingSnapshots());
		final FormStylePopup form = new FormStylePopup("images/snapshot.png", //NON-NLS
                constants.CreateASnapshotForDeployment());
		form
				.addRow(new HTML(
                        constants.SnapshotDescription()));

		final VerticalPanel vert = new VerticalPanel();
		form.addAttribute(constants.ChooseOrCreateSnapshotName(), vert);
		final List radioList = new ArrayList();
		final TextBox newName = new TextBox();
		final String newSnapshotText = constants.NEW() + ": ";

		RepositoryServiceFactory.getService().listSnapshots(packageName,
				new GenericCallback<SnapshotInfo[]>() {
					public void onSuccess(SnapshotInfo[] result) {
						for (int i = 0; i < result.length; i++) {
							RadioButton existing = new RadioButton(
									"snapshotNameGroup", result[i].name); //NON-NLS
							radioList.add(existing);
							vert.add(existing);
						}
						HorizontalPanel newSnap = new HorizontalPanel();

						final RadioButton newSnapRadio = new RadioButton(
								"snapshotNameGroup", newSnapshotText); //NON-NLS
						newSnap.add(newSnapRadio);
						newName.setEnabled(false);
						newSnapRadio.addClickListener(new ClickListener() {

							public void onClick(Widget w) {
								newName.setEnabled(true);
							}

						});

						newSnap.add(newName);
						radioList.add(newSnapRadio);
						vert.add(newSnap);

						LoadingPopup.close();
					}
				});

		final TextBox comment = new TextBox();
		form.addAttribute(constants.Comment(), comment);

		Button create = new Button(constants.CreateNewSnapshot());
		form.addAttribute("", create);

		create.addClickListener(new ClickListener() {
			String name = "";

			public void onClick(Widget w) {
				boolean replace = false;
				for (Iterator iter = radioList.iterator(); iter.hasNext();) {
					RadioButton but = (RadioButton) iter.next();
					if (but.isChecked()) {
						name = but.getText();
						if (!but.getText().equals(newSnapshotText)) {
							replace = true;
						}
						break;
					}
				}
				if (name.equals(newSnapshotText)) {
					name = newName.getText();
				}

				if (name.equals("")) {
					Window
							.alert(constants.YouHaveToEnterOrChoseALabelNameForTheSnapshot());
					return;
				}

                LoadingPopup.showMessage(constants.PleaseWaitDotDotDot());                
				RepositoryServiceFactory.getService().createPackageSnapshot(
						packageName, name, replace, comment.getText(),
						new GenericCallback() {
							public void onSuccess(Object data) {
                                Window.alert(Format.format(constants.TheSnapshotCalled0WasSuccessfullyCreated(), name));
								form.hide();
                                LoadingPopup.close();
							}
						});
			}
		});
		form.show();

	}

}