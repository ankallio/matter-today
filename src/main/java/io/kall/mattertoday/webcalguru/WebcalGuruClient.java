package io.kall.mattertoday.webcalguru;

import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import io.quarkus.cache.CacheResult;

@Path("")
@RegisterRestClient(configKey = "webcalguru")
public interface WebcalGuruClient {
	
	@GET
	@Path("lataa_kalenteri")
	@CacheResult(cacheName = "webcalguru")
	@Consumes("text/calendar")
	String getIcal(@QueryParam("calendar_instance_id") String calendarInstance);
	
}
