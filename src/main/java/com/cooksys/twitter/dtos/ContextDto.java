package com.cooksys.twitter.dtos;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class ContextDto {

	private TweetResponseDto target;

	List<TweetResponseDto> before;

	List<TweetResponseDto> after;
}
