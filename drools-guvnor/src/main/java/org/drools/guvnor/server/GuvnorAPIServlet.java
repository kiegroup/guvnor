/**
 * Copyright 2010 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.bpmn2.legacy.beta1.XmlBPMNProcessDumper;
import org.drools.bpmn2.xml.BPMNDISemanticModule;
import org.drools.bpmn2.xml.BPMNSemanticModule;
import org.drools.compiler.xml.XmlProcessReader;
import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleFlowContentModel;
import org.drools.guvnor.server.contenthandler.BPMN2ProcessHandler;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.ruleflow.core.RuleFlowProcess;
import org.drools.workflow.core.Constraint;
import org.drools.workflow.core.impl.ConstraintImpl;
import org.drools.workflow.core.node.Split;
import org.drools.workflow.core.node.StartNode;
import org.drools.xml.SemanticModules;

import com.google.gwt.user.client.rpc.SerializationException;


/**
 * A servlet opening an API into the Guvnor services.
 *
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public class GuvnorAPIServlet extends HttpServlet {

    private static final LoggingHelper     log  = LoggingHelper.getLogger(GuvnorAPIServlet.class);
    
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        log.debug("Incoming request for Guvnor API:" + request.getRequestURL());
        String action = request.getParameter("action");
        if ("load".equals(action)) {
            String uuid = request.getParameter("uuid");
            if (uuid == null) {
                throw new ServletException(new IllegalArgumentException("The load action requires the parameter uuid"));
            }
            try { 
                RuleAsset asset = RepositoryServiceServlet.getService().loadRuleAsset(uuid);
                if (asset.content != null) {
                    response.setContentType("application/json");
                    String content = null;
                    if (asset.content instanceof RuleFlowContentModel) {
                        content = ((RuleFlowContentModel) asset.content).getXml();
                    } else {
                        content = asset.content.toString();
                    }
                    try {
                    	// TODO fix for non-localhost
                    	content = deserialize(
                			"http://localhost:8080/designer/bpmn2_0deserialization", content);
                    } catch (IOException e) {
                    	log.error(e.getMessage(), e);
                        throw new ServletException(e.getMessage(), e);
                    }
                    log.debug("Sending model");
                    log.debug(content);
                    log.debug("End of sending model");
                    response.setContentLength(content.getBytes().length);
                    response.getOutputStream().write(content.getBytes());
                    response.getOutputStream().close();
                }
            } catch (SerializationException e) {
                log.error(e.getMessage(), e);
                throw new ServletException(e.getMessage(), e);
            }
            
        } else if ("extract".equals(action)) {
        	String json = request.getParameter("json");
        	try {
            	Map<String, String[]> result = extract(json);
            	response.setContentType("application/json");
            	log.debug("extracting");
            	String s = "";
            	int i = 0;
            	for (Map.Entry<String, String[]> entry: result.entrySet()) {
            		log.debug(entry.getKey() + " " + entry.getValue()[0] + " " + entry.getValue()[1]);
            		s += entry.getKey() + "#" + entry.getValue()[0] + "#" + entry.getValue()[1];
            		if (i++ != result.size() - 1) {
            			s += "###";
            		}
            	}
                log.debug("End of extracting");
                response.setContentLength(s.length());
                response.getOutputStream().write(s.getBytes());
                response.getOutputStream().close();
			} catch (Throwable t) {
				throw new ServletException(t);
			}
        } else if ("inject".equals(action)) {
        	String json = request.getParameter("json");
        	try {
        		Map<String, String> constraints = new HashMap<String, String>();
        		String[] s = request.getParameterValues("constraint");
        		for (String c: s) {
        			String nodeId = c.substring(0, c.indexOf(":"));
        			String rule = c.substring(c.indexOf(":") + 1);
        			constraints.put(nodeId, rule);
        		}
        		String result = inject(json, constraints);
            	response.setContentType("application/json");
            	log.debug("injecting");
            	for (Map.Entry<String, String> entry: constraints.entrySet()) {
            		log.debug(entry.getKey() + " " + entry.getValue());
            	}
            	log.debug(result);
                log.debug("End of injecting");
                response.setContentLength(result.length());
                response.getOutputStream().write(result.getBytes());
                response.getOutputStream().close();
			} catch (Throwable t) {
				throw new ServletException(t);
			}
        } else {
            throw new ServletException(new IllegalArgumentException("The servlet requires a parameter named action"));
        }
    }

    public static String deserialize(String deserializeUrl, String modelXml) throws IOException {
		OutputStream out = null;
		InputStream content = null;
		ByteArrayOutputStream bos = null;

		try {
			URL bpmn2_0SerializationURL = new URL(deserializeUrl);
			modelXml = "data=" + URLEncoder.encode(modelXml, "UTF-8");
			byte[] bytes = modelXml.getBytes("UTF-8");

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
			return new String(bytes);
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
    
    public static Map<String, String[]> extract(String json) throws Exception {
    	Map<String, String[]> result = null;
    	String xml = BPMN2ProcessHandler.serialize(
    			"http://localhost:8080/designer/bpmn2_0serialization", json);
		Reader isr = new StringReader(xml);
		SemanticModules semanticModules = new SemanticModules();
		semanticModules.addSemanticModule(new BPMNSemanticModule());
		semanticModules.addSemanticModule(new BPMNDISemanticModule());
		semanticModules.addSemanticModule(new org.drools.bpmn2.legacy.beta1.BPMNSemanticModule());
		semanticModules.addSemanticModule(new org.drools.bpmn2.legacy.beta1.BPMN2SemanticModule());
		semanticModules.addSemanticModule(new org.drools.bpmn2.legacy.beta1.BPMNDISemanticModule());
		XmlProcessReader xmlReader = new XmlProcessReader(semanticModules);
		RuleFlowProcess process = (RuleFlowProcess) xmlReader.read(isr);
		if (process == null) {
			throw new IllegalArgumentException("Could not read process");
		} else {
			log.debug("Processing " + process.getId());
			result = new HashMap<String, String[]>();
			StartNode start = process.getStart();
			Node target = start.getTo().getTo();
			if (target instanceof Split) {
				Split split = (Split) target;
				for (Connection connection: split.getDefaultOutgoingConnections()) {
					Constraint constraint = split.getConstraint(connection);
					if (constraint != null) {
						System.out.println("Found constraint to node " + connection.getTo().getName() + " [" + connection.getTo().getId() + "]: " + constraint.getConstraint());
						result.put(XmlBPMNProcessDumper.getUniqueNodeId(connection.getTo()), new String[] { connection.getTo().getName(), constraint.getConstraint() }); 
					}
				}
			}
		}
		if (isr != null) {
			isr.close();
		}
		return result;
    }
    
    public static String inject(String json, Map<String, String> constraints) throws Exception {
    	String xml = BPMN2ProcessHandler.serialize(
    			"http://localhost:8080/designer/bpmn2_0serialization", json);
		Reader isr = new StringReader(xml);
		SemanticModules semanticModules = new SemanticModules();
		semanticModules.addSemanticModule(new BPMNSemanticModule());
		semanticModules.addSemanticModule(new BPMNDISemanticModule());
		semanticModules.addSemanticModule(new org.drools.bpmn2.legacy.beta1.BPMNSemanticModule());
		semanticModules.addSemanticModule(new org.drools.bpmn2.legacy.beta1.BPMN2SemanticModule());
		semanticModules.addSemanticModule(new org.drools.bpmn2.legacy.beta1.BPMNDISemanticModule());
		XmlProcessReader xmlReader = new XmlProcessReader(semanticModules);
		RuleFlowProcess process = (RuleFlowProcess) xmlReader.read(isr);
		isr.close();
		if (process == null) {
			throw new IllegalArgumentException("Could not read process");
		} else {
			log.debug("Processing " + process.getId());
			StartNode start = process.getStart();
			Node target = start.getTo().getTo();
			if (target instanceof Split) {
				Split split = (Split) target;
				for (Connection connection: split.getDefaultOutgoingConnections()) {
					String s = constraints.get(XmlBPMNProcessDumper.getUniqueNodeId(connection.getTo()));
					if (s != null) {
						System.out.println("Found constraint to node " + connection.getTo().getName() + ": " + s);
						Constraint constraint = split.getConstraint(connection);
						if (constraint == null) {
							constraint = new ConstraintImpl();
							split.setConstraint(connection, constraint);
						}
						constraint.setConstraint(s);
					}
				}
			}
			String newXml = XmlBPMNProcessDumper.INSTANCE.dump(process);
			System.out.println(newXml);
			return deserialize("http://localhost:8080/designer/bpmn2_0deserialization", newXml);
		}
    }

}
