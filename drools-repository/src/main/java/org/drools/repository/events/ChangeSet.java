package org.drools.repository.events;

import java.util.ArrayList;
import java.util.Iterator;


public class ChangeSet {

    private ArrayList assetChanges;
    
    public ChangeSet() {
        assetChanges = new ArrayList();
    }
    
    public void addChange(AssetChange change) {
        this.assetChanges.add( change );
    }
    
    public Iterator getChanges() {
        return this.assetChanges.iterator();
    }
    
    
}
