package io.kall.mattertoday;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.quarkus.runtime.StartupEvent;

@ApplicationScoped
public class ConfigLogger {
	
	private static final Logger log = LoggerFactory.getLogger(ConfigLogger.class);
	
	@ConfigProperty(name = "quarkus.rest-client.mattermost.url")
	String mattermostUrl;
	
	@ConfigProperty(name = "mattermost.teamname")
	String mattermostTeam;
	
	@ConfigProperty(name = "mattermost.channelname")
	String mattermostChannel;
	
	@ConfigProperty(name = "mattermost.authorization")
	String mattermostAuthorization;
	
	@ConfigProperty(name = "slashcommand.token")
	String slashcommandToken;
	
	void onStart(@Observes StartupEvent ev) {
		log.info("Slash command token: {}", slashcommandToken.replaceAll(".", "*"));
		log.info("Mattermost URL: {}", mattermostUrl);
		log.info("Mattermost team: {}", mattermostTeam);
		log.info("Mattermost channel: {}", mattermostChannel);
		log.info("Mattermost auth header: {}", mattermostAuthorization.replaceAll(".", "*"));
	}
	
}
