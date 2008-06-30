package org.drools.repository;

import java.util.Iterator;

import javax.jcr.Node;
import javax.jcr.RepositoryException;
import javax.jcr.version.Version;
import javax.jcr.version.VersionIterator;

/**
 * A lazy iterator for walking back through history.
 * Wraps the version iterator from JCR and allows skipping.
 *
 * @author Michael Neale
 *
 */
public class AssetHistoryIterator
    implements
    Iterator<AssetItem> {

    private Node head;
    private VersionIterator versionIterator;
    private RulesRepository repo;

    public AssetHistoryIterator(RulesRepository repo, Node head) {
        this.head = head;
        this.repo = repo;
        try {
            this.versionIterator =  this.head.getVersionHistory().getAllVersions();
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }

    public boolean hasNext() {
        return versionIterator.hasNext();
    }

    public AssetItem next() {
        return new AssetItem(this.repo, (Version) versionIterator.next());

    }

    /**
     * You can't do this with this sort of iterator.
     * It makes no sense to remove a history item.
     * Removing history is a administrative function only (and in
     * any case, it may have to be archived for legal reasons).
     *
     * @throws UnsupportedOperationException when called.
     */
    public void remove() {
       throw new UnsupportedOperationException();
    }

    /**
     * Skip the specified number of items. As this is a lazy iterator this
     * means less work in pulling it from the database etc.
     */
    public void skip(int i) {
        this.versionIterator.skip( i );
    }

}
