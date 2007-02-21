package org.drools.brms.client.rpc.mock;

import java.util.Date;
import java.util.HashMap;

import org.drools.brms.client.common.AssetFormats;
import org.drools.brms.client.modeldriven.SuggestionCompletionEngine;
import org.drools.brms.client.modeldriven.brxml.ActionAssertFact;
import org.drools.brms.client.modeldriven.brxml.ActionFieldValue;
import org.drools.brms.client.modeldriven.brxml.ActionRetractFact;
import org.drools.brms.client.modeldriven.brxml.ActionSetField;
import org.drools.brms.client.modeldriven.brxml.CompositeFactPattern;
import org.drools.brms.client.modeldriven.brxml.Constraint;
import org.drools.brms.client.modeldriven.brxml.DSLSentence;
import org.drools.brms.client.modeldriven.brxml.DSLSentenceFragment;
import org.drools.brms.client.modeldriven.brxml.FactPattern;
import org.drools.brms.client.modeldriven.brxml.IAction;
import org.drools.brms.client.modeldriven.brxml.IPattern;
import org.drools.brms.client.modeldriven.brxml.RuleModel;
import org.drools.brms.client.rpc.MetaData;
import org.drools.brms.client.rpc.PackageConfigData;
import org.drools.brms.client.rpc.RepositoryServiceAsync;
import org.drools.brms.client.rpc.RuleAsset;
import org.drools.brms.client.rpc.RuleContentText;
import org.drools.brms.client.rpc.RuleModelData;
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
            meta.format = AssetFormats.BUSINESS_RULE;
            RuleModelData data = new RuleModelData();
            data.completionEngine = getDummySuggestionEngine();
            data.model = getDummyData();
            asset.content = data;
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


    private SuggestionCompletionEngine getDummySuggestionEngine() {
        SuggestionCompletionEngine com = new SuggestionCompletionEngine();
        
        com.factTypes = new String[] {"Person", "Vehicle"};
        com.fieldTypes = new HashMap() {{
            put("Person.age", SuggestionCompletionEngine.TYPE_NUMERIC);
            put("Person.name", "String");
            put("Vehicle.type", "String");
            put("Vehcile.make", "String");
        }};

        com.fieldsForType = new HashMap() {{
           put("Person", new String[] {"age", "name"});
           put("Vehicle", new String[] {"type", "make"});
        }};
        
        
        DSLSentence sen = new DSLSentence();
        sen.elements = new DSLSentenceFragment[2];
        sen.elements[0] = new DSLSentenceFragment("This is a dsl expression", false);
        sen.elements[1] = new DSLSentenceFragment("(something)", true);
        com.conditionDSLSentences = new DSLSentence[] {sen};

        
        sen = new DSLSentence();
        sen.elements = new DSLSentenceFragment[3];
        sen.elements[0] = new DSLSentenceFragment("Send an email to [", false);
        sen.elements[1] = new DSLSentenceFragment("(someone)", true);
        sen.elements[2] = new DSLSentenceFragment("]", false);
        
        
        DSLSentence sen2 = new DSLSentence();
        sen2.elements = new DSLSentenceFragment[1];
        sen2.elements[0] = new DSLSentenceFragment("do nothing", false);        
        
        com.actionDSLSentences = new DSLSentence[] {sen, sen2};
        
        
        return com;
    }


    private RuleModel getDummyData() {
        RuleModel model = new RuleModel();
        
        model.lhs = new IPattern[3];
        
        FactPattern p1 = new FactPattern();
        FactPattern p2 = new FactPattern();
        CompositeFactPattern p3 = new CompositeFactPattern();
        
        
        model.lhs[0] = p1;
        model.lhs[1] = p2;
        model.lhs[2] = p3;
        
        DSLSentence dsl = new DSLSentence();
        dsl.elements = new DSLSentenceFragment[2];
        dsl.elements[0] = new DSLSentenceFragment("There is a Storm alert of type", false);
        dsl.elements[1] = new DSLSentenceFragment("(code here)", true);
        
        model.addLhsItem( dsl );
        
        dsl = new DSLSentence();
        dsl.elements = new DSLSentenceFragment[2];
        dsl.elements[0] = new DSLSentenceFragment("- severity rating is not more than", false);
        dsl.elements[1] = new DSLSentenceFragment("(code here)", true);
        
        model.addLhsItem( dsl );
            
        
        
        
        p1.factType = "Person";
        p1.constraints = new Constraint[2];
        p1.constraints[0] = new Constraint();
        p1.constraints[1] = new Constraint();
        p1.constraints[0].fieldName = "age";
        p1.constraints[0].operator = "<";
        p1.constraints[0].value = "42";

        p1.constraints[1].fieldName = "name";
        p1.constraints[1].operator = "==";
        p1.constraints[1].value = "Bob";
        p1.constraints[1].fieldBinding = "n";
  
        
        
        p2.factType = "Vehicle";
        p2.boundName = "car1";
        p2.constraints = new Constraint[1];
        p2.constraints[0] = new Constraint();
        p2.constraints[0].fieldName = "type";
        p2.constraints[0].operator = "!=";
        
        p3.type = "not";
        p3.patterns = new FactPattern[1];
        FactPattern i1 = new FactPattern("Vehicle");
        i1.constraints = new Constraint[1];
        i1.constraints[0] = new Constraint();
        i1.constraints[0].fieldName = "type";
        i1.constraints[0].operator = "==";
        
        p3.patterns[0] = i1;
        
        ActionSetField set = new ActionSetField();
        set.variable = "car1";
        set.fieldValues = new ActionFieldValue[1];
        set.fieldValues[0] = new ActionFieldValue();
        set.fieldValues[0].field = "type";
        
        ActionAssertFact fact = new ActionAssertFact();
        fact.factType = "Person";
        fact.fieldValues = new ActionFieldValue[2];
        fact.fieldValues[0] = new ActionFieldValue("name", "Mike");
        fact.fieldValues[1] = new ActionFieldValue("age", "42");
        
        ActionRetractFact retract = new ActionRetractFact("car1");
        
        model.rhs = new IAction[3];
        model.rhs[0] = set;
        model.rhs[1] = fact;
        model.rhs[2] = retract;
        
        return model;
        
    }

    

    public void checkinVersion(RuleAsset a,
                               AsyncCallback cb) {
        if (a.metaData.coverage.equals( "fail" )) {
            cb.onFailure( new SerializableException("This is an error") );
        } else {
            cb.onSuccess( "alanparsons" );
        }
        
        
    }



    public void loadAssetHistory(String p0,
                                 AsyncCallback cb) {
        cb.onSuccess( null );
    }



    public void restoreVersion(String p0,
                               String p1,
                               String p2,
                               AsyncCallback cb) {
        cb.onSuccess( null );
        
    }



    public void createPackage(String p0,
                              String p1,
                              AsyncCallback cb) {
        cb.onSuccess( "UUIDHERE" );
        
    }



    public void loadPackage(String name,
                            AsyncCallback cb) {
        cb.onSuccess( new PackageConfigData() );
        
    }



    public void savePackage(PackageConfigData p0,
                            AsyncCallback cb) {
        cb.onSuccess( "UUID" );        
    }



    public void listAssetsByFormat(String p0, String p1,
                                   int p2,
                                   int p3,
                                   AsyncCallback cb) {
        loadRuleListForCategories( "/", cb );        
    }



    public void createState(String p0,
                            AsyncCallback cb) {
        cb.onSuccess( "XXX" );
        
    }



    public void listStates(AsyncCallback cb) {
        cb.onSuccess( new String[0] );
        
    }



    public void changeState(String p0,
                            String p1,
                            boolean p2,
                            AsyncCallback cb) {
        cb.onSuccess( null );
        
    }



    public void listAssetsByFormat(String p0,
                                   String[] p1,
                                   int p2,
                                   int p3,
                                   AsyncCallback cb) {
        cb.onSuccess( null );
        
    }
    
    

}
