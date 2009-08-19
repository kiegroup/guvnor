package org.gridcc.mce.mceworkflow.services;

import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.wsdl.Definition;
import javax.wsdl.Input;
import javax.wsdl.Message;
import javax.wsdl.Operation;
import javax.wsdl.Output;
import javax.wsdl.PortType;
import javax.wsdl.Types;
import javax.wsdl.factory.WSDLFactory;
import javax.wsdl.xml.WSDLReader;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class WSDLParser {

    //private String portTypesOperations[][] = new String[][];
    private Document document = null;
    private Element rootElement = null;

    public WSDLParser() {
    }

    public String parseWSDL(String WSName, String uri) {
        String nodeString = "<node label='" + WSName + ":";
        String returnString = "";

        try {
            WSDLFactory factory = WSDLFactory.newInstance();
            WSDLReader reader = factory.newWSDLReader();

            reader.setFeature("javax.wsdl.verbose", true);
            reader.setFeature("javax.wsdl.importDocuments", true);

            Definition wsdlDefination = reader.readWSDL(null, uri);
            String targetNamespace = wsdlDefination.getTargetNamespace();

            String targetNamespaceNode = "namespace='" + targetNamespace + "'>";
            String targetNamespaceAttribute = " namespace='" + targetNamespace + "'";

            Types types = wsdlDefination.getTypes();

            // will add later

            Map portTypesMap = wsdlDefination.getPortTypes();
            Iterator portTypesMapIterator = portTypesMap.values().iterator();

            while (portTypesMapIterator.hasNext()) {
                PortType portType = (PortType) portTypesMapIterator.next();
                //System.out.println(portType.getQName());
                String tempPortTypeString = nodeString +
                                            portType.getQName().getLocalPart() +
                                            "-(Port Type)' type='portType'" + targetNamespaceAttribute + ">";
                List operationsList = portType.getOperations();
                Iterator operationsListIterator = operationsList.iterator();
                while (operationsListIterator.hasNext()) {
                    Operation operation = (Operation) operationsListIterator.
                                          next();
                    //System.out.println("     " + operation.getName());
                    String tempOperationString = nodeString + operation.getName() +
                                                 "-(Operation)' type='operation'" + targetNamespaceAttribute + ">";

                    Input input = operation.getInput();
                    //System.out.println("Input: " + input.getName());
                    Message message = input.getMessage();
                    String tempInputMessageString = nodeString + message.getQName().getLocalPart()
                            + "' type='inputMessage'" + targetNamespaceAttribute + "/>";

                    // Add input message to operation
                    tempOperationString = tempOperationString + tempInputMessageString;

                    Output output = operation.getOutput();
                    //System.out.println("Output: " + output.getName());
                    message = output.getMessage();
                    String tempOutputMessageString = nodeString + message.getQName().getLocalPart()
                            + "' type='inputMessage'" + targetNamespaceAttribute + "/>";

                    // Add output message to operation
                    tempOperationString = tempOperationString + tempOutputMessageString;

                    // Close Operation Tag
                    tempOperationString = tempOperationString + "</node>";

                    tempPortTypeString = tempPortTypeString +
                                         tempOperationString;
                }
                tempPortTypeString = tempPortTypeString + "</node>";
                returnString = returnString + tempPortTypeString;
            }
            returnString = targetNamespaceNode + returnString;

            // Returning parsed WSDL
            return returnString;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "WSDL-Parsing-Exception";
    }

    private void initiateDOM() {
        DocumentBuilderFactory DOMFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = null;
        try {
            builder = DOMFactory.newDocumentBuilder();
        } catch (ParserConfigurationException ex) {
        }
        document = builder.newDocument();
    }

    public void createRootElement(String root) {
        rootElement = (Element) document.createElement("node");
        rootElement.setAttribute("label", root);
        document.appendChild(rootElement);
    }

    public Element createPortType() {
        return null;
    }
}
