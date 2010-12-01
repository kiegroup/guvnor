package org.drools.guvnor.server.files;

import org.drools.guvnor.server.rest.ActionsAPI;
import org.drools.repository.RulesRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;
import org.drools.guvnor.server.RepositoryServiceServlet;
import org.drools.guvnor.server.ServiceImplementation;

/**
 * Entrance point to basic actions API.
 *
 * Fix for GUVNOR-1080
 *
 * @author andrew.waterman@gmail.com
 */
public class ActionsAPIServlet extends RepositoryServlet {

    public static final Logger log = LoggerFactory.getLogger(
            ActionsAPIServlet.class);

    @Override
    protected void doPost(final HttpServletRequest req,
        final HttpServletResponse res) throws ServletException, IOException
    {
        final RulesRepository repository = RestAPIServlet.getRepository();
        final ServiceImplementation service = RepositoryServiceServlet.getService();

        doAuthorizedAction(req, res, new A() {
            public void a() throws Exception {
                Map map = req.getParameterMap();
                ActionsAPI api = new ActionsAPI();
                api.post(service, repository, req, res);
            }
        });
    }
}
