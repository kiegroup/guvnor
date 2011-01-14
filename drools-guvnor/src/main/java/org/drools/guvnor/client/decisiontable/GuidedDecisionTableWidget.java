/*
 * Copyright 2010 JBoss Inc
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

package org.drools.guvnor.client.decisiontable;

import org.drools.guvnor.client.common.DirtyableHorizontalPane;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.PrettyFormLayout;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.decisiontable.widget.DecisionTableControlsWidget;
import org.drools.guvnor.client.decisiontable.widget.DecisionTableWidget;
import org.drools.guvnor.client.decisiontable.widget.VerticalDecisionTableWidget;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.ui.RuleAttributeWidget;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.resources.Images;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.ruleeditor.EditorWidget;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.ruleeditor.SaveEventListener;
import org.drools.guvnor.client.util.AddButton;
import org.drools.guvnor.client.util.DecoratedDisclosurePanel;
import org.drools.guvnor.client.util.Format;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.BaseSingleFieldConstraint;
import org.drools.ide.common.client.modeldriven.dt.ActionCol;
import org.drools.ide.common.client.modeldriven.dt.ActionInsertFactCol;
import org.drools.ide.common.client.modeldriven.dt.ActionSetFieldCol;
import org.drools.ide.common.client.modeldriven.dt.AttributeCol;
import org.drools.ide.common.client.modeldriven.dt.ConditionCol;
import org.drools.ide.common.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.ide.common.client.modeldriven.dt.MetadataCol;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

import com.gwtext.client.core.EventObject;
import com.gwtext.client.widgets.ToolbarMenuButton;
import com.gwtext.client.widgets.menu.BaseItem;
import com.gwtext.client.widgets.menu.Item;
import com.gwtext.client.widgets.menu.Menu;
import com.gwtext.client.widgets.menu.event.BaseItemListenerAdapter;

/**
 * This is the new guided decision table editor for the web.
 * 
 * @author Michael Neale
 */
public class GuidedDecisionTableWidget extends Composite implements
		SaveEventListener, EditorWidget {

	private Constants constants = GWT.create(Constants.class);
	private static Images images = GWT.create(Images.class);

	private GuidedDecisionTable guidedDecisionTable;
	private VerticalPanel layout;
	private PrettyFormLayout configureColumnsNote;
	private VerticalPanel attributeConfigWidget;
	private VerticalPanel conditionsConfigWidget;
	private String packageName;
	private VerticalPanel actionsConfigWidget;
	private SuggestionCompletionEngine sce;
	private GroupingsPanel groupingsPanel = null;

	private DecisionTableWidget dtable;
	private DecisionTableControlsWidget dtableCtrls;

	public GuidedDecisionTableWidget(RuleAsset asset, RuleViewer viewer) {
		this(asset);
	}

	public GuidedDecisionTableWidget(RuleAsset asset) {

		this.guidedDecisionTable = (GuidedDecisionTable) asset.content;
		this.packageName = asset.metaData.packageName;
		this.guidedDecisionTable.setTableName(asset.metaData.name);

		layout = new VerticalPanel();

		configureColumnsNote = new PrettyFormLayout();
		configureColumnsNote.startSection();
		configureColumnsNote.addRow(new HTML("<img src='"
				+ new Image(images.information()).getUrl() + "'/>&nbsp;"
				+ constants.ConfigureColumnsNote()));
		configureColumnsNote.endSection();

		DecoratedDisclosurePanel disclosurePanel = new DecoratedDisclosurePanel(
				constants.DecisionTable());
		disclosurePanel.setWidth("100%");
		disclosurePanel.setTitle(constants.DecisionTable());

		VerticalPanel config = new VerticalPanel();
		config.setWidth("100%");
		disclosurePanel.add(config);

		DecoratedDisclosurePanel conditions = new DecoratedDisclosurePanel(
				constants.ConditionColumns());
		conditions.setOpen(false);
		conditions.setWidth("75%");
		conditions.add(getConditions());
		config.add(conditions);

		DecoratedDisclosurePanel actions = new DecoratedDisclosurePanel(
				constants.ActionColumns());
		actions.setOpen(false);
		actions.setWidth("75%");
		actions.add(getActions());
		config.add(actions);

		DecoratedDisclosurePanel grouping = new DecoratedDisclosurePanel(
				constants.options());
		grouping.setOpen(false);
		grouping.setWidth("75%");
		VerticalPanel groupings = new VerticalPanel();
		groupings.add(getGrouping());
		groupings.add(getAttributes());
		grouping.add(groupings);
		config.add(grouping);

		layout.add(disclosurePanel);

		VerticalPanel buttonPanel = new VerticalPanel();
		buttonPanel.add(getToolbarMenuButton());
		layout.add(buttonPanel);

		layout.add(configureColumnsNote);

		setupDecisionTable();

		initWidget(layout);
	}

	private Widget getGrouping() {

		this.groupingsPanel = new GroupingsPanel(guidedDecisionTable,
				new Command() {

					public void execute() {
						dtable.updateModel();
					}
				});
		return groupingsPanel;
	}

	private Widget getActions() {
		actionsConfigWidget = new VerticalPanel();
		refreshActionsWidget();
		return actionsConfigWidget;
	}

	private void refreshActionsWidget() {
		this.actionsConfigWidget.clear();
		for (ActionCol c : guidedDecisionTable.getActionCols()) {
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(removeAction(c));
			hp.add(editAction(c));
			hp.add(new SmallLabel(c.getHeader()));
			actionsConfigWidget.add(hp);
		}
		actionsConfigWidget.add(newAction());
		setupColumnsNote();
	}

	private Widget editAction(final ActionCol c) {
		return new ImageButton(images.edit(),
				constants.EditThisActionColumnConfiguration(),
				new ClickHandler() {
					public void onClick(ClickEvent w) {
						if (c instanceof ActionSetFieldCol) {
							ActionSetFieldCol asf = (ActionSetFieldCol) c;
							ActionSetColumn ed = new ActionSetColumn(getSCE(),
									dtable, new Command() {
										public void execute() {
											dtable.updateModel();
											refreshActionsWidget();
										}
									}, asf, false);
							ed.show();
						} else if (c instanceof ActionInsertFactCol) {
							ActionInsertFactCol asf = (ActionInsertFactCol) c;
							ActionInsertColumn ed = new ActionInsertColumn(
									getSCE(), dtable, new Command() {
										public void execute() {
											dtable.updateModel();
											refreshActionsWidget();
										}
									}, asf, false);
							ed.show();
						}

					}
				});

	}

	private Widget newAction() {
		AddButton addButton = new AddButton();
		addButton.setText(constants.NewColumn());
		addButton.setTitle(constants.CreateANewActionColumn());

		addButton.addClickHandler(new ClickHandler() { // NON-NLS
					public void onClick(ClickEvent w) {
						final FormStylePopup pop = new FormStylePopup();
						pop.setModal(false);

						final ListBox choice = new ListBox();
						choice.addItem(constants.SetTheValueOfAField(), "set");
						choice.addItem(
								constants.SetTheValueOfAFieldOnANewFact(),
								"insert");
						Button ok = new Button("OK");
						ok.addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent w) {
								String s = choice.getValue(choice
										.getSelectedIndex());
								if (s.equals("set")) {
									showSet();
								} else if (s.equals("insert")) {
									showInsert();
								}
								pop.hide();
							}

							private void showInsert() {
								ActionInsertColumn ins = new ActionInsertColumn(
										getSCE(), dtable, new Command() {
											public void execute() {
												newActionAdded();
											}
										}, new ActionInsertFactCol(), true);
								ins.show();
							}

							private void showSet() {
								ActionSetColumn set = new ActionSetColumn(
										getSCE(), dtable, new Command() {
											public void execute() {
												newActionAdded();
											}
										}, new ActionSetFieldCol(), true);
								set.show();
							}

							private void newActionAdded() {
								dtable.updateModel();
								refreshActionsWidget();
							}
						});
						pop.addAttribute(constants.TypeOfActionColumn(), choice);
						pop.addAttribute("", ok);
						pop.show();
					}

				});

		return addButton;
	}

	private Widget removeAction(final ActionCol c) {
		Image del = new ImageButton(images.deleteItemSmall(),
				constants.RemoveThisActionColumn(), new ClickHandler() {
					public void onClick(ClickEvent w) {
						String cm = Format.format(
								constants.DeleteActionColumnWarning(),
								c.getHeader());
						if (com.google.gwt.user.client.Window.confirm(cm)) {
							guidedDecisionTable.getActionCols().remove(c);
							dtable.deleteColumn(c);
							dtable.updateModel();
							refreshActionsWidget();
						}
					}
				});

		return del;
	}

	private Widget getConditions() {
		conditionsConfigWidget = new VerticalPanel();
		refreshConditionsWidget();
		return conditionsConfigWidget;
	}

	private void refreshConditionsWidget() {
		this.conditionsConfigWidget.clear();
		for (int i = 0; i < guidedDecisionTable.getConditionCols().size(); i++) {
			ConditionCol c = guidedDecisionTable.getConditionCols().get(i);
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(removeCondition(c));
			hp.add(editCondition(c));
			hp.add(new SmallLabel(c.getHeader()));
			conditionsConfigWidget.add(hp);
		}
		conditionsConfigWidget.add(newCondition());
		setupColumnsNote();
	}

	private Widget newCondition() {
		final ConditionCol newCol = new ConditionCol();
		newCol.setConstraintValueType(BaseSingleFieldConstraint.TYPE_LITERAL);
		AddButton addButton = new AddButton();
		addButton.setText(constants.NewColumn());
		addButton.setTitle(constants.AddANewConditionColumn());
		addButton.addClickHandler(new ClickHandler() { // NON-NLS
					public void onClick(ClickEvent w) {
						GuidedDTColumnConfig dialog = new GuidedDTColumnConfig(
								getSCE(), dtable, new Command() {
									public void execute() {
										dtable.updateModel();
										refreshConditionsWidget();
									}
								}, newCol, true);
						dialog.show();
					}
				});
		return addButton;
	}

	private Widget editCondition(final ConditionCol c) {
		return new ImageButton(images.edit(),
				constants.EditThisColumnsConfiguration(), new ClickHandler() {
					public void onClick(ClickEvent w) {
						GuidedDTColumnConfig dialog = new GuidedDTColumnConfig(
								getSCE(), dtable, new Command() {
									public void execute() {
										dtable.updateModel();
										refreshConditionsWidget();
									}
								}, c, false);
						dialog.show();
					}
				});
	}

	private SuggestionCompletionEngine getSCE() {
		if (sce == null) {
			this.sce = SuggestionCompletionCache.getInstance()
					.getEngineFromCache(this.packageName);
		}
		return sce;
	}

	private Widget removeCondition(final ConditionCol c) {
		Image del = new ImageButton(images.deleteItemSmall(),
				constants.RemoveThisConditionColumn(), new ClickHandler() {
					public void onClick(ClickEvent w) {
						String cm = Format.format(
								constants.DeleteConditionColumnWarning(),
								c.getHeader());
						if (com.google.gwt.user.client.Window.confirm(cm)) {
							guidedDecisionTable.getConditionCols().remove(c);
							dtable.deleteColumn(c);
							dtable.updateModel();
							refreshConditionsWidget();
						}
					}
				});

		return del;
	}

	private Widget getAttributes() {
		attributeConfigWidget = new VerticalPanel();
		refreshAttributeWidget();
		return attributeConfigWidget;
	}

	private void refreshAttributeWidget() {
		this.attributeConfigWidget.clear();
		attributeConfigWidget.add(newAttr());
		if (guidedDecisionTable.getMetadataCols().size() > 0) {
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(new HTML("&nbsp;&nbsp;")); // NON-NLS
			hp.add(new SmallLabel(constants.Metadata()));
			attributeConfigWidget.add(hp);
		}
		for (MetadataCol at : guidedDecisionTable.getMetadataCols()) {
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;")); // NON-NLS
			hp.add(removeMeta(at));
			hp.add(new SmallLabel(at.attr));
			attributeConfigWidget.add(hp);
		}
		if (guidedDecisionTable.getAttributeCols().size() > 0) {
			HorizontalPanel hp = new HorizontalPanel();
			hp.add(new HTML("&nbsp;&nbsp;")); // NON-NLS
			hp.add(new SmallLabel(constants.Attributes()));
			attributeConfigWidget.add(hp);
		}

		for (AttributeCol atc : guidedDecisionTable.getAttributeCols()) {
			final AttributeCol at = atc;
			HorizontalPanel hp = new HorizontalPanel();

			hp.add(new SmallLabel(at.attr));

			hp.add(removeAttr(at));
			final TextBox defaultValue = new TextBox();
			defaultValue.setText(at.getDefaultValue());
			defaultValue.addChangeHandler(new ChangeHandler() {
				public void onChange(ChangeEvent event) {
					at.setDefaultValue(defaultValue.getText());
				}
			});

			if (at.attr.equals(RuleAttributeWidget.SALIENCE_ATTR)) {
				hp.add(new HTML("&nbsp;&nbsp;"));
				final CheckBox useRowNumber = new CheckBox();
				useRowNumber.setValue(at.isUseRowNumber());

				hp.add(useRowNumber);
				hp.add(new SmallLabel(constants.UseRowNumber()));
				hp.add(new SmallLabel("("));
				final CheckBox reverseOrder = new CheckBox();
				reverseOrder.setValue(at.isReverseOrder());
				reverseOrder.setEnabled(false);

				useRowNumber.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent sender) {
						at.setUseRowNumber(useRowNumber.getValue());
						reverseOrder.setEnabled(useRowNumber.getValue());
						dtable.updateSystemControlledColumnValues();
						dtable.redrawSystemControlledColumns();
					}
				});

				reverseOrder.addClickHandler(new ClickHandler() {
					public void onClick(ClickEvent sender) {
						at.setReverseOrder(reverseOrder.getValue());
						dtable.updateSystemControlledColumnValues();
						dtable.redrawSystemControlledColumns();
					}
				});
				hp.add(reverseOrder);
				hp.add(new SmallLabel(constants.ReverseOrder()));
				hp.add(new SmallLabel(")"));
			}
			hp.add(new HTML("&nbsp;&nbsp;&nbsp;&nbsp;")); // NON-NLS
			hp.add(new SmallLabel(constants.DefaultValue()));
			hp.add(defaultValue);

			final CheckBox hide = new CheckBox();
			hide.setValue(at.isHideColumn());
			hide.addClickHandler(new ClickHandler() {
				public void onClick(ClickEvent sender) {
					at.setHideColumn(hide.getValue());
					dtable.setColumnVisibility(at, !at.isHideColumn());
				}
			});
			hp.add(hide);
			hp.add(new SmallLabel(constants.HideThisColumn()));

			attributeConfigWidget.add(hp);
			setupColumnsNote();
		}

	}

	private Widget newAttr() {
		ImageButton but = new ImageButton(images.newItem(),
				constants.AddANewAttributeMetadata(), new ClickHandler() {
					public void onClick(ClickEvent w) {
						// show choice of attributes
						final FormStylePopup pop = new FormStylePopup(images
								.config(), constants.AddAnOptionToTheRule());
						final ListBox list = RuleAttributeWidget
								.getAttributeList();
						final Image addbutton = new ImageButton(images
								.newItem());
						final TextBox box = new TextBox();
						box.setVisibleLength(15);

						list.setSelectedIndex(0);

						list.addChangeHandler(new ChangeHandler() {
							public void onChange(ChangeEvent event) {
								AttributeCol attr = new AttributeCol();
								attr.attr = list.getItemText(list
										.getSelectedIndex());
								dtable.addColumn(attr);
								guidedDecisionTable.getAttributeCols()
										.add(attr);
								dtable.updateModel();
								refreshAttributeWidget();
								pop.hide();
							}
						});

						addbutton.setTitle(constants.AddMetadataToTheRule());

						addbutton.addClickHandler(new ClickHandler() {
							public void onClick(ClickEvent w) {
								MetadataCol met = new MetadataCol();
								met.attr = box.getText();
								dtable.addColumn(met);
								guidedDecisionTable.getMetadataCols().add(met);
								dtable.updateModel();
								refreshAttributeWidget();
								pop.hide();
							}
						});
						DirtyableHorizontalPane horiz = new DirtyableHorizontalPane();
						horiz.add(box);
						horiz.add(addbutton);

						pop.addAttribute(constants.Metadata1(), horiz);
						pop.addAttribute(constants.Attribute(), list);
						pop.show();
					}

				});
		HorizontalPanel h = new HorizontalPanel();
		h.add(new SmallLabel(constants.AddAttributeMetadata()));
		h.add(but);
		return h;
	}

	private Widget removeAttr(final AttributeCol at) {
		Image del = new ImageButton(images.deleteItemSmall(),
				constants.RemoveThisAttribute(), new ClickHandler() {
					public void onClick(ClickEvent w) {
						String ms = Format.format(
								constants.DeleteActionColumnWarning(), at.attr);
						if (com.google.gwt.user.client.Window.confirm(ms)) {
							guidedDecisionTable.getAttributeCols().remove(at);
							dtable.deleteColumn(at);
							dtable.updateModel();
							refreshAttributeWidget();
						}
					}
				});

		return del;
	}

	private Widget removeMeta(final MetadataCol md) {
		Image del = new ImageButton(images.deleteItemSmall(),
				constants.RemoveThisMetadata(), new ClickHandler() {
					public void onClick(ClickEvent w) {
						String ms = Format.format(
								constants.DeleteActionColumnWarning(), md.attr);
						if (com.google.gwt.user.client.Window.confirm(ms)) {
							guidedDecisionTable.getMetadataCols().remove(md);
							dtable.deleteColumn(md);
							dtable.updateModel();
							refreshAttributeWidget();
						}
					}
				});

		return del;
	}

	private void setupColumnsNote() {
		configureColumnsNote.setVisible(guidedDecisionTable.getAttributeCols()
				.size() == 0
				&& guidedDecisionTable.getConditionCols().size() == 0
				&& guidedDecisionTable.getActionCols().size() == 0);
	}

	private void setupDecisionTable() {
		if (dtable == null) {
			dtable = new VerticalDecisionTableWidget(getSCE());
			dtable.setPixelSize(1000, 500);
		}
		if (dtableCtrls == null) {
			dtableCtrls = new DecisionTableControlsWidget(dtable);
		}
		dtable.setModel(guidedDecisionTable);
		layout.add(dtable);
		layout.add(dtableCtrls);
	}

	private ToolbarMenuButton getToolbarMenuButton() {
		Menu menu = new Menu();
		menu.addItem(new Item(constants.CopySelectedRowS(),
				new BaseItemListenerAdapter() {
					public void onClick(BaseItem item, EventObject e) {
					}
				}));
		ToolbarMenuButton tbb = new ToolbarMenuButton(constants.Modify(), menu);
		return tbb;
	}

	/**
	 * Need to copy the data from the Decision Table
	 */
	public void onSave() {
		dtable.updateModel();
	}

	public void onAfterSave() {
		// not needed.
	}

}
