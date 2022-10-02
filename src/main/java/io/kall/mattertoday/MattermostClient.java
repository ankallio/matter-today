package io.kall.mattertoday;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@FeignClient(value = "mattermost", url = "${mattermost.url}")
public interface MattermostClient {
	
	@GetMapping(path = "/api/v4/users")
	List<User> getChannelUsers(@RequestParam("in_channel") String channelId);
	
	@GetMapping(path = "/api/v4/teams/name/{team_name}/channels/name/{channel_name}")
	Channel getChannel(@PathVariable("team_name") String teamName, @PathVariable("channel_name") String channelName);
	
	@PostMapping(path = "/api/v4/posts")
	Post postMessage(@RequestBody NewPost newPost);
	
	@Data
	public static class Channel {
		private String id;
	}
	
	@Data
	public static class User {
		private String id;
		private String username;
		@JsonProperty("first_name")
		private String firstName;
	}
	
	@Data
	public static class Post {
		private String id;
	}
	
	@Data
	@AllArgsConstructor
	@NoArgsConstructor
	public static class NewPost {
		private String channel_id;
		private String message;
	}
}
