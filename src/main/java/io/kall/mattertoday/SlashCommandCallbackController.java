package io.kall.mattertoday;

import java.io.IOException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;

@RestController
@Slf4j
public class SlashCommandCallbackController {
	
	@Value("${mattermost.slashcommand.today.token}")
	private String todayToken;
	
	@Autowired
	private TodayMessageBuilder msgBuilder;
	
	@PostMapping(
			path = "slash/today", 
			consumes = {MediaType.APPLICATION_FORM_URLENCODED_VALUE})
	public SlashCommandResponse today(SlashCommand command, Model model) throws IOException, ParserException {
		log.info("Received command: {}", command);
		
		String token = command.getToken();
		if (!todayToken.equals(token)) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Token does not match expected 'today' command token.");
		}
		
		String msg = msgBuilder.buildMsg(command);
		
		SlashCommandResponse response = new SlashCommandResponse();
		response.setResponseType("in_channel");
		response.setText(msg);
		return response;
	}
	
}
