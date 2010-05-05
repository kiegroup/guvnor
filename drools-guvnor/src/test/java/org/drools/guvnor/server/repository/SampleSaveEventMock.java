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
