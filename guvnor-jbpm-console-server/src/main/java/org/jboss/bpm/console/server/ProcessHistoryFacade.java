/**
 * 
 */
package org.jboss.bpm.console.server;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;

import org.jboss.bpm.console.client.model.*;
import org.jboss.bpm.console.server.gson.GsonFactory;
import org.jboss.bpm.console.server.plugin.PluginMgr;
import org.jboss.bpm.console.server.plugin.ProcessHistoryPlugin;
import org.jboss.bpm.console.server.util.ProjectName;
import org.jboss.bpm.console.server.util.RsComment;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author Jeff Yu
 * @date Mar 13, 2011
 */
@Path("/history")
@RsComment(
    title = "Process History",
    description = "Process History Service",
    project = {ProjectName.RIFTSAW}
)
public class ProcessHistoryFacade {
	
	private static final Logger log = LoggerFactory.getLogger(ProcessHistoryFacade.class);
	
	private ProcessHistoryPlugin historyPlugin;
	
	public ProcessHistoryPlugin getProcessHistoryPlugin() {
		if (historyPlugin == null) {
			historyPlugin = PluginMgr.load(ProcessHistoryPlugin.class);
		}
		return historyPlugin;
	}
	
	@GET
	@Produces("applications/json")
	@Path("definition/{id}/instances")
	public Response findHisotryInstances(@PathParam("id") String id, @Context UriInfo info) {
		String status = info.getQueryParameters().getFirst("status");
		String stime = info.getQueryParameters().getFirst("starttime");
		String etime = info.getQueryParameters().getFirst("endtime");
		String ckey = info.getQueryParameters().getFirst("correlationkey");

        checkNotNull("definitionkey", id);
        checkNotNull("status", status);
        checkNotNull("starttime", stime);
        checkNotNull("endtime", etime);

		List<HistoryProcessInstanceRef> refs = getProcessHistoryPlugin().getHistoryProcessInstances(id, status, new Long(stime), new Long(etime), ckey);
		HistoryProcessInstanceRefWrapper wrapper = new HistoryProcessInstanceRefWrapper(refs);
		
		return createJsonResponse(wrapper);
	}


    @GET
    @Produces("application/json")
    @Path("definitions")
    public Response getProcessDefinitionKeys() {
        List<ProcessDefinitionRef> keys = getProcessHistoryPlugin().getProcessDefinitions();
        ProcessDefinitionRefWrapper wrapper = new ProcessDefinitionRefWrapper(keys);
        return createJsonResponse(wrapper);
    }


    @GET
    @Produces("application/json")
    @Path("definition/{id}/instancekeys")
    public Response getProcessInstanceKeys(@PathParam("id") String definitionId) {
        checkNotNull("definitionId", definitionId);
        List<String> instances = getProcessHistoryPlugin().getProcessInstanceKeys(definitionId);
        List<StringRef> result = new ArrayList<StringRef>();
        for (String s: instances) {
            StringRef ref = new StringRef(s);
            result.add(ref);
        }

        return createJsonResponse(new StringRefWrapper(result));
    }

    @GET
    @Produces("application/json")
    @Path("instance/{id}/activities")
    public Response getActivityKeys(@PathParam("id") String instanceId) {
        checkNotNull("instanceId", instanceId);
        List<String> activityIds = getProcessHistoryPlugin().getActivityKeys(instanceId);
        return createJsonResponse(activityIds);
    }


    @GET
    @Produces("application/json")
    @Path("instance/{id}/events")
    public Response getAllEvents(@PathParam("id") String instanceId) {
        checkNotNull("instanceId", instanceId);
        List<String> events = getProcessHistoryPlugin().getAllEvents(instanceId);

        List<StringRef> result = new LinkedList<StringRef>();
        for (String s: events) {
            StringRef ref = new StringRef(s);
            result.add(ref);
        }

        return createJsonResponse(new StringRefWrapper(result));
    }

    @GET
    @Produces("application/json")
    @Path("definition/{id}/instances/completed")
    public Response getCompletedInstances(@PathParam("id") String definitionId,
                                          @QueryParam("timestamp") String timestamp,
                                          @QueryParam("timespan") String timespan) {
        checkNotNull("definitionId", definitionId);
        checkNotNull("timestamp", timestamp);
        checkNotNull("timespan", timespan);

        Set<String> instances = getProcessHistoryPlugin().getCompletedInstances(definitionId,
                new Long(timestamp).longValue(), timespan);

        List<StringRef> result = new ArrayList<StringRef>();
        for (String s: instances) {
            StringRef ref = new StringRef(s);
            result.add(ref);
        }

        return createJsonResponse(new StringRefWrapper(result));
    }

    @GET
    @Produces("application/json")
    @Path("definition/{id}/instances/failed")
    public Response getFailedInstances(@PathParam("id") String definitionId,
                                          @QueryParam("timestamp") String timestamp,
                                          @QueryParam("timespan") String timespan) {

        checkNotNull("definitionId", definitionId);
        checkNotNull("timestamp", timestamp);
        checkNotNull("timespan", timespan);

        Set<String> instances = getProcessHistoryPlugin().getFailedInstances(definitionId,
                                    new Long(timestamp).longValue(), timespan);

        List<StringRef> result = new ArrayList<StringRef>();
        for (String s: instances) {
            StringRef ref = new StringRef(s);
            result.add(ref);
        }

        return createJsonResponse(new StringRefWrapper(result));
    }



    @GET
    @Produces("application/json")
    @Path("definition/{id}/instances/terminated")
    public Response getTerminatedInstances(@PathParam("id") String definitionId,
                                          @QueryParam("timestamp") String timestamp,
                                          @QueryParam("timespan") String timespan) {
        checkNotNull("definitionId", definitionId);
        checkNotNull("timestamp", timestamp);
        checkNotNull("timespan", timespan);

        Set<String> instances = getProcessHistoryPlugin().getTerminatedInstances(definitionId,
                                    new Long(timestamp).longValue(), timespan);

        List<StringRef> result = new ArrayList<StringRef>();
        for (String s: instances) {
            StringRef ref = new StringRef(s);
            result.add(ref);
        }

        return createJsonResponse(new StringRefWrapper(result));
    }


    @GET
    @Produces("application/json")
    @Path("definition/{id}/instances/chart/completed")
    public Response getCompletedInstances4Chart(@PathParam("id") String definitionId,
                                                @QueryParam("timespan") String timespan) {
        checkNotNull("definitionId", definitionId);
        checkNotNull("timespan", timespan);
        String result = getProcessHistoryPlugin().getCompletedInstances4Chart(definitionId, timespan);
        return Response.ok(result).type("application/json").build();
    }

    @GET
    @Produces("application/json")
    @Path("definition/{id}/instances/chart/failed")
    public Response getFailedInstances4Chart(@PathParam("id") String definitionId,
                                             @QueryParam("timespan") String timespan) {
        checkNotNull("definitionId", definitionId);
        checkNotNull("timespan", timespan);
        String result = getProcessHistoryPlugin().getFailedInstances4Chart(definitionId, timespan);
        return Response.ok(result).type("application/json").build();
    }


	private Response createJsonResponse(Object wrapper) {
	    Gson gson = GsonFactory.createInstance();
	    String json = gson.toJson(wrapper);
	    return Response.ok(json).type("application/json").build();
	}


    private void checkNotNull(String name, String value) {
         if (null == value || "".equals(value.trim())) {
             throw new NullPointerException(" " + name + " is null.");
         }
    }
	
	
}
