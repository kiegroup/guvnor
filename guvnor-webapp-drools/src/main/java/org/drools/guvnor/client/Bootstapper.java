package org.drools.guvnor.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;
import org.drools.guvnor.client.configurations.ConfigurationsLoader;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;

@ApplicationScoped
public class Bootstapper {

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
