package io.kall.mattertoday;

import java.io.IOException;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.kall.mattertoday.mattermost.MattermostClient.User;
import io.kall.mattertoday.mattermost.MattermostService;
import io.kall.mattertoday.webcalguru.WebcalGuruService;
import io.quarkus.scheduler.Scheduled;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;

@ApplicationScoped
@Slf4j
public class ScheduledTasks {
	
	@Inject
	WebcalGuruService calService;
	
	@Inject
	MattermostService mattermostService;
	
	@Inject
	TodayMessageBuilder msgBuilder;
	
	@Scheduled(cron = "{scheduled.message.cron}")
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
