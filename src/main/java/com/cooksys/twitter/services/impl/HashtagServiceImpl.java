package com.cooksys.twitter.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.stereotype.Service;

import com.cooksys.twitter.dtos.HashtagDto;
import com.cooksys.twitter.dtos.TweetResponseDto;
import com.cooksys.twitter.entities.Hashtag;
import com.cooksys.twitter.entities.Tweet;
import com.cooksys.twitter.exceptions.NotFoundException;
import com.cooksys.twitter.mappers.HashtagMapper;
import com.cooksys.twitter.mappers.TweetMapper;
import com.cooksys.twitter.repositories.HashtagRepository;
import com.cooksys.twitter.services.HashtagService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class HashtagServiceImpl implements HashtagService {
	
	private final HashtagRepository hashtagRepository;
	private final HashtagMapper hashtagMapper;
	
	private final TweetMapper tweetMapper;
	
	@Override
	public List<HashtagDto> getAllHashtags() {
		List<Hashtag> hashtags = hashtagRepository.findAll();
		return hashtagMapper.entitiesToDtos(hashtags);
	}

	@Override
	public List<TweetResponseDto> getTweetsByHashtagLabel(String label) {
		// Check if the hashtag with the label exists
		Optional<Hashtag> hashtagToCheck = hashtagRepository.findByLabel(label);
		if (hashtagToCheck.isEmpty()) {
			throw new NotFoundException("No tweets with the hashtag #" + label + " were found.");
		}
		
		// If so, find all non-deleted associated tweets
		Hashtag foundHashtag = hashtagToCheck.get();
		List<Tweet> tweets = new ArrayList<Tweet>();
		tweets = foundHashtag.getTweetsUsedIn();
		List<Tweet> validTweets = new ArrayList<Tweet>();
		for (Tweet t : tweets) {
			if (!t.isDeleted()) {
				validTweets.add(t);
			}
		}
		
		return tweetMapper.entitiesToResponseDtos(validTweets);
	}

}
