package net.sf.webdav.methods;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.webdav.MethodExecutor;
import net.sf.webdav.WebdavStatus;

public class DoNotImplemented implements MethodExecutor {

    private static org.slf4j.Logger log = org.slf4j.LoggerFactory
	    .getLogger("net.sf.webdav.methods");
    private boolean readOnly;

    public DoNotImplemented(boolean readOnly) {
	this.readOnly = readOnly;
    }

    public void execute(HttpServletRequest req, HttpServletResponse resp)
	    throws IOException {
	log.trace("-- " + req.getMethod());

	if (readOnly) {
	    resp.sendError(WebdavStatus.SC_FORBIDDEN);

	} else

	    resp.sendError(HttpServletResponse.SC_NOT_IMPLEMENTED);
	// TODO implement proppatch

    }
}
