/*
 * Copyright 2005 JBoss Inc
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
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleFlowContentModel;
import org.drools.guvnor.server.util.LoggingHelper;

import com.google.gwt.user.client.rpc.SerializableException;

/**
 * A servlet opening an API into the Guvnor services.
 *
 * @author <a href="mailto:atoulme@intalio.com">Antoine Toulme</a>
 */
public class GuvnorAPIServlet extends HttpServlet {

    private static final Logger     log  = LoggingHelper.getLogger(GuvnorAPIServlet.class);
    
    public void service(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (log.isDebugEnabled()) {
            log.debug("Incoming request for Guvnor API:" + request.getRequestURL());
        }
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
                    if (log.isDebugEnabled()) {
                        log.debug("Sending model");
                        log.debug(content);
                        log.debug("End of sending model");
                    }
                    response.setContentLength(content.getBytes().length);
                    response.getOutputStream().write(content.getBytes());
                    response.getOutputStream().close();
                }
            } catch (SerializableException e) {
                log.error(e.getMessage(), e);
                throw new ServletException(e.getMessage(), e);
            }
            
        } else {
            throw  new ServletException(new IllegalArgumentException("The servlet requires a parameter named action"));
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

}
