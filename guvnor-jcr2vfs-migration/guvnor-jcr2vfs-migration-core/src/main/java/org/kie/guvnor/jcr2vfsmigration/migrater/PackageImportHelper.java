package org.kie.guvnor.jcr2vfsmigration.migrater;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.StringWriter;

import javax.inject.Inject;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.kie.guvnor.project.service.ProjectService;
import org.uberfire.backend.vfs.Path;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class PackageImportHelper {    
    @Inject
    private ProjectService projectService;
    
    //Check if the xml contains a Package declaration, appending one if it does not exist
    public String assertPackageNameXML( final String xml,
                                        final Path resource) {
        final String requiredPackageName = projectService.resolvePackageName( resource );
        
        if(requiredPackageName == null && "".equals(requiredPackageName)) {
            return xml;
        }
        
        DocumentBuilderFactory domfac=DocumentBuilderFactory.newInstance();

        try {
            DocumentBuilder dombuilder=domfac.newDocumentBuilder();

            Document doc=dombuilder.parse(new ByteArrayInputStream(xml.getBytes()));
            
            if(doc.getElementsByTagName("packageName").getLength() !=0) {
                return xml;
            }
            
            Element root=doc.getDocumentElement();
            Element packageElement = doc.createElement("packageName");
            packageElement.appendChild(doc.createTextNode(requiredPackageName));
            root.appendChild(packageElement);

            //output xml with pretty format
            TransformerFactory transfac = TransformerFactory.newInstance();
            Transformer trans = transfac.newTransformer();
            trans.setOutputProperty(OutputKeys.METHOD, "xml");
            trans.setOutputProperty(OutputKeys.INDENT, "yes");
            trans.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", Integer.toString(2));

            StringWriter sw = new StringWriter();
            StreamResult result = new StreamResult(sw);
            DOMSource s = new DOMSource(root);

            trans.transform(s, result);
            String xmlString = sw.toString();
            
            return xmlString;
        } catch (TransformerConfigurationException e) {
            e.printStackTrace();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (TransformerException e) {
            e.printStackTrace();
        }
        
        return xml;
    }

}
