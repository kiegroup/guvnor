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

import java.util.ArrayList;
import java.util.List;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.EventBus;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IsWidget;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.RadioButton;
import com.google.gwt.user.client.ui.ScrollPanel;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.GenericCallback;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.RulePackageSelector;
import org.drools.guvnor.client.explorer.AcceptItem;
import org.drools.guvnor.client.explorer.ClientFactory;
import org.drools.guvnor.client.explorer.navigation.ClosePlaceEvent;
import org.drools.guvnor.client.explorer.navigation.deployment.SnapshotPlace;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.moduleeditor.AssetViewerActivity;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.Module;
import org.drools.guvnor.client.rpc.ModuleServiceAsync;
import org.drools.guvnor.client.rpc.RepositoryServiceFactory;
import org.drools.guvnor.client.rpc.SnapshotInfo;
import org.drools.guvnor.client.widgets.drools.tables.SnapshotComparisonPagedTable;

/**
 * This is the new snapshot view.
 */
public class SnapshotView extends Composite {

    private static Constants constants = GWT.create(Constants.class);
    private static Images images = GWT.create(Images.class);

    public static final String LATEST_SNAPSHOT = "LATEST";

    private Module parentConf;
    private SnapshotInfo snapInfo;

    private ListBox box = new ListBox();

    private VerticalPanel vert;
    private SnapshotComparisonPagedTable table;

    private final ClientFactory clientFactory;
    private final EventBus eventBus;

    public SnapshotView(
            ClientFactory clientFactory,
            EventBus eventBus,
            SnapshotInfo snapInfo,
            Module parentPackage) {
        this.clientFactory = clientFactory;
        this.eventBus = eventBus;
        vert = new VerticalPanel();
        this.snapInfo = snapInfo;
        this.parentConf = parentPackage;
        PrettyFormLayout head = new PrettyFormLayout();

        head.addHeader(images.snapshot(),
                header());

        vert.add(head);
        
        AssetViewerActivity assetViewerActivity = new AssetViewerActivity(parentConf.uuid,
                clientFactory);
        assetViewerActivity.start(new AcceptItem() {
                    public void add(String tabTitle, IsWidget widget) {
                        ScrollPanel pnl = new ScrollPanel();
                        pnl.setWidth("100%");
                        pnl.add(widget);
                        vert.add(pnl);
                    }
                }, null);
        
        vert.setWidth("100%");
        initWidget(vert);

    }

    private Widget header() {
        FlexTable ft = new FlexTable();

        ft.setWidget(0,
                0,
                new Label(constants.ViewingSnapshot()));
        ft.setWidget(0,
                1,
                new HTML("<b>"
                        + this.snapInfo.getName()
                        + "</b>"));
        ft.getFlexCellFormatter().setHorizontalAlignment(0,
                0,
                HasHorizontalAlignment.ALIGN_RIGHT);

        ft.setWidget(1,
                0,
                new Label(constants.ForPackage()));
        ft.setWidget(1,
                1,
                new Label(this.parentConf.getName()));
        ft.getFlexCellFormatter().setHorizontalAlignment(1,
                0,
                HasHorizontalAlignment.ALIGN_RIGHT);

        HTML dLink = new HTML("<a href='"
                + PackageBuilderWidget.getDownloadLink(this.parentConf)
                + "' target='_blank'>"
                + constants.clickHereToDownloadBinaryOrCopyURLForDeploymentAgent()
                + "</a>");
        ft.setWidget(2,
                0,
                new Label(constants.DeploymentURL()));
        ft.setWidget(2,
                1,
                dLink);
        ft.getFlexCellFormatter().setHorizontalAlignment(2,
                0,
                HasHorizontalAlignment.ALIGN_RIGHT);

        ft.setWidget(3,
                0,
                new Label(constants.SnapshotCreatedOn()));
        ft.setWidget(3,
                1,
                new Label(DateTimeFormat.getFormat(DateTimeFormat.PredefinedFormat.DATE_TIME_SHORT).format(parentConf.getLastModified())));
        ft.getFlexCellFormatter().setHorizontalAlignment(4,
                0,
                HasHorizontalAlignment.ALIGN_RIGHT);

        ft.setWidget(4,
                0,
                new Label(constants.CommentColon()));
        ft.setWidget(4,
                1,
                new Label(parentConf.getCheckinComment()));
        ft.getFlexCellFormatter().setHorizontalAlignment(4,
                0,
                HasHorizontalAlignment.ALIGN_RIGHT);

        HorizontalPanel actions = new HorizontalPanel();

        actions.add(getDeleteButton(this.snapInfo.getName(),
                this.parentConf.getName()));
        actions.add(getCopyButton(this.snapInfo.getName(),
                this.parentConf.getName()));

        ft.setWidget(5,
                0,
                actions);

        ft.setWidget(6,
                0,
                getCompareWidget(this.parentConf.getName(),
                        this.snapInfo.getName()));
        ft.getFlexCellFormatter().setHorizontalAlignment(4,
                0,
                HasHorizontalAlignment.ALIGN_RIGHT);

        ft.getFlexCellFormatter().setColSpan(5,
                0,
                2);

        return ft;
    }

    private Widget getCompareWidget(final String packageName,
            final String snapshotName) {
        HorizontalPanel hPanel = new HorizontalPanel();
        hPanel.add(new Label("Compare to:"));

        RepositoryServiceFactory.getPackageService().listSnapshots(this.parentConf.getName(),
                new GenericCallback<SnapshotInfo[]>() {
                    public void onSuccess(SnapshotInfo[] info) {
                        for (int i = 0; i < info.length; i++) {
                            if (!snapshotName.equals(info[i].getName())) {
                                box.addItem(info[i].getName());
                            }
                        }
                    }
                });
        hPanel.add(box);

        Button button = new Button("Compare");
        button.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (table != null) {
                    vert.remove(table);
                }
                table = new SnapshotComparisonPagedTable(packageName,
                        snapshotName,
                        box.getItemText(box.getSelectedIndex()),
                        clientFactory);
                vert.add(table);
            }
        });

        hPanel.add(button);

        return hPanel;
    }

    private Button getDeleteButton(final String snapshotName,
            final String moduleName) {
        Button btn = new Button(constants.Delete());
        btn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                if (Window.confirm(constants.SnapshotDeleteConfirm(snapshotName, moduleName))) {
                    RepositoryServiceFactory.getPackageService().copyOrRemoveSnapshot(moduleName,
                            snapshotName,
                            true,
                            null,
                            new GenericCallback<java.lang.Void>() {
                                public void onSuccess(Void v) {
                                    Window.alert(constants.SnapshotWasDeleted());

                                    eventBus.fireEvent(getCloseEvent(moduleName));
                                }
                            });
                }
            }

        });
        return btn;
    }

    private ClosePlaceEvent getCloseEvent(String moduleName) {
        return new ClosePlaceEvent(new SnapshotPlace(moduleName, snapInfo.getName()));
    }

    private Button getCopyButton(final String snapshotName,
            final String packageName) {
        final ModuleServiceAsync serv = RepositoryServiceFactory.getPackageService();
        Button btn = new Button(constants.Copy());
        btn.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                serv.listSnapshots(packageName,
                        createGenericCallback(snapshotName,
                                packageName,
                                serv));
            }
        });
        return btn;
    }

    private GenericCallback<SnapshotInfo[]> createGenericCallback(final String snapshotName,
            final String packageName,
            final ModuleServiceAsync serv) {
        return new GenericCallback<SnapshotInfo[]>() {
            public void onSuccess(final SnapshotInfo[] snaps) {
                final FormStylePopup copy = new FormStylePopup(images.snapshot(),
                        constants.CopySnapshotText(snapshotName));
                final List<RadioButton> options = new ArrayList<RadioButton>();
                VerticalPanel vert = new VerticalPanel();
                for (int i = 0; i < snaps.length; i++) {
                    // cant copy onto to itself...
                    if (!snaps[i].getName().equals(snapshotName)) {
                        RadioButton existing = new RadioButton("snapshotNameGroup",
                                snaps[i].getName()); // NON-NLS
                        options.add(existing);
                        vert.add(existing);
                    }
                }

                HorizontalPanel newNameHorizontalPanel = new HorizontalPanel();
                final TextBox newNameTextBox = new TextBox();
                final String newNameText = constants.NEW()
                        + ": ";

                final RadioButton newNameRadioButton = new RadioButton("snapshotNameGroup",
                        newNameText);
                newNameHorizontalPanel.add(newNameRadioButton);
                newNameTextBox.setEnabled(false);
                newNameRadioButton.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        newNameTextBox.setEnabled(true);
                    }
                });

                newNameHorizontalPanel.add(newNameTextBox);
                options.add(newNameRadioButton);
                vert.add(newNameHorizontalPanel);

                copy.addAttribute(constants.ExistingSnapshots(),
                        vert);

                Button ok = new Button(constants.OK());
                copy.addAttribute("",
                        ok);
                ok.addClickHandler(new ClickHandler() {
                    public void onClick(ClickEvent event) {
                        if (!isOneButtonSelected(options)) {
                            Window.alert(constants.YouHaveToEnterOrChoseALabelNameForTheSnapshot());
                            return;
                        }

                        if (newNameRadioButton.getValue()) {
                            if (checkUnique(snaps,
                                    newNameTextBox.getText())) {
                                serv.copyOrRemoveSnapshot(packageName,
                                        snapshotName,
                                        false,
                                        newNameTextBox.getText(),
                                        new GenericCallback<java.lang.Void>() {
                                            public void onSuccess(Void v) {
                                                copy.hide();
                                                Window.alert(constants.CreatedSnapshot0ForPackage1(
                                                        newNameTextBox.getText(),
                                                        packageName));
                                            }
                                        });
                            }
                        } else {
                            for (RadioButton rb : options) {
                                if (rb.getValue()) {
                                    final String newName = rb.getText();
                                    serv.copyOrRemoveSnapshot(packageName,
                                            snapshotName,
                                            false,
                                            newName,
                                            new GenericCallback<java.lang.Void>() {
                                                public void onSuccess(Void v) {
                                                    copy.hide();
                                                    Window.alert(constants.Snapshot0ForPackage1WasCopiedFrom2(
                                                            newName,
                                                            packageName,
                                                            snapshotName));
                                                }
                                            });
                                }
                            }
                        }
                    }

                    private boolean isOneButtonSelected(final List<RadioButton> options) {
                        boolean oneButtonIsSelected = false;
                        for (RadioButton rb : options) {
                            if (rb.getValue()) {
                                oneButtonIsSelected = true;
                                break;
                            }
                        }
                        return oneButtonIsSelected;
                    }

                    private boolean checkUnique(SnapshotInfo[] snaps,
                            String name) {
                        for (SnapshotInfo sn : snaps) {
                            if (sn.getName().equals(name)) {
                                Window.alert(constants.PleaseEnterANonExistingSnapshotName());
                                return false;
                            }
                        }
                        return true;
                    }
                });
                copy.show();
            }
        };
    }

    public static void showNewSnapshot(final Command refreshCmd) {
        final FormStylePopup pop = new FormStylePopup(images.snapshot(),
                constants.NewSnapshot());
        final RulePackageSelector sel = new RulePackageSelector();

        pop.addAttribute(constants.ForPackage(),
                sel);
        Button ok = new Button(constants.OK());
        pop.addAttribute("",
                ok);
        pop.show();

        ok.addClickHandler(new ClickHandler() {
            public void onClick(ClickEvent event) {
                pop.hide();
                String pkg = sel.getSelectedPackage();
                PackageBuilderWidget.showSnapshotDialog(pkg,
                        refreshCmd);
            }
        });

    }

    public static void rebuildBinaries() {
        if (Window.confirm(constants.SnapshotRebuildWarning())) {
            LoadingPopup.showMessage(constants.RebuildingSnapshotsPleaseWaitThisMayTakeSomeTime());
            RepositoryServiceFactory.getPackageService().rebuildSnapshots(new GenericCallback<java.lang.Void>() {
                public void onSuccess(Void v) {
                    LoadingPopup.close();
                    Window.alert(constants.SnapshotsWereRebuiltSuccessfully());
                }
            });
        }
    }

}
