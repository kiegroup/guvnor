package org.drools.repository;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.ItemNotFoundException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFormatException;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

/**
 * This contains logic for categorisable items
 * (not all versionably items are categorisable).
 *
 * @author michael neale
 *
 */
public abstract class CategorisableItem extends VersionableItem {

    public CategorisableItem(RulesRepository rulesRepository,
                             Node node) {
        super( rulesRepository,
               node );
    }

    /**
     * Adds the specified tag to this object's node. Tags are stored as nodes in a tag area of
     * the repository. If the specified tag does not already have a corresponding node, a node is
     * created for it.
     *
     * Please note that this is mainly intended for rule related assets, not packages
     * (although it could be used).
     *
     * @param tag the tag to add to the rule. rules can have multiple tags
     * @throws RulesRepositoryException
     */
    public void addCategory(String tag) throws RulesRepositoryException {
        try {
            //make sure this object's node is the head version
            checkIsUpdateable();

            CategoryItem tagItem = this.rulesRepository.loadCategory( tag );

            //now set the tag property of the rule
            Property tagReferenceProperty;
            int i = 0;
            Value[] newTagValues = null;
            try {
                tagReferenceProperty = this.node.getProperty( CATEGORY_PROPERTY_NAME );
                Value[] oldTagValues = tagReferenceProperty.getValues();

                //first, make sure this tag wasn't already there. while we're at it, lets copy the array
                newTagValues = new Value[oldTagValues.length + 1];
                for ( i = 0; i < oldTagValues.length; i++ ) {
                    if ( oldTagValues[i].getString().equals( tag ) ) {
                        log.info( "tag '" + tag + "' already existed for rule node: " + this.node.getName() );
                        return;
                    }
                    newTagValues[i] = oldTagValues[i];
                }
            } catch ( PathNotFoundException e ) {
                //the property doesn't exist yet, so create it in the finally block
                newTagValues = new Value[1];
            } finally {
                if ( newTagValues != null ) {
                    newTagValues[i] = this.node.getSession().getValueFactory().createValue( tagItem.getNode() );
                    updateCategories( newTagValues );
                } else {
                    log.error( "reached expected path of execution when adding tag '" + tag + "' to ruleNode: " + this.node.getName() );
                }
            }
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    private void updateCategories(Value[] newTagValues) throws UnsupportedRepositoryOperationException,
                                                       LockException,
                                                       RepositoryException,
                                                       ValueFormatException,
                                                       VersionException,
                                                       ConstraintViolationException {
        this.node.checkout();
        this.node.setProperty( CATEGORY_PROPERTY_NAME,
                               newTagValues );
    }

    /**
     * This method sets the categories in one hit, making the
     * ASSUMPTION that the categories were previously set up !
     * (via CategoryItem of course !).
     */
    public void updateCategoryList(String[] categories) {
        this.checkIsUpdateable();
        try {
            Value[] newCats = new Value[categories.length];
            for ( int i = 0; i < categories.length; i++ ) {
                CategoryItem item = this.rulesRepository.loadCategory( categories[i] );

                newCats[i] = this.node.getSession().getValueFactory().createValue( item.getNode() );

            }
            updateCategories( newCats );
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException(e);
        }
    }

    /**
     * Gets a list of CategoryItem objects for this assets node.
     *
     * @return a list of TagItem objects for each tag on the rule. If there are no tags, an empty list.
     * @throws RulesRepositoryException
     */
    public List<CategoryItem> getCategories() throws RulesRepositoryException {
    	final List<CategoryItem> cats = new ArrayList<CategoryItem>();
        doList(new Accum() {
			public void add(CategoryItem c) {
				cats.add(c);
			}
        });
        return cats;
    }

	private void doList(Accum ac) {
		try {
            Node ruleNode = getVersionContentNode();
            try {
                Property tagReferenceProperty = ruleNode.getProperty( CATEGORY_PROPERTY_NAME );
                Value[] tagValues = tagReferenceProperty.getValues();
                for ( int i = 0; i < tagValues.length; i++ ) {
                    try {
                        Node tagNode = this.node.getSession().getNodeByUUID( tagValues[i].getString() );
                        CategoryItem tagItem = new CategoryItem( this.rulesRepository,
                                                                 tagNode );
                        ac.add(tagItem);

                    } catch (ItemNotFoundException e) {
                        //ignore
                        log.debug( "Was unable to load a category by UUID - must have been removed." );
                    }
                }
            } catch ( PathNotFoundException e ) {
                //the property doesn't even exist yet, so just return nothing
            }
        } catch ( RepositoryException e ) {
            log.error( "Error loading cateories", e );
            throw new RulesRepositoryException( e );
        }
	}

    /**
     * This will show a summary list of categories.
     */
    public String getCategorySummary() {
    	final StringBuilder sum = new StringBuilder();
    	doList(new Accum() {
    		int count = 0;
			public void add(CategoryItem c) {
				count++;
				if (count == 4) {
					sum.append("...");
				} else if (count < 4){
					sum.append(c.getName());
					sum.append(' ');
				}
			}
    	});
    	return sum.toString();
    }

    static interface Accum {
    	void add(CategoryItem c);
    }

    /**
     * Removes the specified tag from this object's rule node.
     *
     * @param tag the tag to remove from the rule
     * @throws RulesRepositoryException
     */
    public void removeCategory(String tag) throws RulesRepositoryException {
    	removeCategory(this.node, tag);
    }
    
    /**
     * Removes the specified tag from this object's rule node.
     *
     * @param targetNode the node from which the tag is to be removed.
     * @param tag the tag to remove from the rule
     * @throws RulesRepositoryException
     */
    public static void removeCategory(Node targetNode, String tag) throws RulesRepositoryException {
        try {
            //make sure this object's node is the head version
            if (targetNode.getPrimaryNodeType().getName().equals( "nt:version" ) ) {
                String message = "Error. Tags can only be removed from the head version of a rule node";
                log.error( message );
                throw new RulesRepositoryException( message );
            }

            //now set the tag property of the rule
            Property tagReferenceProperty;
            int i = 0;
            int j = 0;
            Value[] newTagValues = null;
            try {
                tagReferenceProperty = targetNode.getProperty( CATEGORY_PROPERTY_NAME );
                Value[] oldTagValues = tagReferenceProperty.getValues();

                //see if the tag was even there
                boolean wasThere = false;
                for ( i = 0; i < oldTagValues.length; i++ ) {
                    Node tagNode = targetNode.getSession().getNodeByUUID( oldTagValues[i].getString() );
                    if ( tagNode.getName().equals( tag ) ) {
                        wasThere = true;
                    }
                }

                if ( wasThere ) {
                    //copy the array, minus the specified tag
                    newTagValues = new Value[oldTagValues.length + 1];
                    for ( i = 0; i < oldTagValues.length; i++ ) {
                        Node tagNode = targetNode.getSession().getNodeByUUID( oldTagValues[i].getString() );
                        if ( !tagNode.getName().equals( tag ) ) {
                            newTagValues[j] = oldTagValues[i];
                            j++;
                        }
                    }
                } else {
                    return;
                }
            } catch ( PathNotFoundException e ) {
                //the property doesn't exist yet
                return;
            } finally {
                if ( newTagValues != null ) {
                    checkout(targetNode);
                    targetNode.setProperty( CATEGORY_PROPERTY_NAME,
                                           newTagValues );
                } else {
                    log.error( "reached expected path of execution when removing tag '" + tag + "' from ruleNode: " + targetNode.getName() );
                }
            }
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }


}
