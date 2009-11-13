package org.drools.repository;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

import javax.jcr.Node;
import javax.jcr.Repository;
import javax.jcr.Session;
import javax.jcr.Workspace;

import org.apache.jackrabbit.core.TransientRepository;
import org.apache.jackrabbit.core.nodetype.InvalidNodeTypeDefException;
import org.apache.jackrabbit.core.nodetype.NodeTypeDef;
import org.apache.jackrabbit.core.nodetype.NodeTypeManagerImpl;
import org.apache.jackrabbit.core.nodetype.NodeTypeRegistry;
import org.apache.jackrabbit.core.nodetype.compact.CompactNodeTypeDefReader;
import org.apache.log4j.Logger;

/** 
 * This contains code to initialise the repository for jackrabbit.
 * This is mostly a collection of utilities. 
 * Any jackrabbit specific code needs to go in here.
 */
public class JackrabbitRepositoryConfigurator implements JCRRepositoryConfigurator {

    private static final Logger log = Logger.getLogger(JackrabbitRepositoryConfigurator.class);        
    
    /* (non-Javadoc)
     * @see org.drools.repository.RepositoryConfigurator#getJCRRepository()
     */
    public Repository getJCRRepository(String repoRootDir) {
        try {
            if (repoRootDir == null) {
                return new TransientRepository();
            } else { 
                return new TransientRepository(repoRootDir + "/repository.xml", repoRootDir);
            }
        } catch ( IOException e ) {
            throw new RulesRepositoryException("Unable to create a Repository instance.", e);
        }
    }
    
  
    
    /* (non-Javadoc)
     * @see org.drools.repository.RepositoryConfigurator#setupRulesRepository(javax.jcr.Session)
     */
    public void setupRulesRepository(Session session) throws RulesRepositoryException {
        System.out.println("Setting up the repository, registering node types etc.");
        try {
            Node root = session.getRootNode();
            Workspace ws = session.getWorkspace();

            //no need to set it up again, skip it if it has.
            boolean registered = RulesRepositoryAdministrator.isNamespaceRegistered( session );

            if (!registered) {
                ws.getNamespaceRegistry().registerNamespace("drools", RulesRepository.DROOLS_URI);
                
                //Note, the order in which they are registered actually does matter !
                this.registerNodeTypesFromCndFile("/node_type_definitions/tag_node_type.cnd", ws);
                this.registerNodeTypesFromCndFile("/node_type_definitions/state_node_type.cnd", ws);
                this.registerNodeTypesFromCndFile("/node_type_definitions/versionable_node_type.cnd", ws);
                this.registerNodeTypesFromCndFile("/node_type_definitions/versionable_asset_folder_node_type.cnd", ws);
                
                this.registerNodeTypesFromCndFile("/node_type_definitions/rule_node_type.cnd", ws);
                this.registerNodeTypesFromCndFile("/node_type_definitions/rulepackage_node_type.cnd", ws);
             
            }
            
            // Setup the rule repository node
            Node repositoryNode = RulesRepository.addNodeIfNew(root, RulesRepository.RULES_REPOSITORY_NAME, "nt:folder");
                    

            
            // Setup the RulePackageItem area        
            Node packageAreaNode = RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.RULE_PACKAGE_AREA, "nt:folder");
            
            // Setup the global area        
            if(!packageAreaNode.hasNode(RulesRepository.RULE_GLOBAL_AREA)){
                Node globalAreaNode = RulesRepository.addNodeIfNew(packageAreaNode, RulesRepository.RULE_GLOBAL_AREA, PackageItem.RULE_PACKAGE_TYPE_NAME);
                globalAreaNode.addNode( PackageItem.ASSET_FOLDER_NAME,  "drools:versionableAssetFolder" );
                globalAreaNode.setProperty( PackageItem.TITLE_PROPERTY_NAME,  RulesRepository.RULE_GLOBAL_AREA);
                globalAreaNode.setProperty( AssetItem.DESCRIPTION_PROPERTY_NAME, "the global area that holds sharable assets");         
                globalAreaNode.setProperty(AssetItem.FORMAT_PROPERTY_NAME,	PackageItem.PACKAGE_FORMAT);
                globalAreaNode.setProperty(PackageItem.CREATOR_PROPERTY_NAME, session.getUserID());
                Calendar lastModified = Calendar.getInstance();
                globalAreaNode.setProperty(PackageItem.LAST_MODIFIED_PROPERTY_NAME,	lastModified);
            }
            
            // Setup the Snapshot area        
            RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.PACKAGE_SNAPSHOT_AREA, "nt:folder");
                        
            //Setup the Category area                
            RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.TAG_AREA, "nt:folder");
            
            //Setup the State area                
            RulesRepository.addNodeIfNew(repositoryNode, RulesRepository.STATE_AREA, "nt:folder");
            
            //and we need the "Draft" state
            RulesRepository.addNodeIfNew( repositoryNode.getNode( RulesRepository.STATE_AREA ), StateItem.DRAFT_STATE_NAME, StateItem.STATE_NODE_TYPE_NAME );
            
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
