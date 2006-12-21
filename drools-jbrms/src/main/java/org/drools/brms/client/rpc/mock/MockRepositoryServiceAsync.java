package org.drools.brms.client.rpc.mock;

import java.util.Date;

import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.brms.client.rpc.TableConfig;
import org.drools.brms.client.rpc.TableDataResult;
import org.drools.brms.client.rpc.TableDataRow;

import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.rpc.SerializableException;

/**
 * This is a repository back end simulator. 
 */
public class MockRepositoryServiceAsync
    implements
    RepositoryServiceAsync {


    public void loadChildCategories(String categoryPath,
                                 AsyncCallback callback) {

        final AsyncCallback cb = callback;
        final String cat = categoryPath;
        Timer t = new Timer() {
            public void run() {
                log("loadChildCategories", "loading cat path: " + cat);
                if (cat.indexOf( "HR" ) > -1 ) {
                    cb.onSuccess( new String[] { "Leave", "Payroll", "Draft"} );
                } else {

                    cb.onSuccess( new String[] { "HR", "Finance", "Procurement"} );
                }
            }            
        };        
        t.schedule( 500 );
        
    }
    
    
    
    private void log(String serviceName,
                     String message) {
        System.out.println("[" + serviceName + "] " + message);
    }



    public void loadRuleListForCategories(String categoryPath,
                                          AsyncCallback callback)  {
        log("loading rule list", "for cat path: " + categoryPath);
        TableDataResult result = new TableDataResult();
        result.numberOfRows = 42;
        
        result.data = new TableDataRow[42];
        
        for (int i = 0; i < 42; i++) {
            TableDataRow row = new TableDataRow();
            row.id = "woozle" + i;
            row.format = "Rule";
            row.values = new String[] {"name " + i, "another", "yeah", "blah"};        
            result.data[i] = row;
        }

        
        
        callback.onSuccess( result );
        
    }



    public void loadTableConfig(String listName,
                                AsyncCallback callback) {
        log("loading table config", listName);
        final TableConfig config = new TableConfig();
        final AsyncCallback cb = callback;
        Timer t = new Timer() {

            public void run() {
                config.headers = new String[] {"name", "status", "last updated by", "version"};
                config.rowsPerPage = 30;
                cb.onSuccess( config );
            }
            
        };
        t.schedule( 300 );

        
    }



    public void createCategory(String path,
                               String name,
                               String description,
                               AsyncCallback callback) {
        log( "createCategory", "Creating cat in " + path + " called " + name );
        callback.onSuccess( new Boolean(true) );
        
    }



    public void createNewRule(String name,
                           String description,
                           String initialCategory, String initialPackage, String format, AsyncCallback callback) {
        
        System.out.println("creating rule:" + name);
        System.out.println("creating rule description:" + description);
        System.out.println("creating rule initialCategory:" + initialCategory);
        System.out.println("creating rule initialPackage:" + initialPackage);
        System.out.println("creating rule format:" + format);
        
        if (name.equals( "foo" )) {
            callback.onFailure( new SerializableException("thats naughty") );
        } else {
            callback.onSuccess( "UUID-1234567890" );
        }
        
        
    }



    public void listRulePackages(AsyncCallback callback) {
        callback.onSuccess( new String[] {"a package"} );        
    }



    public void loadRuleAsset(String uuid,
                          AsyncCallback cb) {
        
        log( "loadAsset", "loading UUID"  + uuid);
        final RuleAsset asset = new RuleAsset();
        MetaData meta = new MetaData();
        meta.categories = new String[] {"Approval", "Age related"};
        meta.name = "age rejection 1";
        meta.versionNumber = "2";
        meta.createdDate = new Date();
        if (uuid.endsWith( "1" )) {
            meta.format = "DRL";
            RuleContentText text = new RuleContentText();
            asset.content = text;
            text.content = "rule la\n\twhen\n\t\tSomething() ...";
            
        } else {
            meta.format = "DSL";
        }
        
        asset.metaData = meta;
        
        final AsyncCallback finalCb = cb;
        Timer t = new Timer() {

            public void run() {
                finalCb.onSuccess( asset );
            }
            
        };
        t.schedule( 400 );
        
        
    }



    public void checkinVersion(RuleAsset a,
                               AsyncCallback cb) {
        if (a.metaData.coverage.equals( "fail" )) {
            cb.onFailure( new SerializableException("This is an error") );
        } else {
            cb.onSuccess( "alanparsons" );
        }
        
        
    }
    
    

}
