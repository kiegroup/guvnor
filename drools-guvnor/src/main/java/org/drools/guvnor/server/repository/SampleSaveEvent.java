package org.drools.guvnor.server.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.drools.repository.events.SaveEvent;
import org.drools.repository.AssetItem;
import org.drools.repository.CategoryItem;
import org.drools.repository.PackageItem;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.modeldriven.brl.RuleAttribute;
import org.drools.guvnor.client.modeldriven.brl.RuleModel;
import org.drools.guvnor.server.util.BRDRLPersistence;
import org.drools.guvnor.server.util.BRXMLPersistence;

/**
 * @author Michael Neale
 */
public class SampleSaveEvent implements SaveEvent {
    public void onAssetCheckin(AssetItem item) {
        if (item.getFormat().equals(AssetFormats.BUSINESS_RULE)) {
            RuleModel rm = BRXMLPersistence.getInstance().unmarshal(item.getContent(true));
            //StevePersist(item, drl, xml)    <status, uuid, username, categories>
            //StevePersist(guid, hm); 
            HashMap dataToPersist = new HashMap();
            dataToPersist.put("uuid", item.getUUID());
            dataToPersist.put("type", "BRL");
            dataToPersist.put("status", item.getStateDescription());
            dataToPersist.put("lastmodifiedby", item.getLastContributor());
            dataToPersist.put("lastmodifieddate", item.getLastModified());
            List<CategoryItem> lstCategoryItem = item.getCategories();
            List<String> lstCategoryName = new ArrayList<String>();
            for(int i=0;i<lstCategoryItem.size();i++){
            	CategoryItem ci = lstCategoryItem.get(i);
            	lstCategoryName.add(ci.getName());
            }
            //List of Strings
            dataToPersist.put("category", lstCategoryName);
            if (rm.attributes.length > 0) {
				HashMap attributesToPersist = new HashMap();
				for (int att = 0; att < rm.attributes.length; att++) {
					attributesToPersist.put(rm.attributes[att].attributeName,rm.attributes[att].value);
				}
				//Hash of Strings
				dataToPersist.put("attributes", attributesToPersist);
			}
            
            if (rm.metadataList.length > 0) {
				HashMap metadataToPersist = new HashMap();
				for (int meta = 0; meta < rm.metadataList.length; meta++) {
					metadataToPersist.put(rm.metadataList[meta].attributeName,rm.metadataList[meta].value);
				}
				//Hash of Strings
				dataToPersist.put("metadata", metadataToPersist);
			}
            
            dataToPersist.put("status", item.getStateDescription());
            dataToPersist.put("fullxml", item.getContent(true));
            dataToPersist.put("fulldrl", BRDRLPersistence.getInstance().marshal(rm));
            //xml = SteveLoad(guid)
            //list guids = SteveSyncUp()
            //BRXMLPersistence.getInstance().marshal(data)
            System.err.println(item.getUUID());
            System.err.println(item.getStateDescription());
            System.err.println(item.getLastContributor());
            System.err.println(item.getLastModified());
            
            
            
            //TODO getContent calls load, need to flag it off
            
            System.err.println(item.getContent());
            System.err.println(BRDRLPersistence.getInstance().marshal(rm));
            //or use ... item.getContent()
        } else if (item.getFormat().equals(AssetFormats.DECISION_TABLE_GUIDED)) {
            System.err.println(item.getContent());
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
