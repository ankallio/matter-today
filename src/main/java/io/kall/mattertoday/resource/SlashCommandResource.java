package io.kall.mattertoday.resource;

import java.io.IOException;

import javax.inject.Inject;
import javax.ws.rs.BeanParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import org.eclipse.microprofile.config.inject.ConfigProperty;

import io.kall.mattertoday.SlashCommandParams;
import io.kall.mattertoday.SlashCommandResponse;
import io.kall.mattertoday.TodayMessageBuilder;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;

@Path("mattermost/slash/today")
@Slf4j
public class SlashCommandResource {
	
	@ConfigProperty(name = "slashcommand.token")
	String configuredToken;
	
	@Inject
	TodayMessageBuilder msgBuilder;
	
	@POST
	public SlashCommandResponse today(@BeanParam SlashCommandParams cmdParams) {
		log.info("Received command: {}", cmdParams);
		
		// Verify token
		if (!configuredToken.equals(cmdParams.getToken())) {
			// Return error message
			return SlashCommandResponse.builder()
					.text("**Error: Command token does not match configured token!**")
					.build();
		}
		
		try {
			String msg = msgBuilder.buildMsg(cmdParams);
			
			return SlashCommandResponse.builder()
					.responseType(SlashCommandResponse.RESPONSE_TYPE_IN_CHANNEL)
					.text(msg)
					.build();
			
		} catch (IOException|ParserException e) {
			log.error("Failed to generate today message.", e);
			return SlashCommandResponse.builder()
					.text("**Error: "+e.toString()+"**")
					.build();
		}
		
		
	}
	
}
