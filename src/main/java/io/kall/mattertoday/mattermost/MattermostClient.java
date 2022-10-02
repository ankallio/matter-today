package io.kall.mattertoday.mattermost;

import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import org.eclipse.microprofile.rest.client.annotation.ClientHeaderParam;
import org.eclipse.microprofile.rest.client.inject.RegisterRestClient;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Path("")
@RegisterRestClient(configKey = "mattermost")
@ClientHeaderParam(name = "Authorization", value = "${mattermost.authorization}")
public interface MattermostClient {

	@GET
	@Path("api/v4/users")
	List<User> getChannelUsers(@QueryParam("in_channel") String channelId);
	
	@GET
	@Path("api/v4/teams/name/{team_name}/channels/name/{channel_name}")
	Channel getChannel(@PathParam("team_name") String teamName, @PathParam("channel_name") String channelName);
	
	@POST
	@Path("api/v4/posts")
	Post postMessage(NewPost newPost);
	
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
