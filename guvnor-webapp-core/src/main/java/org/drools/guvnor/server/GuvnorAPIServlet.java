/*
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

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.drools.definition.process.Connection;
import org.drools.definition.process.Node;
import org.drools.guvnor.client.rpc.Asset;
import org.drools.guvnor.client.rpc.RuleFlowContentModel;
import org.drools.guvnor.server.contenthandler.BPMN2ProcessHandler;
import org.drools.guvnor.server.util.LoggingHelper;
import org.drools.repository.utils.IOUtils;
import org.drools.xml.SemanticModules;
import org.jbpm.bpmn2.xml.BPMNDISemanticModule;
import org.jbpm.bpmn2.xml.BPMNSemanticModule;
import org.jbpm.bpmn2.xml.XmlBPMNProcessDumper;
import org.jbpm.compiler.xml.XmlProcessReader;
import org.jbpm.ruleflow.core.RuleFlowProcess;
import org.jbpm.workflow.core.Constraint;
import org.jbpm.workflow.core.impl.ConstraintImpl;
import org.jbpm.workflow.core.node.Split;
import org.jbpm.workflow.core.node.StartNode;

import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.guvnor.client.configurations.ApplicationPreferences;

/**
 * A servlet opening an API into the Guvnor services.
 */
public class GuvnorAPIServlet extends HttpServlet {

    private static final String        INJECT  = "inject";
    private static final String        EXTRACT = "extract";
    private static final String        LOAD    = "load";
    private static final LoggingHelper log     = LoggingHelper.getLogger( GuvnorAPIServlet.class );

    @Inject
    private RepositoryAssetService repositoryAssetService;

    public void service(HttpServletRequest request,
                        HttpServletResponse response) throws ServletException,
                                                     IOException {
        log.debug( "Incoming request for Guvnor API:" + request.getRequestURL() );
        String action = request.getParameter( "action" );
        if ( LOAD.equals( action ) ) {
            String uuid = request.getParameter( "uuid" );
            if ( uuid == null ) {
                throw new ServletException( new IllegalArgumentException( "The load action requires the parameter uuid" ) );
            }
            ServletOutputStream outputStream = response.getOutputStream();

            try {
                Asset asset = repositoryAssetService.loadRuleAsset( uuid );
                if ( asset.getContent() != null ) {
                    response.setContentType( "application/json" );
                    String content = null;
                    if ( asset.getContent() instanceof RuleFlowContentModel ) {
                        content = ((RuleFlowContentModel) asset.getContent()).getXml();
                    } else {
                        content = asset.getContent().toString();
                    }
                    try {
                        content = deserialize( getDesignerURL()+"/bpmn2_0deserialization",
                                               content );
                    } catch ( IOException e ) {
                        log.error( e.getMessage(),
                                   e );
                        throw new ServletException( e.getMessage(),
                                                    e );
                    }
                    log.debug( "Sending model" );
                    log.debug( content );
                    log.debug( "End of sending model" );
                    response.setContentLength( content.getBytes().length );
                    outputStream.write( content.getBytes() );
                }
            } catch ( SerializationException e ) {
                log.error( e.getMessage(),
                           e );
                throw new ServletException( e.getMessage(),
                                            e );
            } finally {
                outputStream.close();
            }
        } else if ( EXTRACT.equals( action ) ) {
            String json = request.getParameter( "json" );
            ServletOutputStream outputStream = response.getOutputStream();
            try {
                Map<String, String[]> result = extract( json );
                response.setContentType( "application/json" );
                log.debug( "extracting" );
                String s = "";
                int i = 0;
                for ( Map.Entry<String, String[]> entry : result.entrySet() ) {
                    log.debug( entry.getKey() + " " + entry.getValue()[0] + " " + entry.getValue()[1] );
                    s += entry.getKey() + "#" + entry.getValue()[0] + "#" + entry.getValue()[1];
                    if ( i++ != result.size() - 1 ) {
                        s += "###";
                    }
                }
                log.debug( "End of extracting" );
                response.setContentLength( s.getBytes().length );
                outputStream.write( s.getBytes() );
            } catch ( Throwable t ) {
                throw new ServletException( t );
            } finally {
                outputStream.close();
            }
        } else if ( INJECT.equals( action ) ) {
            String json = request.getParameter( "json" );
            ServletOutputStream outputStream = response.getOutputStream();
            try {
                Map<String, String> constraints = new HashMap<String, String>();
                String[] constraint = request.getParameterValues( "constraint" );
                for ( String c : constraint ) {
                    String nodeId = c.substring( 0,
                                                 c.indexOf( ":" ) );
                    String rule = c.substring( c.indexOf( ":" ) + 1 );
                    constraints.put( nodeId,
                                     rule );
                }
                String result = inject( json,
                                        constraints );
                response.setContentType( "application/json" );
                log.debug( "injecting" );
                if ( log.isDebugEnabled() ) {
                    for ( Map.Entry<String, String> entry : constraints.entrySet() ) {
                        log.debug( entry.getKey() + " " + entry.getValue() );
                    }
                }
                log.debug( result );
                log.debug( "End of injecting" );
                response.setContentLength( result.getBytes().length );
                outputStream.write( result.getBytes() );
            } catch ( Throwable t ) {
                throw new ServletException( t );
            } finally {
                outputStream.close();
            }
        } else {
            throw new ServletException( new IllegalArgumentException( "The servlet requires a parameter named action" ) );
        }
    }

    public static String deserialize(String deserializeUrl,
                                     String modelXml) throws IOException {
        OutputStream out = null;
        InputStream content = null;
        ByteArrayOutputStream bos = null;

        try {
            URL bpmn2_0SerializationURL = new URL( deserializeUrl );
            modelXml = "data=" + URLEncoder.encode( modelXml,
                                                    "UTF-8" );
            byte[] bytes = modelXml.getBytes( "UTF-8" );

            HttpURLConnection connection = (HttpURLConnection) bpmn2_0SerializationURL.openConnection();
            connection.setRequestMethod( "POST" );
            connection.setRequestProperty( "Content-Type",
                                           "application/x-www-form-urlencoded" );
            connection.setFixedLengthStreamingMode( bytes.length );
            connection.setDoOutput( true );
            out = connection.getOutputStream();
            out.write( bytes );
            out.close();

            content = connection.getInputStream();

            bos = new ByteArrayOutputStream();
            int b = 0;
            while ( (b = content.read()) > -1 ) {
                bos.write( b );
            }
            bytes = bos.toByteArray();
            content.close();
            bos.close();
            return new String( bytes );
        } finally {
            IOUtils.closeQuietly( out );
            IOUtils.closeQuietly( content );
            IOUtils.closeQuietly( bos );
        }
    }

    public static Map<String, String[]> extract(String json) throws Exception {
        Map<String, String[]> result = null;
        String xml = BPMN2ProcessHandler.serialize( getDesignerURL()+"/bpmn2_0serialization",
                                                    json );
        Reader isr = new StringReader( xml );
        SemanticModules semanticModules = new SemanticModules();
        semanticModules.addSemanticModule( new BPMNSemanticModule() );
        semanticModules.addSemanticModule( new BPMNDISemanticModule() );
        XmlProcessReader xmlReader = new XmlProcessReader( semanticModules,
                                                           getClassLoader() );
        RuleFlowProcess process = (RuleFlowProcess) xmlReader.read( isr );
        if ( process == null ) {
            throw new IllegalArgumentException( "Could not read process" );
        }
        log.debug( "Processing " + process.getId() );
        result = new HashMap<String, String[]>();
        StartNode start = process.getStart();
        Node target = start.getTo().getTo();
        if ( target instanceof Split ) {
            Split split = (Split) target;
            for ( Connection connection : split.getDefaultOutgoingConnections() ) {
                Constraint constraint = split.getConstraint( connection );
                if ( constraint != null ) {
                    System.out.println( "Found constraint to node " + connection.getTo().getName() + " [" + connection.getTo().getId() + "]: " + constraint.getConstraint() );
                    result.put( XmlBPMNProcessDumper.getUniqueNodeId( connection.getTo() ),
                                new String[]{connection.getTo().getName(), constraint.getConstraint()} );
                }
            }
        }

        if ( isr != null ) {
            isr.close();
        }
        return result;
    }

    public static String inject(String json,
                                Map<String, String> constraints) throws Exception {
        String xml = BPMN2ProcessHandler.serialize( getDesignerURL()+"/bpmn2_0serialization",
                                                    json );
        Reader isr = new StringReader( xml );
        SemanticModules semanticModules = new SemanticModules();
        semanticModules.addSemanticModule( new BPMNSemanticModule() );
        semanticModules.addSemanticModule( new BPMNDISemanticModule() );
        XmlProcessReader xmlReader = new XmlProcessReader( semanticModules,
                                                           getClassLoader() );
        RuleFlowProcess process = (RuleFlowProcess) xmlReader.read( isr );
        isr.close();
        if ( process == null ) {
            throw new IllegalArgumentException( "Could not read process" );
        } else {
            log.debug( "Processing " + process.getId() );
            StartNode start = process.getStart();
            Node target = start.getTo().getTo();
            if ( target instanceof Split ) {
                Split split = (Split) target;
                for ( Connection connection : split.getDefaultOutgoingConnections() ) {
                    String s = constraints.get( XmlBPMNProcessDumper.getUniqueNodeId( connection.getTo() ) );
                    if ( s != null ) {
                        System.out.println( "Found constraint to node " + connection.getTo().getName() + ": " + s );
                        Constraint constraint = split.getConstraint( connection );
                        if ( constraint == null ) {
                            constraint = new ConstraintImpl();
                            split.setConstraint( connection,
                                                 constraint );
                        }
                        constraint.setConstraint( s );
                    }
                }
            }
            String newXml = XmlBPMNProcessDumper.INSTANCE.dump( process );
            System.out.println( newXml );
            return deserialize( getDesignerURL()+"/bpmn2_0deserialization",
                                newXml );
        }
    }

    private static ClassLoader getClassLoader() {
        ClassLoader cl = Thread.currentThread().getContextClassLoader();
        if ( cl == null ) {
            cl = GuvnorAPIServlet.class.getClassLoader();
        }
        return cl;
    }

    private static String getDesignerURL(){
        return System.getProperty(ApplicationPreferences.DESIGNER_URL)+"/"+System.getProperty(ApplicationPreferences.DESIGNER_CONTEXT);
    } 
}
