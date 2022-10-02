package io.kall.mattertoday;

import java.util.List;

import lombok.Data;

@Data
public class SlashCommand {
	private String channel_id;
	private String channel_name;
	private String command;
	private String response_url;
	private String team_domain;
	private String team_id;
	private String text;
	private String token;
	private String user_id;
	private String user_name;
	private List<String> channel_mentions;
	private List<String> channel_mentions_ids;
	private List<String> user_mentions;
	private List<String> user_mentions_ids;
}