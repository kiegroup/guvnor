package org.drools.guvnor.client.util;

import com.google.gwt.user.client.ui.IsWidget;

public interface TabbedPanel {

    public void addTab( final String tabTitle,
                        IsWidget widget,
                        final String token );

    boolean contains( String key );

    void show( String key );

    void close( String key );
}
