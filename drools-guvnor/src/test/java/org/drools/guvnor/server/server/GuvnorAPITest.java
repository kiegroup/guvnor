/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.drools.guvnor.server.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;
import org.drools.guvnor.server.GuvnorAPIServlet;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author esteban
 */
public class GuvnorAPITest {

    public GuvnorAPITest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testExtract() throws Exception {
        String json =
                "{\"resourceId\":\"oryx-canvas123\",\"childShapes\":[{\"dockers\":[{\"y\":44.5,\"x\":52.5},{\"y\":15,\"x\":15}],\"bounds\":{\"lowerRight\":{\"y\":179.5,\"x\":229.5},\"upperLeft\":{\"y\":179.5,\"x\":229.5}},\"resourceId\":\"_6-oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\",\"target\":{\"resourceId\":\"oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\"}]},{\"dockers\":[{\"y\":15,\"x\":15},{\"y\":20,\"x\":20}],\"bounds\":{\"lowerRight\":{\"y\":179,\"x\":40},\"upperLeft\":{\"y\":179,\"x\":40}},\"resourceId\":\"oryx_0EE1FEF4-2AF5-4374-A183-3A454BF44321-_4\",\"target\":{\"resourceId\":\"_4\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_4\"}]},{\"dockers\":[{\"y\":20,\"x\":20},{\"y\":59,\"x\":120},{\"y\":43.5,\"x\":52.5}],\"bounds\":{\"lowerRight\":{\"y\":179,\"x\":120},\"upperLeft\":{\"y\":59,\"x\":120}},\"resourceId\":\"_4-_5\",\"target\":{\"resourceId\":\"_5\"},\"childShapes\":[],\"properties\":{\"name\":\"constraint1\",\"conditiontype\":\"None\",\"conditionexpression\":\"<rule><name>Constraint1</name><modelVersion>1.0</modelVersion><attributes/><lhs/><rhs/></rule>\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_5\"}]},{\"dockers\":[{\"y\":20,\"x\":20},{\"y\":44.5,\"x\":52.5}],\"bounds\":{\"lowerRight\":{\"y\":179,\"x\":120},\"upperLeft\":{\"y\":179,\"x\":120}},\"resourceId\":\"_4-_6\",\"target\":{\"resourceId\":\"_6\"},\"childShapes\":[],\"properties\":{\"name\":\"constraint2\",\"conditiontype\":\"None\",\"conditionexpression\":\"this is constraint 2\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_6\"}]},{\"dockers\":[{\"y\":20,\"x\":20},{\"y\":301,\"x\":120},{\"y\":46,\"x\":52.5}],\"bounds\":{\"lowerRight\":{\"y\":301,\"x\":120},\"upperLeft\":{\"y\":179,\"x\":120}},\"resourceId\":\"_4-_7\",\"target\":{\"resourceId\":\"_7\"},\"childShapes\":[],\"properties\":{\"name\":\"constraint3\",\"conditiontype\":\"None\",\"conditionexpression\":\"this is constraint 3\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_7\"}]},{\"dockers\":[{\"y\":43.5,\"x\":52.5},{\"y\":15,\"x\":15}],\"bounds\":{\"lowerRight\":{\"y\":59.5,\"x\":229.5},\"upperLeft\":{\"y\":59.5,\"x\":229.5}},\"resourceId\":\"_5-_8\",\"target\":{\"resourceId\":\"_8\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_8\"}]},{\"dockers\":[{\"y\":46,\"x\":52.5},{\"y\":15,\"x\":15}],\"bounds\":{\"lowerRight\":{\"y\":302,\"x\":229.5},\"upperLeft\":{\"y\":302,\"x\":229.5}},\"resourceId\":\"_7-_9\",\"target\":{\"resourceId\":\"_9\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_9\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":194,\"x\":55},\"upperLeft\":{\"y\":164,\"x\":25}},\"resourceId\":\"oryx_0EE1FEF4-2AF5-4374-A183-3A454BF44321\",\"childShapes\":[],\"properties\":{\"name\":\"\"},\"stencil\":{\"id\":\"StartNoneEvent\"},\"outgoing\":[{\"resourceId\":\"oryx_0EE1FEF4-2AF5-4374-A183-3A454BF44321-_4\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":194,\"x\":353},\"upperLeft\":{\"y\":164,\"x\":323}},\"resourceId\":\"oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\",\"childShapes\":[],\"properties\":{\"name\":\"\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"outgoing\":[]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":199,\"x\":140},\"upperLeft\":{\"y\":159,\"x\":100}},\"resourceId\":\"_4\",\"childShapes\":[],\"properties\":{\"name\":\"Gateway\",\"markervisible\":\"false\"},\"stencil\":{\"id\":\"Exclusive_Databased_Gateway\"},\"outgoing\":[{\"resourceId\":\"_4-_5\"},{\"resourceId\":\"_4-_6\"},{\"resourceId\":\"_4-_7\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":103,\"x\":282},\"upperLeft\":{\"y\":16,\"x\":177}},\"resourceId\":\"_5\",\"childShapes\":[],\"properties\":{\"tasktype\":\"Script\",\"name\":\"Script\",\"script\":\"\",\"scriptLanguage\":\"\"},\"stencil\":{\"id\":\"Task\"},\"outgoing\":[{\"resourceId\":\"_5-_8\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":224,\"x\":282},\"upperLeft\":{\"y\":135,\"x\":177}},\"resourceId\":\"_6\",\"childShapes\":[],\"properties\":{\"tasktype\":\"Script\",\"name\":\"Script\",\"script\":\"\",\"scriptLanguage\":\"\"},\"stencil\":{\"id\":\"Task\"},\"outgoing\":[{\"resourceId\":\"_6-oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":348,\"x\":282},\"upperLeft\":{\"y\":256,\"x\":177}},\"resourceId\":\"_7\",\"childShapes\":[],\"properties\":{\"tasktype\":\"Script\",\"name\":\"Script\",\"script\":\"\",\"scriptLanguage\":\"\"},\"stencil\":{\"id\":\"Task\"},\"outgoing\":[{\"resourceId\":\"_7-_9\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":74,\"x\":353},\"upperLeft\":{\"y\":44,\"x\":323}},\"resourceId\":\"_8\",\"childShapes\":[],\"properties\":{\"name\":\"End\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"outgoing\":[]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":317,\"x\":353},\"upperLeft\":{\"y\":287,\"x\":323}},\"resourceId\":\"_9\",\"childShapes\":[],\"properties\":{\"name\":\"End\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"outgoing\":[]}],\"properties\":{},\"ssextensions\":[],\"stencilset\":{\"url\":\"/Process Designer/stencilsets/bpmn2.0/bpmn2.0.json\",\"namespace\":\"http://b3mn.org/stencilset/bpmn2.0#\"},\"stencil\":{\"id\":\"BPMNDiagram\"}}";
        GuvnorAPIServlet.extract(json);
    }

    @Test
    public void testExtract2() throws Exception {
        OutputStream out = null;
        InputStream content = null;
        ByteArrayOutputStream bos = null;

        try {
            String json =
                    "{\"resourceId\":\"oryx-canvas123\",\"childShapes\":[{\"dockers\":[{\"y\":44.5,\"x\":52.5},{\"y\":15,\"x\":15}],\"bounds\":{\"lowerRight\":{\"y\":179.5,\"x\":229.5},\"upperLeft\":{\"y\":179.5,\"x\":229.5}},\"resourceId\":\"_6-oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\",\"target\":{\"resourceId\":\"oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\"}]},{\"dockers\":[{\"y\":15,\"x\":15},{\"y\":20,\"x\":20}],\"bounds\":{\"lowerRight\":{\"y\":179,\"x\":40},\"upperLeft\":{\"y\":179,\"x\":40}},\"resourceId\":\"oryx_0EE1FEF4-2AF5-4374-A183-3A454BF44321-_4\",\"target\":{\"resourceId\":\"_4\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_4\"}]},{\"dockers\":[{\"y\":20,\"x\":20},{\"y\":59,\"x\":120},{\"y\":43.5,\"x\":52.5}],\"bounds\":{\"lowerRight\":{\"y\":179,\"x\":120},\"upperLeft\":{\"y\":59,\"x\":120}},\"resourceId\":\"_4-_5\",\"target\":{\"resourceId\":\"_5\"},\"childShapes\":[],\"properties\":{\"name\":\"constraint1\",\"conditiontype\":\"None\",\"conditionexpression\":\"this is constraint 1\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_5\"}]},{\"dockers\":[{\"y\":20,\"x\":20},{\"y\":44.5,\"x\":52.5}],\"bounds\":{\"lowerRight\":{\"y\":179,\"x\":120},\"upperLeft\":{\"y\":179,\"x\":120}},\"resourceId\":\"_4-_6\",\"target\":{\"resourceId\":\"_6\"},\"childShapes\":[],\"properties\":{\"name\":\"constraint2\",\"conditiontype\":\"None\",\"conditionexpression\":\"this is constraint 2\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_6\"}]},{\"dockers\":[{\"y\":20,\"x\":20},{\"y\":301,\"x\":120},{\"y\":46,\"x\":52.5}],\"bounds\":{\"lowerRight\":{\"y\":301,\"x\":120},\"upperLeft\":{\"y\":179,\"x\":120}},\"resourceId\":\"_4-_7\",\"target\":{\"resourceId\":\"_7\"},\"childShapes\":[],\"properties\":{\"name\":\"constraint3\",\"conditiontype\":\"None\",\"conditionexpression\":\"this is constraint 3\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_7\"}]},{\"dockers\":[{\"y\":43.5,\"x\":52.5},{\"y\":15,\"x\":15}],\"bounds\":{\"lowerRight\":{\"y\":59.5,\"x\":229.5},\"upperLeft\":{\"y\":59.5,\"x\":229.5}},\"resourceId\":\"_5-_8\",\"target\":{\"resourceId\":\"_8\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_8\"}]},{\"dockers\":[{\"y\":46,\"x\":52.5},{\"y\":15,\"x\":15}],\"bounds\":{\"lowerRight\":{\"y\":302,\"x\":229.5},\"upperLeft\":{\"y\":302,\"x\":229.5}},\"resourceId\":\"_7-_9\",\"target\":{\"resourceId\":\"_9\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_9\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":194,\"x\":55},\"upperLeft\":{\"y\":164,\"x\":25}},\"resourceId\":\"oryx_0EE1FEF4-2AF5-4374-A183-3A454BF44321\",\"childShapes\":[],\"properties\":{\"name\":\"\"},\"stencil\":{\"id\":\"StartNoneEvent\"},\"outgoing\":[{\"resourceId\":\"oryx_0EE1FEF4-2AF5-4374-A183-3A454BF44321-_4\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":194,\"x\":353},\"upperLeft\":{\"y\":164,\"x\":323}},\"resourceId\":\"oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\",\"childShapes\":[],\"properties\":{\"name\":\"\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"outgoing\":[]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":199,\"x\":140},\"upperLeft\":{\"y\":159,\"x\":100}},\"resourceId\":\"_4\",\"childShapes\":[],\"properties\":{\"name\":\"Gateway\",\"markervisible\":\"false\"},\"stencil\":{\"id\":\"Exclusive_Databased_Gateway\"},\"outgoing\":[{\"resourceId\":\"_4-_5\"},{\"resourceId\":\"_4-_6\"},{\"resourceId\":\"_4-_7\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":103,\"x\":282},\"upperLeft\":{\"y\":16,\"x\":177}},\"resourceId\":\"_5\",\"childShapes\":[],\"properties\":{\"tasktype\":\"Script\",\"name\":\"Script\",\"script\":\"\",\"scriptLanguage\":\"\"},\"stencil\":{\"id\":\"Task\"},\"outgoing\":[{\"resourceId\":\"_5-_8\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":224,\"x\":282},\"upperLeft\":{\"y\":135,\"x\":177}},\"resourceId\":\"_6\",\"childShapes\":[],\"properties\":{\"tasktype\":\"Script\",\"name\":\"Script\",\"script\":\"\",\"scriptLanguage\":\"\"},\"stencil\":{\"id\":\"Task\"},\"outgoing\":[{\"resourceId\":\"_6-oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":348,\"x\":282},\"upperLeft\":{\"y\":256,\"x\":177}},\"resourceId\":\"_7\",\"childShapes\":[],\"properties\":{\"tasktype\":\"Script\",\"name\":\"Script\",\"script\":\"\",\"scriptLanguage\":\"\"},\"stencil\":{\"id\":\"Task\"},\"outgoing\":[{\"resourceId\":\"_7-_9\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":74,\"x\":353},\"upperLeft\":{\"y\":44,\"x\":323}},\"resourceId\":\"_8\",\"childShapes\":[],\"properties\":{\"name\":\"End\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"outgoing\":[]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":317,\"x\":353},\"upperLeft\":{\"y\":287,\"x\":323}},\"resourceId\":\"_9\",\"childShapes\":[],\"properties\":{\"name\":\"End\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"outgoing\":[]}],\"properties\":{},\"ssextensions\":[],\"stencilset\":{\"url\":\"/Process Designer/stencilsets/bpmn2.0/bpmn2.0.json\",\"namespace\":\"http://b3mn.org/stencilset/bpmn2.0#\"},\"stencil\":{\"id\":\"BPMNDiagram\"}}";
            URL bpmn2_0SerializationURL = new URL("http://localhost:8080/drools-guvnor/org.drools.guvnor.Guvnor/guvnorAPI");
            String params = "action=extract&json=" + URLEncoder.encode(json, "UTF-8");
            byte[] bytes = params.getBytes("UTF-8");

            HttpURLConnection connection = (HttpURLConnection) bpmn2_0SerializationURL.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setFixedLengthStreamingMode(bytes.length);
            connection.setDoOutput(true);
            out = connection.getOutputStream();
            out.write(bytes);
            out.close();

            content = connection.getInputStream();

            bos = new ByteArrayOutputStream();
            int b = 0;
            while ((b = content.read()) > -1) {
                bos.write(b);
            }
            bytes = bos.toByteArray();
            content.close();
            bos.close();
            System.out.println(new String(bytes));
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (content != null) {
                    content.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
            }
        }
    }

    public void testInject() throws Exception {
        String json =
                "{\"resourceId\":\"oryx-canvas123\",\"childShapes\":[{\"dockers\":[{\"y\":44.5,\"x\":52.5},{\"y\":15,\"x\":15}],\"bounds\":{\"lowerRight\":{\"y\":179.5,\"x\":229.5},\"upperLeft\":{\"y\":179.5,\"x\":229.5}},\"resourceId\":\"_6-oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\",\"target\":{\"resourceId\":\"oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\"}]},{\"dockers\":[{\"y\":15,\"x\":15},{\"y\":20,\"x\":20}],\"bounds\":{\"lowerRight\":{\"y\":179,\"x\":40},\"upperLeft\":{\"y\":179,\"x\":40}},\"resourceId\":\"oryx_0EE1FEF4-2AF5-4374-A183-3A454BF44321-_4\",\"target\":{\"resourceId\":\"_4\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_4\"}]},{\"dockers\":[{\"y\":20,\"x\":20},{\"y\":59,\"x\":120},{\"y\":43.5,\"x\":52.5}],\"bounds\":{\"lowerRight\":{\"y\":179,\"x\":120},\"upperLeft\":{\"y\":59,\"x\":120}},\"resourceId\":\"_4-_5\",\"target\":{\"resourceId\":\"_5\"},\"childShapes\":[],\"properties\":{\"name\":\"constraint1\",\"conditiontype\":\"None\",\"conditionexpression\":\"this is constraint 1\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_5\"}]},{\"dockers\":[{\"y\":20,\"x\":20},{\"y\":44.5,\"x\":52.5}],\"bounds\":{\"lowerRight\":{\"y\":179,\"x\":120},\"upperLeft\":{\"y\":179,\"x\":120}},\"resourceId\":\"_4-_6\",\"target\":{\"resourceId\":\"_6\"},\"childShapes\":[],\"properties\":{\"name\":\"constraint2\",\"conditiontype\":\"None\",\"conditionexpression\":\"this is constraint 2\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_6\"}]},{\"dockers\":[{\"y\":20,\"x\":20},{\"y\":301,\"x\":120},{\"y\":46,\"x\":52.5}],\"bounds\":{\"lowerRight\":{\"y\":301,\"x\":120},\"upperLeft\":{\"y\":179,\"x\":120}},\"resourceId\":\"_4-_7\",\"target\":{\"resourceId\":\"_7\"},\"childShapes\":[],\"properties\":{\"name\":\"constraint3\",\"conditiontype\":\"None\",\"conditionexpression\":\"this is constraint 3\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_7\"}]},{\"dockers\":[{\"y\":43.5,\"x\":52.5},{\"y\":15,\"x\":15}],\"bounds\":{\"lowerRight\":{\"y\":59.5,\"x\":229.5},\"upperLeft\":{\"y\":59.5,\"x\":229.5}},\"resourceId\":\"_5-_8\",\"target\":{\"resourceId\":\"_8\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_8\"}]},{\"dockers\":[{\"y\":46,\"x\":52.5},{\"y\":15,\"x\":15}],\"bounds\":{\"lowerRight\":{\"y\":302,\"x\":229.5},\"upperLeft\":{\"y\":302,\"x\":229.5}},\"resourceId\":\"_7-_9\",\"target\":{\"resourceId\":\"_9\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_9\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":194,\"x\":55},\"upperLeft\":{\"y\":164,\"x\":25}},\"resourceId\":\"oryx_0EE1FEF4-2AF5-4374-A183-3A454BF44321\",\"childShapes\":[],\"properties\":{\"name\":\"\"},\"stencil\":{\"id\":\"StartNoneEvent\"},\"outgoing\":[{\"resourceId\":\"oryx_0EE1FEF4-2AF5-4374-A183-3A454BF44321-_4\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":194,\"x\":353},\"upperLeft\":{\"y\":164,\"x\":323}},\"resourceId\":\"oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\",\"childShapes\":[],\"properties\":{\"name\":\"\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"outgoing\":[]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":199,\"x\":140},\"upperLeft\":{\"y\":159,\"x\":100}},\"resourceId\":\"_4\",\"childShapes\":[],\"properties\":{\"name\":\"Gateway\",\"markervisible\":\"false\"},\"stencil\":{\"id\":\"Exclusive_Databased_Gateway\"},\"outgoing\":[{\"resourceId\":\"_4-_5\"},{\"resourceId\":\"_4-_6\"},{\"resourceId\":\"_4-_7\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":103,\"x\":282},\"upperLeft\":{\"y\":16,\"x\":177}},\"resourceId\":\"_5\",\"childShapes\":[],\"properties\":{\"tasktype\":\"Script\",\"name\":\"Script\",\"script\":\"\",\"scriptLanguage\":\"\"},\"stencil\":{\"id\":\"Task\"},\"outgoing\":[{\"resourceId\":\"_5-_8\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":224,\"x\":282},\"upperLeft\":{\"y\":135,\"x\":177}},\"resourceId\":\"_6\",\"childShapes\":[],\"properties\":{\"tasktype\":\"Script\",\"name\":\"Script\",\"script\":\"\",\"scriptLanguage\":\"\"},\"stencil\":{\"id\":\"Task\"},\"outgoing\":[{\"resourceId\":\"_6-oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":348,\"x\":282},\"upperLeft\":{\"y\":256,\"x\":177}},\"resourceId\":\"_7\",\"childShapes\":[],\"properties\":{\"tasktype\":\"Script\",\"name\":\"Script\",\"script\":\"\",\"scriptLanguage\":\"\"},\"stencil\":{\"id\":\"Task\"},\"outgoing\":[{\"resourceId\":\"_7-_9\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":74,\"x\":353},\"upperLeft\":{\"y\":44,\"x\":323}},\"resourceId\":\"_8\",\"childShapes\":[],\"properties\":{\"name\":\"End\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"outgoing\":[]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":317,\"x\":353},\"upperLeft\":{\"y\":287,\"x\":323}},\"resourceId\":\"_9\",\"childShapes\":[],\"properties\":{\"name\":\"End\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"outgoing\":[]}],\"properties\":{},\"ssextensions\":[],\"stencilset\":{\"url\":\"/Process Designer/stencilsets/bpmn2.0/bpmn2.0.json\",\"namespace\":\"http://b3mn.org/stencilset/bpmn2.0#\"},\"stencil\":{\"id\":\"BPMNDiagram\"}}";
        Map<String, String> constraints = new HashMap<String, String>();
        constraints.put("_5", "brl for constraint1");
        constraints.put("_6", "brl for constraint2");
        constraints.put("_7", "brl for constraint3");
        String result = GuvnorAPIServlet.inject(json, constraints);
        System.out.println(result);
    }

    public void testInject2() throws Exception {
        OutputStream out = null;
        InputStream content = null;
        ByteArrayOutputStream bos = null;

        try {
            String json =
                    "{\"resourceId\":\"oryx-canvas123\",\"childShapes\":[{\"dockers\":[{\"y\":44.5,\"x\":52.5},{\"y\":15,\"x\":15}],\"bounds\":{\"lowerRight\":{\"y\":179.5,\"x\":229.5},\"upperLeft\":{\"y\":179.5,\"x\":229.5}},\"resourceId\":\"_6-oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\",\"target\":{\"resourceId\":\"oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\"}]},{\"dockers\":[{\"y\":15,\"x\":15},{\"y\":20,\"x\":20}],\"bounds\":{\"lowerRight\":{\"y\":179,\"x\":40},\"upperLeft\":{\"y\":179,\"x\":40}},\"resourceId\":\"oryx_0EE1FEF4-2AF5-4374-A183-3A454BF44321-_4\",\"target\":{\"resourceId\":\"_4\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_4\"}]},{\"dockers\":[{\"y\":20,\"x\":20},{\"y\":59,\"x\":120},{\"y\":43.5,\"x\":52.5}],\"bounds\":{\"lowerRight\":{\"y\":179,\"x\":120},\"upperLeft\":{\"y\":59,\"x\":120}},\"resourceId\":\"_4-_5\",\"target\":{\"resourceId\":\"_5\"},\"childShapes\":[],\"properties\":{\"name\":\"constraint1\",\"conditiontype\":\"None\",\"conditionexpression\":\"this is constraint 1\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_5\"}]},{\"dockers\":[{\"y\":20,\"x\":20},{\"y\":44.5,\"x\":52.5}],\"bounds\":{\"lowerRight\":{\"y\":179,\"x\":120},\"upperLeft\":{\"y\":179,\"x\":120}},\"resourceId\":\"_4-_6\",\"target\":{\"resourceId\":\"_6\"},\"childShapes\":[],\"properties\":{\"name\":\"constraint2\",\"conditiontype\":\"None\",\"conditionexpression\":\"this is constraint 2\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_6\"}]},{\"dockers\":[{\"y\":20,\"x\":20},{\"y\":301,\"x\":120},{\"y\":46,\"x\":52.5}],\"bounds\":{\"lowerRight\":{\"y\":301,\"x\":120},\"upperLeft\":{\"y\":179,\"x\":120}},\"resourceId\":\"_4-_7\",\"target\":{\"resourceId\":\"_7\"},\"childShapes\":[],\"properties\":{\"name\":\"constraint3\",\"conditiontype\":\"None\",\"conditionexpression\":\"this is constraint 3\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_7\"}]},{\"dockers\":[{\"y\":43.5,\"x\":52.5},{\"y\":15,\"x\":15}],\"bounds\":{\"lowerRight\":{\"y\":59.5,\"x\":229.5},\"upperLeft\":{\"y\":59.5,\"x\":229.5}},\"resourceId\":\"_5-_8\",\"target\":{\"resourceId\":\"_8\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_8\"}]},{\"dockers\":[{\"y\":46,\"x\":52.5},{\"y\":15,\"x\":15}],\"bounds\":{\"lowerRight\":{\"y\":302,\"x\":229.5},\"upperLeft\":{\"y\":302,\"x\":229.5}},\"resourceId\":\"_7-_9\",\"target\":{\"resourceId\":\"_9\"},\"childShapes\":[],\"properties\":{\"name\":\"\",\"conditiontype\":\"None\"},\"stencil\":{\"id\":\"SequenceFlow\"},\"outgoing\":[{\"resourceId\":\"_9\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":194,\"x\":55},\"upperLeft\":{\"y\":164,\"x\":25}},\"resourceId\":\"oryx_0EE1FEF4-2AF5-4374-A183-3A454BF44321\",\"childShapes\":[],\"properties\":{\"name\":\"\"},\"stencil\":{\"id\":\"StartNoneEvent\"},\"outgoing\":[{\"resourceId\":\"oryx_0EE1FEF4-2AF5-4374-A183-3A454BF44321-_4\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":194,\"x\":353},\"upperLeft\":{\"y\":164,\"x\":323}},\"resourceId\":\"oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\",\"childShapes\":[],\"properties\":{\"name\":\"\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"outgoing\":[]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":199,\"x\":140},\"upperLeft\":{\"y\":159,\"x\":100}},\"resourceId\":\"_4\",\"childShapes\":[],\"properties\":{\"name\":\"Gateway\",\"markervisible\":\"false\"},\"stencil\":{\"id\":\"Exclusive_Databased_Gateway\"},\"outgoing\":[{\"resourceId\":\"_4-_5\"},{\"resourceId\":\"_4-_6\"},{\"resourceId\":\"_4-_7\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":103,\"x\":282},\"upperLeft\":{\"y\":16,\"x\":177}},\"resourceId\":\"_5\",\"childShapes\":[],\"properties\":{\"tasktype\":\"Script\",\"name\":\"Script\",\"script\":\"\",\"scriptLanguage\":\"\"},\"stencil\":{\"id\":\"Task\"},\"outgoing\":[{\"resourceId\":\"_5-_8\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":224,\"x\":282},\"upperLeft\":{\"y\":135,\"x\":177}},\"resourceId\":\"_6\",\"childShapes\":[],\"properties\":{\"tasktype\":\"Script\",\"name\":\"Script\",\"script\":\"\",\"scriptLanguage\":\"\"},\"stencil\":{\"id\":\"Task\"},\"outgoing\":[{\"resourceId\":\"_6-oryx_17D8BC5E-C308-47F4-97CB-12706C4AEB8D\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":348,\"x\":282},\"upperLeft\":{\"y\":256,\"x\":177}},\"resourceId\":\"_7\",\"childShapes\":[],\"properties\":{\"tasktype\":\"Script\",\"name\":\"Script\",\"script\":\"\",\"scriptLanguage\":\"\"},\"stencil\":{\"id\":\"Task\"},\"outgoing\":[{\"resourceId\":\"_7-_9\"}]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":74,\"x\":353},\"upperLeft\":{\"y\":44,\"x\":323}},\"resourceId\":\"_8\",\"childShapes\":[],\"properties\":{\"name\":\"End\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"outgoing\":[]},{\"dockers\":[],\"bounds\":{\"lowerRight\":{\"y\":317,\"x\":353},\"upperLeft\":{\"y\":287,\"x\":323}},\"resourceId\":\"_9\",\"childShapes\":[],\"properties\":{\"name\":\"End\"},\"stencil\":{\"id\":\"EndNoneEvent\"},\"outgoing\":[]}],\"properties\":{},\"ssextensions\":[],\"stencilset\":{\"url\":\"/Process Designer/stencilsets/bpmn2.0/bpmn2.0.json\",\"namespace\":\"http://b3mn.org/stencilset/bpmn2.0#\"},\"stencil\":{\"id\":\"BPMNDiagram\"}}";
            URL bpmn2_0SerializationURL = new URL("http://localhost:8080/drools-guvnor/org.drools.guvnor.Guvnor/guvnorAPI");
            String params = "action=inject&json=" + URLEncoder.encode(json, "UTF-8");
            params += "&constraint=_5:brl for constraint1";
            params += "&constraint=_6:brl for constraint2";
            params += "&constraint=_7:brl for constraint3";
            byte[] bytes = params.getBytes("UTF-8");

            HttpURLConnection connection = (HttpURLConnection) bpmn2_0SerializationURL.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setFixedLengthStreamingMode(bytes.length);
            connection.setDoOutput(true);
            out = connection.getOutputStream();
            out.write(bytes);
            out.close();

            content = connection.getInputStream();

            bos = new ByteArrayOutputStream();
            int b = 0;
            while ((b = content.read()) > -1) {
                bos.write(b);
            }
            bytes = bos.toByteArray();
            content.close();
            bos.close();
            System.out.println(new String(bytes));
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (content != null) {
                    content.close();
                }
                if (bos != null) {
                    bos.close();
                }
            } catch (IOException e) {
            }
        }
    }
}
