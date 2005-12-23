package org.drools.repo.jcr;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;

import org.drools.metamodel.Asset;
import org.drools.metamodel.DRLSourceFile;
import org.drools.metamodel.Rule;
import org.drools.metamodel.RuleSet;
import org.drools.metamodel.RuleSetConfig;
import org.drools.metamodel.RuleSetFile;
import org.drools.metamodel.VersionInfo;
import org.drools.repo.RepositoryService;
import org.drools.repo.RuleRepositoryException;

public class RepositoryServiceImpl
    implements
    RepositoryService
{
    private Session session;

    public RepositoryServiceImpl(Session session)
    {
        this.session = session;

    }

    public void createNamespace()
    {
        try
        {
            NamespaceRegistry reg = this.session.getWorkspace( ).getNamespaceRegistry( );
            if ( nameSpaceExists( reg ) )
            {
                return;
            }
            else
            {
                log( "Registering new Namespace" );
                reg.registerNamespace( RepositoryService.RULE_NAMESPACE_PREFIX,
                                       RepositoryService.RULE_NAMESPACE );
            }
        }
        catch ( RepositoryException e )
        {
            throw new RuleRepositoryException( e );
        }

    }

    public void createNewRepo()
    {
        createNamespace( );
        createRuleRepoNode( );


    }

    private void createRuleRepoNode()
    {
        try
        {
            Node rn = this.session.getRootNode( );
            if ( !rn.hasNode( nodeNameOf( "repository" ) ) )
            {
                this.session.getRootNode( ).addNode( nodeNameOf( "repository" ) );
            }
        }
        catch ( Exception e )
        {
            throw new RuleRepositoryException( "Unable to create new repository root node.",
                                               e );
        }
    }

    private String nodeNameOf(String string)
    {
        return RepositoryService.RULE_NAMESPACE_PREFIX + ":" + string;
    }

    private void log(String string)
    {
        System.out.println( "REPO:" + string );
    }

    private boolean nameSpaceExists(NamespaceRegistry registry) throws RepositoryException
    {
        String[] uris = registry.getURIs( );
        for ( int i = 0; i < uris.length; i++ )
        {
            if ( uris[i].equals( RepositoryService.RULE_NAMESPACE ) )
            {
                log( "Name space already exists." );
                return true;
            }
        }
        return false;

    }

    public void xxxxsaveNewRuleset(String name,
                               String content)
    {
        Node repo = getRepository( );
        try
        {
            Node ruleset = repo.addNode(nodeNameOf("ruleset"));
            ruleset.setProperty("content", content);
            ruleset.setProperty("ruleset-name", name);
            save();
        }
        catch ( Exception e ) {
            throw new RuleRepositoryException("Unable to add a new Ruleset called: " + name, e);
        }
    }

    private Node getRepository() 
    {
        try
        {
            return session.getRootNode().getNode(nodeNameOf("repository"));
        }
        catch ( PathNotFoundException e )
        {
            throw new RuleRepositoryException("Unable to find repository root node. Repository may not have been setup correctly.", e);
        }
        catch ( RepositoryException e )
        {
            throw new RuleRepositoryException(e);
        }
    }

    public List xxxxfindAllRuleSetDRL()
    {
        List rulesets = new ArrayList();
        Node repo = getRepository();
        try
        {
            NodeIterator nodeIt = repo.getNodes();
            while(nodeIt.hasNext()) {
                Node rulesetNode = nodeIt.nextNode();
                String content = rulesetNode.getProperty("content").getString();   
                String name = rulesetNode.getProperty("ruleset-name").getString();    
                DRLSourceFile drl = new DRLSourceFile(content, name);
                rulesets.add(drl);
            }            
        }
        catch ( RepositoryException e )
        {
            throw new RuleRepositoryException("Unable to list rulesets.", e);
        }
        return rulesets;
    }
    
    public void save() {
        try
        {
            session.save( );
        }
        catch ( Exception e )
        {
            throw new RuleRepositoryException( "Unable to save session.",
                                               e );
        }        
    }

    public void xxxxeraseAllRuleSetDRL()
    {
        
        Node repo = this.getRepository();
        NodeIterator it;
        try
        {
            it = repo.getNodes();
            while (it.hasNext()) {
                it.nextNode().remove();
            }
            save();
        }
        catch ( RepositoryException e )
        {
            throw new RuleRepositoryException("Unable to delete DRL nodes.", e);
        }
    }



    public RuleSetFile addNewRulesetFile(RuleSetFile file,
                                         String initialComment)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public RuleSetFile checkoutFile(String ruleBaseName,
                                    String ruleSetName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public RuleSetConfig checkoutRuleSetConfig(String ruleBaseName,
                                               String ruleSetName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Rule checkoutRule(String ruleBaseName,
                             String ruleSetName,
                             String ruleName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public RuleSetFile addNewFile(String ruleBaseName,
                                  RuleSetFile file)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public RuleSet addNewRuleSet(String ruleBaseName,
                                 RuleSet ruleSet)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public Rule addNewRule(String ruleBaseName,
                           String ruleSetName,
                           Rule rule)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public VersionInfo checkin(Asset asset,
                               String comment)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public VersionInfo changeStatus(Asset asset,
                                    String newStatus)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List listRuleBaseName()
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List listRuleSetNames(String ruleBase)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public RuleSet retrieveRuleSet(String ruleBaseName,
                                   String ruleSetName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public RuleSetFile retrieveRuleSetFile(String ruleBaseName,
                                           String ruleSetName)
    {
        // TODO Auto-generated method stub
        return null;
    }

    public List searchRules(Properties searchProperties)
    {
        // TODO Auto-generated method stub
        return null;
    }



}
