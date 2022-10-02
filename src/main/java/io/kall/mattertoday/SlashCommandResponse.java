package io.kall.mattertoday;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SlashCommandResponse {
	@JsonProperty("response_type")
	private String responseType;
	
	private String text;
}