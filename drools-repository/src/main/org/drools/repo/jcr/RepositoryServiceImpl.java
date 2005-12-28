package org.drools.repo.jcr;

import java.util.ArrayList;
import java.util.List;

import javax.jcr.ItemExistsException;
import javax.jcr.NamespaceRegistry;
import javax.jcr.Node;
import javax.jcr.NodeIterator;
import javax.jcr.PathNotFoundException;
import javax.jcr.RepositoryException;
import javax.jcr.Session;
import javax.jcr.lock.LockException;
import javax.jcr.nodetype.ConstraintViolationException;
import javax.jcr.version.VersionException;

import org.drools.metamodel.DRLSourceFile;
import org.drools.metamodel.RuleDefinition;
import org.drools.repo.RepositoryService;
import org.drools.repo.RuleRepositoryException;

public class RepositoryServiceImpl
    implements
    RepositoryService {
    private Session session;

    public RepositoryServiceImpl(Session session){
        this.session = session;

    }

    public void createNamespace(){
        try {
            NamespaceRegistry reg = this.session.getWorkspace( ).getNamespaceRegistry( );
            if ( nameSpaceExists( reg ) ) {
                return;
            }
            else {
                log( "Registering new Namespace" );
                reg.registerNamespace( RepositoryService.RULE_NAMESPACE_PREFIX,
                                       RepositoryService.RULE_NAMESPACE );
            }
        }
        catch ( RepositoryException e ) {
            throw new RuleRepositoryException( e );
        }

    }

    /**
     * This will create a brand new repository. All rules are stored in the
     * drools:repository node.
     */
    public void createNewRepo(){
        createNamespace( );
        createRuleRepoNode( );
    }

    /**
     * This creates the root drools repository node, if it doesn't already
     * exist.
     * 
     */
    private void createRuleRepoNode(){
        try {
            Node rn = this.session.getRootNode( );
            if ( !rn.hasNode( nodeNameOf( "repository" ) ) ) {
                this.session.getRootNode( ).addNode( nodeNameOf( "repository" ) );
            }
        }
        catch ( Exception e ) {
            throw new RuleRepositoryException( "Unable to create new repository root node.",
                                               e );
        }
    }

    private String nodeNameOf(String string){
        return RepositoryService.RULE_NAMESPACE_PREFIX + ":" + string;
    }

    private void log(String string){
        System.out.println( "REPO:" + string );
    }

    private boolean nameSpaceExists(NamespaceRegistry registry) throws RepositoryException{
        String[] uris = registry.getURIs( );
        for ( int i = 0; i < uris.length; i++ ) {
            if ( uris[i].equals( RepositoryService.RULE_NAMESPACE ) ) {
                log( "Name space already exists." );
                return true;
            }
        }
        return false;

    }

    public void xxxxsaveNewRuleset(String name,
                                   String content){
        Node repo = getRepository( );
        try {
            Node ruleset = repo.addNode( nodeNameOf( "ruleset" ) );
            ruleset.setProperty( "content",
                                 content );
            ruleset.setProperty( "ruleset-name",
                                 name );
            session.save( );
        }
        catch ( Exception e ) {
            throw new RuleRepositoryException( "Unable to add a new Ruleset called: " + name,
                                               e );
        }
    }

    private Node getRepository(){
        try {
            return session.getRootNode( ).getNode( nodeNameOf( "repository" ) );
        }
        catch ( PathNotFoundException e ) {
            throw new RuleRepositoryException( "Unable to find repository root node. Repository may not have been setup correctly.",
                                               e );
        }
        catch ( RepositoryException e ) {
            throw new RuleRepositoryException( e );
        }
    }

    public List xxxxfindAllRuleSetDRL(){
        List rulesets = new ArrayList( );
        Node repo = getRepository( );
        try {
            NodeIterator nodeIt = repo.getNodes( );
            while ( nodeIt.hasNext( ) ) {
                Node rulesetNode = nodeIt.nextNode( );
                String content = rulesetNode.getProperty( "content" ).getString( );
                String name = rulesetNode.getProperty( "ruleset-name" ).getString( );
                DRLSourceFile drl = new DRLSourceFile( content,
                                                       name );
                rulesets.add( drl );
            }
        }
        catch ( RepositoryException e ) {
            throw new RuleRepositoryException( "Unable to list rulesets.",
                                               e );
        }
        return rulesets;
    }

    public void xxxxeraseAllRuleSetDRL(){

        Node repo = this.getRepository( );
        NodeIterator it;
        try {
            it = repo.getNodes( );
            while ( it.hasNext( ) ) {
                it.nextNode( ).remove( );
            }
            session.save( );
        }
        catch ( RepositoryException e ) {
            throw new RuleRepositoryException( "Unable to delete DRL nodes.",
                                               e );
        }
    }

    public RuleDefinition addNewRule(String ruleBase,
                                     String ruleSet,
                                     RuleDefinition rule){
        try {
            Node ruleSetNode = getOrCreateRuleSetNode( ruleBase,
                                                       ruleSet );
            if ( ruleSetNode.hasNode( nodeNameOf( rule.getRuleName( ) ) ) ) {
                throw new RuleRepositoryException( "Rule with that name [" + rule.getRuleName( ) 
                                                   + "] already exists in ruleset [" 
                                                   + ruleSet + "]" );
            }
            Node newRuleNode = ruleSetNode.addNode( nodeNameOf( rule.getRuleName( ) ) );
            newRuleNode.setProperty( nodeNameOf( "fragment" ),
                                     rule.getFragment( ) );


        }
        catch ( PathNotFoundException e ) {
            throw new RuleRepositoryException( "Not able to locate ruleset or base to store rule.",
                                               e );
        }
        catch ( RepositoryException e ) {
            throw new RuleRepositoryException( e );
        }

        return rule;
    }

    private Node getOrCreateRuleSetNode(String ruleBase,
                                        String ruleSet) throws PathNotFoundException,
                                                       ItemExistsException,
                                                       VersionException,
                                                       ConstraintViolationException,
                                                       LockException,
                                                       RepositoryException{
        Node ruleBaseNode = getOrCreateRuleBaseNode( ruleBase );
        if ( ruleBaseNode.hasNode( nodeNameOf( ruleSet ) ) ) {
            return ruleBaseNode.getNode( nodeNameOf( ruleSet ) );
        }
        else {
            return ruleBaseNode.addNode( nodeNameOf( ruleSet ) );
        }
    }

    private Node getOrCreateRuleBaseNode(String ruleBase) throws PathNotFoundException,
                                                         ItemExistsException,
                                                         VersionException,
                                                         ConstraintViolationException,
                                                         LockException,
                                                         RepositoryException{
        Node repo = getRepository( );
        String nodeName = nodeNameOf( ruleBase );
        if ( repo.hasNode( nodeName ) ) {
            return repo.getNode( nodeName );
        }
        else {
            return repo.addNode( nodeName );
        }
    }

    public void save(){
        try {
            session.save( );
        }
        catch ( Exception e ) {
            throw new RuleRepositoryException( "Unable to save the session.",
                                               e );
        }
    }

    public void removeRule(String ruleBase,
                           String ruleSet,
                           String ruleName){
        try {
            String path = calcPath( new String[]{ruleBase, ruleSet, ruleName} );
            Node ruleNode = getRepository( ).getNode( path );
            ruleNode.remove( );
        }
        catch ( PathNotFoundException e ) {
            throw new RuleRepositoryException( "Unable to find rule to remove.",
                                               e );
        }
        catch ( RepositoryException e ) {
            throw new RuleRepositoryException( e );
        }

    }

    public void removeRuleSet(String ruleBase,
                              String ruleSet){
        String[] path = new String[] {ruleBase, ruleSet};
        try {
            getRepository().getNode(calcPath(path)).remove();            
        }
        catch ( Exception e )  {
            throw new RuleRepositoryException("Unable to remove the ruleset [" + ruleSet + "]", e);
        }
    }

    /**
     * convert a list of names to a path to retrieve.
     */
    String calcPath(String[] names){
        StringBuffer buf = new StringBuffer( );
        for ( int i = 0; i < names.length; i++ ) {
            buf.append( nodeNameOf( names[i] ) );
            if ( i != names.length - 1 ) {
                buf.append( '/' );
            }
        }
        return buf.toString( );
    }

    public RuleDefinition retrieveRule(String ruleBase,
                                       String ruleSet,
                                       String ruleName){
        try {
            Node ruleNode = getRepository().getNode(calcPath(new String[] {ruleBase, ruleSet, ruleName}));
            RuleDefinition rule = new RuleDefinition();
            rule.setFragment(ruleNode.getProperty(nodeNameOf("fragment")).getString());

            return rule;
        }
        catch ( PathNotFoundException e ) {
            throw new RuleRepositoryException("Rule does not exist.", e);
        }
        catch ( RepositoryException e ) {
            throw new RuleRepositoryException(e);
        }
    }

}
