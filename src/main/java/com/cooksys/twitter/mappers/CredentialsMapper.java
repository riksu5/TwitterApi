package com.cooksys.twitter.mappers;

import org.mapstruct.Mapper;

import com.cooksys.twitter.dtos.CredentialsDto;
import com.cooksys.twitter.entities.Credentials;

@Mapper(componentModel = "spring")
public interface CredentialsMapper {

	Credentials dtoToEntity(CredentialsDto credentialsDto);
	
	CredentialsDto entityToDto(Credentials credentials);
}
