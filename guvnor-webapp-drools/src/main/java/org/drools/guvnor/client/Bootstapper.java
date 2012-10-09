package org.drools.guvnor.client;

import com.google.gwt.animation.client.Animation;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.Style;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.RootPanel;
import org.drools.guvnor.client.configurations.ConfigurationsLoader;
import org.drools.guvnor.client.resources.*;
import org.drools.guvnor.client.resources.decisiontable.DecisionTableResources;
import org.drools.guvnor.client.simulation.resources.SimulationResources;
import org.uberfire.client.workbench.widgets.menu.WorkbenchMenuBarPresenter;
import org.uberfire.client.workbench.widgets.menu.impl.DefaultMenuItemCommand;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

@ApplicationScoped
public class Bootstapper {

    @Inject
    private WorkbenchMenuBarPresenter menubar;

    @PostConstruct
    public void init() {
        loadStyles();

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

        menubar.addMenuItem(new DefaultMenuItemCommand("Guvnor", new org.uberfire.client.mvp.Command() {
            @Override
            public void execute() {

            }
        }));

        hideLoadingPopup();
    }

    private void loadStyles() {
        GuvnorResources.INSTANCE.headerCss().ensureInjected();
        GuvnorResources.INSTANCE.guvnorCss().ensureInjected();
        DroolsGuvnorResources.INSTANCE.titledTextCellCss().ensureInjected();
        GuvnorResources.INSTANCE.guvnorCss().ensureInjected();
        DroolsGuvnorResources.INSTANCE.droolsGuvnorCss().ensureInjected();
        RoundedCornersResource.INSTANCE.roundCornersCss().ensureInjected();
        OperatorsResource.INSTANCE.operatorsCss().ensureInjected();
        WizardCellListResources.INSTANCE.cellListStyle().ensureInjected();
        WizardResources.INSTANCE.style().ensureInjected();
        DecisionTableResources.INSTANCE.style().ensureInjected();
        SimulationResources.INSTANCE.style().ensureInjected();
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
