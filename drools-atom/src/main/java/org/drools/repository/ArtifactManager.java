package org.drools.repository;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.jcr.ItemExistsException;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.Property;
import javax.jcr.PropertyIterator;
import javax.jcr.PropertyType;
import javax.jcr.RepositoryException;
import javax.jcr.Value;

import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;


public class ArtifactManager {

    private RulesRepository repository;
    
    public static final String METADATA_TYPE_STRING = "metadata_type_string";
    public static final String METADATA_TYPE_MULTI_VALUE_STRING = "metadata_type_multi_value_string";
    
    public ArtifactManager(RulesRepository repo) {
        this.repository = repo;
        
        MetaData md = new MetaData();
        md.setMetaDataName("archived");
        md.setMetaDataType(METADATA_TYPE_STRING);
        md.setDescription("is archived or not");        
        createMetadataType(md);
        
        md = new MetaData();
        md.setMetaDataName("format");
        md.setMetaDataType(METADATA_TYPE_STRING);
        createMetadataType(md);
        
        md = new MetaData();
        md.setMetaDataName("description");
        md.setMetaDataType(METADATA_TYPE_STRING);
        createMetadataType(md);        
        
        md = new MetaData();
        md.setMetaDataName("multi-value-property");
        md.setMetaDataType(METADATA_TYPE_MULTI_VALUE_STRING);
        createMetadataType(md);
    }

    /**
     * Create artifact
     * @param Artifact
     * @throws RepositoryException
     */
    public void createArtifact(Artifact artifact) {
    	try {
    		//TODO: should allow creating an artifact if it exists already
	    	Node artifactNode = getArtifactNode(artifact.getName());
	    	artifactNode.remove(); //remove this so we get a fresh set
	    	artifactNode = getArtifactNode(artifact.getName()).addNode("jcr:content", "nt:unstructured");
	    	
	    	artifactNode.setProperty(AssetItem.TITLE_PROPERTY_NAME,
	    			artifact.getName() ); 	

	    	if (artifact.getDescription() != null) {
				artifactNode.setProperty(AssetItem.DESCRIPTION_PROPERTY_NAME,
						artifact.getDescription());
			}

            artifactNode.setProperty(VersionableItem.CHECKIN_COMMENT,
                                  "Initial");
            
            Calendar lastModified = Calendar.getInstance();
            artifactNode.setProperty(AssetItem.LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            Map<String, List<String>> metadata = artifact.getMetadata();

            if (artifact.getMetadata() != null) {
				for (Iterator<Map.Entry<String, List<String>>> iterator = metadata
						.entrySet().iterator(); iterator.hasNext();) {
					Map.Entry<String, List<String>> en = iterator.next();
					String key = en.getKey();
					List<String> targets = en.getValue();
					if (targets == null) {
						targets = new ArrayList<String>();
					}
					if(targets.size() == 1) {
					    artifactNode.setProperty(key, targets.get(0));
					} else {
						artifactNode.setProperty(key, targets.toArray(new String[targets.size()]));
					}
				}
			}

	    	if (!artifact.isBinary() && artifact.getContent() != null) {
				artifactNode.setProperty(AssetItem.CONTENT_PROPERTY_NAME,
						artifact.getContent());
			} else {
				if(artifact.getSrcLink() != null) {
					//If its a binary content, normally we will have a link to the actual source. We do not store 
					//the binary content direclty on artifact node.
					artifactNode.setProperty("drools:srcLink",
							artifact.getSrcLink());					
				} else {
					//TODO
				}
			}
	        
	    	this.repository.save();
    	} catch (RepositoryException e) {
            if ( e instanceof ItemExistsException ) {
                throw new RulesRepositoryException( "An artifact of that name already exists in that package.",
                                                    e );
            } else {
                throw new RulesRepositoryException( e );
            }
    	}
    }
    
    /**
     * update artifact
     * @param Artifact
     * @throws RepositoryException
     */
    public void updateArtifact(Artifact artifact) {
    	try {
	    	if (!getArtifactNode(artifact.getName()).hasNode("jcr:content")) {
	    		throw new RulesRepositoryException("Artifact " + artifact.getName() + " is not found");
	    	}
	    	Node artifactNode = getArtifactNode(artifact.getName()).getNode("jcr:content");
	    	
	    	if (artifact.getDescription() != null) {
				artifactNode.setProperty(AssetItem.DESCRIPTION_PROPERTY_NAME,
						artifact.getDescription());
			}

            Calendar lastModified = Calendar.getInstance();
            artifactNode.setProperty(AssetItem.LAST_MODIFIED_PROPERTY_NAME, lastModified);
            
            Map<String, List<String>> metadata = artifact.getMetadata();

            if (metadata != null) {
				for (Iterator<Map.Entry<String, List<String>>> iterator = metadata
						.entrySet().iterator(); iterator.hasNext();) {
					Map.Entry<String, List<String>> en = iterator.next();
					String key = en.getKey();
					List<String> targets = en.getValue();
					if (targets == null) {
						targets = new ArrayList<String>();
					}
					if(targets.size() == 1) {
					    artifactNode.setProperty(key, targets.get(0));
					} else {
						artifactNode.setProperty(key, targets.toArray(new String[targets.size()]));
					}
				}
			}

	    	if (!artifact.isBinary() && artifact.getContent() != null) {
				artifactNode.setProperty(AssetItem.CONTENT_PROPERTY_NAME,
						artifact.getContent());
			} else {
				if(artifact.getSrcLink() != null) {
					//If its a binary content, normally we will have a link to the actual source. We do not store 
					//the binary content direclty on artifact node.
					artifactNode.setProperty("drools:srcLink",
							artifact.getSrcLink());					
				} else {
					//TODO
				}
			}
	        
	    	this.repository.save();
    	} catch (RepositoryException e) {
            if ( e instanceof ItemExistsException ) {
                throw new RulesRepositoryException( "An artifact of that name already exists in that package.",
                                                    e );
            } else {
                throw new RulesRepositoryException( e );
            }
    	}
    }
    
    public Artifact getArtifact(String artifactName) {
    	try {   		
	    	if (!getArtifactNode(artifactName).hasNode("jcr:content")) {
	    		throw new RulesRepositoryException("Artifact " + artifactName + " is not found");
	    	}
	    	
    		Artifact artifact = new Artifact();
	    	Map<String, List<String>> metadata = new HashMap<String, List<String>>(10);
	    	
	    	Node permsNode = getArtifactNode(artifactName).getNode("jcr:content");
	    	PropertyIterator it = permsNode.getProperties();

	    	while (it.hasNext()) {
	    		Property p = (Property) it.next();
	    		String name = p.getName();
	    		if (!name.startsWith("jcr")) {
	    			if(AssetItem.TITLE_PROPERTY_NAME.equals(name)) {
	    				artifact.setName(p.getValue().getString());
	    				continue;
	    			} else if(AssetItem.DESCRIPTION_PROPERTY_NAME.equals(name)) {
	    				artifact.setDescription(p.getValue().getString());
	    				continue;
	    			} else if(AssetItem.CONTENT_PROPERTY_NAME.equals(name)) {
	    				artifact.setContent(p.getValue().getString());
	    				continue;
	    			} else if(AssetItem.CONTENT_PROPERTY_BINARY_NAME.equals(name)) {
	    				artifact.setBinaryContent(p.getValue().getStream());
	    				continue;
	    			} else if(AssetItem.LAST_MODIFIED_PROPERTY_NAME.equals(name)) {
	    				artifact.setLastModified(p.getValue().getDate());
	    				continue;
	    			} else if("drools:srcLink".equals(name)) {
	    				artifact.setSrcLink(p.getValue().getString());
	    				continue;
	    			} 

	    			//other properties are treated as meta data
	    			if (p.getDefinition().isMultiple()) {
			    		Value[] vs = p.getValues();
			    		List<String> perms = new ArrayList<String>();
			    		for (int i = 0; i < vs.length; i++) {
							perms.add(vs[i].getString());
						}
			    		metadata.put(name, perms);
	    			} else {
	    				Value v = p.getValue();
	    				List<String> perms = new ArrayList<String>(1);
	    				perms.add(v.getString());
	    				metadata.put(name, perms);
	    			}
	    		}
	    	}
	    	artifact.setMetadata(metadata);
	    	if(artifact.getName() == null) {
	    		artifact.setName(artifactName);
	    	}
	    	
	    	
	    	return artifact;
    	} catch (RepositoryException e) {
    		throw new RulesRepositoryException(e);
    	}   	
    }
    
	//TODO: add meta-data. For example, if it is esb type, we need to extract jboss-esb.xml from the esb
	//jar, figure out service category and service name etc as the meta-data.
    //Create two nodes for an esb jar type. one under esbs, one under artifacts
    public void createEBSJar(String esbJarName, InputStream data) {
    	try {
    		//REVISIT: should not allow create a new node if it exists already
    		
	    	Node artifactNode = getESBJarNode(esbJarName);
	    	artifactNode.remove(); //remove this so we get a fresh set
	    	artifactNode = getESBJarNode(esbJarName).addNode("jcr:content", "nt:unstructured");
	    	
	    	artifactNode.setProperty(AssetItem.CONTENT_PROPERTY_BINARY_NAME,
                    data);            
	    	
			
			Artifact newArtifact = new Artifact();
			newArtifact.setName(esbJarName);
			newArtifact.setSrcLink("/repository/esbs/" + esbJarName);
	        createArtifact(newArtifact);
		
	    	this.repository.save();
    	} catch (RepositoryException e) {
			throw new RulesRepositoryException(e);
		}    	
    }
    
    public InputStream getEBSJar(String esbJarName) {
    	try {
 	    	if (!getESBJarNode(esbJarName).hasNode("jcr:content")) {
	    		throw new RulesRepositoryException("ESBJar " + esbJarName + " is not found");
	    	}
 	    	
	    	InputStream is = null;	    	
	    	Node node = getESBJarNode(esbJarName).getNode("jcr:content");
	    	PropertyIterator it = node.getProperties();
	    	
	    	while (it.hasNext()) {
	    		Property p = (Property) it.next();
	    		String name = p.getName();
	    		if (!name.startsWith("jcr")) {
	    			if(AssetItem.CONTENT_PROPERTY_BINARY_NAME.equals(name)) {
	    				is = p.getValue().getStream();
	    				break;
	    			}
	    		}
	    	}
	    	
	    	return is;    				
	     
    	} catch (RepositoryException e) {
			throw new RulesRepositoryException(e);
		}    	
    }
 
    /**
     * Create a metadata type
     * @param metadataName
     * @param type
     * @throws RepositoryException
     */
    public void createMetadataType(MetaData metaData) {
    	try {
	    	Node metadataTypeNode = getMetaDataTypeNode(metaData.getMetaDataName());
	    	metadataTypeNode.remove(); //remove this so we get a fresh set
	    	metadataTypeNode = getMetaDataTypeNode(metaData.getMetaDataName()).addNode("jcr:content", "nt:unstructured");
	    	
	    	metadataTypeNode.setProperty("metadata_type", metaData.getMetaDataType()); 	
	    	
	    	//JCR wont create a property if its value is null
	    	if(metaData.getDescription() != null) {
	    	metadataTypeNode.setProperty(AssetItem.DESCRIPTION_PROPERTY_NAME, metaData.getDescription()); 
	    	} else {
		    	metadataTypeNode.setProperty(AssetItem.DESCRIPTION_PROPERTY_NAME, ""); 	    		
	    	}
        
	    	this.repository.save();
    	} catch (RepositoryException e) {
			throw new RulesRepositoryException(e);
		}
	}
    
    public Map<String, MetaData> getMetadataTypes() {
       	Map<String, MetaData> metaDataList = new HashMap<String, MetaData>();

 		try{
    		Node root = this.repository.getSession().getRootNode().getNode(RulesRepository.RULES_REPOSITORY_NAME);
        	NodeIterator nodes = getNode(root, "metadata_names", "nt:folder").getNodes();
         	while (nodes.hasNext()) {
         		MetaData md = new MetaData();         		
        		Node node = nodes.nextNode();
        		md.setMetaDataName(node.getName());
        		md.setMetaDataType(node.getNode("jcr:content").getProperty("metadata_type").getString());
        		md.setDescription(node.getNode("jcr:content").getProperty(AssetItem.DESCRIPTION_PROPERTY_NAME).getString());
        		metaDataList.put(node.getName(), md);
        	}

 		} catch (RepositoryException e) {
 			throw new RulesRepositoryException(e);
 		}
 		
    	return metaDataList;
    }
    
    
    public void deleteMetadataType(String metaDataName) {
 		try{ 	 		
 	    	Node metadataTypeNode = getMetaDataTypeNode(metaDataName);
 	    	metadataTypeNode.remove();

 		} catch (RepositoryException e) {
 			throw new RulesRepositoryException(e);
 		}
    }
    
	private Node getArtifactNode(String artifactName)
			throws RepositoryException {
		Node root = this.repository.getSession().getRootNode().getNode(RulesRepository.RULES_REPOSITORY_NAME);
    	Node node = getNode(getNode(root, "artifacts", "nt:folder"), artifactName, "nt:file");
		return node;
	}

	private Node getMetaDataTypeNode(String metadataType)
			throws RepositoryException {
		Node root = this.repository.getSession().getRootNode().getNode(RulesRepository.RULES_REPOSITORY_NAME);
    	Node node = getNode(getNode(root, "metadata_names", "nt:folder"), metadataType, "nt:file");
		return node;
	}
	
	private Node getESBJarNode(String esbJarName)
			throws RepositoryException {
		Node root = this.repository.getSession().getRootNode().getNode(
				RulesRepository.RULES_REPOSITORY_NAME);
		Node node = getNode(getNode(root, "esbs", "nt:folder"),
				esbJarName, "nt:file");
		return node;
	}	
    /**
     * Gets or creates a node.
     */
	private Node getNode(Node node, String name, String nodeType) throws RepositoryException {
		Node resultNode;
		if (!node.hasNode(name)) {
			resultNode = node.addNode(name, nodeType);
    	} else {
    		resultNode = node.getNode(name);
    	}
		return resultNode;
	}

	private boolean isValideArtifactName(String artifactName) {
		if("".equals(artifactName.trim()) || artifactName.trim().length() == 0) {
			return false;
		}
		return true;
	}
}
