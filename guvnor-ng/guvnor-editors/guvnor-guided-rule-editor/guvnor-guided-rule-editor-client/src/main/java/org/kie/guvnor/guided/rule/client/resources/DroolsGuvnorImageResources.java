/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.kie.guvnor.guided.rule.client.resources;

import com.google.gwt.core.client.GWT;
import com.google.gwt.resources.client.ClientBundle;
import com.google.gwt.resources.client.ImageResource;
import org.kie.guvnor.commons.ui.client.resources.ItemImages;

public interface DroolsGuvnorImageResources
        extends
        ClientBundle {

    DroolsGuvnorImageResources INSTANCE = GWT.create( DroolsGuvnorImageResources.class );

    @Source("images/function_assets.gif")
    ImageResource functionAssets();

    @Source("images/error.gif")
    ImageResource error();

    @Source("images/config.png")
    ImageResource config();

    ItemImages itemImages();

    @Source("images/edit.gif")
    ImageResource edit();

    @Source("images/editDisabled.gif")
    ImageResource editDisabled();

    @Source("images/add_field_to_fact.gif")
    ImageResource addFieldToFact();

    @Source("images/add_connective.gif")
    ImageResource addConnective();

    @Source("images/new_item_below.png")
    ImageResource newItemBelow();

    @Source("images/shuffle_down.gif")
    ImageResource shuffleDown();

    @Source("images/shuffle_up.gif")
    ImageResource shuffleUp();

    @Source("images/warning.gif")
    ImageResource warning();

    @Source("images/new_wiz.gif")
    ImageResource newWiz();

    @Source("images/field.gif")
    ImageResource field();

    @Source("images/fact.gif")
    ImageResource fact();

    @Source("images/guidedRuleIcon.gif")
    ImageResource guidedRuleIcon();

    @Source("images/guidedRuleTemplateIcon.gif")
    ImageResource guidedRuleTemplateIcon();

}
