package com.cooksys.twitter.dtos;

import java.sql.Timestamp;

import org.hibernate.annotations.CreationTimestamp;

import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@Data
public class TweetResponseDto {

	private Long id;
	
	private UserResponseDto author;
	
	@CreationTimestamp
	private Timestamp posted;
	
	// Optional
	private String content;
	
	// Optional
	private TweetResponseDto inReplyTo;
	
	// Optional
	private TweetResponseDto repostOf;
}

/***
	3 types of tweets:
	- Simple: content value, but no inReplyTo or repostOf values
	- Repost: Has a repostOf value, but no content or inReplyTo values
	- Reply: Has content and replyTo values, but no repostOf value

**/