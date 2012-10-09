package org.drools.guvnor.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;
import org.drools.guvnor.client.configurations.ConfigurationsLoader;
import org.jboss.errai.bus.client.api.RemoteCallback;
import org.jboss.errai.ioc.client.api.Caller;
import org.uberfire.backend.FileExplorerRootService;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.util.Collection;

@ApplicationScoped
public class Bootstapper {

    @Inject
    private WorkbenchMenuBarPresenter menubar;
    
    @PostConstruct
    public void init() {
        ConfigurationsLoader.loadPreferences(new Command() {
            public void execute() {
//                loadUserCapabilities("userName");
            }
        });
        ConfigurationsLoader.loadUserCapabilities(new Command() {
            public void execute() {
//                setUpMain(userName);
            }
        });

        menubar. addMenuItem(new DefaultMenuItemCommand("Guvnor",new org.uberfire.client.mvp.Command() {
            @Override
            public void execute() {

            }
        }));
        
        hideLoadingPopup();
    }

    /*
     * Fade out the "Loading application" pop-up
     */
    private void hideLoadingPopup() {
        final Element loadingElement = RootPanel.get("loading").getElement();

        Animation animation = new Animation() {

            @Override
            protected void onUpdate(double progress) {
                loadingElement.getStyle().setOpacity(1.0 - progress);
            }

            @Override
            protected void onComplete() {
                loadingElement.getStyle().setVisibility(Style.Visibility.HIDDEN);
            }

        };

        animation.run(500);

    }
}
