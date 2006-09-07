package org.drools.repository;

import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Iterator;
import java.util.List;

import javax.jcr.LoginException;
import javax.jcr.Node;
import javax.jcr.PathNotFoundException;
import javax.jcr.Repository;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.SimpleCredentials;
import javax.jcr.Workspace;

import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;
import org.apache.jackrabbit.core.nodetype.NodeTypeDef;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;
import org.apache.jackrabbit.core.nodetype.compact.CompactNodeTypeDefReader;
import org.apache.log4j.Logger;

/** This contains code to initialise the repository for jackrabbit */
public class RepositoryConfigurator {

    private static final Logger log = Logger.getLogger(RepositoryConfigurator.class);        
    private Repository repository;
    
    
    /**
     * This will create a new repository (or clear an existing one)
     * @param clearRepository True if you want it to wipe the contents and set it up.
     */
    public RepositoryConfigurator(boolean clearRepository) {
        try {

            repository = new TransientRepository();
            
            if(clearRepository) {    
                Session session = login();
                this.clearRepository(session);
                this.setupRepository(session); 
            }
            
                       
        }
        catch (Exception e) {
            log.error("Caught Exception", e);
        }         
    }
    
    public RepositoryConfigurator() {
        this(false);
    }

    /** Create a new user session */
    public Session login() throws LoginException,
                        RepositoryException {
        Session session = repository.login(
                                   new SimpleCredentials("username", "password".toCharArray()));

        if(session == null) {
            log.error("LOGIN FAILED! SESSION IS NULL!");
            throw new RulesRepositoryException("Unable to login to repository.");
        }
        return session;
    }
    
    /**
     * Clears out the entire tree below the rules repository node of the JCR repository.
     */
    public void clearRepository(Session session) {
        try {
            
            if (session.getRootNode().hasNode( RulesRepository.RULES_REPOSITORY_NAME )) {
                System.out.println("Clearing rules repository");
                Node node = session.getRootNode().getNode(RulesRepository.RULES_REPOSITORY_NAME);
                node.remove();
            } else {
                System.out.println("Repo not setup, ergo not clearing it !");
            }
        }
        catch(PathNotFoundException e) {                
            //doesn't exist yet. no biggie.
        }          
        catch(RepositoryException e) {
            //this will happen on the first setup. no biggie.
        }
    }    
    
    /**
     * Attempts to setup the repository.  If the work that it tries to do has already been done, it 
     * will return with modifying the repository.
     * 
     * @throws RulesRepositoryException     
     */
    protected void setupRepository(Session session) throws RulesRepositoryException {
        System.out.println("Setting up the repository, registering node types etc.");
        try {
            Node root = session.getRootNode();
            Workspace ws = session.getWorkspace();

            //no need to set it up again, skip it if it has.
            boolean registered = false;
            String uris[] = ws.getNamespaceRegistry().getURIs();            
            for ( int i = 0; i < uris.length; i++ ) {
                if (RulesRepository.DROOLS_URI.equals( uris[i]) ) {
                    registered = true;
                }
            }

            if (!registered) {
                ws.getNamespaceRegistry().registerNamespace("drools", RulesRepository.DROOLS_URI);
                
                this.registerNodeTypesFromCndFile("/node_type_definitions/versionable_node_type.cnd", ws);
                this.registerNodeTypesFromCndFile("/node_type_definitions/dsl_node_type.cnd", ws);            
                this.registerNodeTypesFromCndFile("/node_type_definitions/tag_node_type.cnd", ws);
                this.registerNodeTypesFromCndFile("/node_type_definitions/state_node_type.cnd", ws);
                this.registerNodeTypesFromCndFile("/node_type_definitions/rule_node_type.cnd", ws);
                this.registerNodeTypesFromCndFile("/node_type_definitions/function_node_type.cnd", ws);
                this.registerNodeTypesFromCndFile("/node_type_definitions/rulepackage_node_type.cnd", ws);
            }
            
            // Setup the rule repository node
            Node repositoryNode = RulesRepository.addNodeIfNew(root, RulesRepository.RULES_REPOSITORY_NAME, "nt:folder");
                    
            // Setup the Rule area
            RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.RULE_AREA, "nt:folder");
            
            //Setup the Rule area
            RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.FUNCTION_AREA, "nt:folder");
            
            // Setup the RulePackageItem area        
            RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.RULE_PACKAGE_AREA, "nt:folder");
            
            // Setup the DSL area                
            RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.DSL_AREA, "nt:folder");
            
            //Setup the DSL area                
            RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.TAG_AREA, "nt:folder");
            
            //Setup the State area                
            RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.STATE_AREA, "nt:folder");
            
            session.save();                        
        }
        catch(Exception e) {
            log.error("Caught Exception", e);
            System.err.println(e.getMessage());
            throw new RulesRepositoryException(e);
        }
    }
    
    private void registerNodeTypesFromCndFile(String cndFileName, Workspace ws) throws RulesRepositoryException, InvalidNodeTypeDefException {
        try {
            //Read in the CND file
            Reader in = new InputStreamReader(this.getClass().getResourceAsStream( cndFileName ));
            
            // Create a CompactNodeTypeDefReader
            CompactNodeTypeDefReader cndReader = new CompactNodeTypeDefReader(in, cndFileName);
            
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
    
    
}
