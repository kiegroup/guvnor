package org.kie.guvnor.testscenario.client.resources.images;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;

public interface TestScenarioImages
        extends ClientBundle {

    public static TestScenarioImages INSTANCE = GWT.create(TestScenarioImages.class);

    @Source("images/rule_asset.gif")
    public ImageResource RuleAsset();

    @Source("images/add_field_to_fact.gif")
    ImageResource addFieldToFact();

    @Source("images/new_wiz.gif")
    ImageResource newWiz();

    @Source("images/execution_trace.gif")
    ImageResource executionTrace();

    @Source("images/test_passed.png")
    ImageResource testPassed();
}
