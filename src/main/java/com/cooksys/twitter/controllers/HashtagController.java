package com.cooksys.twitter.controllers;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.cooksys.twitter.dtos.HashtagDto;
import com.cooksys.twitter.dtos.TweetResponseDto;
import com.cooksys.twitter.services.HashtagService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/tags")
@RequiredArgsConstructor
public class HashtagController {

	private final HashtagService hashtagService;
	
	// Add relevant endpoints here
	@GetMapping
	public List<HashtagDto> getAllHashtags() {
		return hashtagService.getAllHashtags();
	}
	
	@GetMapping("/{label}")
	public List<TweetResponseDto> getTweetsByHashtagLabel(@PathVariable String label) {
		return hashtagService.getTweetsByHashtagLabel(label);
	}
}
