/*
 * Copyright 2011 JBoss Inc
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package org.drools.guvnor.client.moduleeditor.drools;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;

import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.BuilderResult;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.widgets.categorynav.CategoryExplorerWidget;
import org.drools.guvnor.client.widgets.categorynav.CategorySelectHandler;
import org.drools.guvnor.client.widgets.drools.tables.BuildPackageErrorsSimpleTable;

import java.util.ArrayList;
import java.util.List;

/**
 * This is the widget for building packages, validating etc. Visually decorates
 * or wraps a rule editor widget with buttons for this purpose.
 */
public class PackageBuilderWidget extends Composite {

    private static Constants constants = GWT.create(Constants.class);
    private static Images images = GWT.create(Images.class);

    private Module conf;

    private final FormStyleLayout buildWholePackageLayout = new FormStyleLayout();
    private final FormStyleLayout builtInSelectorLayout = new FormStyleLayout();
    private final FormStyleLayout customSelectorLayout = new FormStyleLayout();
    private String buildMode = "buildWholePackage";
    private final ClientFactory clientFactory;

    public PackageBuilderWidget(final Module conf,
                                ClientFactory clientFactory) {

        this.conf = conf;
        this.clientFactory = clientFactory;

        // UI above the results table
        FormStyleLayout layout = new FormStyleLayout();
        final VerticalPanel container = new VerticalPanel();
        final VerticalPanel buildResults = new VerticalPanel();

        RadioButton wholePackageRadioButton = new RadioButton("action",
                constants.BuildWholePackage());
        RadioButton builtInSelectorRadioButton = new RadioButton("action",
                constants.BuildPackageUsingBuiltInSelector());
        RadioButton customSelectorRadioButton = new RadioButton("action",
                constants.BuildPackageUsingCustomSelector());
        wholePackageRadioButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                buildWholePackageLayout.setVisible(true);
                builtInSelectorLayout.setVisible(false);
                customSelectorLayout.setVisible(false);
                buildMode = "buildWholePackage";
            }
        });
        builtInSelectorRadioButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                buildWholePackageLayout.setVisible(false);
                builtInSelectorLayout.setVisible(true);
                customSelectorLayout.setVisible(false);
                buildMode = "BuiltInSelector";
            }
        });
        customSelectorRadioButton.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                buildWholePackageLayout.setVisible(false);
                builtInSelectorLayout.setVisible(false);
                customSelectorLayout.setVisible(true);
                buildMode = "customSelector";
            }
        });

        VerticalPanel verticalPanel = new VerticalPanel();

        HorizontalPanel wholePackageRadioButtonPanel = new HorizontalPanel();
        wholePackageRadioButtonPanel.add(wholePackageRadioButton);
        wholePackageRadioButtonPanel.add(new InfoPopup(constants.BuildWholePackage(),
                constants.BuildWholePackageTip()));
        verticalPanel.add(wholePackageRadioButtonPanel);

        HorizontalPanel builtInSelectorRadioButtonPanel = new HorizontalPanel();
        builtInSelectorRadioButtonPanel.add(builtInSelectorRadioButton);
        builtInSelectorRadioButtonPanel.add(new InfoPopup(constants.BuiltInSelector(),
                constants.BuiltInSelectorTip()));
        verticalPanel.add(builtInSelectorRadioButtonPanel);

        HorizontalPanel customSelectorRadioButtonPanel = new HorizontalPanel();
        customSelectorRadioButtonPanel.add(customSelectorRadioButton);
        customSelectorRadioButtonPanel.add(new InfoPopup(constants.CustomSelector(),
                constants.SelectorTip()));
        verticalPanel.add(customSelectorRadioButtonPanel);

        layout.addAttribute("",
                verticalPanel);
        wholePackageRadioButton.setValue(true);

        buildWholePackageLayout.setVisible(true);
        builtInSelectorLayout.setVisible(false);
        customSelectorLayout.setVisible(false);

        // Build whole package layout
        layout.addRow(buildWholePackageLayout);

        // Built-in selector layout
        builtInSelectorLayout.addRow(new HTML("&nbsp;&nbsp;<i>"
                + constants.BuildPackageUsingFollowingAssets()
                + "</i>"));

        HorizontalPanel builtInSelectorStatusPanel = new HorizontalPanel();
        final CheckBox enableStatusCheckBox = new CheckBox();
        enableStatusCheckBox.setValue(false);
        builtInSelectorStatusPanel.add(enableStatusCheckBox);
        builtInSelectorStatusPanel.add(new HTML("&nbsp;&nbsp;<i>"
                + constants.BuildPackageUsingBuiltInSelectorStatus()
                + " </i>"));
        final ListBox statusOperator = new ListBox();
        String[] vals = new String[]{"=", "!="};
        for (int i = 0; i < vals.length; i++) {
            statusOperator.addItem(vals[i],
                    vals[i]);
        }
        builtInSelectorStatusPanel.add(statusOperator);

        final TextBox statusValue = new TextBox();
        statusValue.setTitle(constants.WildCardsSearchTip());
        builtInSelectorStatusPanel.add(statusValue);

        builtInSelectorLayout.addRow(builtInSelectorStatusPanel);

        HorizontalPanel builtInSelectorCatPanel = new HorizontalPanel();
        final CheckBox enableCategoryCheckBox = new CheckBox();
        enableCategoryCheckBox.setValue(false);
        builtInSelectorCatPanel.add(enableCategoryCheckBox);
        builtInSelectorCatPanel.add(new HTML("&nbsp;&nbsp;<i>"
                + constants.BuildPackageUsingBuiltInSelectorCat()
                + " </i>"));
        final ListBox catOperator = new ListBox();
        String[] catVals = new String[]{"=", "!="};
        for (int i = 0; i < catVals.length; i++) {
            catOperator.addItem(catVals[i],
                    catVals[i]);
        }
        builtInSelectorCatPanel.add(catOperator);
        final CategoryExplorerWidget catChooser = new CategoryExplorerWidget(new CategorySelectHandler() {
            public void selected(String selectedPath) {
            }
        });
        ScrollPanel catScroll = new ScrollPanel(catChooser);
        catScroll.setAlwaysShowScrollBars(true);
        catScroll.setSize("300px",
                "130px");

        builtInSelectorCatPanel.add(catScroll);
        builtInSelectorLayout.addRow(builtInSelectorCatPanel);

        layout.addRow(builtInSelectorLayout);

        // Custom selector layout
        customSelectorLayout.setVisible(false);
        HorizontalPanel customSelectorPanel = new HorizontalPanel();
        customSelectorPanel.add(new HTML("&nbsp;&nbsp;<i>"
                + constants.BuildPackageUsingCustomSelectorSelector()
                + " </i>")); // NON-NLS

        final ListBox customSelector = new ListBox();
        customSelector.setTitle(constants.WildCardsSearchTip());
        customSelectorPanel.add(customSelector);
        loadCustomSelectorList(customSelector);

        customSelectorLayout.addRow(customSelectorPanel);
        layout.addRow(customSelectorLayout);

        final Button b = new Button(constants.BuildPackage());
        b.setTitle(constants.ThisWillValidateAndCompileAllTheAssetsInAPackage());
        b.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                doBuild(buildResults,
                        statusOperator.getValue(statusOperator.getSelectedIndex()),
                        statusValue.getText(),
                        enableStatusCheckBox.getValue(),
                        catOperator.getValue(catOperator.getSelectedIndex()),
                        catChooser.getSelectedPath(),
                        enableCategoryCheckBox.getValue(),
                        customSelector.getSelectedIndex() != -1 ? customSelector.getValue(customSelector.getSelectedIndex()) : null);
            }
        });
        HorizontalPanel buildStuff = new HorizontalPanel();
        buildStuff.add(b);

        layout.addAttribute(constants.BuildBinaryPackage(),
                buildStuff);
        layout.addRow(new HTML("<i><small>"
                + constants.BuildingPackageNote()
                + "</small></i>"));// NON-NLS
        container.add(layout);

        // The build results
        container.add(buildResults);

        // UI below the results table
        layout = new FormStyleLayout();
        Button snap = new Button(constants.CreateSnapshotForDeployment());
        snap.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                showSnapshotDialog(conf.getName(),
                        null);
            }
        });
        layout.addAttribute(constants.TakeSnapshot(),
                snap);
        container.add(layout);

        initWidget(container);
    }

    private void loadCustomSelectorList(final ListBox customSelector) {
        RepositoryServiceFactory.getService().getCustomSelectors(new GenericCallback<String[]>() {

            public void onSuccess(String[] list) {
                for (int i = 0; i < list.length; i++) {
                    customSelector.addItem(list[i],
                            list[i]);
                }
            }
        });
    }

    private void doBuild(final Panel buildResults,
                         final String statusOperator,
                         final String statusValue,
                         final boolean enableStatusSelector,
                         final String categoryOperator,
                         final String category,
                         final boolean enableCategorySelector,
                         final String customSelector) {
        buildResults.clear();

        final HorizontalPanel busy = new HorizontalPanel();
        busy.add(new Label(constants.ValidatingAndBuildingPackagePleaseWait()));
        busy.add(new Image(images.redAnime()));

        buildResults.add(busy);

        Scheduler scheduler = Scheduler.get();
        scheduler.scheduleDeferred(new Command() {
            public void execute() {
                RepositoryServiceFactory.getPackageService().buildPackage(conf.getUuid(),
                        true,
                        buildMode,
                        statusOperator,
                        statusValue,
                        enableStatusSelector,
                        categoryOperator,
                        category,
                        enableCategorySelector,
                        customSelector,
                        new GenericCallback<BuilderResult>() {
                            public void onSuccess(BuilderResult result) {
                                LoadingPopup.close();
                                if (result == null || !result.hasLines()) {
                                    showSuccessfulBuild(buildResults);
                                } else {
                                    showBuilderErrors(result,
                                            buildResults,
                                            clientFactory);
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
     * Actually build the source, and display it.
     */
    public static void doBuildSource(final String uuid,
                                     final String name) {
        LoadingPopup.showMessage(constants.AssemblingPackageSource());

        Scheduler scheduler = Scheduler.get();
        scheduler.scheduleDeferred(new Command() {
            public void execute() {
                RepositoryServiceFactory.getPackageService().buildModuleSource(uuid,
                        new GenericCallback<java.lang.String>() {
                            public void onSuccess(String content) {
                                showSource(content,
                                        name);
                            }
                        });
            }
        });
    }

    /**
     * Popup the view source dialog, showing the given content.
     */
    public static void showSource(final String content,
                                  String name) {
        Constants constants = GWT.create( Constants.class );
        int windowWidth = Window.getClientWidth() / 2;
        int windowHeight = Window.getClientHeight() / 2;
        final FormStylePopup pop = new FormStylePopup( images.viewSource(),
                                                       constants.ViewingSourceFor0( name ),
                                                       windowWidth );

        String[] rows = content.split( "\n" );

        FlexTable table = new FlexTable();
        for ( int i = 0; i < rows.length; i++ ) {

            table.setHTML( i,
                           0,
                           "<span style='color:grey;'>"
                                   + (i + 1)
                                   + ".</span>" );
            table.setHTML( i,
                           1,
                           "<span style='color:green;' >|</span>" );
            table.setHTML( i,
                           2,
                           addSyntaxHilights( rows[i] ) );
        }

        ScrollPanel scrollPanel = new ScrollPanel( table );

        scrollPanel.setHeight( windowHeight + "px" );
        scrollPanel.setWidth( windowWidth + "px" );

        pop.addRow( scrollPanel );

        LoadingPopup.close();

        pop.show();

    }

    private static String addSyntaxHilights(String text) {

        if (text.trim().startsWith("#")) {
            text = "<span style='color:green'>"
                    + text
                    + "</span>";
        } else {

            String[] keywords = {"rule", "when", "then", "end", "accumulate", "collect", "from", "null", "over", "lock-on-active", "date-effective", "date-expires", "no-loop", "auto-focus", "activation-group", "agenda-group", "ruleflow-group",
                    "entry-point", "duration", "package", "import", "dialect", "salience", "enabled", "attributes", "extend", "template", "query", "declare", "function", "global", "eval", "exists", "forall", "action", "reverse", "result", "end",
                    "init"};

            for (String keyword : keywords) {
                if (text.contains(keyword)) {
                    text = text.replace(keyword,
                            "<span style='color:red;'>"
                                    + keyword
                                    + "</span>");
                }
            }

            text = handleStrings("\"",
                    text);
        }
        text = text.replace("\t",
                "&nbsp;&nbsp;&nbsp;&nbsp;");

        return text;
    }

    private static String handleStrings(String character,
                                        String text) {
        int stringStart = text.indexOf(character);
        while (stringStart >= 0) {
            int stringEnd = text.indexOf(character,
                    stringStart + 1);
            if (stringEnd < 0) {
                stringStart = -1;
                break;
            }

            String oldString = text.substring(stringStart,
                    stringEnd + 1);

            String newString = "<span style='color:green;'>"
                    + oldString
                    + "</span>";

            String beginning = text.substring(0,
                    stringStart);
            String end = text.substring(stringEnd + 1);

            text = beginning
                    + newString
                    + end;

            int searchStart = stringStart
                    + newString.length()
                    + 1;

            if (searchStart < text.length()) {
                stringStart = text.indexOf(character,
                        searchStart);
            } else {
                stringStart = -1;
            }
        }
        return text;
    }

    /**
     * This is called to display the success (and a download option).
     *
     * @param buildResults
     */
    private void showSuccessfulBuild(Panel buildResults) {
        buildResults.clear();
        VerticalPanel vert = new VerticalPanel();

        vert.add(new HTML(AbstractImagePrototype.create(images.greenTick()).getHTML()
                + "<i>"
                + constants.PackageBuiltSuccessfully()
                + " "
                + conf.getLastModified()
                + "</i>"));

        final String hyp = getDownloadLink(this.conf);

        HTML html = new HTML("<a href='"
                + hyp
                + "' target='_blank'>"
                + constants.DownloadBinaryPackage()
                + "</a>");

        vert.add(html);

        buildResults.add(vert);
    }

    /**
     * Get a download link for the binary package.
     */
    public static String getDownloadLink(Module conf) {
        String hurl = GWT.getModuleBaseURL()
                + "package/"
                + conf.getName(); // NON-NLS
        if (!conf.isSnapshot()) {
            hurl = hurl
                    + "/"
                    + SnapshotView.LATEST_SNAPSHOT;
        } else {
            hurl = hurl
                    + "/"
                    + conf.getSnapshotName();
        }
        final String uri = hurl;
        return uri;
    }

    /**
     * This is called in the unhappy event of there being errors.
     */
    public static void showBuilderErrors(BuilderResult results,
                                         Panel buildResults,
                                         ClientFactory clientFactory) {
        buildResults.clear();

        BuildPackageErrorsSimpleTable errorsTable = new BuildPackageErrorsSimpleTable(clientFactory);
        errorsTable.setRowData(results.getLines());
        errorsTable.setRowCount(results.getLines().size());
        buildResults.add(errorsTable);
    }

    /**
     * This will display a dialog for creating a snapshot.
     */
    public static void showSnapshotDialog(final String packageName,
                                          final Command refreshCmd) {
        LoadingPopup.showMessage(constants.LoadingExistingSnapshots());
        final FormStylePopup form = new FormStylePopup(images.snapshot(),
                constants.CreateASnapshotForDeployment());
        form.addRow(new HTML(constants.SnapshotDescription()));

        final VerticalPanel vert = new VerticalPanel();
        form.addAttribute(constants.ChooseOrCreateSnapshotName(),
                vert);
        final List<RadioButton> radioList = new ArrayList<RadioButton>();
        final TextBox newName = new TextBox();
        final String newSnapshotText = constants.NEW()
                + ": ";

        RepositoryServiceFactory.getPackageService().listSnapshots(packageName,
                new GenericCallback<SnapshotInfo[]>() {
                    public void onSuccess(SnapshotInfo[] result) {
                        for (int i = 0; i < result.length; i++) {
                            RadioButton existing = new RadioButton("snapshotNameGroup",
                                    result[i].getName()); // NON-NLS
                            radioList.add(existing);
                            vert.add(existing);
                        }
                        HorizontalPanel newSnap = new HorizontalPanel();

                        final RadioButton newSnapRadio = new RadioButton("snapshotNameGroup",
                                newSnapshotText); // NON-NLS
                        newSnap.add(newSnapRadio);
                        newName.setEnabled(false);
                        newSnapRadio.addClickHandler(new ClickHandler() {
                            public void onClick(ClickEvent event) {
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
        form.addAttribute(constants.Comment(),
                comment);

        Button create = new Button(constants.CreateNewSnapshot());
        form.addAttribute("",
                create);

        create.addClickHandler(new ClickHandler() {
            String name = "";

            public void onClick(ClickEvent event) {
                boolean replace = false;
                for (RadioButton but : radioList) {
                    if (but.getValue()) {
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
                    Window.alert(constants.YouHaveToEnterOrChoseALabelNameForTheSnapshot());
                    return;
                }

                LoadingPopup.showMessage(constants.PleaseWaitDotDotDot());
                RepositoryServiceFactory.getPackageService().createModuleSnapshot(packageName,
                        name,
                        replace,
                        comment.getText(),
                        new GenericCallback<java.lang.Void>() {
                            public void onSuccess(Void v) {
                                Window.alert(constants.TheSnapshotCalled0WasSuccessfullyCreated(name));
                                form.hide();
                                if (refreshCmd != null) {
                                    refreshCmd.execute();
                                }
                                LoadingPopup.close();
                            }
                        });
            }
        });
        form.show();

    }

}
