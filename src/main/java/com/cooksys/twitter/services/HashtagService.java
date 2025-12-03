package com.cooksys.twitter.services;

import java.util.List;

import com.cooksys.twitter.dtos.HashtagDto;
import com.cooksys.twitter.dtos.TweetResponseDto;

public interface HashtagService {

	List<HashtagDto> getAllHashtags();

	List<TweetResponseDto> getTweetsByHashtagLabel(String label);

}
