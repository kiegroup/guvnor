package org.drools.brms.client.common;


import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

public class LoginInfoWidget extends Composite{
    private HTML widgetcontent;
    private StringBuffer content; 
    
    public LoginInfoWidget () {
        content = new StringBuffer();
        initWidgets();
    }

    private void initWidgets() {
        content.append( "<div id=" + "user_info"+ ">" );
        content.append( "Howdy, Fernando Meyer " );
        content.append( "<a href='/drools-jbrms/logout.jsp'>[Sign Out]</a>" );
        content.append( "</div>" );

        widgetcontent = new HTML( content.toString() );
        initWidget( widgetcontent );
    }
    
}
