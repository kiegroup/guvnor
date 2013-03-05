package org.drools.guvnor.server.files;

import org.drools.guvnor.client.rpc.RuleAsset;
import org.drools.guvnor.client.rpc.RuleFlowContentModel;
import org.drools.guvnor.server.RepositoryServiceServlet;
import org.drools.guvnor.server.util.LoggingHelper;
import org.jboss.seam.contexts.Contexts;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class OryxEditorServlet extends RepositoryServlet {

    private static final LoggingHelper log = LoggingHelper.getLogger(OryxEditorServlet.class);

    public void service(final HttpServletRequest request,
                        final HttpServletResponse response)
            throws ServletException,
            IOException {
        log.debug("Incoming request from Oryx Designer:" + request.getRequestURL());

        if (!Contexts.isApplicationContextActive()) {
            throw new ServletException("No application context active.");
        }

        doAuthorizedAction(request,
                response,
                new Command() {
                    public void execute() throws Exception {
                        String uuid = request.getParameter("uuid");
                        try {
                            RuleAsset asset = RepositoryServiceServlet.getAssetService().loadRuleAsset(uuid);
                            if (asset.getContent() != null) {
                                response.setContentType("application/xml");
                                response.setCharacterEncoding("UTF-8");
                                String content = asset.getContent().toString();
                                if (asset.getContent() instanceof RuleFlowContentModel) {
                                    content = ((RuleFlowContentModel) asset.getContent()).getXml();
                                }

                                if (content != null) {
                                    response.getOutputStream().write(content.getBytes("UTF-8"));
                                    response.getOutputStream().close();
                                } else {
                                    setDefaultResponse(response);
                                }

                            } else {
                                setDefaultResponse(response);
                            }
                        } catch (Throwable t) {
                            log.error(t.getMessage(),
                                    t);
                            setDefaultResponse(response);
                        }
                    }
                });
    }

    private void setDefaultResponse(HttpServletResponse response) throws ServletException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        String result = "";
        response.setContentLength(result.length());
        try {
            response.getOutputStream().write(result.getBytes());
            response.getOutputStream().close();
        } catch (IOException e) {
            throw new ServletException(e.getMessage());
        }
    }
}
