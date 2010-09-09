package org.drools.guvnor.client.qa.testscenarios;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.DirtyableComposite;
import org.drools.guvnor.client.common.DirtyableFlexTable;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.ImageButton;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.HumanReadable;
import org.drools.ide.common.client.modeldriven.DropDownData;
import org.drools.ide.common.client.modeldriven.MethodInfo;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ActionCallMethod;
import org.drools.ide.common.client.modeldriven.brl.ActionFieldFunction;
import org.drools.ide.common.client.modeldriven.testing.CallFieldValue;
import org.drools.ide.common.client.modeldriven.testing.CallMethod;
import org.drools.ide.common.client.modeldriven.testing.ExecutionTrace;
import org.drools.ide.common.client.modeldriven.testing.FactData;
import org.drools.ide.common.client.modeldriven.testing.Scenario;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyPressEvent;
import com.google.gwt.event.dom.client.KeyPressHandler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

public class CallMethodWidget extends DirtyableComposite {

	protected static Constants constants = ((Constants) GWT
			.create(Constants.class));

	protected final ScenarioWidget parent;
	protected final Scenario scenario;
	protected final CallMethod mCall;
	protected final String factName;
	private final ExecutionTrace executionTrace;

	final private DirtyableFlexTable layout;
	private boolean isBoundFact = false;

	private String[] fieldCompletionTexts;
	private String[] fieldCompletionValues;
	private String variableClass;

	private final SuggestionCompletionEngine suggestionCompletionEngine;

	public CallMethodWidget(String factName, ScenarioWidget parent,
			Scenario scenario, CallMethod mCall, ExecutionTrace executionTrace) {
		super();
		this.factName = factName;
		this.parent = parent;
		this.scenario = scenario;
		this.mCall = mCall;
		this.executionTrace = executionTrace;
		this.suggestionCompletionEngine = parent.suggestionCompletionEngine;

		this.layout = new DirtyableFlexTable();

		layout.setStyleName("model-builderInner-Background"); // NON-NLS

		if (suggestionCompletionEngine.isGlobalVariable(mCall.variable)) {

			List<MethodInfo> infos = suggestionCompletionEngine
					.getMethodInfosForGlobalVariable(mCall.variable);
			this.fieldCompletionTexts = new String[infos.size()];
			this.fieldCompletionValues = new String[infos.size()];
			int i = 0;
			for (MethodInfo info : infos) {
				this.fieldCompletionTexts[i] = info.getName();
				this.fieldCompletionValues[i] = info.getNameWithParameters();
				i++;
			}

			this.variableClass = (String) suggestionCompletionEngine
					.getGlobalVariable(mCall.variable);
		} else {
			FactData pattern = (FactData) scenario.getFactTypes().get(mCall.variable);
			if (pattern != null) {
				List<String> methodList = suggestionCompletionEngine
						.getMethodNames(pattern.type);
				fieldCompletionTexts = new String[methodList.size()];
				fieldCompletionValues = new String[methodList.size()];
				int i = 0;
				for (String methodName : methodList) {
					fieldCompletionTexts[i] = methodName;
					fieldCompletionValues[i] = methodName;
					i++;
				}
				this.variableClass = pattern.type;
				this.isBoundFact = true;
			}
		}

		doLayout();
		initWidget(this.layout);
	}

	private void doLayout() {
		layout.clear();
		layout.setWidget(0, 0, getSetterLabel());
		DirtyableFlexTable inner = new DirtyableFlexTable();
		for (int i = 0; i < mCall.callFieldValues.length; i++) {
			CallFieldValue val = mCall.callFieldValues[i];

			inner.setWidget(i, 0, fieldSelector(val));
			inner.setWidget(i, 1, valueEditor(val));
		}
		layout.setWidget(0, 1, inner);
	}

	private Widget getSetterLabel() {
		HorizontalPanel horiz = new HorizontalPanel();

		if (mCall.state == ActionCallMethod.TYPE_UNDEFINED) {
			Image edit = new ImageButton("images/add_field_to_fact.gif"); // NON-
			// NLS
			edit.setTitle(constants.AddAnotherFieldToThisSoYouCanSetItsValue());
			
			edit.addClickHandler(new ClickHandler() {
				
				public void onClick(ClickEvent event) {
					Image w = (Image)event.getSource();
					showAddFieldPopup(w);
					
				}
			});
				
//			edit.addClickListener(new ClickListener() {
//				public void onClick(Widget w) {
//					showAddFieldPopup(w);
//				}
//			});
			horiz.add(new SmallLabel(HumanReadable.getActionDisplayName("call")
					+ " [" + mCall.variable + "]")); // NON-NLS
                horiz.add( edit );
		} else {
			horiz.add(new SmallLabel(HumanReadable.getActionDisplayName("call")
					+ " [" + mCall.variable + "." + mCall.methodName + "]")); // NON-NLS
		}

		return horiz;
	}

	protected void showAddFieldPopup(Widget w) {

		final FormStylePopup popup = new FormStylePopup("images/newex_wiz.gif",
				constants.ChooseAMethodToInvoke()); // NON-NLS
		ListBox box = new ListBox();
		box.addItem("...");

		for (int i = 0; i < fieldCompletionTexts.length; i++) {
			box.addItem(fieldCompletionTexts[i], fieldCompletionValues[i]);
		}

		box.setSelectedIndex(0);

		popup.addAttribute(constants.ChooseAMethodToInvoke(), box);
		box.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				mCall.state = ActionCallMethod.TYPE_DEFINED;
				ListBox sourceW = (ListBox)event.getSource();
				String methodName = sourceW.getItemText(sourceW.getSelectedIndex());
				String methodNameWithParams = sourceW.getValue(sourceW
						.getSelectedIndex());

				mCall.methodName = methodName;
				List<String> fieldList = new ArrayList<String>();

				fieldList.addAll(suggestionCompletionEngine.getMethodParams(variableClass,
						methodNameWithParams));

				// String fieldType = completions.getFieldType( variableClass,
				// fieldName );
				int i = 0;
				for (String fieldParameter : fieldList) {
					mCall.addFieldValue(new CallFieldValue(methodName,
							String.valueOf(i), fieldParameter));
					i++;
				}

				parent.renderEditor();
				popup.hide();
				
			}
		});

		popup.setPopupPosition(w.getAbsoluteLeft(), w.getAbsoluteTop());
		popup.show();

	}

	private Widget valueEditor(final CallFieldValue val) {


		String type = "";
		if (suggestionCompletionEngine.isGlobalVariable(this.mCall.variable)) {
			type = suggestionCompletionEngine.getGlobalVariable(this.mCall.variable);
		} else {
			Map<String, String> mFactTypes = scenario.getVariableTypes();
			type	= mFactTypes.get(this.mCall.variable);
		}

		DropDownData enums = suggestionCompletionEngine.getEnums(type, this.mCall.callFieldValues,
				val.field);
		return new MethodParameterCallValueEditor(val, enums, executionTrace,scenario,
				val.type, new Command() {

					public void execute() {
						makeDirty();
					}
				});
	}

	/**
	 * This will return a keyboard listener for field setters, which will obey
	 * numeric conventions - it will also allow formulas (a formula is when the
	 * first value is a "=" which means it is meant to be taken as the user
	 * typed)
	 */
	public static KeyPressHandler getNumericFilter(final TextBox box){
		return new KeyPressHandler() {
			
			public void onKeyPress(KeyPressEvent event) {
				TextBox w = (TextBox)event.getSource();
				char c= event.getCharCode();
				if (Character.isLetter(c) && c != '='
					&& !(box.getText().startsWith("="))) {
				((TextBox) w).cancelKey();
			}
				
			}
		};
	}
	
//	public static KeyboardListener getNumericFilter(final TextBox box) {
//		return new KeyboardListener() {
//
//			public void onKeyDown(Widget arg0, char arg1, int arg2) {
//
//			}
//
//			public void onKeyPress(Widget w, char c, int i) {
//				if (Character.isLetter(c) && c != '='
//						&& !(box.getText().startsWith("="))) {
//					((TextBox) w).cancelKey();
//				}
//			}
//
//			public void onKeyUp(Widget arg0, char arg1, int arg2) {
//			}
//
//		};
//	}

	private Widget fieldSelector(final CallFieldValue val) {
		return new SmallLabel(val.type);
	}

	private Widget actionSelector(final ActionFieldFunction val) {

		final ListBox box = new ListBox();
		final String fieldType = val.type;
		final String[] modifiers = suggestionCompletionEngine.getModifiers(fieldType);

		if (modifiers != null) {
			for (int i = 0; i < modifiers.length; i++) {
				box.addItem(modifiers[i]);
			}
		}
		box.addChangeHandler(new ChangeHandler() {
			
			public void onChange(ChangeEvent event) {
				String methodName = box.getItemText(box.getSelectedIndex());
				val.setMethod(methodName);
			}
		});

		return box;
	}

	/**
	 * This returns true if the values being set are on a fact.
	 */
	public boolean isBoundFact() {
		return isBoundFact;
	}

	public boolean isDirty() {
		return layout.hasDirty();
	}

}
