package org.drools.brms.client;

import org.drools.brms.client.common.FormStyleLayout;
import org.drools.brms.client.common.GenericCallback;
import org.drools.brms.client.common.LoadingPopup;
import org.drools.brms.client.rpc.RepositoryServiceFactory;

import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.DeferredCommand;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ClickListener;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.PasswordTextBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.Widget;

/**
 * Used for logging in, obviously !
 * 
 * @author Michael Neale
 */
public class LoginWidget extends Composite {

    private FormStyleLayout layout;
    private TextBox userName;
    private Command loggedInEvent;

    
    public LoginWidget() {
        layout = new FormStyleLayout("images/login.gif", "Please enter your details");
        
        userName = new TextBox();
        userName.setTabIndex( 1 );
        layout.addAttribute( "User name:", userName );
        
        final PasswordTextBox password = new PasswordTextBox();
        password.setTabIndex( 2 );
        layout.addAttribute( "Password:", password );
        
        Button login = new Button("Login");
        login.setTabIndex( 3 );
        layout.addAttribute( "", login );
        
        login.addClickListener( new ClickListener() {
            public void onClick(Widget w) {
                LoadingPopup.showMessage( "Logging in..." );
                
                DeferredCommand.add( new Command() {
                    public void execute() {
                        doLogin( loggedInEvent, userName, password );
                    }
                });
            }

        });
        
        
        
        initWidget( layout );
        
        userName.setFocus( true );
        
        setStyleName( "login-Form" );
    }
    
    private void doLogin(final Command loggedInEvent, final TextBox userName, final PasswordTextBox password) {
        RepositoryServiceFactory.login( userName.getText(), password.getText(), new GenericCallback() {
            public void onSuccess(Object o) {
                LoadingPopup.close();
                Boolean success = (Boolean) o;
                if (!success.booleanValue()) {
                    Window.alert( "Incorrect username or password." );
                } else {
                    loggedInEvent.execute();
                }
            }
        });
    }
    
    
    /**
     * Return the name that was entered.
     */
    public String getUserName() {
        return userName.getText();
    }

    /**
     * This is required for a callback after a successful login.
     */
    public void setLoggedInEvent(Command loggedInEvent) {
        this.loggedInEvent = loggedInEvent;
    }
    
}
