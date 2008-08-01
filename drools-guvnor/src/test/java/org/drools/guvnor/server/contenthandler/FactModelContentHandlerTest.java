package org.drools.guvnor.server.contenthandler;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.factmodel.FactMetaModel;
import org.drools.guvnor.client.factmodel.FactModels;
import org.drools.guvnor.client.factmodel.FieldMetaModel;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleContentText;
import org.drools.guvnor.server.util.TestEnvironmentSessionHelper;
import org.drools.repository.AssetItem;
import org.drools.repository.PackageItem;
import org.drools.repository.RulesRepository;

public class FactModelContentHandlerTest extends TestCase {

    public void testToDrl() {

        List<FieldMetaModel> fields = new ArrayList<FieldMetaModel>();
        fields.add(new FieldMetaModel("f1", "int"));
        fields.add(new FieldMetaModel("f2", "String"));

        FactMetaModel mm = new FactMetaModel("FooBar", fields);

        FactModelContentHandler ch = new FactModelContentHandler();
        String drl = ch.toDRL(mm);
        assertNotNull(drl);
        System.err.println(drl);
        assertEquals("declare FooBar\n\tf1: int\n\tf2: String\nend", drl);


        FactMetaModel mm2 = new FactMetaModel("BooBah", new ArrayList());
        List<FactMetaModel> models = new ArrayList<FactMetaModel>();
        models.add(mm);
        models.add(mm2);

        drl = ch.toDRL(models);
        System.err.println(drl);
        assertTrue(drl.indexOf("FooBar") > -1);
        assertTrue(drl.indexOf("BooBah") > drl.indexOf("FooBar"));
    }

    public void testFromDrl()  throws Exception {
    	String drl = "declare FooBar\n\tf1: int\n\tf2: String\nend";

    	FactModelContentHandler ch = new FactModelContentHandler();
    	List<FactMetaModel> list = ch.toModel(drl);
    	assertEquals(1, list.size());
    	FactMetaModel mm = list.get(0);
    	assertEquals("FooBar", mm.name);
    	assertEquals(2, mm.fields.size());
    	for (int i = 0; i < mm.fields.size(); i++) {
        	FieldMetaModel fm = (FieldMetaModel) mm.fields.get(1);
        	if (fm.name.equals("f1")) {
	        	assertEquals("f1", fm.name);
	        	assertEquals("int", fm.type);
        	} else {
            	assertEquals("f2", fm.name);
            	assertEquals("String", fm.type);
        	}
		}



    	drl = "declare FooBar\n\t @role(event)  \nend";
    	try {
    		ch.toModel(drl);
    		fail("should not parse this");
    	} catch (DroolsParserException e) {
    		assertNotNull(e.getMessage());
    	}






    }


    public void testFromEmptyDrl() throws Exception {
    	String drl = "";

    	FactModelContentHandler ch = new FactModelContentHandler();
    	List<FactMetaModel> list = ch.toModel(drl);
    	assertNotNull(list);


    }


    public void testStore() throws Exception {
    	FactModelContentHandler ch = new FactModelContentHandler();

        RulesRepository repo = new RulesRepository( TestEnvironmentSessionHelper.getSession() );
        PackageItem pkg = repo.loadDefaultPackage();
        AssetItem asset = pkg.addAsset( "testDeclaredTypeStore", "" );
        asset.updateFormat("model.drl");
        asset.updateContent("declare Foo\n name: String\n end");
    	asset.checkin("");

    	RuleAsset ass = new RuleAsset();
    	ch.retrieveAssetContent(ass, pkg, asset);
    	assertTrue(ass.content instanceof FactModels);
    	FactModels fm = (FactModels) ass.content;

    	assertEquals(1, fm.models.size());
    	FactMetaModel mm = (FactMetaModel) fm.models.get(0);
    	assertEquals(1, mm.fields.size());
    	assertEquals("Foo", mm.name);

    	FieldMetaModel fmm = (FieldMetaModel) mm.fields.get(0);
    	assertEquals("name", fmm.name);

    	mm.fields.add(new FieldMetaModel("age", "int"));

    	ch.storeAssetContent(ass, asset);

    	assertTrue(asset.getContent().indexOf("age: int") > -1);


    	asset.updateContent("rubbish here");
    	asset.checkin("");

    	ch.retrieveAssetContent(ass, pkg, asset);
    	assertTrue(ass.content instanceof RuleContentText);

    	ch.storeAssetContent(ass, asset);

    	assertEquals("rubbish here", asset.getContent());

    }
}

