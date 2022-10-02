package io.kall.mattertoday;

import javax.ws.rs.FormParam;

import lombok.Data;

@Data
public class SlashCommandParams {
	@FormParam("channel_id")
	private String channelId;
	@FormParam("channel_name")
	private String channelName;
	@FormParam("command")
	private String command;
	@FormParam("response_url")
	private String responseUrl;
	@FormParam("team_domain")
	private String teamDomain;
	@FormParam("team_id")
	private String teamId;
	@FormParam("text")
	private String text;
	@FormParam("token")
	private String token;
	@FormParam("trigger_id")
	private String triggerId;
	@FormParam("user_id")
	private String userId;
	@FormParam("user_name")
	private String userName;
}