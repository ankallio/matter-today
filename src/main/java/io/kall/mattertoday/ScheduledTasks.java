package io.kall.mattertoday;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import io.kall.mattertoday.MattermostClient.User;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;

@Component
@Slf4j
public class ScheduledTasks {
	
	@Autowired
	private WebcalGuruService calService;
	
	@Autowired
	private MattermostService mattermostService;
	
	@Autowired
	private TodayMessageBuilder msgBuilder;
	
	@Scheduled(cron = "0 0 8 * * *")
	public void postTodayInfoIfNecessary() throws IOException, ParserException {
		// Post today message to a specified channel, if any of:
		// * One of channel's users has name-day today
		// * Today is any holidays- or holidays_good_to_know-day
		
		boolean shouldSend = sendBecauseHoliday() || sendBecauseHolidayGoodToKnow() || sendBecauseNameDay();
		
		log.info("Checked if should send message: {}", shouldSend);
		
		if (shouldSend) {
			// Send message
			String msg = msgBuilder.buildMsg(null);
			// post msg to channel
			mattermostService.sendMessageToConfiguredChannel(msg);
		}
	}
	
	private boolean sendBecauseHoliday() throws IOException, ParserException {
		return calService.getPyhat().findAny().isPresent();
	}
	
	private boolean sendBecauseHolidayGoodToKnow() throws IOException, ParserException {
		return calService.getHyvaTietaa().findAny().isPresent();
	}
	
	private boolean sendBecauseNameDay() throws IOException, ParserException {
		// Check if nameday
		List<User> users = mattermostService.getConfiguredChannelUsers();
		List<String> nimipaivaNimet = calService.getNimipaivaSankarit().stream().map(String::toLowerCase).collect(Collectors.toList());
		
		return users.stream()
				.map(u -> u.getFirstName()).filter(Objects::nonNull).map(String::toLowerCase)
				.anyMatch(name -> nimipaivaNimet.contains(name));
	}
}
