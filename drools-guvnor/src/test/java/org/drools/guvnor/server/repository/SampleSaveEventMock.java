/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.repository;

import org.drools.repository.events.SaveEvent;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.ide.common.client.modeldriven.brl.RuleModel;
import org.drools.ide.common.server.util.BRXMLPersistence;

/**
 * @author Michael Neale
 */
public class SampleSaveEventMock implements SaveEvent {
    public void onAssetCheckin(AssetItem item) {
        if (item.getFormat().equals(AssetFormats.BUSINESS_RULE)) {
            RuleModel m = BRXMLPersistence.getInstance().unmarshal(item.getContent());
            System.err.println(m.name);
        } else if (item.getFormat().equals(AssetFormats.DECISION_TABLE_GUIDED)) {
            System.err.println("Here !");
        }
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onAssetDelete(AssetItem item) {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public void onPackageCreate(PackageItem item) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
