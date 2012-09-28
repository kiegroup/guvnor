/*
 * Copyright 2012 JBoss Inc
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.drools.guvnor.server.files;


import com.google.gwt.user.client.rpc.SerializationException;
import org.drools.compiler.DroolsParserException;
import org.drools.guvnor.client.common.AssetFormats;
import org.drools.guvnor.client.rpc.BulkTestRunResult;
import org.drools.guvnor.client.rpc.DetailedSerializationException;
import org.drools.guvnor.client.rpc.TestScenarioService;
import org.drools.guvnor.server.RepositoryModuleService;
import org.drools.guvnor.server.ServiceImplementation;
import org.drools.guvnor.server.TestScenarioServiceImplementation;
import org.drools.guvnor.server.repository.Preferred;
import org.drools.guvnor.server.util.FormData;
import org.drools.repository.AssetItem;
import org.drools.repository.AssetItemIterator;
import org.drools.repository.ModuleItem;
import org.drools.repository.RulesRepository;
import org.drools.repository.RulesRepositoryException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;


/**
 * This servlet deals with providing packages in binary form.
 */
public class PackageDeploymentServlet extends RepositoryServlet {

    private static final long serialVersionUID = 510l;

    private static final String RFC822DATEFORMAT = "EEE', 'dd' 'MMM' 'yyyy' 'HH:mm:ss' 'Z";
    private static final Locale HEADER_LOCALE = Locale.US;

    @Inject @Preferred
    private RulesRepository rulesRepository;

    @Inject
    private TestScenarioServiceImplementation testScenarioServiceImplementation;


    @Inject
    private FileManagerService fileManagerService;

    @Override
    protected long getLastModified(HttpServletRequest request) {
        PackageDeploymentURIHelper helper = null;
        try {
            helper = new PackageDeploymentURIHelper(request.getRequestURI());
            return fileManagerService.getLastModified(helper.getPackageName(),
                    helper.getVersion());
        } catch (UnsupportedEncodingException e) {
            return super.getLastModified(request);
        }
    }

    @Override
    protected void doHead(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
            IOException {
        if (request.getMethod().equals("HEAD")) {
            SimpleDateFormat dateFormat = new SimpleDateFormat(RFC822DATEFORMAT, HEADER_LOCALE);
            PackageDeploymentURIHelper helper = new PackageDeploymentURIHelper(request.getRequestURI());
            long mod = fileManagerService.getLastModified(helper.getPackageName(),
                    helper.getVersion());
            response.addHeader("lastModified",
                    "" + mod);
            response.addHeader("Last-Modified",
                    dateFormat.format(new Date(mod)));

        } else {
            super.doHead(request,
                    response);
        }
    }

    /**
     * This is used for importing legacy DRL.
     */
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response) throws ServletException,
            IOException {
        response.setContentType("text/html");
        String packageName = request.getParameter("packageName");
        FormData data = FileManagerService.getFormData(request);

        try {
            fileManagerService.importClassicDRL(data.getFile().getInputStream(),
                    packageName);
            response.getWriter().write("OK");
        } catch (IllegalArgumentException e) {
            response.getWriter().write(e.getMessage());
        } catch (RulesRepositoryException e) {
            response.getWriter().write("Unable to process import: " + e.getMessage());
        }

    }

    /**
     * Get the binary package.
     * This will get the compiled package stuff from either the latest package,
     * or a snapshot.
     * <p/>
     * The end of the URI is of the form:
     * /<packageName>/(<snapshotVersionName> | LATEST)
     * <p/>
     * if you pass in "LATEST" it will get the latest (not a snapshot) if it exists.
     * Normally that will only be used when downloading on demand, otherwise you should ONLY
     * use a snapshot as they are always "up to date".
     */
    protected void doGet(final HttpServletRequest req,
                         final HttpServletResponse res) throws ServletException,
            IOException {

        doAuthorizedAction(req, res, new Command() {
            public void execute() throws Exception {
                PackageDeploymentURIHelper helper = new PackageDeploymentURIHelper(req.getRequestURI());

                log.info("PackageName: " + helper.getPackageName());
                log.info("PackageVersion: " + helper.getVersion());
                log.info("PackageIsLatest: " + helper.isLatest());
                log.info("PackageIsSource: " + helper.isSource());

                ByteArrayOutputStream out = new ByteArrayOutputStream();
                String fileName = null;
                if (helper.isSource()) {
                    if (helper.isAsset()) {
                        fileName = fileManagerService.loadSourceAsset(helper.getPackageName(),
                                helper.getVersion(),
                                helper.isLatest(),
                                helper.getAssetName(),
                                out);
                    } else {
                        fileName = fileManagerService.loadSourcePackage(helper.getPackageName(),
                                helper.getVersion(),
                                helper.isLatest(),
                                out);
                    }
                } else if (helper.isDocumentation()) {

                    ModuleItem pkg = rulesRepository.loadModule(helper.getPackageName());

                    GuvnorDroolsDocsBuilder builder;
                    try {
                        builder = GuvnorDroolsDocsBuilder.getInstance(pkg);
                    } catch (DroolsParserException e) {
                        throw new ServletException("Could not parse the rule package.");

                    }

                    fileName = "documentation.pdf";

                    builder.writePDF(out);

                } else if (helper.isPng()) {
                    ModuleItem pkg = rulesRepository.loadModule(helper.getPackageName());
                    AssetItem asset = pkg.loadAsset(helper.getAssetName());

                    fileName = fileManagerService.loadFileAttachmentByUUID(asset.getUUID(),
                            out);
                } else {
                    if (req.getRequestURI().endsWith("SCENARIOS")) {
                        fileName = "TestScenariosResult.txt";
                        doRunScenarios(helper,
                                out);
                    } else if (req.getRequestURI().endsWith("ChangeSet.xml")) {
                        String url = req.getRequestURL().toString().replace("/ChangeSet.xml",
                                "");
                        fileName = "ChangeSet.xml";
                        String xml = "";
                        xml += "<change-set xmlns='http://drools.org/drools-5.0/change-set'\n";
                        xml += "    xmlns:xs='http://www.w3.org/2001/XMLSchema-instance'\n";
                        xml += "    xs:schemaLocation='http://drools.org/drools-5.0/change-set http://anonsvn.jboss.org/repos/labs/labs/jbossrules/trunk/drools-api/src/main/resources/change-set-1.0.0.xsd' >\n";
                        xml += "    <add>\n ";
                        xml += "        <resource source='" + url + "' type='PKG' />\n";
                        xml += "    </add>\n";
                        xml += "</change-set>";
                        out.write(xml.getBytes());
                    } else if (req.getRequestURI().endsWith("MODEL")) {
                        ModuleItem pkg = rulesRepository.loadModule(helper.getPackageName());
                        AssetItemIterator it = pkg.listAssetsByFormat(AssetFormats.MODEL);
                        BufferedInputStream inputFile = null;
                        byte[] data = new byte[1000];
                        int count = 0;
                        int numberOfAssets = 0;
                        while (it.hasNext()) {
                            it.next();
                            numberOfAssets++;
                        }

                        if (numberOfAssets == 0) {
                            res.setContentType("text/html");
                            PrintWriter outEM = res.getWriter();
                            outEM.println("<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0 " +
                                    "Transitional//EN\">\n" +
                                    "<HTML>\n" +
                                    "<HEAD><TITLE>Empty POJO Model(jar)</TITLE></HEAD>\n" +
                                    "<BODY>\n" +
                                    "<H1>EMPTY MODEL</H1>\n" +
                                    "</BODY></HTML>");
                            return;
                        }

                        if (numberOfAssets > 1) {
                            fileName = "Model.zip";
                            inputFile = new BufferedInputStream(zipModel(pkg));
                            while ((count = inputFile.read(data, 0, 1000)) != -1) {
                                out.write(data, 0, count);
                            }

                            inputFile.close();
                        } else {
                            fileName = "ModelJar.jar";
                            inputFile = new BufferedInputStream(zipModel(pkg));
                            while ((count = inputFile.read(data, 0, 1000)) != -1) {
                                out.write(data, 0, count);
                            }

                            inputFile.close();

                        }


                    } else if (req.getRequestURI().contains("/SpringContext/")) {

                        String uri = req.getRequestURI();
                        int lastIndexOfSlash = uri.lastIndexOf('/');
                        String assetName = uri.substring(lastIndexOfSlash + 1);
                        fileName = assetName + ".xml";

                        ModuleItem pkg = rulesRepository.loadModule(helper.getPackageName());
                        AssetItem asset = pkg.loadAsset(assetName);
                        out.write(asset.getBinaryContentAsBytes());

                    } else {
                        fileName = fileManagerService.loadBinaryPackage(helper.getPackageName(),
                                helper.getVersion(),
                                helper.isLatest(),
                                out);
                    }

                }

                res.setContentType("application/x-download");
                res.setHeader("Content-Disposition",
                        "attachment; filename=" + fileName + ";");
                res.setContentLength(out.size());
                res.getOutputStream().write(out.toByteArray());
                res.getOutputStream().flush();
            }
        });
    }

    private void doRunScenarios(PackageDeploymentURIHelper helper,
                                ByteArrayOutputStream out) throws IOException {
        ModuleItem pkg;
        if (helper.isLatest()) {
            pkg = rulesRepository.loadModule(helper.getPackageName());
        } else {
            pkg = rulesRepository.loadModuleSnapshot(helper.getPackageName(),
                    helper.getVersion());
        }
        try {
            BulkTestRunResult result = testScenarioServiceImplementation.runScenariosInPackage(pkg);
            out.write(result.toString().getBytes());
        } catch (DetailedSerializationException e) {
            log.error("Unable to run scenarios.", e);
            out.write(e.getMessage().getBytes());
        } catch (SerializationException e) {
            log.error("Unable to run scenarios.", e);
            out.write(e.getMessage().getBytes());
        }
    }

    /**
     * Zip Model
     */
    public InputStream zipModel(ModuleItem pkg) {

        LinkedList<AssetItem> jarAssets = new LinkedList<AssetItem>();
        AssetZipper assetZipper = null;

        Iterator<AssetItem> it = pkg.getAssets();
        while (it.hasNext()) {
            AssetItem asset = it.next();
            if (asset.getFormat().contentEquals("jar")) jarAssets.add(asset);
        }
        if (jarAssets.size() != 0) {
            assetZipper = new AssetZipper(jarAssets, pkg);

            return assetZipper.zipAssets();
        }

        return null;
    }

}
