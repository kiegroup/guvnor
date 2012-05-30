package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;

public class FieldNameWidget implements FieldNameWidgetView.Presenter {

    private final FieldNameWidgetView view;


    public FieldNameWidget(String fieldName,
                           SuggestionCompletionEngine suggestionCompletionEngine,
                           FieldNameWidgetView view) {
        this.view = view;
        this.view.setPresenter(this);
        this.view.setTitle("fieldName");
    }

    @Override
    public void onClick() {
        view.openNewFieldSelector();
    }
}
