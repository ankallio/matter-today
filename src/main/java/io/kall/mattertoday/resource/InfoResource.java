package io.kall.mattertoday.resource;

import java.io.IOException;
import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;

import io.kall.mattertoday.webcalguru.WebcalGuruService;
import net.fortuna.ical4j.data.ParserException;

@Path("info")
public class InfoResource {
	
	@Inject
	WebcalGuruService calService;
	
	@GET
	@Path("nimet")
	public List<String> nimipaivasankarit() throws IOException, ParserException {
		return calService.getNimipaivaSankarit();
	}
	
	@GET
	@Path("hauskat")
	public List<String> hauskat() throws IOException, ParserException {
		return calService.getHauskatMerkkipaivat().toList();
	}
	
	@GET
	@Path("hyvatietaa")
	public List<String> hyvatietaa() throws IOException, ParserException {
		return calService.getHyvaTietaa().toList();
	}
	
	@GET
	@Path("pyhat")
	public List<String> pyhat() throws IOException, ParserException {
		return calService.getPyhat().toList();
	}
	
}
