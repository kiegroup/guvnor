package org.drools.guvnor.client.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.safehtml.client.SafeHtmlTemplates;
import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.user.client.ui.HTML;

public class SmallLabel extends HTML {

    interface SmallLabelTemplate
        extends
        SafeHtmlTemplates {

        @Template("<div class='form-field'>{0}</div>")
        SafeHtml message(SafeHtml message);
    }

    private static final SmallLabelTemplate TEMPLATE = GWT.create( SmallLabelTemplate.class );

    public SmallLabel() {
    }

    public SmallLabel(String text) {
        setText( text );
    }

    public void setText(final String text) {
        setHTML( TEMPLATE.message( new SafeHtml() {

            private static final long serialVersionUID = 510L;

            public String asString() {
                return text;
            }
        } ) );
    }
}
