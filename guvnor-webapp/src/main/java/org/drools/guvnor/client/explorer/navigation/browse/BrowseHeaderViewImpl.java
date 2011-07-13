/*
 * Copyright 2011 JBoss Inc
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 */

package org.drools.guvnor.client.explorer.navigation.browse;

import com.google.gwt.core.client.GWT;
import org.drools.guvnor.client.common.StackItemHeaderViewImpl;
import org.drools.guvnor.client.messages.Constants;
import org.drools.guvnor.client.resources.Images;

public class BrowseHeaderViewImpl extends StackItemHeaderViewImpl implements BrowseHeaderView {

    private static Constants constants = GWT.create(Constants.class);
    private static Images images = GWT.create(Images.class);

    public BrowseHeaderViewImpl() {
        setText(constants.Browse());
        setImageResource(images.ruleAsset());
    }

}
