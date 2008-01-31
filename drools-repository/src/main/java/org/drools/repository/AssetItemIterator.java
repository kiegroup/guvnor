package org.drools.repository;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.NodeIterator;

/**
 * This iterates over nodes and produces RuleItem's.
 * Also allows "skipping" of results to jump to certain items,
 * as per JCRs "skip".
 *
 * JCR iterators are/can be lazy, so this makes the most of it for large
 * numbers of assets.
 *
 * @author Michael Neale
 */
public class AssetItemIterator
    implements
    Iterator {

    private NodeIterator    it;
    private RulesRepository rulesRepository;

    public AssetItemIterator(NodeIterator nodes,
                            RulesRepository repo) {
        this.it = nodes;
        this.rulesRepository = repo;
    }

    public boolean hasNext() {
        return it.hasNext();
    }

    public Object next() {
        return new AssetItem( rulesRepository,
                             (Node) it.next() );
    }

    public void remove() {
        throw new UnsupportedOperationException( "You can't remove a rule this way." );
    }

    /**
     * @param i The number of rules to skip.
     */
    public void skip(int i) {
        it.skip( i );
    }

    /**
     * @return the size of the underlying iterator's potential data set.
     * May be -1 if not known.
     */
    public long getSize() {
    	return it.getSize();
    }

}
