package com.cooksys.twitter.mappers;

import java.util.List;

import org.mapstruct.Mapper;

import com.cooksys.twitter.dtos.HashtagDto;
import com.cooksys.twitter.entities.Hashtag;

@Mapper(componentModel = "spring")
public interface HashtagMapper {

//	Hashtag dtoToEntity(HashtagDto hashtagDto);
	
//	List<Hashtag> dtosToEntities(List<HashtagDto> hashtagDtos);
	
	HashtagDto entityToDto(Hashtag hashtag);
	
	List<HashtagDto> entitiesToDtos(List<Hashtag> hashtags);
}
