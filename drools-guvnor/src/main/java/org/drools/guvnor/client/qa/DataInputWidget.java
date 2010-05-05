package org.drools.guvnor.client.qa;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.gwtext.client.util.Format;
import org.drools.guvnor.client.common.*;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.FieldData;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import java.util.*;

/**
 * Created by IntelliJ IDEA.
 * User: nheron
 * Date: 7 nov. 2009
 * Time: 19:34:49
 * To change this template use File | Settings | File Templates.
 */
public class DataInputWidget extends DirtyableComposite {


    private Grid outer;
	private Scenario scenario;
	private SuggestionCompletionEngine sce;
	private String type;
	private ScenarioWidget parent;
    private Constants constants = ((Constants) GWT.create(Constants.class));
    private ExecutionTrace executionTrace;

    public DataInputWidget(String factType, List<FactData> defList, boolean isGlobal, Scenario sc, SuggestionCompletionEngine sce, ScenarioWidget parent,ExecutionTrace executionTrace) {

        outer = new Grid(2, 1);
        scenario = sc;
        this.sce = sce;
        this.type = factType;

        this.parent = parent;
        this.executionTrace = executionTrace;
        outer.getCellFormatter().setStyleName(0, 0, "modeller-fact-TypeHeader"); //NON-NLS
        outer.getCellFormatter().setAlignment(0, 0, HasHorizontalAlignment.ALIGN_CENTER, HasVerticalAlignment.ALIGN_MIDDLE );
        outer.setStyleName("modeller-fact-pattern-Widget"); //NON-NLS


        if (isGlobal) {
            outer.setWidget(0, 0, getLabel(Format.format(constants.globalForScenario(), factType), defList, parent, sc));
        } else {
            FactData first = (FactData) defList.get(0);
            if (first.isModify) {
                outer.setWidget(0, 0,  getLabel(Format.format(constants.modifyForScenario(), factType), defList, parent, sc));
            } else {
                outer.setWidget(0, 0, getLabel(Format.format(constants.insertForScenario(), factType), defList, parent, sc));
            }
        }

        FlexTable t = render(defList, parent, sc);



        //parent.renderEditor();



        //outer.setWidget(1, 1, new Button("Remove"));


        outer.setWidget(1, 0, t);
        initWidget(outer);
    }

	private Widget getLabel(String text, final List defList, ScenarioWidget parent, Scenario sc) {
        //now we put in button to add new fields
        //Image newField = new ImageButton("images/add_field_to_fact.gif", "Add a field.");
        //Image newField = getNewFieldButton(defList);
        ClickableLabel clbl = new ClickableLabel(text, addFieldCL(defList, parent, sc));
        //HorizontalPanel h = new HorizontalPanel();
        //h.add(new SmallLabel(text)); h.add(newField);
        return clbl;
	}

    /*
	private ImageButton getNewFieldButton(final List defList) {
		ImageButton newField = new ImageButton("images/add_field_to_fact.gif", constants.AddAField()); //NON-NLS
        newField.addClickListener(addFieldCL(defList));
		return newField;
	}
	*/

	private ClickListener addFieldCL(final List<FactData> defList, final ScenarioWidget parent, final Scenario sc) {
		return new ClickListener() {
			public void onClick(Widget w) {

				//build up a list of what we have got, don't want to add it twice
				HashSet existingFields = new HashSet();
				if (defList.size() > 0) {
					FactData d = (FactData) defList.get(0);
					for (Iterator iterator = d.fieldData.iterator(); iterator.hasNext();) {
						FieldData f = (FieldData) iterator.next();
						existingFields.add(f.name);
					}

				}
				String[] fields = (String[]) sce.getModelFields(type);
				final FormStylePopup pop = new FormStylePopup(); //NON-NLS
                pop.setTitle(constants.ChooseDotDotDot());
				final ListBox b = new ListBox();
				for (int i = 0; i < fields.length; i++) {
					String fld = fields[i];
					if (!existingFields.contains(fld)) b.addItem(fld);
				}

				Button ok = new Button(constants.OK());
				ok.addClickListener(new ClickListener() {
									public void onClick(Widget w) {
										String f = b.getItemText(b.getSelectedIndex());
										for (Iterator iterator = defList.iterator(); iterator.hasNext();) {
											FactData fd = (FactData) iterator.next();
											fd.fieldData.add(new FieldData(f, ""));
										}
								        outer.setWidget(1, 0, render(defList, parent, sc));
								        pop.hide();
									}
								});
                HorizontalPanel h = new HorizontalPanel();
                h.add(b);
                h.add(ok);
                pop.addAttribute(constants.ChooseAFieldToAdd(), h);


                Button remove = new Button(constants.RemoveThisBlockOfData());
                remove.addClickListener(new ClickListener() {
                    public void onClick(Widget sender) {
                        if (Window.confirm(constants.AreYouSureYouWantToRemoveThisBlockOfData())) {
                            scenario.globals.removeAll( defList );
                            parent.renderEditor();
                            pop.hide();
                        }
                    }
                });
                pop.addAttribute("", remove);


				pop.show();
			}
		};
	}

	private FlexTable render(final List defList, final ScenarioWidget parent, final Scenario sc) {
		DirtyableFlexTable t = new DirtyableFlexTable();
		if (defList.size() == 0) {
			parent.renderEditor();
		}

		//This will work out what row is for what field, addin labels and remove icons

        Map fields = new HashMap();
        int col = 0;
        int totalCols = defList.size();
        for (Iterator iterator = defList.iterator(); iterator.hasNext();) {
            final FactData d = (FactData) iterator.next();

            for (int i = 0; i < d.fieldData.size(); i++) {
                final FieldData fd = d.fieldData.get(i);
                if (!fields.containsKey(fd.name)) {
                    int idx = fields.size() + 1;
                    fields.put(fd.name, new Integer(idx));
                    t.setWidget(idx, 0, new SmallLabel(fd.name + ":"));
                    Image del = new ImageButton("images/delete_item_small.gif", constants.RemoveThisRow(), new ClickListener() {
        				public void onClick(Widget w) {
        					if (Window.confirm(constants.AreYouSureYouWantToRemoveThisRow())) {
        						ScenarioHelper.removeFields(defList, fd.name);
        						outer.setWidget(1, 0, render(defList, parent, sc));

        					}
        				}
        			});
                    t.setWidget(idx, totalCols + 1, del);
                    t.getCellFormatter().setHorizontalAlignment(idx, 0, HasHorizontalAlignment.ALIGN_RIGHT);
                }
            }
        }

        int totalRows = fields.size();

        t.getFlexCellFormatter().setHorizontalAlignment(totalRows + 1, 0, HasHorizontalAlignment.ALIGN_RIGHT);

        //now we go through the facts and the fields, adding them to the grid
        //if a fact is missing a FieldData, we will add it in (so people can enter data later on)
        col = 0;
        for (Iterator iterator = defList.iterator(); iterator.hasNext();) {
            final FactData d = (FactData) iterator.next();
            t.setWidget(0, ++col, new SmallLabel("[" + d.name + "]"));
            Image del = new ImageButton("images/delete_item_small.gif", Format.format(constants.RemoveTheColumnForScenario(), d.name), new ClickListener() {
				public void onClick(Widget w) {
					if (scenario.isFactNameUsed(d)) {
                        Window.alert(Format.format(constants.CanTRemoveThisColumnAsTheName0IsBeingUsed(), d.name));
					} else if (Window.confirm(constants.AreYouSureYouWantToRemoveThisColumn())) {
						scenario.removeFixture(d);
						defList.remove(d);
						outer.setWidget(1, 0, render(defList, parent, sc));
					}
				}
			});
            t.setWidget(totalRows + 1, col, del);
            Map presentFields = new HashMap(fields);
            for (int i = 0; i < d.fieldData.size(); i++) {
                FieldData fd = d.fieldData.get(i);
                int fldRow = ((Integer) fields.get(fd.name)).intValue();
                t.setWidget(fldRow, col, editableCell(fd, d,d.type,this.executionTrace));
                presentFields.remove(fd.name);
            }

            for (Iterator missing = presentFields.entrySet().iterator(); missing.hasNext();) {
                Map.Entry e = (Map.Entry) missing.next();
                int fldRow = ((Integer) e.getValue()).intValue();
                FieldData fd = new FieldData((String) e.getKey(), "");
                d.fieldData.add(fd);
                t.setWidget(fldRow, col, editableCell(fd, d,d.type,this.executionTrace));
            }
        }

        if (fields.size() == 0) {
        	//HorizontalPanel h = new HorizontalPanel();
        	Button b = new Button(constants.AddAField());
        	b.addClickListener(addFieldCL(defList, parent, sc));

        	//h.add(new HTML("<i><small>Add fields:</small></i>"));
        	//h.add(getNewFieldButton(defList));
        	t.setWidget(1, 1, b);
        }
        return t;
	}


	/**
	 * This will provide a cell editor. It will filter non numerics, show choices etc as appropriate.
	 * @param fd
	 * @param factType
	 * @return
	 */
	private Widget editableCell(final FieldData fd,FactData factData, String factType,ExecutionTrace executionTrace) {
        return new FieldDataConstraintEditor(factType, new ValueChanged() {
			public void valueChanged(String newValue) {
				fd.value = newValue;
				makeDirty();
			}
		}, fd,factData,sce,scenario,executionTrace);
    }
}



