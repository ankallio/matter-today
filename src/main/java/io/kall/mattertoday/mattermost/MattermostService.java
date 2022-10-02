package io.kall.mattertoday.mattermost;

import java.time.Duration;
import java.time.Instant;
import java.util.List;

import javax.enterprise.context.ApplicationScoped;

import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import io.kall.mattertoday.mattermost.MattermostClient.Channel;
import io.kall.mattertoday.mattermost.MattermostClient.NewPost;
import io.kall.mattertoday.mattermost.MattermostClient.Post;
import io.kall.mattertoday.mattermost.MattermostClient.User;
import lombok.extern.slf4j.Slf4j;

@ApplicationScoped
@Slf4j
public class MattermostService {
	
	@RestClient
	MattermostClient mattermost;
	
	@ConfigProperty(name = "mattermost.teamname")
	String teamName;
	
	@ConfigProperty(name = "mattermost.channelname")
	String channelName;
	
	public List<User> getChannelUsers(String channelId) {
		Instant before = Instant.now();
		List<User> users = mattermost.getChannelUsers(channelId);
		Duration took = Duration.between(before, Instant.now());
		log.info("Found {} users from channel {} in {}", users.size(), channelId, took);
		return users;
	}
	
	public List<User> getConfiguredChannelUsers() {
		try {
			Instant before = Instant.now();
			Channel channel = mattermost.getChannel(teamName, channelName);
			Duration took = Duration.between(before, Instant.now());
			log.info("Channel info lookup took {}", took);
			String channelId = channel.getId();
			return getChannelUsers(channelId);
		} catch (Exception e) {
			log.warn("Failed to get channel user information.", e);
			return List.of();
		}
	}
	
	public Post sendMessageToConfiguredChannel(String message) {
		Instant before = Instant.now();
		Channel channel = mattermost.getChannel(teamName, channelName);
		Duration took = Duration.between(before, Instant.now());
		log.info("Channel info lookup took {}", took);
		String channelId = channel.getId();
		
		before = Instant.now();
		Post result = mattermost.postMessage(new NewPost(channelId, message));
		took = Duration.between(before, Instant.now());
		log.info("Message posting took {}", took);
		
		return result;
	}
	
}
