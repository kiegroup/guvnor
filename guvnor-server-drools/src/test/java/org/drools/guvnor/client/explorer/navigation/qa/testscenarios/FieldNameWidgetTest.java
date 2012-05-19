package org.drools.guvnor.client.explorer.navigation.qa.testscenarios;

import org.drools.ide.common.client.modeldriven.SuggestionCompletionEngine;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;


public class FieldNameWidgetTest {


    private FieldNameWidgetView view;
    private FieldNameWidgetView.Presenter presenter;
    private SuggestionCompletionEngine suggestionCompletionEngine;


    @Before
    public void setUp() throws Exception {
        view = mock(FieldNameWidgetView.class);
        suggestionCompletionEngine = mock(SuggestionCompletionEngine.class);
        presenter = new FieldNameWidget("fieldName", suggestionCompletionEngine, view);
    }

    @Test
    public void testSetPresenter() throws Exception {
        verify(view).setPresenter(presenter);
    }

    @Test
    public void testTitleIsSet() throws Exception {
        verify(view).setTitle("fieldName");
    }

    // TODO: Test the chain person.address.street.number and so on

//    @Test
//    public void testAddNewFieldsOnClick() throws Exception {
//
//        modelFieldNames.add("age");
//        modelFieldNames.add("name");
//
//        presenter.onClick();
//
//        verify(view).openNewFieldSelector();
//    }
}
