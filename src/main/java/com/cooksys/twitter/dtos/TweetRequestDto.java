package com.cooksys.twitter.dtos;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TweetRequestDto {

	private String content;
	
	private CredentialsDto credentials;
}
