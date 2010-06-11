package org.drools.guvnor.client.modeldriven.ui;

import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.event.shared.HasHandlers;

public interface HasExpressionTypeChangeHandlers extends HasHandlers {

	HandlerRegistration addExpressionTypeChangeHandler(ExpressionTypeChangeHandler handler);

}