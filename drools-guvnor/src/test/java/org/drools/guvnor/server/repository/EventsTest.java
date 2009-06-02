package org.drools.guvnor.server.repository;

import junit.framework.TestCase;
import org.drools.repository.events.StorageEventManager;
import org.drools.repository.RulesRepository;
import org.drools.repository.PackageItem;
import org.drools.repository.AssetItem;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.client.modeldriven.dt.GuidedDecisionTable;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.guvnor.server.util.BRXMLPersistence;
import org.drools.guvnor.server.util.GuidedDTXMLPersistence;

/**
 * @author Michael Neale
 */
public class EventsTest extends TestCase {
    public void testLoadSave() throws Exception {
        System.setProperty("guvnor.saveEventListener", "org.drools.guvnor.server.repository.SampleSaveEvent");

        ServiceImplementation impl = getService();

        PackageItem pkg = impl.getRulesRepository().createPackage("testLoadSaveEvents", "");
        AssetItem asset = pkg.addAsset("testLoadSaveEvent", "");
        asset.updateFormat(AssetFormats.BUSINESS_RULE);

        RuleModel m = new RuleModel();
        m.name = "mrhoden";


        asset.updateContent(BRXMLPersistence.getInstance().marshal(m));
        asset.checkin("");


        asset = pkg.addAsset("testLoadSaveEventDT", "");
        asset.updateFormat(AssetFormats.DECISION_TABLE_GUIDED);
        GuidedDecisionTable gt = new GuidedDecisionTable();
        asset.updateContent(GuidedDTXMLPersistence.getInstance().marshal(gt));
        asset.checkin("");


        

    }


    private ServiceImplementation getService() throws Exception {
		ServiceImplementation impl = new ServiceImplementation();

		impl.repository = new RulesRepository(TestEnvironmentSessionHelper
				.getSession());
		return impl;
	}
}
