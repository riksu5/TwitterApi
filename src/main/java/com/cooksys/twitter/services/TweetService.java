package com.cooksys.twitter.services;

import java.util.List;

import com.cooksys.twitter.dtos.ContextDto;
import com.cooksys.twitter.dtos.CredentialsDto;
import com.cooksys.twitter.dtos.HashtagDto;
import com.cooksys.twitter.dtos.TweetRequestDto;
import com.cooksys.twitter.dtos.TweetResponseDto;
import com.cooksys.twitter.dtos.UserResponseDto;

public interface TweetService {

	List<TweetResponseDto> getAllTweets();

	TweetResponseDto createTweet(TweetRequestDto tweetRequestDto);

	TweetResponseDto getTweetById(Long id);

	TweetResponseDto deleteTweet(Long id, CredentialsDto credentialsDto);

	void createTweetLike(Long id, CredentialsDto credentialsDto);

	TweetResponseDto createTweetReply(Long id, TweetRequestDto tweetRequestDto);

	TweetResponseDto createTweetRepost(Long id, CredentialsDto credentialsDto);

	List<HashtagDto> getTagsById(Long id);

	List<UserResponseDto> getLikesById(Long id);

	ContextDto getContextById(Long id);

	List<TweetResponseDto> getRepliesById(Long id);

	List<TweetResponseDto> getRepostsById(Long id);

	List<UserResponseDto> getMentionsById(Long id);

}
