package com.cooksys.twitter.mappers;

import java.util.List;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import com.cooksys.twitter.dtos.UserRequestDto;
import com.cooksys.twitter.dtos.UserResponseDto;
import com.cooksys.twitter.entities.User;

@Mapper(componentModel = "spring")
public interface UserMapper {

	@Mapping(target = "id", ignore = true)
	@Mapping(target = "joined", ignore = true)
	@Mapping(target = "deleted", ignore = true)
	@Mapping(target = "followers", ignore = true)
	@Mapping(target = "following", ignore = true)
	@Mapping(target = "tweets", ignore = true)
	@Mapping(target = "likedTweets", ignore = true)
	@Mapping(target = "tweetsUserIsMentionedIn", ignore = true)
	User requestDtoToEntity(UserRequestDto userRequestDto);
	
	List<User> requestDtosToEntities(List<UserRequestDto> userRequestDtos);
	
	@Mapping(source = "credentials.username", target = "username")
	@Mapping(source = "joined", target = "joined")
	@Mapping(source = "profile", target = "profile")
	UserResponseDto entityToResponseDto(User user);
	
	List<UserResponseDto> entitiesToResponseDtos(List<User> users);
	
}
