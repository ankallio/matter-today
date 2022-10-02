package io.kall.mattertoday;

import java.io.IOException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.List;
import java.util.stream.Collectors;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;

import io.kall.mattertoday.mattermost.MattermostClient.User;
import io.kall.mattertoday.mattermost.MattermostService;
import io.kall.mattertoday.webcalguru.WebcalGuruService;
import lombok.extern.slf4j.Slf4j;
import net.fortuna.ical4j.data.ParserException;

@ApplicationScoped
@Slf4j
public class TodayMessageBuilder {
	
	@Inject
	WebcalGuruService calService;
	
	@Inject
	MattermostService mattermostService;

	private static final ZoneId ZONE = ZoneId.systemDefault();
	
	public String buildMsg(SlashCommandParams command) throws IOException, ParserException {
		boolean automated = false;
		if (command == null) {
			automated = true;
			command = new SlashCommandParams();
		}

		LocalDate date = LocalDate.now(ZONE);
		
		StringBuilder sb = new StringBuilder("## Tänään " + date.toString());
		
		List<String> names = calService.getNimipaivaSankarit();
		if (!names.isEmpty()) {
			sb.append("\n* Nimipäiväsankarit: **"+names.stream().collect(Collectors.joining(", "))+"**");
			
			String channelId = command.getChannelId();
			List<User> matchingNameUsers;
			if (channelId != null) {
				try {
					List<User> channelUsers = mattermostService.getChannelUsers(channelId);
					log.info("Found {} users on channel: {}", channelUsers.size(), channelUsers);
					matchingNameUsers = channelUsers.stream()
							.filter(user -> names.contains(user.getFirstName()))
							.collect(Collectors.toList());
				} catch (Exception e) {
					log.error("User lookup failure", e);
					matchingNameUsers = List.of();
				}
			} else {
				log.warn("No channel ID in request");
				matchingNameUsers = List.of();
			}
			
			if (!matchingNameUsers.isEmpty()) {
				sb.append("\n* Onnittelut siis: "
						+ matchingNameUsers.stream().map(u -> "@"+u.getUsername()).collect(Collectors.joining(" "))
						+ " :partying_face:");
			}
		}
		
		List<String> pyhat = calService.getPyhat().collect(Collectors.toList());
		if (!pyhat.isEmpty()) {
			sb.append("\n* Pyhät: " + pyhat.stream().collect(Collectors.joining(", ")));
		}
		
		List<String> hyvatietaa = calService.getHyvaTietaa().collect(Collectors.toList());
		if (!hyvatietaa.isEmpty()) {
			sb.append("\n* Hyvä tietää: " + hyvatietaa.stream().collect(Collectors.joining(", ")));
		}
		
		List<String> hauskat = calService.getHauskatMerkkipaivat().collect(Collectors.toList());
		if (!hauskat.isEmpty()) {
			sb.append("\n* Muuta: " + hauskat.stream().collect(Collectors.joining(", ")));
		}
		
		// Add info thing to last line
		StringBuilder commandInfoBuilder = new StringBuilder();
		if (command.getCommand() != null) {
			commandInfoBuilder.append("Näytetty päivän tiedot komennolla [" + command.getCommand() + "]. ");
		}
		if (command.getUserName() != null) {
			commandInfoBuilder.append("Käynnistäjä: [" + command.getUserName() + "]. ");
		}
		if (automated) {
			commandInfoBuilder.append("Ajastettu toiminto. ");
		}
		
		String commandInfo = commandInfoBuilder.toString().replace("\"", "'");
		sb.append(" [[?](# \"" + commandInfo + "\")]");
		
		return sb.toString();
	}
	
}
