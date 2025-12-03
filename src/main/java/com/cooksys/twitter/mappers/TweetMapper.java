package com.cooksys.twitter.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cooksys.twitter.dtos.TweetRequestDto;
import com.cooksys.twitter.dtos.TweetResponseDto;
import com.cooksys.twitter.entities.Tweet;

@Mapper(componentModel = "spring", uses = { UserMapper.class })
public interface TweetMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "author", ignore = true)
	@Mapping(target = "posted", ignore = true)
	@Mapping(target = "deleted", ignore = true)
	@Mapping(target = "inReplyTo", ignore = true)
	@Mapping(target = "repostOf", ignore = true)
	@Mapping(target = "usersThatLiked", ignore = true)
	@Mapping(target = "usersMentioned", ignore = true)
	@Mapping(target = "hashtags", ignore = true)
	Tweet requestDtoToEntity(TweetRequestDto tweetRequestDto);
	
	List<Tweet> requestDtosToEntities(List<TweetRequestDto> tweetRequestDtos);
	
	@Mapping(source = "author", target = "author")
	TweetResponseDto entityToResponseDto(Tweet tweet);
	
	List<TweetResponseDto> entitiesToResponseDtos(List<Tweet> tweets);
	
}
