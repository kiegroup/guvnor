package org.jboss.bpm.console.client.history;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.mvc4g.client.Controller;
import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.URLBuilder;
import org.jboss.bpm.console.client.common.AbstractRESTAction;
import org.jboss.bpm.console.client.model.JSOParser;
import org.jboss.bpm.console.client.model.ProcessDefinitionRef;
import org.jboss.bpm.console.client.util.ConsoleLog;

import java.util.List;

/**
 * User: Jeff Yu
 * Date: 11/04/11
 */
public class LoadProcessDefinitionsAction extends AbstractRESTAction {

    public static final String ID = LoadProcessDefinitionsAction.class.getName();

    protected LoadProcessDefinitionsAction(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getUrl(Object event) {
        return URLBuilder.getInstance().getProcessHistoryDefinitionsURL();
    }

    @Override
    public RequestBuilder.Method getRequestMethod() {
        return RequestBuilder.GET;
    }

    @Override
    public void handleSuccessfulResponse(Controller controller, Object event, Response response) {

        ProcessHistorySearchView view = (ProcessHistorySearchView) controller.getView(ProcessHistorySearchView.ID);
        List<ProcessDefinitionRef> refs = JSOParser.parseProcessDefinitions(response.getText());

        view.initialize(refs);

        ConsoleLog.debug("loaded " + refs.size() + " historic process definitions : " + response.getText());

    }
}
