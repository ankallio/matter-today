package io.kall.mattertoday;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SlashCommandResponse {
	
	public static final String RESPONSE_TYPE_EPHEMERAL = "ephemeral";
	public static final String RESPONSE_TYPE_IN_CHANNEL = "in_channel";
	
	private String text;
	
	@JsonProperty("response_type")
	private String responseType;
	
}
