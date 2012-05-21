package org.jboss.bpm.console.client.history;

import com.google.gwt.http.client.RequestBuilder;
import com.google.gwt.http.client.Response;
import com.mvc4g.client.Controller;
import org.jboss.bpm.console.client.ApplicationContext;
import org.jboss.bpm.console.client.URLBuilder;
import org.jboss.bpm.console.client.common.AbstractRESTAction;
import org.jboss.bpm.console.client.model.JSOParser;
import org.jboss.bpm.console.client.model.StringRef;

import java.util.List;

/**
 * User: Jeff Yu
 * Date: 13/04/11
 */
public class LoadProcessInstanceEventsAction extends AbstractRESTAction {

    public static final String ID = LoadProcessInstanceEventsAction.class.getName();

    protected LoadProcessInstanceEventsAction(ApplicationContext applicationContext) {
        super(applicationContext);
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public String getUrl(Object event) {
        String instanceId = (String)event;
        return URLBuilder.getInstance().getProcessHistoryEventsURL(instanceId);
    }

    @Override
    public RequestBuilder.Method getRequestMethod() {
        return RequestBuilder.GET;
    }

    @Override
    public void handleSuccessfulResponse(Controller controller, Object event, Response response) {

        ProcessHistoryInstanceListView view = (ProcessHistoryInstanceListView)controller.getView(ProcessHistoryInstanceListView.ID);
        List<StringRef> refs = JSOParser.parseStringRef(response.getText());

        view.populateInstanceEvents(refs);

    }
}
