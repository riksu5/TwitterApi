package com.cooksys.twitter.mappers;

//import java.util.Optional;

import org.mapstruct.Mapper;

import com.cooksys.twitter.dtos.ProfileDto;
import com.cooksys.twitter.entities.Profile;

@Mapper(componentModel = "spring")
public interface ProfileMapper {

	Profile dtoToEntity(ProfileDto profileDto);
	
	ProfileDto entityToDto(Profile profile);
	
//	default <T> T mapOptional(Optional<T> optional) {
//		return optional.orElse(null);
//	}
//	
//	default <T> Optional<T> unmapOptional(T value) {
////		Optional<T> optional = Optional.empty();
//		return Optional.ofNullable(value);
//	}
	
}
