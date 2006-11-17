package org.drools.repository;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.ValueFactory;

import org.apache.log4j.Logger;

/**
 * A ruleSet object aggregates a set of rules. This is advantageous for systems using the JBoss Rules
 * engine where the application might make use of many related rules.  
 * <p>
 * A rule set refers to rule nodes within the RulesRepository.  It can either have the reference to a 
 * specific rule follow the head version of that rule, or have this reference continue to refer to 
 * a specific version of that rule even when a new version of the rule is checked into the repository
 * 
 * @author btruitt
 */
public class RulePackageItem extends VersionableItem {
    private static Logger      log                              = Logger.getLogger( RulePackageItem.class );

    /**
     * This is the name of the rules "subfolder" where rules are kept
     * for this package.
     */
    public static final String RULES_FOLDER_NAME                = "rules";

    /**
     * The name of the reference property on the rulepackage_node_type type node that objects of
     * this type hold a reference to
     */
    public static final String RULE_REFERENCE_PROPERTY_NAME     = "drools:ruleReference";

    /**
     * The name of the reference property on the rulepackage_node_type type node that objects of
     * this type hold a reference to
     */
    public static final String FUNCTION_REFERENCE_PROPERTY_NAME = "drools:functionReference";

    /**
     * The name of the rule package node type
     */
    public static final String RULE_PACKAGE_TYPE_NAME           = "drools:rulepackageNodeType";

    /**
     * The folder where functions are kept
     */
    public static final String FUNCTION_FOLDER_NAME             = "functions";

    /**
     * Constructs an object of type RulePackageItem corresponding the specified node
     * @param rulesRepository the rulesRepository that instantiated this object
     * @param node the node to which this object corresponds
     * @throws RulesRepositoryException 
     */
    public RulePackageItem(RulesRepository rulesRepository,
                           Node node) throws RulesRepositoryException {
        super( rulesRepository,
               node );

        try {
            //make sure this node is a rule package node       
            if ( !(this.node.getPrimaryNodeType().getName().equals( RULE_PACKAGE_TYPE_NAME ) ||
                    isHistoricalVersion())  ) {
                String message = this.node.getName() + " is not a node of type " + RULE_PACKAGE_TYPE_NAME + ". It is a node of type: " + this.node.getPrimaryNodeType().getName();
                log.error( message );
                throw new RulesRepositoryException( message );
            }
        } catch ( Exception e ) {
            log.error( "Caught exception: " + e );
            throw new RulesRepositoryException( e );
        }
    }


    /**
     * Adds a rule to the current package with no category (not recommended !).
     * Without categories, its going to be hard to find rules later on
     * (unless packages are enough for you).
     */
    public RuleItem addRule(String ruleName, String description) {
        return addRule(ruleName, description, null);
    }
    

    /**
     * This adds a rule to the current physical package (you can move it later).
     * With the given category
     */
    public RuleItem addRule(String ruleName,
                            String description, String initialCategory) {
        Node ruleNode;
        try {

            Node rulesFolder = this.node.getNode( RULES_FOLDER_NAME );
            ruleNode = rulesFolder.addNode( ruleName,
                                            RuleItem.RULE_NODE_TYPE_NAME );
            ruleNode.setProperty( RuleItem.TITLE_PROPERTY_NAME,
                                  ruleName );

            ruleNode.setProperty( RuleItem.DESCRIPTION_PROPERTY_NAME,
                                  description );
            ruleNode.setProperty( RuleItem.FORMAT_PROPERTY_NAME,
                                  RuleItem.RULE_FORMAT );
            

            ruleNode.setProperty( VersionableItem.CHECKIN_COMMENT,
                                  "Initial" );

            Calendar lastModified = Calendar.getInstance();
            
            ruleNode.setProperty( RuleItem.LAST_MODIFIED_PROPERTY_NAME, lastModified );            
            ruleNode.setProperty( RuleItem.CREATION_DATE_PROPERTY, lastModified );
            
            ruleNode.setProperty( RuleItem.PACKAGE_NAME_PROPERTY, this.getName() );
            
            RuleItem rule = new RuleItem( this.rulesRepository, ruleNode );
            
            if (initialCategory != null) {
                rule.addCategory( initialCategory );
            }
            
            
            
            this.rulesRepository.save();
            
            rule.checkin( "Initial" );
            return rule;

        } catch ( Exception e ) {
            if ( e instanceof RuntimeException ) {
                throw (RuntimeException) e;
            } else if ( e instanceof ItemExistsException ) {
                throw new RulesRepositoryException( "A rule of that name already exists in that package.",
                                                    e );
            } else {
                throw new RulesRepositoryException( e );
            }
        }

    }

    /** Remove a rule by name */
    public void removeRule(String name) {
        try {
            this.node.getNode( RULES_FOLDER_NAME + "/" + name ).remove();
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }
    }

    
    
    // The following should be kept for reference on how to add a reference that 
    //is either locked to a version or follows head - FOR SHARING RULES
    //    /**
    //     * Adds a rule to the rule package node this object represents.  The reference to the rule
    //     * will optionally follow the head version of the specified rule's node or the specific 
    //     * current version.
    //     * 
    //     * @param ruleItem the ruleItem corresponding to the node to add to the rule package this 
    //     *                 object represents
    //     * @param followRuleHead if true, the reference to the rule node will follow the head version 
    //     *                       of the node, even if new versions are added. If false, will refer 
    //     *                       specifically to the current version.
    //     * @throws RulesRepositoryException
    //     */
    //    public void addRuleReference(RuleItem ruleItem, boolean followRuleHead) throws RulesRepositoryException {        
    //        try {
    //            ValueFactory factory = this.node.getSession().getValueFactory();
    //            int i = 0;
    //            Value[] newValueArray = null;
    //            
    //            try {
    //                Value[] oldValueArray = this.node.getProperty(RULE_REFERENCE_PROPERTY_NAME).getValues();
    //                newValueArray = new Value[oldValueArray.length + 1];                
    //                
    //                for(i=0; i<oldValueArray.length; i++) {
    //                    newValueArray[i] = oldValueArray[i];
    //                }
    //            }
    //            catch(PathNotFoundException e) {
    //                //the property has not been created yet. do so now
    //                newValueArray = new Value[1];
    //            }
    //            finally {
    //                if(newValueArray != null) { //just here to make the compiler happy
    //                    if(followRuleHead) {                    
    //                        newValueArray[i] = factory.createValue(ruleItem.getNode());
    //                    }
    //                    else {
    //                        //this is the magic that ties it to a specific version
    //                        newValueArray[i] = factory.createValue(ruleItem.getNode().getBaseVersion());
    //                    }
    //                    this.node.checkout();
    //                    this.node.setProperty(RULE_REFERENCE_PROPERTY_NAME, newValueArray);                
    //                    this.node.getSession().save();
    //                    this.node.checkin();
    //                }
    //                else {
    //                    throw new RulesRepositoryException("Unexpected null pointer for newValueArray");
    //                }
    //            }                    
    //        }
    //        catch(UnsupportedRepositoryOperationException e) {
    //            String message = "";
    //            try {
    //                message = "Error: Caught UnsupportedRepositoryOperationException when attempting to get base version for rule: " + ruleItem.getNode().getName() + ". Are you sure your JCR repository supports versioning? ";
    //                log.error(message + e);
    //            }
    //            catch (RepositoryException e1) {
    //                log.error("Caught exception: " + e1);
    //                throw new RulesRepositoryException(message, e1);
    //            }
    //            log.error("Caught exception: " + e);
    //            throw new RulesRepositoryException(e);
    //        }
    //        catch(Exception e) {
    //            log.error("Caught exception: " + e);
    //            throw new RulesRepositoryException(e);
    //        }
    //    }

    /**
     * Adds a function to the rule package node this object represents.  The reference to the 
     * function node will follow the head version of the specified node.
     * 
     * @param functionItem the functionItem corresponding to the node to add to the rule package this
     *                 object represents
     * @throws RulesRepositoryException
     */
    public void addFunction(FunctionItem functionItem) throws RulesRepositoryException {
        this.addFunction( functionItem,
                          true );
    }

    /**
     * Adds a function to the rule package node this object represents.  The reference to the function
     * will optionally follow the head version of the specified node or the specific current version.
     * 
     * @param functionItem the functionItem corresponding to the node to add to the rule package this 
     *                 object represents
     * @param followRuleHead if true, the reference to the function node will follow the head version 
     *                       of the node, even if new versions are added. If false, will refer 
     *                       specifically to the current version.
     * @throws RulesRepositoryException
     */
    public void addFunction(FunctionItem functionItem,
                            boolean followFunctionHead) throws RulesRepositoryException {
        try {
            ValueFactory factory = this.node.getSession().getValueFactory();
            int i = 0;
            Value[] newValueArray = null;

            try {
                Value[] oldValueArray = this.node.getProperty( FUNCTION_REFERENCE_PROPERTY_NAME ).getValues();
                newValueArray = new Value[oldValueArray.length + 1];

                for ( i = 0; i < oldValueArray.length; i++ ) {
                    newValueArray[i] = oldValueArray[i];
                }
            } catch ( PathNotFoundException e ) {
                //the property has not been created yet. do so now
                newValueArray = new Value[1];
            } finally {
                if ( newValueArray != null ) { //just here to make the compiler happy
                    if ( followFunctionHead ) {
                        newValueArray[i] = factory.createValue( functionItem.getNode() );
                    } else {
                        newValueArray[i] = factory.createValue( functionItem.getNode().getBaseVersion() );
                    }
                    this.node.checkout();
                    this.node.setProperty( FUNCTION_REFERENCE_PROPERTY_NAME,
                                           newValueArray );
                    this.node.getSession().save();
                    this.node.checkin();
                } else {
                    throw new RulesRepositoryException( "Unexpected null pointer for newValueArray" );
                }
            }
        } catch ( UnsupportedRepositoryOperationException e ) {
            String message = "";
            try {
                message = "Error: Caught UnsupportedRepositoryOperationException when attempting to get base version for function: " + functionItem.getNode().getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error( message + e );
            } catch ( RepositoryException e1 ) {
                log.error( "Caught exception: " + e1 );
                throw new RulesRepositoryException( message,
                                                    e1 );
            }
            log.error( "Caught exception: " + e );
            throw new RulesRepositoryException( e );
        } catch ( Exception e ) {
            log.error( "Caught exception: " + e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * Removes the specified rule from the rule package node this object represents.  
     * 
     * @param ruleItem the ruleItem corresponding to the node to remove from the rule package 
     *                 this object represents
     * @throws RulesRepositoryException
     */
    public void removeRuleReference(RuleItem ruleItem) throws RulesRepositoryException {
        try {
            Value[] oldValueArray = this.node.getProperty( RULE_REFERENCE_PROPERTY_NAME ).getValues();
            Value[] newValueArray = new Value[oldValueArray.length - 1];

            boolean wasThere = false;

            int j = 0;
            for ( int i = 0; i < oldValueArray.length; i++ ) {
                Node ruleNode = this.node.getSession().getNodeByUUID( oldValueArray[i].getString() );
                RuleItem currentRuleItem = new RuleItem( this.rulesRepository,
                                                         ruleNode );
                if ( currentRuleItem.equals( ruleItem ) ) {
                    wasThere = true;
                } else {
                    newValueArray[j] = oldValueArray[i];
                    j++;
                }
            }

            if ( !wasThere ) {
                return;
            } else {
                this.node.checkout();
                this.node.setProperty( RULE_REFERENCE_PROPERTY_NAME,
                                       newValueArray );
                this.node.getSession().save();
                this.node.checkin();
            }
        } catch ( PathNotFoundException e ) {
            //the property has not been created yet. 
            return;
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * Removes the specified function from the rule package node this object represents.  
     * 
     * @param functionItem the functionItem corresponding to the node to remove from the rule package 
     *                 this object represents
     * @throws RulesRepositoryException
     */
    public void removeFunction(FunctionItem functionItem) throws RulesRepositoryException {
        try {
            Value[] oldValueArray = this.node.getProperty( FUNCTION_REFERENCE_PROPERTY_NAME ).getValues();
            Value[] newValueArray = new Value[oldValueArray.length - 1];

            boolean wasThere = false;

            int j = 0;
            for ( int i = 0; i < oldValueArray.length; i++ ) {
                Node functionNode = this.node.getSession().getNodeByUUID( oldValueArray[i].getString() );
                FunctionItem currentFunctionItem = new FunctionItem( this.rulesRepository,
                                                                     functionNode );
                if ( currentFunctionItem.equals( functionItem ) ) {
                    wasThere = true;
                } else {
                    newValueArray[j] = oldValueArray[i];
                    j++;
                }
            }

            if ( !wasThere ) {
                return;
            } else {
                this.node.checkout();
                this.node.setProperty( FUNCTION_REFERENCE_PROPERTY_NAME,
                                       newValueArray );
                this.node.getSession().save();
                this.node.checkin();
            }
        } catch ( PathNotFoundException e ) {
            //the property has not been created yet. 
            return;
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * Gets a list of FunctionItem objects for each function node in this rule package
     * 
     * @return the List object holding the FunctionItem objects in this rule package
     * @throws RulesRepositoryException 
     */
    public List getFunctions() throws RulesRepositoryException {
        try {
            Value[] valueArray = this.node.getProperty( FUNCTION_REFERENCE_PROPERTY_NAME ).getValues();
            List returnList = new ArrayList();

            for ( int i = 0; i < valueArray.length; i++ ) {
                Node functionNode = this.node.getSession().getNodeByUUID( valueArray[i].getString() );
                returnList.add( new FunctionItem( this.rulesRepository,
                                                  functionNode ) );
            }
            return returnList;
        } catch ( PathNotFoundException e ) {
            //the property has not been created yet. 
            return new ArrayList();
        } catch ( Exception e ) {
            log.error( "Caught exception: " + e );
            throw new RulesRepositoryException( e );
        }
    }

    //    /**
    //     * Gets a list of RuleItem objects for each rule node in this rule package
    //     * 
    //     * @return the List object holding the RuleItem objects in this rule package
    //     * @throws RulesRepositoryException 
    //     */
    //    public List getRules() throws RulesRepositoryException {
    //        try {                       
    //            Value[] valueArray = this.node.getProperty(RULE_REFERENCE_PROPERTY_NAME).getValues();
    //            List returnList = new ArrayList();
    //           
    //            for(int i=0; i<valueArray.length; i++) {
    //                Node ruleNode = this.node.getSession().getNodeByUUID(valueArray[i].getString());
    //                returnList.add(new RuleItem(this.rulesRepository, ruleNode));
    //            }
    //            return returnList;
    //        }
    //        catch(PathNotFoundException e) {
    //            //the property has not been created yet. 
    //            return new ArrayList();
    //        }                                       
    //        catch(Exception e) {
    //            log.error("Caught exception: " + e);
    //            throw new RulesRepositoryException(e);
    //        }
    //    }   

    /** Return an iterator for the rules in this package */
    public Iterator getRules() {
        try {
            Node content = getVersionContentNode();
            RuleItemIterator it = new RuleItemIterator( content.getNode( RULES_FOLDER_NAME ).getNodes(),
                                                        this.rulesRepository );
            return it;
        } catch ( PathNotFoundException e ) {
            throw new RulesRepositoryException( e );
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException( e );
        }

    }
    
    /**
     * Load a specific rule asset by name.
     */
    public RuleItem loadRule(String name) {

        try {
            Node content = getVersionContentNode();
            return new RuleItem(
                        this.rulesRepository, 
                        content.getNode( RULES_FOLDER_NAME ).getNode( name ));
        } catch ( RepositoryException e ) {
             throw new RulesRepositoryException(e);
       }
        
    }

    /**
     * Nicely formats the information contained by the node that this object encapsulates    
     */
    public String toString() {
        try {
            StringBuffer returnString = new StringBuffer();
            returnString.append( "Content of the rule package named " + this.node.getName() + ":" );
            returnString.append( "Description: " + this.getDescription() + "\n" );
            returnString.append( "Format: " + this.getFormat() + "\n" );
            returnString.append( "Last modified: " + this.getLastModified() + "\n" );
            returnString.append( "Title: " + this.getTitle() + "\n" );
            returnString.append( "----\n" );

            return returnString.toString();
        } catch ( Exception e ) {
            log.error( "Caught Exception",
                       e );
            return null;
        }
    }

    public VersionableItem getPrecedingVersion() throws RulesRepositoryException {
        try {
            Node precedingVersionNode = this.getPrecedingVersionNode();
            if ( precedingVersionNode != null ) {
                return new RulePackageItem( this.rulesRepository,
                                            precedingVersionNode );
            } else {
                return null;
            }
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    public VersionableItem getSucceedingVersion() throws RulesRepositoryException {
        try {
            Node succeedingVersionNode = this.getSucceedingVersionNode();
            if ( succeedingVersionNode != null ) {
                return new RulePackageItem( this.rulesRepository,
                                            succeedingVersionNode );
            } else {
                return null;
            }
        } catch ( Exception e ) {
            log.error( "Caught exception",
                       e );
            throw new RulesRepositoryException( e );
        }
    }

    /**
     * This iterates over nodes and produces RuleItem's.
     * Also allows "skipping" of results to jump to certain items,
     * as per JCRs "skip".
     */
    static class RuleItemIterator
        implements
        Iterator {

        private NodeIterator    it;
        private RulesRepository rulesRepository;

        public RuleItemIterator(NodeIterator nodes,
                                RulesRepository repo) {
            this.it = nodes;
            this.rulesRepository = repo;
        }

        public boolean hasNext() {
            return it.hasNext();
        }

        public Object next() {
            return new RuleItem( rulesRepository,
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

    }

    
    /**
     * This will return a list of rules for a given state.
     * It works through the rules that belong to this package, and 
     * if they are not in the correct state, walks backwards until it finds one
     * in the correct state. 
     * 
     * If it walks all the way back up the versions looking for the "latest" 
     * version with the appropriate state, and can't find one, 
     * that asset is not included in the result.
     */
    public Iterator getRules(final StateItem state) {
        final Iterator rules = getRules();
        
        List result = new ArrayList();
        while(rules.hasNext()) {
            RuleItem head = (RuleItem) rules.next();
            if (head.sameState( state )) {
                result.add( head );
            } else {
                Iterator prev = head.getPredecessorVersionsIterator();
                while (prev.hasNext()) {
                    RuleItem prevRule = (RuleItem) prev.next();
                    if (prevRule.sameState( state )) {
                        result.add( prevRule );
                        break;
                    }
                }
            }
        }
        return result.iterator();
    }

    /**
     * This will create a new version of a package, effectively freezing the state.
     * This means in the "head" version of the package, rules can be added
     * removed, without effecting the baseline that was created.
     */
    public void createBaseline(String comment,
                               StateItem state) {
        Iterator rules = getRules();
        while(rules.hasNext()) {
            RuleItem rule = (RuleItem) rules.next();
            rule.updateState( state );
            rule.checkin( comment );
        }
        
        checkin( comment );
        try {
            this.node.checkout();
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException("Unable to check out package node after creating a new baseline.", e);
        }
        
    }
}