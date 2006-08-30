package org.drools.repository;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;

import javax.jcr.NamespaceException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.UnsupportedRepositoryOperationException;
import javax.jcr.Value;
import javax.jcr.Workspace;

import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;
import org.apache.jackrabbit.core.nodetype.NodeTypeDef;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;
import org.apache.jackrabbit.core.nodetype.compact.CompactNodeTypeDefReader;
import org.apache.log4j.Logger;


/**
 * RulesRepository is the class that defines the bahavior for the JBoss Rules (drools) rule repository
 * based upon the JCR specification (JSR-170).  
 * <p>
 * An instance of this class is capable of storing rules used by the JBoss Rule engine.  It also 
 * provides a versioning capability for rules.  Rules can be imported from specified files.  The 
 * RulesRepository is also capable of storing DSL content.  Rules can be explicitly tied to a 
 * particular DSL node within the repository, and this reference can either follow the head version,
 * or a specific version of the DSL node.  
 * <p>
 * The RulesRepository also is capable of storing RulePackages, which aggregate one or more Rules into 
 * a set.  RulePackages hold references to the nodes storing the content of the rules in the set within 
 * the repository.  Each entry in a rulepackage can either refer to the head version of the given rule
 * node, or a specific version.   
 * <p>
 * Rules can be tagged. Tags are stored in a separate area of the repository, and can be added on
 * demand.  Rules can have 0 or more tags. Tags are intended to help provide a means for searching
 * for specific types of rules quickly, even when they aren't all part of the same rulepackage.
 * <p>
 * Rules can be associated with 0 or 1 states.  States are created in a seperate area of the 
 * repository. States are intended to help track the progress of a rule as it traverses its life-
 * cycle. (e.g. draft, approved, deprecated, etc.)
 * <p>
 * The RulesRepository provides versioning of rules, rule packages, and DSLs.  This versioning works
 * in a strictly linear fashion, with one version having at most 1 predecessor version (or none, if
 * it is the first version), and at most 1 successor version (or none, if it is the most recently
 * checked-in version).  The JCR specification supports a more complicated versioning system, and 
 * if there is sufficient demand, we can modify our versioning scheme to be better aligned with JCR's
 * versioning abilities.
 * 
 * @author btruitt
 */
public class RulesRepository {
    private static final Logger log = Logger.getLogger(RulesRepository.class);
    
    /**
     * The name of the rulepackage area of the repository
     */
    public final static String RULE_PACKAGE_AREA = "drools:rulepackage_area";
    
    /**
     * The name of the rule area of the repository
     */
    public final static String RULE_AREA = "drools:rule_area";
    
    /**
     * The name of the rule area of the repository
     */
    public final static String FUNCTION_AREA = "drools:function_area";
    
    /**
     * The name of the DSL area of the repository
     */
    public final static String DSL_AREA = "drools:dsl_area";
    
    /**
     * The name of the tag area of the repository
     */
    public final static String TAG_AREA = "drools:tag_area";
    
    /**
     * The name of the state area of the repository
     */
    public final static String STATE_AREA = "drools:state_area";
    
    /**
     * The name of the rules repository within the JCR repository
     */
    public final static String RULES_REPOSITORY_NAME = "drools:repository";
    
    private Repository repository;
    private Session session;

    /**
     * This will create the JCR repository automatically if it does not already exist.  
     * It will call setupRepository() to attempt to setup the repository, in case
     * it has not already been setup.  
     */
    public RulesRepository() {
        this(false);
    }
    
    /**
     * This will create the JCR repository automatically if it does not already exist.  
     * It will call setupRepository() to attempt to setup the repository, in case
     * it has not already been setup.
     *   
     * @param clearRepository whether or not to erase the contents of the rules repository
     *                        portion of the JCR repository 
     */
    public RulesRepository(boolean clearRepository) {       
        try {
            //TODO: probably want to do something more serious than automatic creation of a 
            //      transientRepository here.  (e.g. manual creation of the repository to be 
            //      JCR implementation neutral). be sure to update the javadoc
            repository = new TransientRepository();
            session = repository.login(
                                       new SimpleCredentials("username", "password".toCharArray()));

            if(this.session == null) {
                log.error("LOGIN FAILED! SESSION IS NULL!");
            }                  
            
            if(clearRepository) {
                this.clearRepository();
            }
            
            setupRepository();            
        }
        catch (Exception e) {
            log.error("Caught Exception", e);
        }    
    }
    
    /**
     * Clears out the entire tree below the rules repository node of the JCR repository.
     */
    public void clearRepository() {
        try {
            log.info("Clearing rules repository");
            Node node = session.getRootNode().getNode(RULES_REPOSITORY_NAME);
            node.remove();
        }
        catch(PathNotFoundException e) {                
            //doesn't exist yet. no biggie.
        }          
        catch(RepositoryException e) {
            //this will happen on the first setup. no biggie.
        }
    }
    
    private void registerNodeTypesFromCndFile(String cndFileName, Workspace ws) throws RulesRepositoryException, InvalidNodeTypeDefException {
        try {
            //Read in the CND file
            FileReader fileReader = new FileReader(cndFileName);
            
            // Create a CompactNodeTypeDefReader
            CompactNodeTypeDefReader cndReader = new CompactNodeTypeDefReader(fileReader, cndFileName);
            
            // Get the List of NodeTypeDef objects
            List ntdList = cndReader.getNodeTypeDefs();
            
            // Get the NodeTypeManager from the Workspace.
            // Note that it must be cast from the generic JCR NodeTypeManager to the
            // Jackrabbit-specific implementation.
            NodeTypeManagerImpl ntmgr = (NodeTypeManagerImpl)ws.getNodeTypeManager();
            
            // Acquire the NodeTypeRegistry
            NodeTypeRegistry ntreg = ntmgr.getNodeTypeRegistry();
            
            // Loop through the prepared NodeTypeDefs
            for(Iterator i = ntdList.iterator(); i.hasNext();) {                               
                // Get the NodeTypeDef...
                NodeTypeDef ntd = (NodeTypeDef)i.next();                                        
                
                log.debug("Attempting to regsiter node type named: " + ntd.getName());
                
                // ...and register it            
                ntreg.registerNodeType(ntd);
            }
        }
        catch(InvalidNodeTypeDefException e) {
            log.warn("InvalidNodeTypeDefinitionException caught when trying to add node from CND file: " + cndFileName + ". This will happen if the node type was already registered. " + e);
            throw e;
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Attempts to setup the repository.  If the work that it tries to do has already been done, it 
     * will return with modifying the repository.
     * 
     * @throws RulesRepositoryException     
     */
    protected void setupRepository() throws RulesRepositoryException {
        try {
            Node root = session.getRootNode();
            Workspace ws = session.getWorkspace();
    
            // Setup the namespace
            try {
                ws.getNamespaceRegistry().registerNamespace("drools", "http://www.jboss.org/drools-repository/1.0");
            }
            catch(NamespaceException e) {
                //already registered. do nothing.
                log.info("drools namespace already registered");
            }        
            
            // Setup the versionable node type
            try {                                
                //TODO: remove hard-coded path
                this.registerNodeTypesFromCndFile("./src/node_type_definitions/versionable_node_type.cnd", ws);
            }
            catch(InvalidNodeTypeDefException e) {
                //This will happen in the node type is already registered, so ignore it                
            }
            
            // Setup the dsl node type
            try {                                
                //TODO: remove hard-coded path
                this.registerNodeTypesFromCndFile("./src/node_type_definitions/dsl_node_type.cnd", ws);
            }
            catch(InvalidNodeTypeDefException e) {
                //This will happen in the node type is already registered, so ignore it                
            }
            
            // Setup the tag node type
            try {
                //TODO: remove hard-coded path
                this.registerNodeTypesFromCndFile("./src/node_type_definitions/tag_node_type.cnd", ws);
            }
            catch(InvalidNodeTypeDefException e) {
                //This will happen in the node type is already registered, so ignore it                
            }

            //Setup the tag node type
            try {
                //TODO: remove hard-coded path
                this.registerNodeTypesFromCndFile("./src/node_type_definitions/state_node_type.cnd", ws);
            }
            catch(InvalidNodeTypeDefException e) {
                //This will happen in the node type is already registered, so ignore it                
            }
            
            // Setup the rule node type
            try {
                //TODO: remove hard-coded path
                this.registerNodeTypesFromCndFile("./src/node_type_definitions/rule_node_type.cnd", ws);
            }
            catch(InvalidNodeTypeDefException e) {
                //This will happen in the node type is already registered, so ignore it
            }
            
            //Setup the function node type
            try {
                //TODO: remove hard-coded path
                this.registerNodeTypesFromCndFile("./src/node_type_definitions/function_node_type.cnd", ws);
            }
            catch(InvalidNodeTypeDefException e) {
                //This will happen in the node type is already registered, so ignore it
            }
            
            // Setup the rulepackage node type
            try {
                //TODO: remove hard-coded path
                this.registerNodeTypesFromCndFile("./src/node_type_definitions/rulepackage_node_type.cnd", ws);
            }
            catch(InvalidNodeTypeDefException e) {
                //This will happen in the node type is already registered, so ignore it
            }
            
            // Setup the rule repository node
            Node repositoryNode = addNodeIfNew(root, RULES_REPOSITORY_NAME, "nt:folder");
                    
            // Setup the Rule area
            addNodeIfNew(repositoryNode, RULE_AREA, "nt:folder");
            
            //Setup the Rule area
            addNodeIfNew(repositoryNode, FUNCTION_AREA, "nt:folder");
            
            // Setup the RulePackageItem area        
            addNodeIfNew(repositoryNode, RULE_PACKAGE_AREA, "nt:folder");
            
            // Setup the DSL area                
            addNodeIfNew(repositoryNode, DSL_AREA, "nt:folder");
            
            //Setup the DSL area                
            addNodeIfNew(repositoryNode, TAG_AREA, "nt:folder");
            
            //Setup the State area                
            addNodeIfNew(repositoryNode, STATE_AREA, "nt:folder");
            
            session.save();                        
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Will add a node named 'nodeName' of type 'type' to 'parent' if such a node does not already
     * exist.
     * 
     * @param parent the parent node to add the new node to
     * @param nodeName the name of the new node
     * @param type the type of the new node
     * @return a reference to the Node object that is created by the addition, or, if the node already
     *         existed, a reference to the pre-existant node.
     * @throws RulesRepositoryException
     */
    protected Node addNodeIfNew(Node parent, String nodeName, String type) throws RulesRepositoryException {              
        Node node;
        try {
            node = parent.getNode(nodeName);                
        }
        catch(PathNotFoundException e) {
            //it doesn't exist yet, so create it                       
            try {
                log.debug("Adding new node of type: " + type + " named: " + nodeName + " to parent node named " + parent.getName());
                
                node = parent.addNode(nodeName, type);
            }
            catch (Exception e1) {                
                log.error("Caught Exception", e);
                throw new RulesRepositoryException(e1);
            }
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
        return node;
    }
    
    /**
     * Explicitly logout of the underlying JCR repository.  If this is the last session to that
     * repository, the repository will automatically be shutdown.
     */
    public void logout() {
        this.session.logout();
    }
    
    /**
     * Recursively outputs the contents of the workspace starting from root. The large subtree
     * called jcr:system is skipped.  This method is just here for programmatic debugging 
     * purposes, and should be removed.
     * 
     * @throws RulesRepositoryException
     */
    public void dumpRepository() throws RulesRepositoryException {
        try {
            this.dump(this.session.getRootNode());
        }
        catch(Exception e) {
            log.error("Caught exception: " + e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /** 
     * Recursively outputs the contents of the given node. Used for debugging purposes. 
     */
    private void dump(final Node node) throws RulesRepositoryException {
        try {
            // First output the node path
            System.out.println(node.getPath());
            // Skip the virtual (and large!) jcr:system subtree
            /*if (node.getName().equals("jcr:system")) {
                return;
            }*/
    
            // Then output the properties
            PropertyIterator properties = node.getProperties();
            while (properties.hasNext()) {
                Property property = properties.nextProperty();
                if (property.getDefinition().isMultiple()) {
                    // A multi-valued property, print all values
                    Value[] values = property.getValues();
                    for (int i = 0; i < values.length; i++) {
                        System.out.println(
                            property.getPath() + " = " + values[i].getString());
                    }
                } else {
                    // A single-valued property
                    System.out.println(
                        property.getPath() + " = " + property.getString());
                }
            }
    
            // Finally output all the child nodes recursively
            NodeIterator nodes = node.getNodes();
            while (nodes.hasNext()) {
                dump(nodes.nextNode());
            }
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }                
    
    private Node getAreaNode(String areaName) throws RulesRepositoryException {
        Node folderNode = null;
        int tries = 0;
        while(folderNode == null && tries < 2) {
            try {
                tries++;                                                
                folderNode = this.session.getRootNode().getNode(RULES_REPOSITORY_NAME + "/" + areaName);
            }
            catch(PathNotFoundException e) {
                if(tries == 1) {
                    //hmm..repository must have gotten screwed up.  set it up again                
                    log.warn("The repository appears to have become corrupted. It will be re-setup now.");
                    this.setupRepository();
                }
                else {
                    log.error("Unable to correct repository corruption");
                }
            }
            catch(Exception e) {
                log.error("Caught Exception", e);
                throw new RulesRepositoryException("Caught exception " + e.getClass().getName(), e);
            }
        }
        if(folderNode == null) {
            String message = "Could not get a reference to a node for " + RULES_REPOSITORY_NAME + "/" + areaName;
            log.error(message);
            throw new RulesRepositoryException(message);
        }
        return folderNode;
    }
    
    /**
     * Adds a DSL node in the repository using the content and attributes of the specified file
     * 
     * @param file the file to use to import the DSL content and attributes
     * @return a DslItem object encapsulating the node that gets added
     * @throws RulesRepositoryException 
     */
    public DslItem addDsl(String name, String content) throws RulesRepositoryException { 
        Node folderNode = this.getAreaNode(DSL_AREA);            
        
        try {
            //create the node - see section 6.7.22.6 of the spec
            Node dslNode = folderNode.addNode(name, DslItem.DSL_NODE_TYPE_NAME);
            
            dslNode.setProperty(DslItem.TITLE_PROPERTY_NAME, name);
            
            //TODO: set this property correctly once we've figured out logging in / JAAS
            dslNode.setProperty(DslItem.CONTRIBUTOR_PROPERTY_NAME, "not yet implemented");
                        
            dslNode.setProperty(DslItem.DESCRIPTION_PROPERTY_NAME, "");
            dslNode.setProperty(DslItem.FORMAT_PROPERTY_NAME, DslItem.DSL_FORMAT);
            
            dslNode.setProperty( DslItem.DSL_CONTENT, content );
            dslNode.setProperty(DslItem.LAST_MODIFIED_PROPERTY_NAME, Calendar.getInstance());
            
            this.session.save();
            
            try {
                dslNode.checkin();
            }
            catch(UnsupportedRepositoryOperationException e) {
                String message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkin dsl: " + dslNode.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error(message + e);
                throw new RulesRepositoryException(message, e);
            }
            
            return new DslItem(this, dslNode);
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }                
    }    
    
    /**
     * Adds a Function node in the repository using the content specified.
     * 
     * @param functionName the name of the function
     * @param content the content of the function
     * @return a FunctionItem object encapsulating the node that gets added
     * @throws RulesRepositoryException
     */
    public FunctionItem addFunction(String functionName, String content) throws RulesRepositoryException {
        Node folderNode = this.getAreaNode(FUNCTION_AREA);        
        
        try {        
            //create the node - see section 6.7.22.6 of the spec
            Node functionNode = folderNode.addNode(functionName, FunctionItem.FUNCTION_NODE_TYPE_NAME);
                        
            functionNode.setProperty(FunctionItem.TITLE_PROPERTY_NAME, functionName);
            functionNode.setProperty(FunctionItem.CONTENT_PROPERTY_NAME, content);
            functionNode.setProperty(FunctionItem.DESCRIPTION_PROPERTY_NAME, "");
            
            //TODO: set contributor correctly
            functionNode.setProperty(FunctionItem.CONTRIBUTOR_PROPERTY_NAME, "");
            
            functionNode.setProperty(FunctionItem.FORMAT_PROPERTY_NAME, FunctionItem.FUNCTION_FORMAT);
            
            Calendar lastModified = Calendar.getInstance();
            functionNode.setProperty(FunctionItem.LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            session.save();
            
            try {
                functionNode.checkin();
            }
            catch(UnsupportedRepositoryOperationException e) {
                String message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkin node: " + functionNode.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error(message + e);
                throw new RulesRepositoryException(message, e);
            }
            
            return new FunctionItem(this, functionNode);
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Adds a Function node in the repository using the content specified.
     * 
     * @param functionName the name of the function
     * @param content the content of the function
     * @param description the description of the function
     * @return a FunctionItem object encapsulating the node that gets added
     * @throws RulesRepositoryException
     */
    public FunctionItem addFunction(String functionName, String content, String description) throws RulesRepositoryException {
        Node folderNode = this.getAreaNode(FUNCTION_AREA);        
        
        try {        
            //create the node - see section 6.7.22.6 of the spec
            Node functionNode = folderNode.addNode(functionName, FunctionItem.FUNCTION_NODE_TYPE_NAME);
                        
            functionNode.setProperty(FunctionItem.TITLE_PROPERTY_NAME, functionName);
            functionNode.setProperty(FunctionItem.CONTENT_PROPERTY_NAME, content);
            functionNode.setProperty(FunctionItem.DESCRIPTION_PROPERTY_NAME, description);
            functionNode.setProperty(FunctionItem.FORMAT_PROPERTY_NAME, FunctionItem.FUNCTION_FORMAT);
            
            //TODO: set contributor correctly
            functionNode.setProperty(FunctionItem.CONTRIBUTOR_PROPERTY_NAME, "");
            
            Calendar lastModified = Calendar.getInstance();
            functionNode.setProperty(FunctionItem.LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            session.save();
            
            try {
                functionNode.checkin();
            }
            catch(UnsupportedRepositoryOperationException e) {
                String message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkin node: " + functionNode.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error(message + e);
                throw new RulesRepositoryException(message, e);
            }
            
            return new FunctionItem(this, functionNode);
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Adds a Rule node in the repository using the content specified, associating it with
     * the specified DSL node
     * 
     * @param ruleName the name of the rule
     * @param lhsContent the lhs of the rule
     * @param rhsContent the rhs of the rule
     * @param dslItem the dslItem encapsulting the dsl node to associate this rule node with
     * @paaram followDslHead whether or not to follow the head revision of the dsl node
     * @return a RuleItem object encapsulating the node that gets added
     * @throws RulesRepositoryException
     */
    public RuleItem addRule(String ruleName, String lhsContent, String rhsContent, DslItem dslItem, boolean followDslHead) throws RulesRepositoryException {
        Node folderNode = this.getAreaNode(RULE_AREA);        
        
        try {        
            //create the node - see section 6.7.22.6 of the spec
            Node ruleNode = folderNode.addNode(ruleName, RuleItem.RULE_NODE_TYPE_NAME);
                        
            ruleNode.setProperty(RuleItem.TITLE_PROPERTY_NAME, ruleName);
            ruleNode.setProperty(RuleItem.LHS_PROPERTY_NAME, lhsContent);
            ruleNode.setProperty(RuleItem.RHS_PROPERTY_NAME, rhsContent);
            ruleNode.setProperty(RuleItem.DESCRIPTION_PROPERTY_NAME, "");
            ruleNode.setProperty(RuleItem.FORMAT_PROPERTY_NAME, RuleItem.RULE_FORMAT);
            
            //TODO: set this correctly
            ruleNode.setProperty(RuleItem.CONTRIBUTOR_PROPERTY_NAME, "");
            
            if(followDslHead) {
                ruleNode.setProperty(RuleItem.DSL_PROPERTY_NAME, dslItem.getNode());
            }
            else {
                //tie the ruleNode to specifically the current version of the dslNode
                ruleNode.setProperty(RuleItem.DSL_PROPERTY_NAME, dslItem.getNode().getBaseVersion());
            }
            
            Calendar lastModified = Calendar.getInstance();
            ruleNode.setProperty(RuleItem.LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            session.save();
            
            try {
                ruleNode.checkin();
            }
            catch(UnsupportedRepositoryOperationException e) {
                String message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkin rule: " + ruleNode.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error(message + e);
                throw new RulesRepositoryException(message, e);
            }
            
            return new RuleItem(this, ruleNode);
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Adds a Rule node in the repository using the content specified, with the specified
     * effective and expiration dates, and referencing the specified dsl node
     * 
     * @param ruleName the name of the rule
     * @param lhsContent the lhs of the rule
     * @param rhsContent the rhs of the rule
     * @param dslItem the DslItem object encapsuling the dsl node to assocaite this node with
     * @param followDslHead whether or not to follow the head revision of the DSL node
     * @param effectiveDate the date the rule becomes effective
     * @param expiredDate the date teh rule expires
     * @param description the description of the rule
     * @return a RuleItem object encapsulating the node that gets added
     * @throws RulesRepositoryException
     */
    public RuleItem addRule(String ruleName, String lhsContent, String rhsContent, DslItem dslItem, boolean followDslHead, Calendar effectiveDate, Calendar expiredDate, String description) throws RulesRepositoryException {
        Node folderNode = this.getAreaNode(RULE_AREA);        
        
        try {        
            //create the node - see section 6.7.22.6 of the spec
            Node ruleNode = folderNode.addNode(ruleName, RuleItem.RULE_NODE_TYPE_NAME);
                        
            ruleNode.setProperty(RuleItem.TITLE_PROPERTY_NAME, ruleName);
            
            //TODO: set this property correctly once we've figured out logging in / JAAS
            ruleNode.setProperty(RuleItem.CONTRIBUTOR_PROPERTY_NAME, "not yet implemented");
                        
            ruleNode.setProperty(RuleItem.DESCRIPTION_PROPERTY_NAME, description);
            ruleNode.setProperty(RuleItem.FORMAT_PROPERTY_NAME, RuleItem.RULE_FORMAT);
            ruleNode.setProperty(RuleItem.LHS_PROPERTY_NAME, lhsContent);
            ruleNode.setProperty(RuleItem.RHS_PROPERTY_NAME, rhsContent);            
            ruleNode.setProperty(RuleItem.DSL_PROPERTY_NAME, dslItem.getNode());
            ruleNode.setProperty(RuleItem.DATE_EFFECTIVE_PROPERTY_NAME, effectiveDate);
            ruleNode.setProperty(RuleItem.DATE_EXPIRED_PROPERTY_NAME, expiredDate);
            
            Calendar lastModified = Calendar.getInstance();
            ruleNode.setProperty(RuleItem.LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            session.save();
            
            try {
                ruleNode.checkin();
            }
            catch(UnsupportedRepositoryOperationException e) {
                String message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkin rule: " + ruleNode.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error(message + e);
                throw new RulesRepositoryException(message, e);
            }
            
            return new RuleItem(this, ruleNode);
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Adds a Rule node in the repository using the content specified
     * 
     * @param ruleName the name of the rule
     * @param lhsContent the lhs of the rule
     * @param rhsContent the rhs of the rule
     * @return a RuleItem object encapsulating the node that gets added
     * @throws RulesRepositoryException
     */
    public RuleItem addRule(String ruleName, String lhsContent, String rhsContent) throws RulesRepositoryException {
        Node folderNode = this.getAreaNode(RULE_AREA);        
        
        try {        
            //create the node - see section 6.7.22.6 of the spec
            Node ruleNode = folderNode.addNode(ruleName, RuleItem.RULE_NODE_TYPE_NAME);
                        
            ruleNode.setProperty(RuleItem.TITLE_PROPERTY_NAME, ruleName);
            
            //TODO: set this property correctly once we've figured out logging in / JAAS
            ruleNode.setProperty(RuleItem.CONTRIBUTOR_PROPERTY_NAME, "not yet implemented");
                        
            ruleNode.setProperty(RuleItem.DESCRIPTION_PROPERTY_NAME, "");
            ruleNode.setProperty(RuleItem.FORMAT_PROPERTY_NAME, RuleItem.RULE_FORMAT);
            ruleNode.setProperty(RuleItem.LHS_PROPERTY_NAME, lhsContent);
            ruleNode.setProperty(RuleItem.RHS_PROPERTY_NAME, rhsContent);                        
            
            Calendar lastModified = Calendar.getInstance();
            ruleNode.setProperty(RuleItem.LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            session.save();
            
            try {
                ruleNode.checkin();
            }
            catch(UnsupportedRepositoryOperationException e) {
                String message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkin rule: " + ruleNode.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error(message + e);
                throw new RulesRepositoryException(message, e);
            }
            
            return new RuleItem(this, ruleNode);
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }
    
    /**
     * Adds a Rule node in the repository using the content specified, with the specified
     * effective and expiration dates
     * 
     * @param ruleName the name of the rule
     * @param lhsContent the lhs of the rule
     * @param rhsContent the rhs of the rule
     * @param effectiveDate the date the rule becomes effective
     * @param expiredDate the date teh rule expires
     * @return a RuleItem object encapsulating the node that gets added
     * @throws RulesRepositoryException
     */
    public RuleItem addRule(String ruleName, String lhsContent, String rhsContent, Calendar effectiveDate, Calendar expiredDate) throws RulesRepositoryException {
        Node folderNode = this.getAreaNode(RULE_AREA);        
        
        try {        
            //create the node - see section 6.7.22.6 of the spec
            Node ruleNode = folderNode.addNode(ruleName, RuleItem.RULE_NODE_TYPE_NAME);
                        
            ruleNode.setProperty(RuleItem.TITLE_PROPERTY_NAME, ruleName);

            //TODO: set this property correctly once we've figured out logging in / JAAS
            ruleNode.setProperty(RuleItem.CONTRIBUTOR_PROPERTY_NAME, "not yet implemented");
                        
            ruleNode.setProperty(RuleItem.DESCRIPTION_PROPERTY_NAME, "");
            ruleNode.setProperty(RuleItem.FORMAT_PROPERTY_NAME, RuleItem.RULE_FORMAT);
            
            ruleNode.setProperty(RuleItem.LHS_PROPERTY_NAME, lhsContent);
            ruleNode.setProperty(RuleItem.RHS_PROPERTY_NAME, rhsContent);            
            ruleNode.setProperty(RuleItem.DATE_EFFECTIVE_PROPERTY_NAME, effectiveDate);
            ruleNode.setProperty(RuleItem.DATE_EXPIRED_PROPERTY_NAME, expiredDate);
            
            Calendar lastModified = Calendar.getInstance();
            ruleNode.setProperty(RuleItem.LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            session.save();
            
            try {
                ruleNode.checkin();
            }
            catch(UnsupportedRepositoryOperationException e) {
                String message = "Error: Caught UnsupportedRepositoryOperationException when attempting to checkin rule: " + ruleNode.getName() + ". Are you sure your JCR repository supports versioning? ";
                log.error(message + e);
                throw new RulesRepositoryException(message, e);
            }
            
            return new RuleItem(this, ruleNode);
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }    
    
    /**
     * Adds a rule package node in the repository. This node has a property called 
     * drools:rule_reference that is a multi-value reference property.  It will hold an array of 
     * references to rule nodes that are subsequently added.
     *   
     * @param name what to name the node added
     * @return a RulePackageItem, encapsulating the created node
     * @throws RulesRepositoryException
     */
    public RulePackageItem createRulePackage(String name) throws RulesRepositoryException {
        Node folderNode = this.getAreaNode(RULE_PACKAGE_AREA);
                 
        try {
            //create the node - see section 6.7.22.6 of the spec
            Node rulePackageNode = folderNode.addNode(name, RulePackageItem.RULE_PACKAGE_TYPE_NAME);
            
            rulePackageNode.setProperty(RulePackageItem.TITLE_PROPERTY_NAME, name);
            
            //TODO: set this property correctly once we've figured out logging in / JAAS
            rulePackageNode.setProperty(RulePackageItem.CONTRIBUTOR_PROPERTY_NAME, "not yet implemented");
                        
            rulePackageNode.setProperty(RulePackageItem.DESCRIPTION_PROPERTY_NAME, "");
            rulePackageNode.setProperty(RulePackageItem.FORMAT_PROPERTY_NAME, RulePackageItem.RULE_PACKAGE_FORMAT);
            
            Calendar lastModified = Calendar.getInstance();
            rulePackageNode.setProperty(RulePackageItem.LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            this.session.save();
            rulePackageNode.checkin();
            return new RulePackageItem(this, rulePackageNode);
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }                                       

    /**
     * Adds a rule package node in the repository. This node has a property called 
     * drools:rule_reference that is a multi-value reference property.  It will hold an array of 
     * references to rule nodes that are subsequently added.
     *   
     * @param name what to name the node added
     * @param description what description to use for the node
     * @return a RulePackageItem, encapsulating the created node
     * @throws RulesRepositoryException
     */
    public RulePackageItem createRulePackage(String name, String description) throws RulesRepositoryException {
        Node folderNode = this.getAreaNode(RULE_PACKAGE_AREA);
                 
        try {
            //create the node - see section 6.7.22.6 of the spec
            Node rulePackageNode = folderNode.addNode(name, RulePackageItem.RULE_PACKAGE_TYPE_NAME);
            
            rulePackageNode.setProperty(RulePackageItem.TITLE_PROPERTY_NAME, name);
            
            //TODO: set this property correctly once we've figured out logging in / JAAS
            rulePackageNode.setProperty(RuleItem.CONTRIBUTOR_PROPERTY_NAME, "not yet implemented");
                        
            rulePackageNode.setProperty(RuleItem.DESCRIPTION_PROPERTY_NAME, description);
            rulePackageNode.setProperty(RuleItem.FORMAT_PROPERTY_NAME, RuleItem.RULE_PACKAGE_FORMAT);
            
            Calendar lastModified = Calendar.getInstance();
            rulePackageNode.setProperty(RulePackageItem.LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            this.session.save();
            rulePackageNode.checkin();
            return new RulePackageItem(this, rulePackageNode);
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            throw new RulesRepositoryException(e);
        }
    }                                       
    
    /**
     * Gets a StateItem for the specified state name.  If a node for the specified state does not
     * yet exist, one is first created.
     * 
     * @param name the name of the state to get
     * @return a StateItem object encapsulating the retreived node
     * @throws RulesRepositoryException
     */
    public StateItem getState(String name) throws RulesRepositoryException {
        try {
            Node folderNode = this.getAreaNode(STATE_AREA);
            Node stateNode = this.addNodeIfNew(folderNode, name, StateItem.STATE_NODE_TYPE_NAME);
            return new StateItem(this, stateNode);
        }
        catch(Exception e) {
            log.error("Caught Exception: " + e);
            throw new RulesRepositoryException(e);
        }
    }
        
    /**
     * Gets a TagItem object that encapsulates the node for the specified tag name.  If the tag does
     * not already exist in the repository when this is called, it is first added to the repository
     * and then returned.
     * 
     * @param tagName the name of the tag to get. If the tag to get is within a heirarchy of
     *                tag nodes, specify the full path to the tag node of interest (e.g. if
     *                you want to get back 'child-tag', use "parent-tag/child-tag")
     * @return a TagItem object encapsulating the node for the tag in the repository
     * @throws RulesRepositoryException 
     */
    public CategoryItem getOrCreateCategory(String tagName) throws RulesRepositoryException {
        log.debug("getting tag with name: " + tagName);
        
        try {
            Node folderNode = this.getAreaNode(TAG_AREA);
            Node tagNode = null;
            
            StringTokenizer tok = new StringTokenizer(tagName, "/");
            while(tok.hasMoreTokens()) {                                
                String currentTagName = tok.nextToken();
                
                tagNode = this.addNodeIfNew(folderNode, currentTagName, CategoryItem.TAG_NODE_TYPE_NAME);
                folderNode = tagNode;
            }             
                                    
            return new CategoryItem(this, tagNode);
        }
        catch(Exception e) {
            log.error("Caught Exception: " + e);
            throw new RulesRepositoryException(e);
        }
    }

    /**
     * This will retrieve a list of FunctionItem objects - that are allocated to the 
     * provided category.
     * Only the latest versions of each FunctionItem will be returned (you will have 
     * to delve into the functions' deepest darkest history yourself... mahahahaha).
     */
    public List findFunctionsByTag(String categoryTag) throws RulesRepositoryException {        
        CategoryItem item = this.getOrCreateCategory( categoryTag );
        List results = new ArrayList();
        try {
            PropertyIterator it = item.getNode().getReferences();
            while(it.hasNext()) {
                Property ruleLink = (Property) it.next();
                Node parentNode = ruleLink.getParent();
                if(parentNode.getPrimaryNodeType().getName().equals(FunctionItem.FUNCTION_NODE_TYPE_NAME) ||
                   (parentNode.getPrimaryNodeType().getName().equals("nt:version") && 
                    parentNode.getProperty(VersionableItem.FORMAT_PROPERTY_NAME).getString().equals(VersionableItem.FUNCTION_FORMAT))) {
                    results.add(new FunctionItem(this, parentNode));
                }
            }
            return results;
        } catch (RepositoryException e) {            
            throw new RulesRepositoryException(e);
        }        
    }
    
    /**
     * This will retrieve a list of RuleItem objects - that are allocated to the 
     * provided category.
     * Only the latest versions of each RuleItem will be returned (you will have 
     * to delve into the rules deepest darkest history yourself... mahahahaha).
     */
    public List findRulesByTag(String categoryTag) throws RulesRepositoryException {
        
        CategoryItem item = this.getOrCreateCategory( categoryTag );
        List results = new ArrayList();
        try {
            PropertyIterator it = item.getNode().getReferences();
            while(it.hasNext()) {
                Property ruleLink = (Property) it.next();
                Node parentNode = ruleLink.getParent();
                if(parentNode.getPrimaryNodeType().getName().equals(RuleItem.RULE_NODE_TYPE_NAME) ||
                   (parentNode.getPrimaryNodeType().getName().equals("nt:version") && 
                    parentNode.getProperty(VersionableItem.FORMAT_PROPERTY_NAME).getString().equals(VersionableItem.RULE_FORMAT))) {
                    results.add(new RuleItem(this, parentNode));
                }
            }
            return results;
        } catch ( RepositoryException e ) {            
            throw new RulesRepositoryException(e);
        }        
    }
    
    /**
     * @return an Iterator which will provide RulePackageItem's.
     * This will show ALL the packages, only returning latest versions, by default.
     */
    public Iterator listPackages()  {
        Node folderNode = this.getAreaNode(RULE_PACKAGE_AREA);
        try {
            return new RulePackageIterator(this, folderNode.getNodes());
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException(e);
        }
    }
    
    /** 
     * This will provide a list of top level category strings. 
     * Use getCategory to get a specific category to drill down into it.
     */
    public List listCategoryNames() throws RulesRepositoryException {
        try {
            
            Node folderNode = this.getAreaNode(TAG_AREA);
            NodeIterator it = folderNode.getNodes();            
            
            List nodeNames = new ArrayList();
            while(it.hasNext()) {
                Node catNode = (Node) it.next();
                nodeNames.add( catNode.getName() );
            }
            
            return nodeNames;
        } catch ( RepositoryException e ) {
            throw new RulesRepositoryException(e);
        }
    }
}