package org.drools.guvnor.server.contenthandler;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.drools.guvnor.client.factmodel.FactMetaModel;
import org.drools.guvnor.client.factmodel.FieldMetaModel;

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
    	FieldMetaModel fm = (FieldMetaModel) mm.fields.get(1);
    	assertEquals("f1", fm.name);
    	assertEquals("int", fm.type);

    	fm = (FieldMetaModel) mm.fields.get(0);
    	assertEquals("f2", fm.name);
    	assertEquals("String", fm.type);

    }
}

