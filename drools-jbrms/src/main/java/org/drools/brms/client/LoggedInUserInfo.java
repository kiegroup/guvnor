package org.drools.brms.client;


import org.drools.brms.client.common.ErrorPopup;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTML;

/**
 * Simple widget to show who is logged in, and a link to logout.
 * @author Fernando Meyer
 */
public class LoggedInUserInfo extends Composite{
    private HTML widgetcontent;
    

    public LoggedInUserInfo() {

        widgetcontent = new HTML();
        initWidget( widgetcontent );
    }

    public void setUserName(String userName) {
        StringBuffer content = new StringBuffer();

        content.append( "<div id='user_info'>" );
        content.append( "Welcome: &nbsp;" + userName );
        content.append( "&nbsp;&nbsp;&nbsp;<a href='/drools-jbrms/logout.jsp'>[Sign Out]</a>" );
        content.append( "</div>" );
        widgetcontent.setHTML( content.toString() );
         
        
        //we have the timer to keep the session alive.
        Timer timer = new Timer() {

            public void run() {
                RepositoryServiceFactory.getSecurityService().getCurrentUser( new AsyncCallback() {

                    public void onFailure(Throwable t) {
                    }

                    public void onSuccess(Object o) {
                        if (o == null) {
                            ErrorPopup.showMessage( "Your session has expired. Please press the 'Sign out' link so you can re-login." );
                        }
                    }
                    
                });
            }
            
        };
        
        timer.scheduleRepeating( 300000 );
        
        
    }
    
}