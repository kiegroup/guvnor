package org.drools.repository;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

/**
 * This wraps a node iterator, and provides PackageItems when requested.
 * This supports lazy loading if needed.
 */
public class PackageIterator
    implements
    Iterator {
    
    private final NodeIterator packageNodeIterator;
    private final RulesRepository repository;

    public PackageIterator(RulesRepository repository, NodeIterator packageNodes) {
        this.packageNodeIterator = packageNodes;
        this.repository = repository;
    }

    public boolean hasNext() {
        return this.packageNodeIterator.hasNext();
    }

    public Object next() {        
        return new PackageItem(this.repository, (Node) this.packageNodeIterator.next());
    }

    public void remove() {
        throw new UnsupportedOperationException("You can not remove items this way.");
    }

}
