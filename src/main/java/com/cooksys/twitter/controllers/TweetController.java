package com.cooksys.twitter.controllers;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.twitter.dtos.ContextDto;
import com.cooksys.twitter.dtos.CredentialsDto;
import com.cooksys.twitter.dtos.HashtagDto;
import com.cooksys.twitter.dtos.TweetRequestDto;
import com.cooksys.twitter.dtos.TweetResponseDto;
import com.cooksys.twitter.dtos.UserResponseDto;
import com.cooksys.twitter.services.TweetService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tweets")
@RequiredArgsConstructor
public class TweetController {

	private final TweetService tweetService;
	
	// Add relevant endpoints here
	@GetMapping
	public List<TweetResponseDto> getAllTweets() {
		return tweetService.getAllTweets();
	}
	
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public TweetResponseDto createTweet(@RequestBody TweetRequestDto tweetRequestDto) {
		return tweetService.createTweet(tweetRequestDto);
	}
	
	@GetMapping("/{id}")
	public TweetResponseDto getTweetById(@PathVariable Long id) {
		return tweetService.getTweetById(id);
	}
	
	@DeleteMapping("/{id}")
	public TweetResponseDto deleteTweet(@PathVariable("id") Long id, @RequestBody CredentialsDto credentialsDto) {
		return tweetService.deleteTweet(id, credentialsDto);
	}
	
	@PostMapping("/{id}/like")
	@ResponseStatus(HttpStatus.CREATED)
	public void createTweetLike(@PathVariable Long id, @RequestBody CredentialsDto credentialsDto) {
		tweetService.createTweetLike(id, credentialsDto);
		return;
	}
	
	@PostMapping("/{id}/reply")
	@ResponseStatus(HttpStatus.CREATED)
	public TweetResponseDto createTweetReply(@PathVariable Long id, @RequestBody TweetRequestDto tweetRequestDto) {
		return tweetService.createTweetReply(id, tweetRequestDto);
	}
	
	@PostMapping("/{id}/repost")
	@ResponseStatus(HttpStatus.CREATED)
	public TweetResponseDto createTweetRepost(@PathVariable Long id, @RequestBody CredentialsDto credentialsDto) {
		return tweetService.createTweetRepost(id, credentialsDto);
	}
	
	@GetMapping("/{id}/tags")
	public List<HashtagDto> getTagsById(@PathVariable Long id) {
		return tweetService.getTagsById(id);
	}
	
	@GetMapping("/{id}/likes")
	public List<UserResponseDto> getLikesById(@PathVariable Long id) {
		return tweetService.getLikesById(id);
	}
	
	@GetMapping("/{id}/context")
	public ContextDto getContextById(@PathVariable Long id) {
		return tweetService.getContextById(id);
	}
	
	@GetMapping("/{id}/replies")
	public List<TweetResponseDto> getRepliesById(@PathVariable Long id) {
		return tweetService.getRepliesById(id);
	}
	
	@GetMapping("/{id}/reposts")
	public List<TweetResponseDto> getRepostsById(@PathVariable Long id) {
		return tweetService.getRepostsById(id);
	}
	
	@GetMapping("/{id}/mentions")
	public List<UserResponseDto> getMentionsById(@PathVariable Long id) {
		return tweetService.getMentionsById(id);
	}
	
}
