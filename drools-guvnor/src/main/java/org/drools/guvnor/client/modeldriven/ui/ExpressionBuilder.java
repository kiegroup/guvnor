package org.drools.guvnor.client.modeldriven.ui;

import static org.drools.ide.common.client.modeldriven.brl.ExpressionPartHelper.getExpressionPartForField;
import static org.drools.ide.common.client.modeldriven.brl.ExpressionPartHelper.getExpressionPartForGlobalVariable;
import static org.drools.ide.common.client.modeldriven.brl.ExpressionPartHelper.getExpressionPartForMethod;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.common.ClickableLabel;
import org.drools.guvnor.client.common.FormStylePopup;
import org.drools.guvnor.client.common.SmallLabel;
import org.drools.guvnor.client.messages.Constants;
import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.drools.ide.common.client.modeldriven.brl.ExpressionCollectionIndex;
import org.drools.ide.common.client.modeldriven.brl.ExpressionFieldVariable;
import org.drools.ide.common.client.modeldriven.brl.ExpressionFormLine;
import org.drools.ide.common.client.modeldriven.brl.ExpressionMethod;
import org.drools.ide.common.client.modeldriven.brl.ExpressionPart;
import org.drools.ide.common.client.modeldriven.brl.ExpressionText;
import org.drools.ide.common.client.modeldriven.brl.ExpressionVariable;
import org.drools.ide.common.client.modeldriven.brl.FactPattern;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.util.Format;

public class ExpressionBuilder extends RuleModellerWidget implements HasExpressionTypeChangeHandlers, HasExpressionChangeHandlers {

	private static final String DELETE_VALUE = "_delete_";
	private static final String FIElD_VALUE_PREFIX = "fl";
	private static final String VARIABLE_VALUE_PREFIX = "va";
	// private static final String GLOBAL_COLLECTION_VALUE_PREFIX = "gc";
	private static final String GLOBAL_VARIABLE_VALUE_PREFIX = "gv";
	private static final String METHOD_VALUE_PREFIX = "mt";
	private final SmallLabelClickHandler slch = new SmallLabelClickHandler();
	private Constants constants = ((Constants) GWT.create(Constants.class));
	// private FlowPanel panel = new FlowPanel();
	private HorizontalPanel panel = new HorizontalPanel();
	private ExpressionFormLine expression;
	private boolean readOnly;

	public ExpressionBuilder(RuleModeller modeller, ExpressionFormLine expression) {
		this(modeller, expression, false);
	}

	public ExpressionBuilder(RuleModeller modeller, ExpressionFormLine expression, Boolean readOnly) {
		super(modeller);

		if (readOnly == null) {
			this.readOnly = !modeller.getSuggestionCompletions().containsFactType(
					modeller.getSuggestionCompletions().getFactNameFromType(
							this.expression.getRootExpression().getClassType()));
		} else {
			this.readOnly = readOnly;
		}

		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		this.expression = expression;
		if (expression == null || expression.isEmpty()) {
			if (this.readOnly) {
				panel.add(new SmallLabel("<b>-</b>"));
			} else {
				panel.add(createStartPointWidget());
			}
		} else {
			if (this.readOnly) {
				panel.add(createWidgetForExpression("<b>" + getBoundText() + expression.getText(false) + "</b>"));
			} else {
				panel.add(createWidgetForExpression("<b>" + getBoundText() + expression.getText(false) + ".</b>"));
				panel.add(getWidgetForCurrentType());
			}
		}
		initWidget(panel);
	}
	
	private String getBoundText() {
		if (expression.isBound()) {
			return "[" + expression.getBinding() + "] ";
		}
		return "";
	}

	private Widget createStartPointWidget() {
		ListBox startPoint = new ListBox();
		panel.add(startPoint);

		startPoint.addItem(constants.ChooseDotDotDot(), "");

		// TODO {baunax} uncomment when global collections is implemented.
		// for (String gc : getCompletionEngine().getGlobalCollections()) {
		// startPoint.addItem(gc, GLOBAL_COLLECTION_VALUE_PREFIX + "." + gc);
		// }

		for (String gv : getCompletionEngine().getGlobalVariables()) {
			startPoint.addItem(gv, GLOBAL_VARIABLE_VALUE_PREFIX + "." + gv);
		}

		for (String v : getRuleModel().getBoundFacts()) {
			startPoint.addItem(v, VARIABLE_VALUE_PREFIX + "." + v);
		}

		startPoint.setVisibleItemCount(1);
		startPoint.addChangeHandler(new ChangeHandler() {

			public void onChange(ChangeEvent event) {
				ListBox lb = (ListBox) event.getSource();
				int index = lb.getSelectedIndex();
				if (index > 0) {
					ExpressionBuilder.this.makeDirty();
					startPointChange(lb.getValue(index));
				}
			}
		});
		return startPoint;
	}

	@Override
	public void makeDirty() {
		super.makeDirty();
		setModified(true);
	}

	private void startPointChange(String value) {
		setModified(true);
		panel.clear();
		Widget w;
		int dotPos = value.indexOf('.');
		String prefix = value.substring(0, dotPos);
		String attrib = value.substring(dotPos + 1);
		if (prefix.equals(VARIABLE_VALUE_PREFIX)) {
			FactPattern fact = getRuleModel().getBoundFact(attrib);
			ExpressionPart variable;
			if (fact != null) {
				variable = new ExpressionVariable(fact);
			} else {
				//TODO {baunax} fix it!!! to make recursive
				variable = new ExpressionFieldVariable(attrib);
			}
			expression.appendPart(variable);

		} else if (prefix.equals(GLOBAL_VARIABLE_VALUE_PREFIX)) {
			expression.appendPart(getExpressionPartForGlobalVariable(getCompletionEngine(), attrib));
		}
		w = getWidgetForCurrentType();

		if (!expression.isEmpty()) {
			panel.add(createWidgetForExpression("<b>" + expression.getText() + ".</b>"));
		}
		if (w != null) {
			panel.add(w);
		}
		fireExpressionChangeEvent();
		fireExpressionTypeChangeEvent();
	}

	private Widget getWidgetForCurrentType() {
		if (expression.isEmpty()) {
			return createStartPointWidget();
		}
		
		ChangeHandler ch = new ChangeHandler() {
			public void onChange(ChangeEvent event) {
				ListBox box = (ListBox) event.getSource();
				panel.remove(box);
				if (box.getSelectedIndex() > 0) {
					onChangeSelection(box.getValue(box.getSelectedIndex()));
				}
			}
		};
		
		ListBox lb = new ListBox();
		lb.setVisibleItemCount(1);
		lb.addItem(constants.ChooseDotDotDot(), "");
		lb.addItem("<==" + constants.DeleteItem(), DELETE_VALUE);
		for (Map.Entry<String, String> entry : getCompletionsForCurrentType().entrySet()) {
			lb.addItem(entry.getKey(), entry.getValue());
		}
		lb.addChangeHandler(ch);
		return lb;
	}

	private void onCollectionChange(String value) {
		if ("size".contains(value)) {
			expression.appendPart(new ExpressionMethod("size", "int", SuggestionCompletionEngine.TYPE_NUMERIC));
		} else if ("isEmpty".equals(value)) {
			expression.appendPart(new ExpressionMethod("isEmpty", "boolean", SuggestionCompletionEngine.TYPE_BOOLEAN));
		} else {
			ExpressionCollectionIndex collectionIndex;
			String factName = getCompletionEngine().getFactNameFromType(getCurrentParametricType());
			if (getCurrentParametricType() != null && factName != null) {
				collectionIndex = new ExpressionCollectionIndex("get", getCurrentParametricType(), factName);
			} else {
				collectionIndex = new ExpressionCollectionIndex("get", "java.lang.Object",
						SuggestionCompletionEngine.TYPE_OBJECT);
			}
			if ("first".equals(value)) {
				collectionIndex.putParam("index", new ExpressionFormLine(new ExpressionText("0")));
				expression.appendPart(collectionIndex);
			} else if ("last".equals(value)) {
				ExpressionFormLine index = new ExpressionFormLine(expression);
				index.appendPart(new ExpressionMethod("size", "int", SuggestionCompletionEngine.TYPE_NUMERIC));
				index.appendPart(new ExpressionText("-1"));

				collectionIndex.putParam("index", index);
				expression.appendPart(collectionIndex);
			}
		}
	}

	private void onChangeSelection(String value) {
		setModified(true);
		String oldType = getCurrentGenericType();
		String prevFactName = null;
		if (DELETE_VALUE.equals(value)) {
			expression.removeLast();
		} else if (SuggestionCompletionEngine.TYPE_COLLECTION.equals(getCurrentGenericType())) {
			onCollectionChange(value);
		} else if (SuggestionCompletionEngine.TYPE_STRING.equals(getCurrentGenericType())) {
			if ("size".equals(value)) {
				expression.appendPart(new ExpressionMethod("size", "int", SuggestionCompletionEngine.TYPE_NUMERIC));
			} else if ("isEmpty".equals(value)) {
				expression.appendPart(new ExpressionText(".size() == 0", "", SuggestionCompletionEngine.TYPE_NUMERIC));
			}
		} else {
			int dotPos = value.indexOf('.');
			String prefix = value.substring(0, dotPos);
			String attrib = value.substring(dotPos + 1);

			prevFactName = getCompletionEngine().getFactNameFromType(getCurrentClassType());
			// String genericType = SuggestionCompletionEngine.TYPE_OBJECT;
			if (FIElD_VALUE_PREFIX.equals(prefix)) {
				expression.appendPart(getExpressionPartForField(getCompletionEngine(), prevFactName, attrib));
			} else if (METHOD_VALUE_PREFIX.equals(prefix)) {
				expression.appendPart(getExpressionPartForMethod(getCompletionEngine(), prevFactName, attrib));
			}
		}
		Widget w = getWidgetForCurrentType();

		panel.clear();
		if (!expression.isEmpty()) {
			panel.add(createWidgetForExpression("<b>" + expression.getText() + ".</b>"));
		}
		if (w != null) {
			panel.add(w);
		}
		fireExpressionChangeEvent();
		fireExpressionTypeChangeEvent(oldType);
	}

	private Map<String, String> getCompletionsForCurrentType() {
		Map<String, String> completions = new LinkedHashMap<String, String>();

		if (SuggestionCompletionEngine.TYPE_FINAL_OBJECT.equals(getCurrentGenericType())) {
			return completions;
		}

		if (SuggestionCompletionEngine.TYPE_COLLECTION.equals(getCurrentGenericType())) {
			completions.put("size()", "size");
			completions.put("first()", "first");
			completions.put("last()", "last");
			completions.put("isEmpty()", "isEmpty");
			return completions;
		}

		if (SuggestionCompletionEngine.TYPE_STRING.equals(getCurrentGenericType())) {
			completions.put("size()", "size");
			completions.put("isEmpty()", "isEmpty");
			return completions;
		}

		if (SuggestionCompletionEngine.TYPE_BOOLEAN.equals(getCurrentGenericType())
				|| SuggestionCompletionEngine.TYPE_NUMERIC.equals(getCurrentGenericType())
				|| SuggestionCompletionEngine.TYPE_DATE.equals(getCurrentGenericType())
				|| SuggestionCompletionEngine.TYPE_OBJECT.equals(getCurrentGenericType())) {
			return completions;
		}

		String factName = getCompletionEngine().getFactNameFromType(getCurrentClassType());
		if (factName != null) {
			// we currently only support 0 param method calls
			List<String> methodNames = getCompletionEngine().getMethodFullNames(factName, 0);

			for (String field : getCompletionEngine().getFieldCompletions(factName)) {
				boolean changed = false;
				for (Iterator<String> i = methodNames.iterator(); i.hasNext();) {
					String method = i.next();
					if (method.startsWith(field)) {
						completions.put(method, METHOD_VALUE_PREFIX + "." + method);
						i.remove();
						changed = true;
					}
				}
				if (!changed) {
					completions.put(field, FIElD_VALUE_PREFIX + "." + field);
				}
			}
		}
		// else {We don't know anything about this type, so return empty map}
		return completions;
	}

	// private String getCurrentPartName() {
	// return expression.getCurrentName();
	// }

	private RuleModel getRuleModel() {
		return this.getModeller().getModel();
	}

	private SuggestionCompletionEngine getCompletionEngine() {
		return this.getModeller().getSuggestionCompletions();
	}

	private String getCurrentClassType() {
		return expression.getClassType();
	}

	private String getCurrentGenericType() {
		return expression.getGenericType();
	}

	private String getPreviousGenericType() {
		return expression.getPreviousGenericType();
	}

	private String getCurrentParametricType() {
		return expression.getParametricType();
	}

	// private String getPreviousClassType() {
	// return expression.getPreviousType();
	// }
	//
	// private ExpressionPart getRootExpression() {
	// return expression.getRootExpression();
	// }

	@Override
	public boolean isReadOnly() {
		return this.readOnly;
	}

	/**
	 * @see org.drools.guvnor.client.modeldriven.ui.HasExpressionTypeChangeHandlers#addExpressionTypeChangeHandler(org.drools.guvnor.client.modeldriven.ui.ExpressionTypeChangeHandler)
	 */
	public HandlerRegistration addExpressionTypeChangeHandler(ExpressionTypeChangeHandler handler) {
		return addHandler(handler, ExpressionTypeChangeEvent.getType());
	}

	private void fireExpressionChangeEvent() {
		fireEvent(new ExpressionChangeEvent());
	}
	
	private void fireExpressionTypeChangeEvent() {
		fireExpressionTypeChangeEvent(getPreviousGenericType());
	}

	private void fireExpressionTypeChangeEvent(String previousGenericType) {
		String currentGenericType = getCurrentGenericType();
		if ((previousGenericType == null || !previousGenericType.equals(currentGenericType)) 
				|| currentGenericType != null) {
			fireEvent(new ExpressionTypeChangeEvent(previousGenericType, currentGenericType));
		}
	}

	public HandlerRegistration addExpressionChangeHandler(ExpressionChangeHandler handler) {
		return addHandler(handler, ExpressionChangeEvent.getType());
	}
	
	private void showBindingPopUp() {
		final FormStylePopup popup = new FormStylePopup();
		popup.setWidth(500);
		HorizontalPanel vn = new HorizontalPanel();
		final TextBox varName = new TextBox();
		Button ok = new Button(constants.Set());
		vn.add(new Label(constants.BindTheExpressionToAVariable()));
		vn.add(varName);
		vn.add(ok);

		ok.addClickHandler(new ClickHandler() {
			public void onClick(ClickEvent event) {
				String var = varName.getText();
				if (getModeller().isVariableNameUsed(var)) {
					Window.alert(Format.format(constants.TheVariableName0IsAlreadyTaken(), var));
					return;
				}
				expression.setBinding(var);
				getModeller().refreshWidget();
				popup.hide();
			}
		});

		popup.addRow(vn);
		popup.show();
	}
	
	private class SmallLabelClickHandler implements ClickHandler {
		public void onClick(ClickEvent event) {
			showBindingPopUp();
		}
	}
	
	private ClickableLabel createWidgetForExpression(String text) {
		ClickableLabel label = new ClickableLabel(text, slch);
		return label;
	}
}
