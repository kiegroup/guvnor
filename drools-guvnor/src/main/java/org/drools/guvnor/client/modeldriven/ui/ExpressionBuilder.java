package org.drools.guvnor.client.modeldriven.ui;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.modeldriven.MethodInfo;
import org.drools.guvnor.client.modeldriven.SuggestionCompletionEngine;
import org.drools.guvnor.client.modeldriven.brl.ExpressionCollection;
import org.drools.guvnor.client.modeldriven.brl.ExpressionCollectionIndex;
import org.drools.guvnor.client.modeldriven.brl.ExpressionField;
import org.drools.guvnor.client.modeldriven.brl.ExpressionFormLine;
import org.drools.guvnor.client.modeldriven.brl.ExpressionGlobalVariable;
import org.drools.guvnor.client.modeldriven.brl.ExpressionMethod;
import org.drools.guvnor.client.modeldriven.brl.ExpressionPart;
import org.drools.guvnor.client.modeldriven.brl.ExpressionText;
import org.drools.guvnor.client.modeldriven.brl.ExpressionVariable;
import org.drools.guvnor.client.modeldriven.brl.FactPattern;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.ChangeListener;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Widget;
import com.gwtext.client.widgets.form.Label;
import org.drools.guvnor.client.common.SmallLabel;

public class ExpressionBuilder extends RuleModellerWidget {

    private static final String DELETE_VALUE = "_delete_";
    private static final String FIElD_VALUE_PREFIX = "fl";
    private static final String VARIABLE_VALUE_PREFIX = "va";
    private static final String GLOBAL_COLLECTION_VALUE_PREFIX = "gc";
    private static final String GLOBAL_VARIABLE_VALUE_PREFIX = "gv";
    private static final String METHOD_VALUE_PREFIX = "mt";
    private Constants constants = ((Constants) GWT.create(Constants.class));
    //private FlowPanel panel = new FlowPanel();
    private HorizontalPanel panel = new HorizontalPanel();
    private RuleModeller modeller;
    private ExpressionFormLine expression;
    private boolean readOnly;

    public ExpressionBuilder(RuleModeller modeller,
            ExpressionFormLine expression) {
        this(modeller, expression, false);
    }

    public ExpressionBuilder(RuleModeller modeller,
            ExpressionFormLine expression, Boolean readOnly) {
        super();

        if (readOnly == null){
            this.readOnly = !modeller.getSuggestionCompletions().containsFactType(modeller.getSuggestionCompletions().getFactNameFromType(this.expression.getRootExpression().getClassType()));
        }else{
            this.readOnly = readOnly;
        }

        this.expression = expression;
        this.modeller = modeller;
        if (expression == null || expression.getText().length() == 0) {
            if (this.readOnly) {
                panel.add(new SmallLabel("<b>-</b"));
            } else {
                panel.add(createStartPointWidget());
            }
        } else {
            if (this.readOnly) {
                panel.add(new SmallLabel("<b>" + expression.getText() + "</b"));
            } else {
                panel.add(new SmallLabel("<b>" + expression.getText() + ".</b"));
                panel.add(getWidgetForCurrentType());
            }
        }
        initWidget(panel);
    }

    private Widget createStartPointWidget() {
        ListBox startPoint = new ListBox();
        panel.add(startPoint);

        startPoint.addItem(constants.ChooseDotDotDot(), "");

        //TODO {baunax} uncomment when global collections is implemented.
//		for (String gc : getCompletionEngine().getGlobalCollections()) {
//			startPoint.addItem(gc, GLOBAL_COLLECTION_VALUE_PREFIX + "." + gc);
//		}

        for (String gv : getCompletionEngine().getGlobalVariables()) {
            startPoint.addItem(gv, GLOBAL_VARIABLE_VALUE_PREFIX + "." + gv);
        }

        for (String v : getRuleModel().getBoundFacts()) {
            startPoint.addItem(v, VARIABLE_VALUE_PREFIX + "." + v);
        }

        startPoint.setVisibleItemCount(1);
        startPoint.addChangeListener(new ChangeListener() {

            public void onChange(Widget sender) {
                ListBox lb = (ListBox) sender;
                int index = lb.getSelectedIndex();
                if (index > 0) {
                    ExpressionBuilder.this.makeDirty();
                    startPointChange(lb.getValue(index));
                }
            }
        });
        return startPoint;
    }

    private void startPointChange(String value) {
        panel.clear();
        Widget w;
        int dotPos = value.indexOf('.');
        String prefix = value.substring(0, dotPos);
        String attrib = value.substring(dotPos + 1);
        if (prefix.equals(VARIABLE_VALUE_PREFIX)) {
            FactPattern fact = getRuleModel().getBoundFact(attrib);
            ExpressionVariable variable = new ExpressionVariable(fact);
            expression.appendPart(variable);

        } else if (prefix.equals(GLOBAL_VARIABLE_VALUE_PREFIX)) {
            String globalVarType = getCompletionEngine().getGlobalVariable(attrib);
            ExpressionGlobalVariable variable = new ExpressionGlobalVariable(attrib, globalVarType, globalVarType);
            expression.appendPart(variable);
        }
        w = getWidgetForCurrentType();

        if (!expression.isEmpty()) {
            panel.add(new Label(expression.getText()));
        }
        if (w != null) {
            panel.add(w);
        }

        // panel.add(getWidgetFor(startPoint.getValue(index)));
        // ExpressionBuilder.this.expression.appendText(v);
    }

    private Widget getWidgetForCurrentType() {
        if (expression.isEmpty()) {
            return createStartPointWidget();
        }
        String factName = getCompletionEngine().getFactNameFromType(
                getCurrentClassType());
        if (factName != null) {
            ListBox lb = new ListBox();
            lb.setVisibleItemCount(1);
            lb.addItem(constants.ChooseDotDotDot(), "");
            lb.addItem("<==" + constants.DeleteItem(), DELETE_VALUE);
            for (Map.Entry<String, String> entry : getCompletionsForCurrentType().entrySet()) {
                lb.addItem(entry.getKey(), entry.getValue());
            }

            lb.addChangeListener(new ChangeListener() {

                public void onChange(Widget sender) {
                    ListBox box = (ListBox) sender;
                    panel.remove(box);
                    if (box.getSelectedIndex() > 0) {
                        onChangeSelection(box.getValue(box.getSelectedIndex()));
                    }
                }
            });
            return lb;
        } else {//if (SuggestionCompletionEngine.TYPE_COLLECTION.equals(getCurrentGenericType())) {
            ListBox lb = new ListBox();
            lb.setVisibleItemCount(1);
            lb.addItem(constants.ChooseDotDotDot(), "");
            lb.addItem("<==" + constants.DeleteItem(), DELETE_VALUE);
            for (Map.Entry<String, String> entry : getCompletionsForCurrentType().entrySet()) {
                lb.addItem(entry.getKey(), entry.getValue());
            }
            lb.addChangeListener(new ChangeListener() {

                public void onChange(Widget sender) {
                    ListBox box = (ListBox) sender;
                    panel.remove(box);
                    if (box.getSelectedIndex() > 0) {
                        onChangeSelection(box.getValue(box.getSelectedIndex()));
                    }
                }
            });
            return lb;
        }
//		return null;
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
                collectionIndex = new ExpressionCollectionIndex("get", "java.lang.Object", SuggestionCompletionEngine.TYPE_OBJECT);
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

            prevFactName = getCompletionEngine().getFactNameFromType(
                    getCurrentClassType());
//			String genericType = SuggestionCompletionEngine.TYPE_OBJECT;
            if (FIElD_VALUE_PREFIX.equals(prefix)) {
                String fieldClassName = getCompletionEngine().getFieldClassName(prevFactName, attrib);
                String fieldGenericType = getCompletionEngine().getFieldType(prevFactName, attrib);
                if (SuggestionCompletionEngine.TYPE_COLLECTION.equals(fieldGenericType)) {
                    String fieldParametricType = getCompletionEngine().getParametricFieldType(prevFactName, attrib);
                    expression.appendPart(new ExpressionCollection(attrib, fieldClassName, fieldGenericType, fieldParametricType));
                } else {
                    expression.appendPart(new ExpressionField(attrib, fieldClassName, fieldGenericType));
                }
            } else if (METHOD_VALUE_PREFIX.equals(prefix)) {
                MethodInfo mi = getCompletionEngine().getMethodinfo(prevFactName, attrib);
                if (SuggestionCompletionEngine.TYPE_COLLECTION.equals(mi.getGenericType())) {
                    expression.appendPart(new ExpressionCollection(attrib, mi.getReturnClassType(), mi.getGenericType(), mi.getParametricReturnType()));
                } else {
                    expression.appendPart(new ExpressionMethod(mi.getName(), mi.getReturnClassType(), mi.getGenericType()));
                }
            }
        }
        Widget w = getWidgetForCurrentType();

        panel.clear();
        if (!expression.isEmpty()) {
            panel.add(new Label(expression.getText()));
        }
        if (w != null) {
            panel.add(w);
        }
    }

    private Map<String, String> getCompletionsForCurrentType() {
        Map<String, String> completions = new LinkedHashMap<String, String>();

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
            //we currently only support 0 param method calls
            List<String> methodNames = getCompletionEngine().getMethodFullNames(factName, 0);

            for (String field : getCompletionEngine().getFieldCompletions(
                    factName)) {
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
        //else {We don't know anything about this type, so return empty map}
        return completions;
    }

    private String getCurrentPartName() {
        return expression.getCurrentName();
    }

    private RuleModel getRuleModel() {
        return modeller.getModel();
    }

    private SuggestionCompletionEngine getCompletionEngine() {
        return modeller.getSuggestionCompletions();
    }

    private String getCurrentClassType() {
        return expression.getClassType();
    }

    private String getCurrentGenericType() {
        return expression.getGenericType();
    }

    private String getCurrentParametricType() {
        return expression.getParametricType();
    }

    private String getPreviousClassType() {
        return expression.getPreviousType();
    }

    private ExpressionPart getRootExpression() {
        return expression.getRootExpression();
    }
    
    @Override
    public boolean isReadOnly() {
        return this.readOnly;
    }
}
