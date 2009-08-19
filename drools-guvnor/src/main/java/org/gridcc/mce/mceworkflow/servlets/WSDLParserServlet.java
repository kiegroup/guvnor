package org.gridcc.mce.mceworkflow.servlets;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.gridcc.mce.mceworkflow.services.WSDLParser;

public class WSDLParserServlet extends HttpServlet {
	private static final String CONTENT_TYPE = "text/html";

	// Initialize global variables
	public void init() throws ServletException {
	}

	// Process the HTTP Get request
	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}

	// Process the HTTP Post request
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {

		String workflowName = request.getParameter("WorkflowName");
		// System.out.println("Workflow Name: " + workflowName);
		String operationName = request.getParameter("operation");
		// System.out.println("Operation Name: " + operationName);
		String fullPath = request.getParameter("fullPath");
		// System.out.println("Full Path: " + fullPath);
		// System.out.println(request.getContextPath());
		String var0 = request.getParameter("WSname");
		if (var0 == null) {
			var0 = "";
		}
		String returnString = "<node label='" + var0 + "' type='wsdl' ";
		String var1 = request.getParameter("WSDLURL");
		if (var1 != null) {
			// System.out.println("WSDLURL: " + var1);
			WSDLParser parser = new WSDLParser();

			if (!parser.parseWSDL(var0, var1).equalsIgnoreCase(
					"WSDL-Parsing-Exception")) {
				returnString = returnString + parser.parseWSDL(var0, var1);
			} else {
				// Still returning valid XML
				// even when WSDL Parser Failed
				returnString = "<node label='" + var0 + "' type='wsdl'>";
			}

		}
		returnString = returnString + "</node>";
		// System.out.println(returnString);

		response.setContentType("text/plain");
		PrintWriter out = response.getWriter();
		out.print(returnString);
		out.close();
	}

	// Clean up resources
	public void destroy() {
	}
}
