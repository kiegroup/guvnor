package org.drools.repository;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

/**
 * This wraps a node iterator, and provides RulePackageItems when requested.
 * This supports lazy loading if needed.
 */
public class RulePackageIterator
    implements
    Iterator {
    
    
    
    private NodeIterator packageNodeIterator;
    private RulesRepository repository;

    public RulePackageIterator(RulesRepository repository, NodeIterator packageNodes) {
        this.packageNodeIterator = packageNodes;
    }

    public boolean hasNext() {
        return this.packageNodeIterator.hasNext();
    }

    public Object next() {        
        return new PackageItem(this.repository, (Node) this.packageNodeIterator.next());
    }

    public void remove() {
        this.packageNodeIterator.remove();
    }

}
