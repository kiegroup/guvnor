package org.drools.guvnor.client.factmodel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.LoadingPopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.packages.SuggestionCompletionCache;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.client.ruleeditor.DefaultRuleContentWidget;
import org.drools.guvnor.client.ruleeditor.RuleViewer;
import org.drools.guvnor.client.ruleeditor.SaveEventListener;
import org.drools.guvnor.client.messages.Constants;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlexTable;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.KeyboardListener;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;
import com.google.gwt.user.client.ui.FlexTable.FlexCellFormatter;
import com.google.gwt.core.client.GWT;
import com.gwtext.client.widgets.form.FormPanel;
import com.gwtext.client.util.Format;

/**
 * The editor for fact models (drl declared types).
 *
 * @author Michael Neale
 */
public class FactModelWidget extends Composite implements SaveEventListener {

	private RuleAsset asset;
	private VerticalPanel layout;
	private int editingFact = -1;
    private static Constants constants = ((Constants) GWT.create(Constants.class));
    private static Map<String, String> TYPE_DESCRIPTIONS = new HashMap<String, String>() {
        {
            put ("Integer", constants.WholeNumberInteger());
            put ("Boolean", constants.TrueOrFalse());
            put ("java.util.Date", constants.Date());
            put ("java.math.BigDecimal", constants.DecimalNumber());
            put ("String", constants.Text());

        }
    };


    public FactModelWidget(RuleAsset asset, RuleViewer viewer) {
        this(asset);
    }

    public FactModelWidget(final RuleAsset asset) {
		this.asset = asset;
		this.layout = new VerticalPanel();

		if (asset.content instanceof RuleContentText) {
			layout.add(new DefaultRuleContentWidget(asset));
		} else {
			//loadTestData(asset);
			if (asset.content == null) {
				asset.content = new FactModels();
			}
			renderEditor();
		}

		layout.setWidth("100%");
		initWidget(layout);
		setStyleName("model-builder-Background");   //NON-NLS
	}


	private void renderEditor() {
		layout.clear();
		final FactModels m = (FactModels) asset.content;

		String factHeaderStyle = "modeller-fact-TypeHeader"; //NON-NLS
		for (int i = 0; i < m.models.size(); i++) {

			final FactMetaModel mm = (FactMetaModel) m.models.get(i);

	        FormPanel config = new FormPanel();
	        config.setTitle(mm.name);
	        config.setCollapsible(true);
	        config.setCollapsed(!(editingFact == i));


			FlexTable tb = new FlexTable();
			config.add(tb);
			tb.setStyleName("modeller-fact-pattern-Widget");    //NON-NLS
			tb.setWidth("100%");
			//layout.add(tb);
			layout.add(config);

			HorizontalPanel headerPanel = new HorizontalPanel();
			//headerPanel.add(new HTML("<b><small>" + mm.name + "</small></b>"));

			//ImageButton addField = new ImageButton("images/add_field_to_fact.gif");
			Button addField = new Button(constants.AddField());
			addField.addClickListener(new ClickListener() {
				public void onClick(Widget arg0) {
					showFieldEditor(m, mm, null);
				}
			});
			headerPanel.add(addField);
			headerPanel.add(editFact(mm, m));

			tb.setWidget(0, 0, headerPanel);
			FlexCellFormatter formatter = tb.getFlexCellFormatter();
			formatter.setColSpan(0, 0, 2);
			formatter.setStyleName(0, 0, factHeaderStyle);
			formatter.setHorizontalAlignment(0, 0, HasHorizontalAlignment.ALIGN_LEFT);

			for (int j = 0; j < mm.fields.size(); j++) {
				final FieldMetaModel fm = (FieldMetaModel) mm.fields.get(j);
                String ms = Format.format(constants.FieldName(), fm.name);
				tb.setWidget(j + 1, 0, new HTML(ms));
				formatter.setHorizontalAlignment(j + 1, 0, HasHorizontalAlignment.ALIGN_RIGHT);

				HorizontalPanel type = new HorizontalPanel();
				type.add(new SmallLabel(getDesc(fm)));
				ImageButton del = new ImageButton("images/delete_item_small.gif"); //NON-NLS
				del.addClickListener(new ClickListener() {
					public void onClick(Widget w) {
                        if (Window.confirm(Format.format(constants.AreYouSureYouWantToRemoveTheField0(), fm.name))) {
							mm.fields.remove(fm);
							editingFact = m.models.indexOf(mm);
							renderEditor();
						}
					}
				});

				ImageButton edit = new ImageButton("images/edit.gif"); //NON-NLS
				edit.addClickListener(new ClickListener() {
					public void onClick(Widget arg0) {
						showFieldEditor(m, mm, fm);
					}
				});

				type.add(edit);
				type.add(del);



				tb.setWidget(j + 1, 1, type);
				formatter.setHorizontalAlignment(j + 1, 1, HasHorizontalAlignment.ALIGN_LEFT);
			}



		}
		final Button addNewFact = new Button(constants.AddNewFactType());
		addNewFact.addClickListener(new ClickListener() {
			public void onClick(Widget w) {
				String type = Window.prompt(constants.NewType(), constants.EnterNewTypeName());
				if (type != null)  {
					if (uniqueName(type, m.models)) {
						m.models.add(new FactMetaModel(type, new ArrayList()));
						editingFact = m.models.size() -1;
						renderEditor();
					} else {
                        Window.alert(Format.format(constants.TypeNameExistsWarning(), type));
						addNewFact.click();
					}
				}
			}
		});
		layout.add(addNewFact);

	}

	private boolean uniqueName(String type, List<FactMetaModel> models) {
		for (FactMetaModel m : models) {
			if (m.name.equals(type)) {
				return false;
			}
		}
		return true;
	}


    private String getDesc(FieldMetaModel fm) {
        if (TYPE_DESCRIPTIONS.containsKey(fm.type)) {
            return TYPE_DESCRIPTIONS.get(fm.type);
        }
        return fm.type;
    }


    /**
	 * Display the field editor.
	 */
	private void showFieldEditor(final FactModels models, final FactMetaModel mm, final FieldMetaModel field) {
		final FormStylePopup pop = new FormStylePopup();
		final TextBox fieldName = new TextBox();
		final TextBox fieldType = new TextBox();
		fieldName.addKeyboardListener(noSpaceListener());
		fieldType.addKeyboardListener(noSpaceListener());
		if (field != null) {
			fieldName.setText(field.name);
			fieldType.setText(field.type);
		}
		HorizontalPanel typeP = new HorizontalPanel();
		typeP.add(fieldType);
		final ListBox typeChoice = new ListBox();
		typeChoice.addItem(constants.chooseType());

        for (String k : TYPE_DESCRIPTIONS.keySet()) {
            typeChoice.addItem(TYPE_DESCRIPTIONS.get(k), k);
        }


		int idx = models.models.indexOf(mm);
		for (int i = 0; i < idx; i++) {
			FactMetaModel mm_ = (FactMetaModel) models.models.get(i);
			typeChoice.addItem(mm_.name);
		}
		typeChoice.setSelectedIndex(0);
		typeChoice.addChangeListener(new ChangeListener() {
			public void onChange(Widget w) {
				fieldType.setText(typeChoice.getValue(typeChoice.getSelectedIndex()));
			}
		});

		typeP.add(typeChoice);

		pop.addAttribute(constants.FieldNameAttribute(), fieldName);
		pop.addAttribute(constants.Type(), typeP);

		Button ok = new Button(constants.OK());
		ok.addClickListener(new ClickListener() {
			public void onClick(Widget arg0) {
				FieldMetaModel fld = field;
				if (field == null) {
					fld = new FieldMetaModel();
					mm.fields.add(fld);
				}
				fld.name = fieldName.getText();
				fld.type = fieldType.getText();
				editingFact = models.models.indexOf(mm);
				renderEditor();
				pop.hide();
			}
		});
		pop.addAttribute("", ok);

		pop.show();
	}

	/**
	 * An editor for fact header name.
	 * @param m
	 */
	private Widget editFact(final FactMetaModel mm, final FactModels m) {
		ImageButton edit = new ImageButton("images/edit.gif");  //NON-NLS
		//Button edit = new Button("Edit/remove");
		edit.addClickListener(new ClickListener() {
			public void onClick(Widget arg0) {
				final FormStylePopup pop = new FormStylePopup();
				HorizontalPanel changeName = new HorizontalPanel();
				final TextBox name = new TextBox();
				name.setText(mm.name);
				changeName.add(name);
				Button nameBut = new Button(constants.ChangeName());

				nameBut.addKeyboardListener(noSpaceListener());

				nameBut.addClickListener(new ClickListener() {
					public void onClick(Widget w) {
                        if (!uniqueName(name.getText(), m.models)) {
                            Window.alert(Format.format(constants.NameTakenForModel(), name.getText()));
                            return;
                        }
						if (Window.confirm(constants.ModelNameChangeWarning())) {
							mm.name = name.getText();
							pop.hide();
							renderEditor();
						}
					}
				});
				changeName.add(nameBut);
				pop.addAttribute(constants.ChangeFactName(), changeName);

				Button delFact = new Button(constants.Delete());
				delFact.addClickListener(new ClickListener() {
					public void onClick(Widget w) {
						if (Window.confirm(constants.AreYouSureYouWantToRemoveThisFact())) {
							m.models.remove(mm);
							pop.hide();
							renderEditor();
						}
					}
				});
				pop.addAttribute(constants.RemoveThisFactType(), delFact);

				pop.show();
			}

		});
		return edit;
	}

	private KeyboardListener noSpaceListener() {
		return new KeyboardListener() {
			public void onKeyDown(Widget arg0, char arg1, int arg2) {
			}

	        public void onKeyPress(Widget w, char c, int i) {
	                if (c == ' ') {
	                    ((TextBox) w).cancelKey();
	                }
	        }

			public void onKeyUp(Widget arg0, char arg1, int arg2) {

			}

		};
	}

	public void onAfterSave() {
		LoadingPopup.showMessage(constants.RefreshingModel());
		SuggestionCompletionCache.getInstance().loadPackage(this.asset.metaData.packageName, new Command() {
			public void execute() {
				LoadingPopup.close();
			}
		});
	}

	public void onSave() {
		//not needed.

	}


}
